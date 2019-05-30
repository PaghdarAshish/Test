package com.activiti.oaf.adapter.util;

import com.activiti.oaf.adapter.bean.OrderApproval;
import com.activiti.oaf.adapter.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.activiti.oaf.adapter.controller.OAFController.KSCHL_UNIQUE_FIELD_VALUE;
import static org.apache.http.Consts.UTF_8;
import static org.apache.http.HttpHeaders.*;

/**
 * Bean for OAF related operations
 * 
 * @author Pradip Patel
 */
public class OAFOperation {

	private static final Logger logger = LoggerFactory.getLogger(OAFOperation.class);

	@Value("${oaf.acs.alfresco.base-url}")
	private String ACS_ALFRESCO_BASE_URL;
	@Value("${oaf.acs.doc.search.service}")
	private String ACS_DOC_SEARCH_SERVICE;
	@Value("${oaf.acs.doc.search.property-name}")
	private String ACS_DOC_SEARCH_PROPERTY_NAME;
	@Value("${oaf.acs.doc.download.service}")
	private String ACS_DOC_DOWNLOAD_SERVICE;
	@Value("${oaf.acs.doc.content.context}")
	private String ACS_DOC_CONTENT_CONTEXT;
	@Value("${oaf.acs.username}")
	private String ACS_USERNAME;
	@Value("${oaf.acs.password}")
	private String ACS_PASSWORD;
	@Value("${oaf.process.doc.attachment.default-category}")
	private String DOC_DEFAULT_CATEGORY;
	@Value("${oaf.attachment.fetch.retry.count}")
	private int ATTACHMENT_RETRY_COUNT;
	@Value("${oaf.attachment.fetch.second.delay}")
	private int ATTACHMENT_FETCH_DELAY;

	/**
	 * This method is responsible to generate previous version of
	 * {@link OrderApproval}
	 * 
	 * @param orderApproval
	 * @return com.activiti.oaf.adapter.bean.OrderApproval
	 */
	public static OrderApproval generatePreviousOrderApprovalVersion(OrderApproval orderApproval) {
		OrderApproval preOrderApproval = new OrderApproval();
		preOrderApproval.setAction(orderApproval.getAction());
		preOrderApproval.setInquiryNumber(orderApproval.getInquiryNumber());
		preOrderApproval.setQuotationNumber(orderApproval.getQuotationNumber());
		preOrderApproval.setCreated(orderApproval.getCreated());
		preOrderApproval.setVersion(orderApproval.getVersion() - 1);
		preOrderApproval.setStatus(orderApproval.getStatus());
		return preOrderApproval;
	}

	/**
	 * This method is responsible to verify any changes in five unique fields.
	 * 
	 * @param quotationDetail
	 * @param existingOrderApproval
	 * @return
	 */
	public boolean verifyQuoteCompositeFields(UIQuotationDetail quotationDetail, OrderApproval existingOrderApproval) {
		if (logger.isDebugEnabled()) {
			logger.debug("OAFOperation.verifyQuoteCompositeFields");
		}
		UIQuotationDetail _uiQuotationDetail = OAFUtil.convertJsonStringToQuote(existingOrderApproval.getJsonPayload());

		UIHeaderSegment headerSegment = quotationDetail.getHeaderSegment();
		UIHeaderSegment existingSegment = _uiQuotationDetail.getHeaderSegment();

		// PROJMT -> Tonnage of the quote – Weight of the quote
		// MarginCPP -> GM % of the quote – Gross margin calculated at the time of
		// submission for approval
		// VKBUR ->  Sales office – Sales office of the quote
		// KSCHL_UNIQUE_FIELD_VALUE  Engineering services – Condition in the quote
		// KST -> Kirby standard terms
		return (this.isChanged(headerSegment, existingSegment, "projmt")
				|| this.isChanged(headerSegment, existingSegment, "marginCPP")
				|| this.isChanged(headerSegment, existingSegment, "vkbur")
				|| this.isChanged(headerSegment, existingSegment, "kst")
				|| validateOtherChargesKschlFieldModified(quotationDetail, _uiQuotationDetail));
	}

