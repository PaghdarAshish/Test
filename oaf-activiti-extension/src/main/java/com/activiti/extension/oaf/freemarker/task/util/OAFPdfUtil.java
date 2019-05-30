package com.activiti.extension.oaf.freemarker.task.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.model.HeaderElement;
import com.activiti.extension.oaf.model.UIAttachment;
import com.activiti.extension.oaf.model.UIOtherCharges;
import com.activiti.extension.oaf.model.UIPriceAndWeightBreakup;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.PaperSize;
import com.aspose.words.PreferredWidth;
import com.aspose.words.SaveFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * This class is responsible to generate the pdf and has some static method
 * @author Keval Bhatt
 *
 */
public class OAFPdfUtil implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFPdfUtil.class);

	public static Template getFreemarkerTemplate(String templateName) {
		Template template = null;
		try {
			template = OAFFreeMarkerUtil.freemarkerConfiguration().getTemplate(templateName);
		} catch (TemplateNotFoundException e) {
			logger.error("OAFPdfUtil.getFreemarkerTemplate() :: TemplateNotFoundException : " + e);
		} catch (MalformedTemplateNameException e) {
			logger.error("OAFPdfUtil.getFreemarkerTemplate() :: MalformedTemplateNameException : " + e);
		} catch (ParseException e) {
			logger.error("OAFPdfUtil.getFreemarkerTemplate() :: ParseException : " + e);
		} catch (IOException e) {
			logger.error("OAFPdfUtil.getFreemarkerTemplate() :: IOException : " + e);
		}
		return template;

	}

	/**
	 * Putting the data in a map
	 * @param oafForm
	 * @return
	 */
	public static Map<String, Object> getting_pdf_data(String oafForm) {
		UIQuotationDetail uiQuotationDetail = OAFCommonUtil.convertJsonStringToQuote(oafForm);
		Map<String, Object> map = new HashMap<>();

		if (uiQuotationDetail.getHeaderSegment() != null) {
			map.put("vtext", getFieldValue(uiQuotationDetail.getHeaderSegment().getVtext()));
			map.put("erdat", getFieldValue(uiQuotationDetail.getHeaderSegment().getErdat()));
			map.put("nature", getFieldValue(uiQuotationDetail.getHeaderSegment().getNature()));
			map.put("inq", getFieldValue(uiQuotationDetail.getHeaderSegment().getInq()));
			map.put("vbeln", getFieldValue(uiQuotationDetail.getHeaderSegment().getVbeln()));
			map.put("quo", getFieldValue(uiQuotationDetail.getHeaderSegment().getQuo()));
			map.put("district", getFieldValue(uiQuotationDetail.getHeaderSegment().getDistrict()));
			map.put("bstnk", getFieldValue(uiQuotationDetail.getHeaderSegment().getBstnk()));
			map.put("sname", getFieldValue(uiQuotationDetail.getHeaderSegment().getSname()));
			map.put("custname", getFieldValue(uiQuotationDetail.getHeaderSegment().getCustname()));
			map.put("waerk", getFieldValue(uiQuotationDetail.getHeaderSegment().getWaerk()));
			map.put("kurrf", getFieldValue(uiQuotationDetail.getHeaderSegment().getKurrf()));
			map.put("txt04", getFieldValue(uiQuotationDetail.getHeaderSegment().getTxt04()));
			map.put("waers", getFieldValue(uiQuotationDetail.getHeaderSegment().getWaers()));
			map.put("auart", getFieldValue(uiQuotationDetail.getHeaderSegment().getAuart()));
			map.put("cltname", getFieldValue(uiQuotationDetail.getHeaderSegment().getCltname()));
			map.put("bezei", getFieldValue(uiQuotationDetail.getHeaderSegment().getBezei()));
			map.put("cltloc", getFieldValue(uiQuotationDetail.getHeaderSegment().getCltloc()));
		}
		if (uiQuotationDetail.getPriceAndWeightBreakups() != null) {
			List<UIPriceAndWeightBreakup> priceAndWeightBreakups = uiQuotationDetail.getPriceAndWeightBreakups();
			JSONArray pnwbreakUpjsonAarray = new JSONArray();
			int i = 1;
			for (UIPriceAndWeightBreakup priceAndWeightBreakup : priceAndWeightBreakups) {
				JSONObject pnwbreakUp = new JSONObject();
				pnwbreakUp.put("id", i);
				pnwbreakUp.put("area", priceAndWeightBreakup.getAreaSqMt());
				pnwbreakUp.put("bookPrice", priceAndWeightBreakup.getBookPrice());
				pnwbreakUp.put("discount", priceAndWeightBreakup.getDiscount());
				pnwbreakUp.put("discountPrice", priceAndWeightBreakup.getDiscountPrice());
				pnwbreakUp.put("item", priceAndWeightBreakup.getItem());
				pnwbreakUp.put("weightMT", priceAndWeightBreakup.getWeightMt());
				pnwbreakUpjsonAarray.put(pnwbreakUp);
				i++;
			}
			JSONObject pnwbreakUpJsonObject = new JSONObject();
			pnwbreakUpJsonObject.put("pnwbreakUpJsonObject", pnwbreakUpjsonAarray);
			map.put("priceAndWeightBreakup", pnwbreakUpJsonObject);
		}
		if (uiQuotationDetail.getOtherCharges() != null) {
			String[] pdf_nature = PDF_NATURE.split(", ");
			String nature = uiQuotationDetail.getHeaderSegment().getNature().getValue();
			boolean iscontainer = false;
			for (String finalNature : pdf_nature) {
				if (nature.contains(finalNature)) {
					iscontainer = true;
					break;
				}
			}
			String truckOrContainer=(iscontainer ? CONTAINER : TRUCK) ;
			double frieght1 = 0 ;
			double trucks1 = 0;
			double frieght2=0;
			double trucks2=0; 
			double amountTableB11;
			double amountTableB12=0;
			if (getFieldValue(uiQuotationDetail.getHeaderSegment().getFrieght1()) != null
					&& getFieldValue(uiQuotationDetail.getHeaderSegment().getTrucks1()) != null) {
				try {
					frieght1 = Double.parseDouble(uiQuotationDetail.getHeaderSegment().getFrieght1().getValue());
					trucks1 = Double.parseDouble(uiQuotationDetail.getHeaderSegment().getTrucks1().getValue());
					
					//map.put("amountTableB11", frieght1 * trucks1);

				} catch (NumberFormatException e) {
					logger.error("OAFPdfUtil.getting_pdf_data() :: Exeception: " + e);
					map.put("amountTableB11", "-");
				}

			}
			if (getFieldValue(uiQuotationDetail.getHeaderSegment().getFrieght2()) != null
					&& getFieldValue(uiQuotationDetail.getHeaderSegment().getTrucks2()) != null) {
				try {
					frieght2 = Double.parseDouble(uiQuotationDetail.getHeaderSegment().getFrieght2().getValue());
					trucks2 = Double.parseDouble(uiQuotationDetail.getHeaderSegment().getTrucks2().getValue());
					//map.put("amountTableB12", frieght2 * trucks2);
				} catch (NumberFormatException e) {
					logger.error("OAFPdfUtil.getting_pdf_data() :: Exeception: " + e);
					map.put("amountTableB12", "-");
				}

			}
			List<UIOtherCharges> otherCharges = uiQuotationDetail.getOtherCharges();
			JSONArray otherChargesJsonArray = new JSONArray();
			int i = 1;
			JSONObject otherchargesJsonObject1 = new JSONObject();
			JSONObject otherchargesJsonObject2 = new JSONObject();
			for (UIOtherCharges uiOtherCharge : otherCharges) {
				JSONObject otherchargesJsonObject = new JSONObject();
				otherchargesJsonObject.put("id", i);
				otherchargesJsonObject.put("item", uiOtherCharge.getItem());
				otherchargesJsonObject.put("amount", uiOtherCharge.getAmount());
				if (uiOtherCharge.getAmount() != "0") {
					otherChargesJsonArray.put(otherchargesJsonObject);
				}
				i++;
			}
			amountTableB11=frieght1*trucks1;
			amountTableB12=frieght2*trucks2;
			String totalOfTable11=String.valueOf(amountTableB11);
			String totalOfTable12=String.valueOf(amountTableB12);
			
			otherchargesJsonObject1.put("id",otherChargesJsonArray.length()+1);
			otherchargesJsonObject1.put("item","Fright Cost from plant -" +uiQuotationDetail.getHeaderSegment().getFrieght1().getValue()+ truckOrContainer+ "@" +uiQuotationDetail.getHeaderSegment().getTrucks1().getValue()+uiQuotationDetail.getHeaderSegment().getWaers().getValue() +"/" +truckOrContainer);
			otherchargesJsonObject1.put("amount",totalOfTable11);
			otherChargesJsonArray.put(otherchargesJsonObject1);
			i++;
			
			otherchargesJsonObject2.put("id",otherChargesJsonArray.length()+1);
			otherchargesJsonObject2.put("item","Fright Cost - Other Plan -" +uiQuotationDetail.getHeaderSegment().getFrieght2().getValue()+ truckOrContainer+ "@" +uiQuotationDetail.getHeaderSegment().getTrucks2().getValue()+uiQuotationDetail.getHeaderSegment().getWaers().getValue() +"/" +truckOrContainer);
			otherchargesJsonObject2.put("amount",totalOfTable12);
			otherChargesJsonArray.put(otherchargesJsonObject2);
						
			JSONObject finalotherchargesJsonObject = new JSONObject();
			finalotherchargesJsonObject.put("finalotherchargesJsonObject", otherChargesJsonArray);
			map.put("otherCharges", finalotherchargesJsonObject);
		}

		if (uiQuotationDetail.getHeaderSegment() != null) {
			map.put("frieght1", getFieldValue(uiQuotationDetail.getHeaderSegment().getFrieght1()));
			map.put("frieght2", getFieldValue(uiQuotationDetail.getHeaderSegment().getFrieght2()));
			map.put("trucks1", getFieldValue(uiQuotationDetail.getHeaderSegment().getTrucks1()));
			map.put("trucks2", getFieldValue(uiQuotationDetail.getHeaderSegment().getTrucks2()));

			map.put("projmt", getFieldValue(uiQuotationDetail.getHeaderSegment().getProjmt()));
			map.put("projprice", getFieldValue(uiQuotationDetail.getHeaderSegment().getProjprice()));
			map.put("projother", getFieldValue(uiQuotationDetail.getHeaderSegment().getProjother()));
			map.put("projnet", getFieldValue(uiQuotationDetail.getHeaderSegment().getProjnet()));
			map.put("projreal", getFieldValue(uiQuotationDetail.getHeaderSegment().getProjreal()));
			if (getFieldValue(uiQuotationDetail.getHeaderSegment().getProjprice()) != null
					&& getFieldValue(uiQuotationDetail.getHeaderSegment().getKurrf()) != null) {
				try {
					Double netPrice = Double.parseDouble(uiQuotationDetail.getHeaderSegment().getProjnet().getValue());
					Double currencyConversion = Double
							.parseDouble(uiQuotationDetail.getHeaderSegment().getKurrf().getValue());
					map.put("amountTableB2", netPrice * currencyConversion);
				} catch (NumberFormatException e) {
					logger.error("OAFPdfUtil.getting_pdf_data() :: Exeception: " + e);
					map.put("amountTableB2", "-");
				}

			}

			map.put("kst", getFieldValue(uiQuotationDetail.getHeaderSegment().getKst()));
			map.put("ksttext", getFieldValue(uiQuotationDetail.getHeaderSegment().getKsttext()));
			map.put("downpay", getFieldValue(uiQuotationDetail.getHeaderSegment().getDownpay()));

			if (uiQuotationDetail.getHeaderSegment().getDownpaych() != null
					&& uiQuotationDetail.getHeaderSegment().getDownpaych().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getDownpaych().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getDownpaych().getValue().equalsIgnoreCase("Y"))) {
				map.put("downpaych", "checked");
			} else {
				map.put("downpaych", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getDownpaytt() != null
					&& uiQuotationDetail.getHeaderSegment().getDownpaytt().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getDownpaytt().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getDownpaytt().getValue().equalsIgnoreCase("Y"))) {
				map.put("downpaytt", "checked");
			} else {
				map.put("downpaytt", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getDownpaylc() != null
					&& uiQuotationDetail.getHeaderSegment().getDownpaylc().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getDownpaylc().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getDownpaylc().getValue().equalsIgnoreCase("Y"))) {
				map.put("downpaylc", "checked");
			} else {
				map.put("downpaylc", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getDownpayot() != null
					&& uiQuotationDetail.getHeaderSegment().getDownpayot().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getDownpayot().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getDownpayot().getValue().equalsIgnoreCase("Y"))) {
				map.put("downpayot", "checked");
			} else {
				map.put("downpayot", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getBillpaych() != null
					&& uiQuotationDetail.getHeaderSegment().getBillpaych().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getBillpaych().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getBillpaych().getValue().equalsIgnoreCase("Y"))) {
				map.put("billpaych", "checked");
			} else {
				map.put("billpaych", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getBillpaytt() != null
					&& uiQuotationDetail.getHeaderSegment().getBillpaytt().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getBillpaytt().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getBillpaytt().getValue().equalsIgnoreCase("Y"))) {
				map.put("billpaytt", "checked");
			} else {
				map.put("billpaytt", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getBillpaylc() != null
					&& uiQuotationDetail.getHeaderSegment().getBillpaylc().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getBillpaylc().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getBillpaylc().getValue().equalsIgnoreCase("Y"))) {
				map.put("billpaylc", "checked");
			} else {
				map.put("billpaylc", "unchecked");
			}

			if (uiQuotationDetail.getHeaderSegment().getBillpayot() != null
					&& uiQuotationDetail.getHeaderSegment().getBillpayot().getValue() != null
					&& (uiQuotationDetail.getHeaderSegment().getBillpayot().getValue().equalsIgnoreCase("X")
							|| uiQuotationDetail.getHeaderSegment().getBillpayot().getValue().equalsIgnoreCase("Y"))) {
				map.put("billpayot", "checked");
			} else {
				map.put("billpayot", "unchecked");
			}

			map.put("billpay", getFieldValue(uiQuotationDetail.getHeaderSegment().getBillpay()));
			map.put("penalty", getFieldValue(uiQuotationDetail.getHeaderSegment().getPenalty()));
			map.put("penaltytxt", getFieldValue(uiQuotationDetail.getHeaderSegment().getPenaltytxt()));
			map.put("perf", getFieldValue(uiQuotationDetail.getHeaderSegment().getPerf()));
			map.put("perfbond", getFieldValue(uiQuotationDetail.getHeaderSegment().getPerfbond()));
			map.put("perfdura", getFieldValue(uiQuotationDetail.getHeaderSegment().getPerfdura()));
			map.put("advance", getFieldValue(uiQuotationDetail.getHeaderSegment().getAdvance()));
			map.put("advancebond", getFieldValue(uiQuotationDetail.getHeaderSegment().getAdvancebond()));
			map.put("retention", getFieldValue(uiQuotationDetail.getHeaderSegment().getRetention()));
			map.put("retentionbond", getFieldValue(uiQuotationDetail.getHeaderSegment().getRetentionbond()));
			map.put("retentiondura", getFieldValue(uiQuotationDetail.getHeaderSegment().getRetentiondura()));
			map.put("other", getFieldValue(uiQuotationDetail.getHeaderSegment().getOther()));
			map.put("otherbond", getFieldValue(uiQuotationDetail.getHeaderSegment().getOtherbond()));
			map.put("otherdura", getFieldValue(uiQuotationDetail.getHeaderSegment().getOtherdura()));
			map.put("insurance", getFieldValue(uiQuotationDetail.getHeaderSegment().getInsurance()));
			map.put("insutext", getFieldValue(uiQuotationDetail.getHeaderSegment().getInsutext()));
			map.put("dltfrom", getFieldValue(uiQuotationDetail.getHeaderSegment().getDltfrom()));
			map.put("dltto", getFieldValue(uiQuotationDetail.getHeaderSegment().getDltto()));
			map.put("adltfrom", getFieldValue(uiQuotationDetail.getHeaderSegment().getAdltfrom()));
			map.put("adltto", getFieldValue(uiQuotationDetail.getHeaderSegment().getAdltto()));
			map.put("inco1", getFieldValue(uiQuotationDetail.getHeaderSegment().getInco1()));
			map.put("inco1TXT", getFieldValue(uiQuotationDetail.getHeaderSegment().getInco1TXT()));
			map.put("spaint", getFieldValue(uiQuotationDetail.getHeaderSegment().getSpaint()));
			map.put("spainttext", getFieldValue(uiQuotationDetail.getHeaderSegment().getSpainttext()));
			map.put("swp", getFieldValue(uiQuotationDetail.getHeaderSegment().getSwp()));
			map.put("swptext", getFieldValue(uiQuotationDetail.getHeaderSegment().getSwptext()));
			map.put("owj", getFieldValue(uiQuotationDetail.getHeaderSegment().getOwj()));
			map.put("owjtext", getFieldValue(uiQuotationDetail.getHeaderSegment().getOwjtext()));
			map.put("dsw", getFieldValue(uiQuotationDetail.getHeaderSegment().getDsw()));
			map.put("dswtext", getFieldValue(uiQuotationDetail.getHeaderSegment().getDswtext()));
			map.put("osp", getFieldValue(uiQuotationDetail.getHeaderSegment().getOsp()));
			map.put("osptext", getFieldValue(uiQuotationDetail.getHeaderSegment().getOsptext()));
			map.put("remarks", getFieldValue(uiQuotationDetail.getHeaderSegment().getRemarks()));
			map.put("advancedura", getFieldValue(uiQuotationDetail.getHeaderSegment().getAdvancedura()));
			map.put("justificationForAboveDiscount",
					getFieldValue(uiQuotationDetail.getHeaderSegment().getJustificationForAboveDiscount()));
		}

		if (uiQuotationDetail.getAttachments() != null) {
			List<UIAttachment> uiAttachments = uiQuotationDetail.getAttachments();

			JSONArray attachmentJsonArray = new JSONArray();
			JSONObject finalAttachmentJsonObject = new JSONObject();
			int i = 1;

			for (UIAttachment uiAttachment : uiAttachments) {
				JSONObject attachmentJsonObject = new JSONObject();
				attachmentJsonObject.put("id", i);
				attachmentJsonObject.put("date", uiAttachment.getDate());
				attachmentJsonObject.put("docName", uiAttachment.getDocName());
				attachmentJsonArray.put(attachmentJsonObject);
				i++;
			}
			finalAttachmentJsonObject.put("attachmentsJsonObject", attachmentJsonArray);
			map.put("attchments", finalAttachmentJsonObject);
		}
		return map;
	}

	/**
	 * Get the field value
	 * @param element
	 * @return
	 */
	private static String getFieldValue(HeaderElement element) {
		return (element != null && element.getValue() != null && !element.getValue().trim().isEmpty())
				? element.getValue().trim()
				: "-";
	}

	/**
	 * Responsible to store and get the generated file in byte array
	 * @param getting_pdf_data
	 * @param oafForm
	 * @return
	 */
	public static ByteArrayDataSource storeAndGetGeneratedPdf(Map<String, Object> getting_pdf_data, String oafForm, DelegateExecution execution) {
		Template freemarkerTemplate = OAFPdfUtil.getFreemarkerTemplate(OAF_PDF_GENERATOR_FTL);
		try {
			String templateProcessedContent = OAFFreeMarkerUtil.getTemplateProcessedContent(freemarkerTemplate,
					getting_pdf_data);
			logger.debug("OAFPdfUtil.storeAndGetGeneratedPdf() :: generating pdf ....");
			return generatingPDF(templateProcessedContent, oafForm,execution);
		} catch (TemplateException e) {
			logger.error("OAFPdfUtil.storeAndGetGeneratedPdf() :: TemplateException : " + e);
			return null;
		} catch (IOException e) {
			logger.error("OAFPdfUtil.storeAndGetGeneratedPdf() :: IOException : " + e);
			return null;
		}

	}

	/**
	 * Generate the pdf
	 * @param templateProcessedContent
	 * @param oafForm
	 * @return
	 */
	public static ByteArrayDataSource generatingPDF(String templateProcessedContent, String oafForm, DelegateExecution execution) {
		InputStream templateByteArrayInputStream = null;
		Document pdfDocument = null;
		// converting ftlString into input stream
		try {
			templateByteArrayInputStream = new ByteArrayInputStream(templateProcessedContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}

		ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();
		// generating PDF document form InputStream of FTL.
		try {
			pdfDocument = new Document(templateByteArrayInputStream);

			// Set page margin

			DocumentBuilder documentBuilder = new DocumentBuilder(pdfDocument);
			PageSetup pageSetup = documentBuilder.getPageSetup();
			pageSetup.setPaperSize(PaperSize.A4);
			pageSetup.setLeftMargin(ConvertUtil.inchToPoint(0.2));
			pageSetup.setRightMargin(ConvertUtil.inchToPoint(0.2));
			pageSetup.setTopMargin(ConvertUtil.inchToPoint(0.2));
			pageSetup.setBottomMargin(ConvertUtil.inchToPoint(0.2));
			// Formating fix size of table in pdf doc.

			for (com.aspose.words.Table table : (Iterable<com.aspose.words.Table>) pdfDocument
					.getChildNodes(NodeType.TABLE, true)) {
				table.setAllowAutoFit(false);
				table.setPreferredWidth(PreferredWidth.fromPercent(100));
			}
			pdfDocument.updateTableLayout();
		} catch (Exception e) {
			logger.error("OAFPdfUtil.generatingPDF() :: Exception : ", e);
		}

		// store document data into outputstream
		try {
			// pdfDocument.save("D:\\Alfresco\\abc_" + System.currentTimeMillis() + ".pdf");
			pdfDocument.save(documentOutputStream, SaveFormat.PDF);
		} catch (Exception e) {
			logger.error("OAFPdfUtil.generatingPDF() :: Exception : ", e);
		}
		logger.debug("OAFPdfUtil.generatingPDF() ::  generating pdf done");
		return storePdfInACSAndGetForMailAttachment(documentOutputStream, oafForm,execution);

	}

	/**
	 * Set the mimebodypart for email attachment
	 * @param bads
	 * @param emailTemplateContent
	 * @param email
	 */
	public static void setMimeBodyPartPDFFileForMail(ByteArrayDataSource bads,
			OAFEmailTemplateContent emailTemplateContent, OAFEmail email) {
		if (bads != null) {
			MimeBodyPart pdfMimeBodyPart = new MimeBodyPart();
			try {

				String fileName = emailTemplateContent.getInquiry() + "_" + emailTemplateContent.getQuote();
				fileName = fileName + "_" + java.time.LocalDate.now().toString() + ".pdf";
				pdfMimeBodyPart.setDataHandler(new DataHandler(bads));
				pdfMimeBodyPart.setFileName(fileName);
				email.setAttachedSingleFile(pdfMimeBodyPart);
				logger.error("OAFPdfUtil.setMimeBodyPartPDFFileForMail() :: PDF Attached in final approval");
			} catch (MessagingException e) {
				logger.error("OAFPdfUtil.setMimeBodyPartPDFFileForMail() :: MessagingException : ", e);
			} catch (Exception e) {
				logger.error("OAFPdfUtil.setMimeBodyPartPDFFileForMail() :: Exception : ", e);
			}
		}
	}

	/**
	 * Store the pdf in acs and get the Bytearray for email attachment
	 * @param documentOutputStream
	 * @param oafForm
	 * @return
	 */
	private static ByteArrayDataSource storePdfInACSAndGetForMailAttachment(ByteArrayOutputStream documentOutputStream,
			String oafForm, DelegateExecution execution) {
		try {
			UIQuotationDetail uiQuotationDetail = OAFCommonUtil.convertJsonStringToQuote(oafForm);
			String folderName = uiQuotationDetail.getHeaderSegment().getInq().getValue() + "_"
					+ uiQuotationDetail.getHeaderSegment().getQuo().getValue();
			// calling postRequestForFolderCreation() method for creating folder
			// dynamically into ACS.
			String folderPath = postRequestForFolderCreation(folderName);
			if (folderPath != null) {
				logger.debug(
						"OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: Folder successfully created in ACS, folderName :: "
								+ folderName);
				logger.debug("OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: folderPath = " + folderPath);

			} else {
				logger.debug(
						"OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: Unable to create folder in ACS, folderName :: "
								+ folderName);
			}

			// calling postRequestForUploadPdf() method for uploading PDF file
			// into ACS.

			String fileName = uiQuotationDetail.getHeaderSegment().getInq().getValue() + "_"
					+ uiQuotationDetail.getHeaderSegment().getQuo().getValue();

			fileName = fileName + "_" + java.time.LocalDate.now().toString() + ".pdf";
			logger.debug("OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: fileName = " + fileName);
			logger.debug("OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: uploading pdf ...");

			HttpResponse response = postRequestForUploadPdf(documentOutputStream, fileName, folderPath);
			if (response.getStatusLine().getStatusCode() == 200) {
				logger.debug("OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: pdf successfully saved");
				String headerContentType = response.getEntity().getContentType().getValue();
				String responseString = EntityUtils.toString(response.getEntity());
				JsonNode jsonNode = new ObjectMapper().readTree(responseString);
				
				if(jsonNode.path("nodeRef")!=null) {
					JsonNode nodeRefNode = jsonNode.path("nodeRef");
					String nodeRef = nodeRefNode.asText();										
					execution.setVariable(ACS_PDF_GENERATED_NODE_ID, OAFCommonUtil.getNodeId(nodeRef));
				}				
				byte[] pdfBytes = documentOutputStream.toByteArray();
				return new ByteArrayDataSource(pdfBytes, headerContentType);
			} else {
				logger.error(
						"OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: Failed to Access Process Instance Service with HTTP Error Code :"
								+ response.getStatusLine().getStatusCode());
				return null;
			}

		} catch (Exception e) {
			logger.error("OAFPdfUtil.storePdfInACSAndGetForMailAttachment() :: Exception  ", e);
			return null;
		}
	}

	/**
	 * Create the folder in ACS
	 * @param folderName
	 * @return
	 */
	private static String postRequestForFolderCreation(String folderName) {

		// getting year of claim card raised
		// String dateOfClaimRaised;
		String folderPath = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();

		// creating jsonobject for folder
		JSONObject folderJsonObject = new JSONObject();

		// URL for getting folder (documentLibrary) noderef
		String requestURL = ACS_BASE_URL + ACS_GET_NODEREF_URL + ACS_SITE_NAME + "/" + ACS_DOCUMENT_LIBRARY;

		logger.debug("OAFPdfUtil.postRequestForFolderCreation() :: ACS upload requestURL = " + requestURL);
		// send get request for noderefURL

		HttpGet request = new HttpGet(requestURL);
		// do authentication
		String auth = userAuthentication(ACS_USERNAME, ACS_PASSWORD);
		request.setHeader(AUTHORIZATION, auth);
		// getting response
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			logger.error("OAFPdfUtil.postRequestForFolderCreation() :: ClientProtocolException : " + e);
		} catch (IOException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() ::  Error while getting ACS access may be ACS server is not working : "
							+ e);
		}

		// getting response in string from entity
		String jsonStringOfNodeRef = null;
		try {
			jsonStringOfNodeRef = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() :: Error while converting json formated string into string entity cause of UnsupportedEncodingException : "
							+ e);
		} catch (IOException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() :: Error while converting json formated string into string entity cause of UnsupportedEncodingException : "
							+ e);
		}
		// convert response entity string into json

		if (!jsonStringOfNodeRef.isEmpty()) {

			JSONObject nodeRefJson = new JSONObject(jsonStringOfNodeRef);
			nodeRefJson = nodeRefJson.getJSONObject("parent");
			// documentLibrary noderef
			folderJsonObject.put("destination", nodeRefJson.get("nodeRef"));

			// folder jsonArray
			JSONArray folderJsonArray = new JSONArray();
			folderJsonArray.put(folderName);
			folderJsonObject.put("paths", folderJsonArray);

		}

		// converting jsonobject into json string and formatting it.
		String folderJsonString = folderJsonObject.toString();

		String folderJsonFormattedString = folderJsonString.replace("\\", "");

		// converting jsonformatted string into stringentity.
		StringEntity folderStringEntity = null;
		try {
			folderStringEntity = new StringEntity(folderJsonFormattedString);
		} catch (UnsupportedEncodingException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() :: Error while converting json formated string into string entity cause of UnsupportedEncodingException : "
							+ e);
		}

		// creating Httppost request for creating folder
		HttpPost postFolder = getHttpPostFolderRequest();
		postFolder.setEntity(folderStringEntity);

		// getting response of post request.
		HttpResponse folderResponse = null;
		try {
			folderResponse = client.execute(postFolder);
		} catch (ClientProtocolException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() :: Error while executing post folder request cause of ClientProtocolException issue: "
							+ e);
		} catch (IOException e) {
			logger.error(
					"OAFPdfUtil.postRequestForFolderCreation() :: Error while executing post folder request cause of IO issue : "
							+ e);
		}

		if (folderResponse.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed to Access Process Instance Service with HTTP Error Code :"
					+ folderResponse.getStatusLine().getStatusCode());
		} else {
			folderPath = folderName;
		}

		return folderPath;

	}

	/**
	 * Generate the basic authentication token based on username and password
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public static String userAuthentication(String username, String password) {
		String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		return "Basic " + encoding;

	}

	/**
	 * Responsible for getting the url for post folder request
	 *
	 * @return
	 */