	/**
	 * This method is responsible to valadiate any updation in OtherCharges
	 * KSCHL(Engineering service) field.
	 * 
	 * @param quotationDetailNew
	 * @param quotationDetailOld
	 * @return
	 */
	private boolean validateOtherChargesKschlFieldModified(UIQuotationDetail quotationDetailNew,
			UIQuotationDetail quotationDetailOld) {

		boolean isModified = false;
		if (!(quotationDetailNew.getOtherCharges().equals(quotationDetailOld.getOtherCharges()))) {
			if (logger.isDebugEnabled()) {
				logger.debug(" validate validateOtherChargesKschlFieldModified ");
			}
			List<UIOtherCharges> otherCharges = quotationDetailNew.getOtherCharges();
			List<UIOtherCharges> existOtherCharges = quotationDetailOld.getOtherCharges();

			AtomicReference<UIOtherCharges> engServiceInExistOtherCharges = new AtomicReference<>();
			AtomicReference<UIOtherCharges> engServiceInNewOtherCharges = new AtomicReference<>();
			Boolean availableInExist = getOtherChargesIfExist(existOtherCharges, engServiceInExistOtherCharges);
			Boolean availableInNew = getOtherChargesIfExist(otherCharges, engServiceInNewOtherCharges);
//            Boolean availableInNew = otherCharges.stream().anyMatch(other -> KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(other.getKschl()));
			if (logger.isDebugEnabled()) {
				logger.debug("availableInExist = " + availableInExist);
				logger.debug("availableInNew = " + availableInNew);
			}

			if (availableInExist.compareTo(availableInNew) == 0) {
				return !(engServiceInNewOtherCharges.get().equals(engServiceInExistOtherCharges.get()));
			} else {
				isModified = true;
			}

			/*
			 * for (UIOtherCharges otherCharge : otherCharges) {
			 *//*
				 * only check existing other changes element if value for KSCHL is YKES
				 *//*
					 * if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl()) &&
					 * availableInExist) {
					 * 
					 * boolean ykesElementModidied = existOtherCharges.stream() .filter(other ->
					 * KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(other.getKschl())) .anyMatch(ykes
					 * -> ykes.equals(otherCharge));
					 * 
					 * isModified = !ykesElementModidied; break;
					 * 
					 * } else if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl())
					 * && !availableInExist) { isModified = true; break; }
					 * 
					 * }
					 */

		}
		return isModified;
	}