//    private static HttpPost getHttpPostFolderRequest() {
	public static HttpPost getHttpPostFolderRequest() {
		// webscript url for create folder into ACS.
		String contentFolder = ACS_BASE_URL + ACS_CREATE_FOLDER_URL;
		HttpPost postFolder = new HttpPost(contentFolder);
		postFolder.addHeader(CONTENT_TYPE, CONTENT_JSON_VALUE);
		String auth1 = userAuthentication(ACS_USERNAME, ACS_PASSWORD);
		postFolder.addHeader(AUTHORIZATION, auth1);
		return postFolder;
	}

	public static HttpResponse postRequestForUploadPdf(ByteArrayOutputStream outputStream, String fileName,
			String folderPath) {
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpPost request = getHttpPostStoreData();

		// convert outstream data into byte array to pass to post request
		byte[] documentBytesArray = outputStream.toByteArray();

		// creating post request body(entity)
		HttpEntity entity = MultipartEntityBuilder.create().addTextBody(CONTENT_TYPE, ACS_FOLDER_CONTENT_TYPE)
				.addTextBody(ACS_DESCRIPTION, ACS_DESCRIPTION_TEXT).addTextBody(ACS_SITEID, ACS_SITE_NAME)
				.addTextBody(ACS_UPLOAD_DIRECTORY, folderPath).addTextBody(ACS_CONTAINERID, ACS_DOCUMENT_LIBRARY)
				.addBinaryBody(ACS_FILEDATA, documentBytesArray, ContentType.create(ACS_CONTENT_TYPE_PDF), fileName)
				.build();
		request.setEntity(entity);

		// getting response from request
		HttpResponse response = null;
		try {
			response = client.execute(request);
			return response;
		} catch (ClientProtocolException e1) {
			logger.error(
					"OAFPdfUtil.postRequestForUploadPdf() :: Error while executing uploading pdf into folder request because of client protocol issue : "
							+ e1);
		} catch (IOException e1) {
			logger.error(
					"OAFPdfUtil.postRequestForUploadPdf() :: Error while executing uploading pdf file io issue: " + e1);
		}
		return null;

	}

	private static HttpPost getHttpPostStoreData() {
		String requestURL = ACS_BASE_URL + ACS_UPLOAD_CONTENT_URL;
		HttpPost postFolder = new HttpPost(requestURL);
		String auth1 = userAuthentication(ACS_USERNAME, ACS_PASSWORD);
		postFolder.addHeader(AUTHORIZATION, auth1);
		return postFolder;
	}
}