	/**
	 * This method is responsible to get OtherCharges if exist.
	 * 
	 * @param existOtherCharges
	 * @param engServiceInExistOtherCharges
	 * @return
	 */
	private boolean getOtherChargesIfExist(List<UIOtherCharges> existOtherCharges,
			AtomicReference<UIOtherCharges> engServiceInExistOtherCharges) {
		return existOtherCharges.stream().anyMatch(other -> {
			if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(other.getKschl())) {
				engServiceInExistOtherCharges.set(other);
				return true;
			} else {
				return false;
			}
		});
	}

	/**
	 * This method is responsible to check is there any update in request json
	 * payload.
	 * 
	 * @param existingOrderApproval
	 * @param quotationDetail
	 * @return
	 */
	public boolean verifyQuoteFieldsForHighlight(OrderApproval existingOrderApproval,
			UIQuotationDetail quotationDetail) {

		UIQuotationDetail _uiQuotationDetail = OAFUtil.convertJsonStringToQuote(existingOrderApproval.getJsonPayload());
		if (logger.isDebugEnabled()) {
			logger.debug("OAFOperation.verifyQuoteFieldsForHighlight");
		}

		boolean isUpdated;
		UIHeaderSegment segment = quotationDetail.getHeaderSegment();
		UIHeaderSegment existSegment = _uiQuotationDetail.getHeaderSegment();

		isUpdated = this.findDifferenceAndUpdateObject(segment, existSegment);
		quotationDetail.setHeaderSegment(segment);
		segment = null;
		existSegment = null;

		// validate HeaderSegment

		List<UIAttachment> existAttachments = _uiQuotationDetail.getAttachments();
		List<UIAttachment> attachments = quotationDetail.getAttachments();

//        boolean differenceInAttachments = this.findDifferenceInAttachments(attachments, existAttachments);
		boolean differenceInAttachments = !(this.equalLists(attachments, existAttachments));
		if (differenceInAttachments) {
			isUpdated = differenceInAttachments;
		}

		/*
		 * if
		 * (!(quotationDetail.getAttachments().equals(_uiQuotationDetail.getAttachments(
		 * )))) { List<UIAttachment> attachments = quotationDetail.getAttachments();
		 * List<UIAttachment> existAttachments = _uiQuotationDetail.getAttachments();
		 * List<UIAttachment> modifiedAttachments = new ArrayList<>();
		 *
		 * for (UIAttachment attachment : attachments) { for (UIAttachment attach :
		 * existAttachments) { if(attachment.getDocName().equals(attach.getDocName())){
		 * if(!attachment.equals(attach)){ } break; } } } }
		 */

		if (!(quotationDetail.getPriceAndWeightBreakups().equals(_uiQuotationDetail.getPriceAndWeightBreakups()))) {
			if (logger.isDebugEnabled()) {
				logger.debug(" validate PriceAndWeightBreakups ");
			}
			List<UIPriceAndWeightBreakup> priceAndWeightBreakups = quotationDetail.getPriceAndWeightBreakups();
			List<UIPriceAndWeightBreakup> existPriceAndWeightBreakups = _uiQuotationDetail.getPriceAndWeightBreakups();
			List<UIPriceAndWeightBreakup> modifiedPriceAndWeightBreakups = new ArrayList<>();

			for (UIPriceAndWeightBreakup priceAndWeightBreakup : priceAndWeightBreakups) {
				for (UIPriceAndWeightBreakup existPriceAndWeightBreakup : existPriceAndWeightBreakups) {
					if (priceAndWeightBreakup.getItem().equalsIgnoreCase(existPriceAndWeightBreakup.getItem())) {
						if (!priceAndWeightBreakup.equals(existPriceAndWeightBreakup)) {
							priceAndWeightBreakup.setUpdated("true");
						}
						modifiedPriceAndWeightBreakups.add(priceAndWeightBreakup);
						break;
					}
				}
			}

			quotationDetail.setPriceAndWeightBreakups(modifiedPriceAndWeightBreakups);
			priceAndWeightBreakups = null;
			existPriceAndWeightBreakups = null;
			isUpdated = true;
		}

		if (!(quotationDetail.getOtherCharges().equals(_uiQuotationDetail.getOtherCharges()))) {
			if (logger.isDebugEnabled()) {
				logger.debug(" validate OtherCharges ");
			}
			List<UIOtherCharges> otherCharges = quotationDetail.getOtherCharges();
			List<UIOtherCharges> existOtherCharges = _uiQuotationDetail.getOtherCharges();
			List<UIOtherCharges> modifiedOtherCharges = new ArrayList<>();

			for (UIOtherCharges otherCharge : otherCharges) {
				for (UIOtherCharges existOtherCharge : existOtherCharges) {
					if (otherCharge.getItem().equalsIgnoreCase(existOtherCharge.getItem())) {
						if (!otherCharge.equals(existOtherCharge)) {
							otherCharge.setUpdated("true");
						}
						modifiedOtherCharges.add(otherCharge);
						break;
					}
				}
			}

			quotationDetail.setOtherCharges(modifiedOtherCharges);
			otherCharges = null;
			existOtherCharges = null;
			isUpdated = true;
		}

		return isUpdated;
	}

	/**
	 * This method is responsible to check any changes in HeaderSegment properties.
	 * 
	 * @param segment
	 * @param existingSegment
	 * @param elementName
	 * @return
	 */
	private boolean isChanged(UIHeaderSegment segment, UIHeaderSegment existingSegment, final String elementName) {

		PropertyDescriptor segmentField, existingField;
		try {
			segmentField = new PropertyDescriptor(elementName, segment.getClass());
			existingField = new PropertyDescriptor(elementName, existingSegment.getClass());

			Method getSegmentMethod = segmentField.getReadMethod();
			Method getExistingMethod = existingField.getReadMethod();

			HeaderElement segmentElement = ((HeaderElement) getSegmentMethod.invoke(segment));
			HeaderElement existingElement = ((HeaderElement) getExistingMethod.invoke(existingSegment));

			if (segmentElement == null && existingElement == null) {
				return false;
			}
			if ((segmentElement == null && existingElement != null)
					|| segmentElement != null && existingElement == null) {
				return true;
			}
			return !segmentElement.getValue().equals(existingElement.getValue());

		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error("Error while comparing Composite fields.", e);
		}

		return false;

	}

	/**
	 * This method is responsible to compare equality of existing attachments and
	 * new attachments.
	 * 
	 * @param attachments
	 * @param existAttachments
	 * @return
	 */
	private boolean equalLists(List<UIAttachment> attachments, List<UIAttachment> existAttachments) {
		if (attachments == null && existAttachments == null) {
			return true;
		}

		if ((attachments == null && existAttachments != null) || attachments != null && existAttachments == null
				|| attachments.size() != existAttachments.size()) {
			return false;
		}

//        attachments = new ArrayList<>(attachments);
//        existAttachments = new ArrayList<>(existAttachments);

		attachments.sort(Comparator.comparing(UIAttachment::getGuid));
		existAttachments.sort(Comparator.comparing(UIAttachment::getGuid));

		/*
		 * List<UIAttachment> collect = attachments.stream().filter(e ->
		 * (existAttachments.stream() .filter(d -> d.getGuid().equals(e.getGuid()))
		 * .count()) < 1).collect(Collectors.toList());
		 */

		return attachments.equals(existAttachments);
	}

	/**
	 * This method is responsile to find difference in Attachments.
	 * 
	 * @param attachments
	 * @param existAttachments
	 * @return
	 */
	private boolean findDifferenceInAttachments(List<UIAttachment> attachments, List<UIAttachment> existAttachments) {
		boolean isUpdated;

		/*
		 * if (existAttachments.size() == attachments.size()) { //
		 * if(!(attachments.equals(existAttachments))){ // isUpdated = true; if
		 * (logger.isDebugEnabled()) { logger.debug(" validate Attachments "); }
		 * 
		 * boolean match = attachments.stream().allMatch(num ->
		 * existAttachments.contains(num)); for (UIAttachment attachment : attachments)
		 * { for (UIAttachment attach : existAttachments) {
		 * if(attachment.getGuid().equalsIgnoreCase(attach.getGuid())){ isUpdated =
		 * false; } if (!attachment.getGuid().equalsIgnoreCase(attach.getGuid())) {
		 * isUpdated = true; break; } } } // } } else { isUpdated = true; }
		 */
		if (logger.isDebugEnabled()) {
			logger.debug(" validate Attachments ");
		}
		if (existAttachments.size() == attachments.size()) {
//            isUpdated = attachments.stream().allMatch(attachment -> existAttachments.contains(attachment));
			isUpdated = existAttachments.stream()
					.allMatch(existingAttachment -> attachments.contains(existingAttachment));
		} else {
			isUpdated = true;
		}
//        isUpdated = attachments.containsAll(existAttachments);

		return isUpdated;
	}

	/**
	 * This method is responsible to find difference in {@link UIHeaderSegment} and
	 * update it.
	 * 
	 * @param segment
	 * @param existSegment
	 * @return
	 */
	private boolean findDifferenceAndUpdateObject(UIHeaderSegment segment, UIHeaderSegment existSegment) {

		boolean isUpdated = false;

		if (!(existSegment.equals(segment))) {

			isUpdated = true;

			if (logger.isDebugEnabled()) {
				logger.debug(" validate HeaderSegment ");
			}
			PropertyDescriptor segmentField, existingField;
			for (Field field : segment.getClass().getDeclaredFields()) {

				try {
					segmentField = new PropertyDescriptor(field.getName(), segment.getClass());
					existingField = new PropertyDescriptor(field.getName(), existSegment.getClass());

					Method getSegmentMethod = segmentField.getReadMethod();
					Method getExistingSegmentMethod = existingField.getReadMethod();

					HeaderElement segmentElement = ((HeaderElement) getSegmentMethod.invoke(segment));
					HeaderElement existingElement = ((HeaderElement) getExistingSegmentMethod.invoke(existSegment));

					// compare fields for not null
					if (segmentElement != null && existingElement != null) {
						segmentElement.setUpdate(!segmentElement.getValue().equals(existingElement.getValue()));
					} else if (existingElement == null && segmentElement != null) {
						segmentElement.setUpdate(Boolean.TRUE);
					}

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| IntrospectionException e) {
					logger.error("Error while comparing JSON Payload.", e);
				}
			}
		}

		return isUpdated;
	}

	/**
	 * This method is responsible to generate attachment download link, with 10 SEC
	 * sleep and 5 iteration per guid.
	 * 
	 * @param attachments
	 * @param orderApproval
	 * @return
	 */
	public List<UIAttachment> generateAttachmentDownloadLinks(List<UIAttachment> attachments,
			OrderApproval orderApproval) {

		if (attachments != null && attachments.size() == 0) {
			return attachments;
		}
		int attachmentSize = attachments.size();
		if (logger.isDebugEnabled()) {
			logger.debug("OAFOperation.generateAttachmentDownloadLinks");
			logger.debug("attachmentSize = " + attachmentSize);
		}
		try {
			TimeUnit.SECONDS.sleep(ATTACHMENT_FETCH_DELAY);
		} catch (InterruptedException e) {
			logger.error(
					"OAFOperation.generateAttachmentDownloadLinks :: Exception -> error occurs while sleeping thread for "+ATTACHMENT_FETCH_DELAY+" seconds.",
					e);
		}
		if (ATTACHMENT_RETRY_COUNT == 0) {
			ATTACHMENT_RETRY_COUNT = 5;
		}
		int nodeRefCount = 0;
		for (int i = 1; i <= ATTACHMENT_RETRY_COUNT; i++) {
			int tempNodeRefCount = 0;
			for (UIAttachment attachment : attachments) {
				if (attachment.getGuid() != null && attachment.getGuid().length() > 1) {
					if (attachment.getCategory() != null && attachment.getCategory().trim().length() == 0) {
						attachment.setCategory(DOC_DEFAULT_CATEGORY);
					}
					if (attachment.getDownloadURL() != null) {
						if (attachment.getDownloadURL().trim().isEmpty()) {
							if (logger.isDebugEnabled()) {
								logger.debug("attachment.getGuid() = " + attachment.getGuid());
							}
							// search document from ACS once
							String nodeRef = this.getNodeRefByDocGuid(attachment.getGuid());
							if (nodeRef != null) {
								String nodeId = this.getNodeId(nodeRef);
								if (logger.isDebugEnabled()) {
									logger.debug("nodeId = " + nodeId);
								}
								attachment.setDownloadURL(nodeId);
//								nodeRefCount += 1;
								tempNodeRefCount+=1;
							} else {
								if (logger.isDebugEnabled()) {
									logger.debug("No Document found for GUID :: " + attachment.getGuid());
								}
							}
						} else {
//							nodeRefCount += 1;
							tempNodeRefCount += 1;
						}
					}
				}
			}
			nodeRefCount = tempNodeRefCount;
			if (nodeRefCount == attachments.size()) {
				break;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("nodeRefCount = " + nodeRefCount);
			logger.debug("attachments.size() = " + attachments.size());
		}
		if (nodeRefCount < attachmentSize) {
			// set found attachment out of n.
			orderApproval.setFoundAttachments("(" + nodeRefCount + "/" + attachmentSize + ")");
			return null;
		}

		return attachments;
	}

	/**
	 * This method is responsible to generate attachment download link.
	 * 
	 * @param attachments
	 * @return
	 */
	public List<UIAttachment> generateAttachmentDownloadLinksWithoutSleep(List<UIAttachment> attachments) {
		int attachmentSize = attachments.size();
		if (logger.isDebugEnabled()) {
			logger.debug("OAFOperation.generateAttachmentDownloadLinks");
			logger.debug("attachmentSize = " + attachmentSize);
		}

		int nodeRefCount = 0;
		for (UIAttachment attachment : attachments) {
			if (attachment.getGuid() != null && attachment.getGuid().length() > 1) {
				if (attachment.getCategory() != null && attachment.getCategory().trim().length() == 0) {
//                    attachment.setCategory("C");
					attachment.setCategory(DOC_DEFAULT_CATEGORY);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("attachment.getGuid() = " + attachment.getGuid());
				}
				// search document from ACS once
				String nodeRef = this.getNodeRefByDocGuid(attachment.getGuid());
				if (nodeRef != null) {
					String nodeId = this.getNodeId(nodeRef);
					if (logger.isDebugEnabled()) {
//                        logger.debug("nodeRef = " + nodeRef);
						logger.debug("nodeId = " + nodeId);
					}
					attachment.setDownloadURL(nodeId);
					nodeRefCount += 1;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("No Document found for GUID :: " + attachment.getGuid());
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("nodeRefCount = " + nodeRefCount);
		}
		/*
		 * if (nodeRefCount == 0 || nodeRefCount < (attachmentSize >> 1)) { return null;
		 * }
		 */
		if (nodeRefCount < attachmentSize) {
			return null;
		}

		return attachments;
	}

	/**
	 * This method is responsible to find nodeRef based on GUID with 5 iteration.
	 * 
	 * @param guid
	 * @return
	 */
	private String getNodeRefByDocGuidInLoop(String guid) {
		if (ATTACHMENT_RETRY_COUNT == 0) {
			ATTACHMENT_RETRY_COUNT = 5;
		}
		String nodeRef = null;
		for (int i = 1; i <= ATTACHMENT_RETRY_COUNT; i++) {
			String refByDocGuid = this.getNodeRefByDocGuid(guid);
			if (refByDocGuid != null) {
				nodeRef = refByDocGuid;
				break;
			}
		}
		return nodeRef;
	}

	/**
	 * This method is responsible to get NodeRef by GUID.
	 * 
	 * @param GUID
	 * @return
	 */
	private String getNodeRefByDocGuid(String GUID) {
		if (logger.isDebugEnabled()) {
			logger.debug("OAFOperation.getNodeRefByDocGuid");
		}
		String url = ACS_ALFRESCO_BASE_URL + ACS_DOC_SEARCH_SERVICE;
		String propertyName = ACS_DOC_SEARCH_PROPERTY_NAME;
		byte[] encoding;
		String nodeRef = null;
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameter("q", propertyName + ":" + GUID + " AND TYPE:'cm:content'");
			uriBuilder.addParameter("lang", "fts-alfresco");
			uriBuilder.addParameter("store", "workspace://SpacesStore");
			uriBuilder.addParameter("maxResults", "2");
			url = uriBuilder.build().toString();
			if (logger.isDebugEnabled()) {
				logger.debug("url = " + url);
			}
			HttpGet httpGet = new HttpGet(url);

			httpGet.setHeader(ACCEPT, "application/json");
			httpGet.setHeader(CONTENT_TYPE, "application/json");
			String userPass = ACS_USERNAME + ":" + ACS_PASSWORD;
			encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
			httpGet.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			if (logger.isDebugEnabled()) {
				logger.debug("httpResponse.getStatusLine() = " + httpResponse.getStatusLine());
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String responseAsString = EntityUtils.toString(httpResponse.getEntity());
				/*
				 * if (logger.isDebugEnabled()) { logger.debug("responseAsString = " +
				 * responseAsString); }
				 */
				JsonNode jsonNode = new ObjectMapper().readTree(responseAsString);
				JsonNode results = jsonNode.get("numResults");
				int noRes = results.asInt();
				if (noRes > 0) {
					JsonNode nodeRefNode = jsonNode.get("results").get(noRes - 1).get("nodeRef");
					nodeRef = nodeRefNode.asText();
					if (logger.isDebugEnabled()) {
						logger.debug("nodeRef = " + nodeRef);
					}
				}
			}
			return nodeRef;
		} catch (JsonProcessingException e) {
			logger.error("Error while processing json :: ", e.getCause());
		} catch (URISyntaxException e) {
			logger.error("Error :: Invalid URI syntax :: ", e.getCause());
		} catch (ClientProtocolException e) {
			logger.error("Error while request " + url + "  :: ", e.getCause());
		} catch (IOException e) {
			logger.error("Error while processing request " + url + "  :: ", e.getCause());
		}
		return nodeRef;
	}

	/**
	 * This method is responsible to get nodeId from nodeRef.
	 * 
	 * @param nodeRef
	 * @return
	 */
	private String getNodeId(String nodeRef) {
		nodeRef = nodeRef.replaceAll("://", "/");
		return nodeRef.substring(nodeRef.lastIndexOf("/") + 1);
	}

	/**
	 * This method is responsible to format date from one to another.
	 * 
	 * @param preDateFormat
	 * @param postDateFormat
	 * @param dateToConvert
	 * @return
	 */
	public String getFormattedDate(String preDateFormat, String postDateFormat, String dateToConvert) {
		try {
			LocalDate date = LocalDate.parse(dateToConvert, DateTimeFormatter.ofPattern(preDateFormat));
			String formattedDate = date.format(DateTimeFormatter.ofPattern(postDateFormat));
			return formattedDate;
		} catch (Exception e) {
			return dateToConvert;
		}
	}

	/**
	 * This method is responsible to set default category of attachment if not
	 * exist.
	 * 
	 * @param quotationDetail
	 */
	public void setAttachmentDefaultCategory(UIQuotationDetail quotationDetail) {
		if (quotationDetail != null && quotationDetail.getAttachments() != null) {
			for (UIAttachment attachment : quotationDetail.getAttachments())
				if (attachment.getCategory() != null && attachment.getCategory().trim().length() == 0) {
					attachment.setCategory(DOC_DEFAULT_CATEGORY);
				}
		}
	}
}
