package com.activiti.extension.bean.service;

import com.activiti.extension.oaf.constants.OAFConstants;

import com.activiti.extension.oaf.constants.OAFStatus;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.freemarker.task.util.OAFEmailTemplateUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFFreeMarkerUtil;
import com.activiti.extension.oaf.model.OAFResponse;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Bean for OAFService related operations
 * 
 * @author Pradip Patel
 */
@Service(value = "oAFService")
public class OAFService implements OAFConstants {

	private static final Logger logger = LoggerFactory.getLogger(OAFService.class);
	private String sapServiceCallResponse = "";
	private int sapRetryInterval = Integer.parseInt(OAF_SERVICE_SAP_FAIL_RETRY_INTERVAL_SECONDS);
	private int sapRetryLimit = Integer.parseInt(OAF_SERVICE_SAP_FAIL_RETRY_LIMIT);
	private boolean isSapUpdateFail = false;
	@Autowired
	private ObjectMapper objectMapper;

	public static int sendJsonPostRequest(String url, String jsonData, String contentType) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		if ((jsonData != null || !jsonData.isEmpty()) && (url != null || !url.isEmpty())
				&& (contentType != null || !contentType.isEmpty())) {
			try {
				logger.debug("OAFService.callPostRequest() :: Custom post request :: Sending post request...");
				httppost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
				StringEntity param = new StringEntity(jsonData);
				httppost.setEntity(param);
				CloseableHttpResponse response = client.execute(httppost);
				int status = response.getStatusLine().getStatusCode();
				// 200
				if (status == HttpStatus.OK.value()) {
					return status;
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("OAFService.callPostRequest() :: Custom post request :: UnsupportedEncodingException : ",
						e);
				return 0;
			} catch (Exception e) {
				logger.error("OAFService.callPostRequest() :: Custom post request :: Exception : ", e);
				return 0;
			}
		} else {
			logger.error(
					"OAFService.callPostRequest() :: Custom post request :: URL or JsonData or ContentType is empty: ");
			return 1;
		}
		return 0;

	}

	/**
	 * @param quotationDetail :: input json payload DTO POJO If false then data is
	 *                        stored in db and scheduler is responsible to taken
	 *                        further action on that.
	 * @return object of OAFResponse, if any error then response is as "success":
	 *         "false" with error code and appropriate message. If no error then
	 *         response is "success":"true".
	 */
	public ResponseEntity<?> manageOAF(UIQuotationDetail quotationDetail) {
//		logger.debug("OAFService.manageOAF() :: Input Data :: " + quotationDetail);
		String url = OAF_ADAPTER_BASE_URL + OAF_API_MANAGEOAF_BASE_URL;
//		logger.debug("OAFService.manageOAF() :: oaf-adapter url :: " + url);
		return callPostRequest(url, quotationDetail, ContentType.APPLICATION_JSON.getMimeType());
	}

	/**
	 * This method is responsible to update status of individual record based on
	 * process instance id.
	 *
	 * @param processInstanceId : Running process id
	 * @param status            {@link OAFStatus}
	 * @param rejectTaskId      : rejectTaskId in case of process rejection or "0"
	 *                          in non rejection scenario.
	 * @return object of OAFResponse, if any error then response is as "success":
	 *         "false" with error code and appropriate message. If no error then
	 *         response is "success":"true".
	 */
	public ResponseEntity updateProcessStatus(String processInstanceId, String status, String rejectTaskId) {
		logger.debug("OAFService.updateProcessStatus() :: Input Data  :: 1). processInstanceId = " + processInstanceId
				+ " \n" + "2). status = " + status + " \n" + "3). rejectTaskId = " + rejectTaskId);
		String url = OAF_ADAPTER_BASE_URL + OAF_API_UPDATE_STATUS_BASE_URL;
		logger.debug("OAFService.updateProcessStatus() :: oaf-adapter url :: " + url);
		if ((processInstanceId == null || StringUtils.isEmpty(processInstanceId))
				|| (status == null || StringUtils.isEmpty(status)) || rejectTaskId == null) {
			logger.error("OAFService.updateProcessStatus() :: Error code - 105 : Required parameter not found.");
			OAFResponse oafResponse = this.generateResponse(false, "105", "Required parameter not found");
			return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
		} else {
			return callPostRequest(url, processInstanceId, status, rejectTaskId);
		}
	}

	/**
	 * This method is responsible to call 'manageOAF' restAPI of oaf-adapter
	 * project.
	 *
	 * @param url
	 * @param quotationDetail
	 * @param contentType
	 * @return
	 */
	private ResponseEntity callPostRequest(String url, UIQuotationDetail quotationDetail, String contentType) {
		String inputJson = null;
		try {
			inputJson = this.objectMapper.writeValueAsString(quotationDetail);
		} catch (JsonProcessingException e) {
			logger.error(
					"OAFService.callPostRequest() :: Error code - 101 : Input json is wrong. Please validate json and try again",
					e);
			OAFResponse oafRes = generateResponse(false, "101",
					"Input json is wrong. Please validate json and try again");
			// 500
			return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.debug("OAFService.callPostRequest() :: url :: " + url);
		logger.debug("OAFService.callPostRequest() :: inputJson :: " + inputJson);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		if (contentType != null) {
			httppost.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
		}
		if (inputJson != null) {
			try {
				StringEntity param = new StringEntity(inputJson);
				httppost.setEntity(param);
			} catch (UnsupportedEncodingException e) {
				logger.error("Error code - 102 : Unsupported encoding.", e);
				OAFResponse oafRes = generateResponse(false, "102", "Unsupported encoding.");
				// 500
				return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httppost);
		} catch (IOException e) {
			logger.error("OAFService.callPostRequest() :: Error code - 400 : Connection to Adepter service failed", e);
			OAFResponse oafRes = generateResponse(false, "400", "Connection to Adepter service failed");
			// 400
			return new ResponseEntity<>(oafRes, HttpStatus.BAD_REQUEST);
		}

		HttpEntity resEntity = response.getEntity();
		int status = response.getStatusLine().getStatusCode();
		// 200
		if (status == HttpStatus.OK.value()) {

			String res = null;
			JsonNode responseAsNode = null;
			try {
				res = EntityUtils.toString(resEntity);
				responseAsNode = this.objectMapper.readTree(res);
			} catch (IOException e) {
				logger.error("Error code - 104 : ManageOAF service is unable to read Adapter response", e);
				OAFResponse oafRes = generateResponse(false, "104",
						"ManageOAF service is unable to read Adapter response");
				// 500
				return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);
//            OAFResponse oafRes = generateResponse(true, "", "");
//            return new ResponseEntity<>(oafRes, HttpStatus.OK);
			return new ResponseEntity<>(responseAsNode, HttpStatus.OK);
		}
		// 401
		else if (status == HttpStatus.UNAUTHORIZED.value()) {
			logger.debug("OAFService.callPostRequest() :: OAF-adapter Response status  :: " + status);
			logger.error("OAFService.callPostRequest() :: Error code - 401 : Authentication to Adepter service failed");
			OAFResponse oafRes = generateResponse(false, "401", "Authentication to Adepter service failed");
			return new ResponseEntity<>(oafRes, HttpStatus.UNAUTHORIZED);

		}
		// 404
		else if (status == HttpStatus.NOT_FOUND.value()) {
			logger.debug("OAFService.callPostRequest() :: Adapter service is down. Status : ", status);
			logger.error("OAFService.callPostRequest() :: Error code - 404 : Adapter service is down.");
			OAFResponse oafRes = generateResponse(false, "404", "Adapter service is down.");
			return new ResponseEntity<>(oafRes, HttpStatus.NOT_FOUND);
		} else {
			// to do need to update and ser errorCode and errorMessage
//            String errorCode = "";
//            String errorMessage = "";
//            OAFResponse oafRes = generateResponse(false, errorCode, errorMessage);

			String res = null;
			JsonNode responseAsNode = null;
			try {
				res = EntityUtils.toString(resEntity);
				responseAsNode = this.objectMapper.readTree(res);
			} catch (IOException e) {
				logger.error(
						"OAFService.callPostRequest() :: Error code - 104 : ManageOAF service is unable to read Adapter response",
						e);
				OAFResponse oafRes = generateResponse(false, "104",
						"ManageOAF service is unable to read Adapter response");
				// 500
				return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);
			return new ResponseEntity<>(responseAsNode, HttpStatus.valueOf(status));
		}
	}

	/**
	 * This method is responsible to call 'updateStatus' restAPI of oaf-adapter
	 * project.
	 *
	 * @param url
	 * @param processInstanceId
	 * @param status
	 * @param rejectTaskId
	 * @return
	 */
	private ResponseEntity callPostRequest(String url, String processInstanceId, String status, String rejectTaskId) {

		System.out.println("OAFService.callPostRequest()");
		logger.debug("OAFService.callPostRequest() :: url :: " + url);
		logger.debug("OAFService.callPostRequest() :: Input Data  :: 1). processInstanceId = " + processInstanceId
				+ " \n" + "2). status = " + status + " \n" + "3). rejectTaskId = " + rejectTaskId);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		List<NameValuePair> form = new ArrayList<>();
		form.add(new BasicNameValuePair("processInstanceId", processInstanceId));
		form.add(new BasicNameValuePair("status", status));
		form.add(new BasicNameValuePair("rejectTaskId", rejectTaskId));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httppost);
		} catch (IOException e) {
			logger.error("OAFService.callPostRequest() :: Error code - 400 : Connection to Adepter service failed", e);
			OAFResponse oafRes = generateResponse(false, "400", "Connection to Adepter service failed");
			// 400
			return new ResponseEntity<>(oafRes, HttpStatus.BAD_REQUEST);
		}

		HttpEntity resEntity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		String res = null;
		JsonNode responseAsNode = null;
		OAFResponse defaultResponse = null;
		try {
			res = EntityUtils.toString(resEntity);
			responseAsNode = this.objectMapper.readTree(res);
			defaultResponse = this.objectMapper.treeToValue(responseAsNode, OAFResponse.class);
		} catch (IOException e) {
			logger.error(
					"OAFService.callPostRequest() :: Error code - 104 : ManageOAF service is unable to read Adapter response",
					e);
			OAFResponse oafRes = generateResponse(false, "104", "ManageOAF service is unable to read Adapter response");
			// 500
			return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 200
		if (statusCode == HttpStatus.OK.value()) {
			logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);
			OAFResponse oafRes = generateResponse(true, "", "");
			return new ResponseEntity<>(oafRes, HttpStatus.OK);
		}
		// 401
		else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
			logger.debug("OAFService.callPostRequest() :: OAF-adapter Response status  :: " + statusCode);
			logger.error("OAFService.callPostRequest() :: Error code - 401 : Authentication to Adepter service failed");
			OAFResponse oafRes = generateResponse(false, "401", "Authentication to Adepter service failed");
			return new ResponseEntity<>(oafRes, HttpStatus.UNAUTHORIZED);

		}
		// 404
		else if (statusCode == HttpStatus.NOT_FOUND.value()) {
			logger.debug("OAFService.callPostRequest() :: Adapter service is down. Status : ", statusCode);
			logger.error("OAFService.callPostRequest() :: Error code - 404 : Adapter service is down.");
			OAFResponse oafRes = generateResponse(false, "404", "Adapter service is down.");
			return new ResponseEntity<>(oafRes, HttpStatus.NOT_FOUND);
		} else {
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);

			return new ResponseEntity<>(defaultResponse, HttpStatus.valueOf(statusCode));
		}
	}

	/**
	 * This method is responsible to call 'restartProcess' restAPI of oaf-adapter
	 * project.
	 *
	 * @param processInstanceId
	 * @param inquiry
	 * @return
	 */
	private ResponseEntity callPostRequest(String url, String processInstanceId, String inquiry) {
		logger.debug("OAFService.callPostRequest() :: url = " + url);
		logger.debug("OAFService.callPostRequest() :: Input Data  :: 1). processInstanceId = " + processInstanceId
				+ " \n" + "2). inquiry = " + inquiry);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		List<NameValuePair> form = new ArrayList<>();
		form.add(new BasicNameValuePair("processInstanceId", processInstanceId));
		form.add(new BasicNameValuePair(MAIL_TEMPLATE_CONTENT_INQUIRY, inquiry));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
		httppost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httppost);
		} catch (IOException e) {
			logger.error("OAFService.callPostRequest() :: Error code - 400 : Connection to Adepter service failed", e);
			OAFResponse oafRes = generateResponse(false, "400", "Connection to Adepter service failed");
			// 400
			return new ResponseEntity<>(oafRes, HttpStatus.BAD_REQUEST);
		}

		HttpEntity resEntity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		String res = null;
		JsonNode responseAsNode = null;
		OAFResponse defaultResponse = null;
		try {
			res = EntityUtils.toString(resEntity);
			responseAsNode = this.objectMapper.readTree(res);
			defaultResponse = this.objectMapper.treeToValue(responseAsNode, OAFResponse.class);
		} catch (IOException e) {
			logger.error(
					"OAFService.callPostRequest() :: Error code - 104 : RestartProcess service is unable to read Adapter response",
					e);
			OAFResponse oafRes = generateResponse(false, "104", "ManageOAF service is unable to read Adapter response");
			// 500
			return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 200
		if (statusCode == HttpStatus.OK.value()) {
			logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);
			OAFResponse oafRes = generateResponse(true, "", "Request successfully processed.");
			return new ResponseEntity<>(oafRes, HttpStatus.OK);
		}
		// 401
		else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
			logger.debug("OAFService.callPostRequest() :: OAF-adapter Response status  :: " + statusCode);
			logger.error("OAFService.callPostRequest() :: Error code - 401 : Authentication to Adepter service failed");
			OAFResponse oafRes = generateResponse(false, "401", "Authentication to Adepter service failed");
			return new ResponseEntity<>(oafRes, HttpStatus.UNAUTHORIZED);

		}
		// 404
		else if (statusCode == HttpStatus.NOT_FOUND.value()) {
			logger.debug("OAFService.callPostRequest() :: Adapter service is down. Status : ", statusCode);
			logger.error("OAFService.callPostRequest() :: Error code - 404 : Adapter service is down.");
			OAFResponse oafRes = generateResponse(false, "404", "Adapter service is down.");
			return new ResponseEntity<>(oafRes, HttpStatus.NOT_FOUND);
		} else {
			logger.debug("OAFService.callPostRequest() :: Adapter service response = " + res);
			return new ResponseEntity<>(defaultResponse, HttpStatus.valueOf(statusCode));
		}
	}

	/**
	 * This nmethod is responsible to generate {@link OAFResponse}
	 *
	 * @param success
	 * @param errorCode
	 * @param message
	 * @return com.activiti.extension.oaf.model.OAFResponse
	 */
	private OAFResponse generateResponse(boolean success, String errorCode, String message) {
		OAFResponse oafResponse = new OAFResponse();
		oafResponse.setErrorCode(errorCode);
		oafResponse.setMessage(message);
		oafResponse.setSuccess(String.valueOf(success));
		return oafResponse;
	}

	public ResponseEntity<?> sendAttachmentEmail(String inquiryNumber, String quotationNumber) {
		if (inquiryNumber == null || StringUtils.isEmpty(inquiryNumber)) {
			logger.error("OAFService.sendAttachmentEmail() :: Error code - 105 : Required parameters not found :: "
					+ "'inquiryNumber'");
			OAFResponse oafResponse = generateResponse(false, "105",
					"Required parameters not found :: " + "'inquiryNumber'");
			return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
		}
		if (quotationNumber == null || StringUtils.isEmpty(quotationNumber)) {
			logger.error("OAFService.sendAttachmentEmail() :: Error code - 105 : Required parameters not found :: "
					+ "'quotationNumber'");
			OAFResponse oafResponse = generateResponse(false, "105",
					"Required parameters not found :: " + "'quotationNumber'");
			return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
		}

		// to do Add AttachmentDocument Fail template and handle response.
		// to do send response as object of OAFResponse class
		String initMail = TEMP_INIT_MAIL;

		Template template = OAFFreeMarkerUtil
				.getFreemarkerTemplateContent(OAFConstants.OAF_MAIL_TEMPLATE_11_FAILURE_FOR_DOCUMENT_ATTACHMENT);
		Map<String, Object> templateData = new HashMap<>();
		templateData.put(MAIL_TEMPLATE_CONTENT_INQUIRY, inquiryNumber);
		templateData.put(MAIL_TEMPLATE_CONTENT_QUOTE, quotationNumber);
		String mailTemplateProcessedContent = "";
		try {
			mailTemplateProcessedContent = OAFFreeMarkerUtil.getTemplateProcessedContent(template, templateData);
		} catch (TemplateException | IOException e) {
			e.printStackTrace();
			OAFResponse oafResponse = generateResponse(false, "107", "Unable to parse template.");
			return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		OAFEmail email = new OAFEmail();
		if (!initMail.isEmpty()) {
			email.setTo(initMail);
			email.setCc(null);
			String subjectMail = "Attachment Fail of Quote " + inquiryNumber + "/" + quotationNumber;
			email.setSubject(subjectMail);

//            if (mailTemplateProcessedContent.length() > 0) {
			email.setTemplateProcessedContent(mailTemplateProcessedContent);
//            }
			email.setAttachedDocumetIdentityList(null);

			// Sending Submission of quote Mail-1
			logger.debug("OAFService.sendAttachmentEmail() :: Sending Email to : " + email.getTo());
			OAFFreeMarkerUtil.sendNotificationFromService(email);
			OAFResponse oafResponse = generateResponse(true, "", "");
			if (logger.isDebugEnabled()) {
				logger.debug(oafResponse.toString());
				logger.debug("Attachment document Email successfully send.");
			}
			return new ResponseEntity<>(oafResponse, HttpStatus.OK);

		} else {
			OAFResponse oafResponse = generateResponse(false, "106", "Unable to send email.");
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to send Attachment document email.");
			}
			return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method is responsible to call SAP Service based on parameters.
	 *
	 * @param execution
	 * @param status
	 * @param rejectTaskId
	 * @return
	 */
	private boolean callSAPService(DelegateExecution execution, String status, String rejectTaskId) {
		String url = generateSAPServiceUrl(execution, status);
		logger.debug("OAFService.callSAPService() :: Sap service url = " + url);
		String processInstanceId = execution.getProcessInstanceId();
		String encoding = getSAPAuthentication();

		logger.debug("OAFService.callSAPService() :: encoding : " + encoding);
		ResponseEntity responseEntity = this.callSAPUpdateRequest(url, encoding);
		logger.debug("OAFService.callSAPService() :: responseEntity = " + responseEntity);
		OAFResponse oafResponse = (OAFResponse) responseEntity.getBody();
		return Boolean.valueOf(oafResponse.getSuccess());
	}

	/**
	 * This method is responsible to call SAP service based on parameters.
	 *
	 * @param execution
	 * @param status
	 * @return
	 */
	private boolean callSAPService(DelegateExecution execution, String status) {
		String url = generateSAPServiceUrl(execution, status);

		logger.debug("OAFService.callSAPService() :: Sap service url = " + url);
		String processInstanceId = execution.getProcessInstanceId();
		String encoding = getSAPAuthentication();

		logger.debug("OAFService.callSAPService() ::  encoding : " + encoding);
		ResponseEntity responseEntity = this.callSAPUpdateRequest(url, encoding);
		// Addding Sap retry interval
		if (OAF_SERVICE_SAP_FAIL_RETRY_ENABLE != null && Boolean.parseBoolean(OAF_SERVICE_SAP_FAIL_RETRY_ENABLE)) {
			if (isSapUpdateFail) {
				int counter = 0;
				while (counter < sapRetryLimit) {
					try {
						logger.debug("OAFService.callSAPService() ::  SAP update thread is waiting for "
								+ sapRetryInterval + " seconds...");
						
						TimeUnit.SECONDS.sleep(sapRetryInterval);
						
						logger.debug("OAFService.callSAPService() ::  Retrying SAP update... ");
						responseEntity = this.callSAPUpdateRequest(url, encoding);
						if (!isSapUpdateFail) {
							logger.debug("OAFService.callSAPService() :: After retrtying SAP is sucessfully updated. ");
							break;
						}
					} catch (InterruptedException e) {
						logger.error("OAFService.callSAPService :: Exception -> error occurs while sleeping thread for "
								+ sapRetryInterval + "seconds.", e);
						break;
					}
					counter++;
				}
			}
		}
		// Setting sap response in execution variable
		execution.setVariable(SAP_SERVICE_CALL_RESPONSE, sapServiceCallResponse);
		logger.debug("OAFService.callSAPService() ::  responseEntity : " + responseEntity);
		OAFResponse oafResponse = (OAFResponse) responseEntity.getBody();
		return Boolean.valueOf(oafResponse.getSuccess());
	}

	/**
	 * This method is responsible to call API to update SAP.
	 *
	 * @param url
	 * @param encoding
	 * @return
	 */
	private ResponseEntity<OAFResponse> callSAPUpdateRequest(String url, String encoding) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader(AUTHORIZATION, BASIC + encoding);
		CloseableHttpResponse response;
		try {
			response = client.execute(httpget);
		} catch (IOException e) {
			logger.error("OAFService.callSAPUpdateRequest() ::  Error code - 400 : Connection to SAP service failed",
					e);
			OAFResponse oafRes = generateResponse(false, "400", "Connection to SAP service failed");
			// 400
			return new ResponseEntity<>(oafRes, HttpStatus.BAD_REQUEST);
		}

		HttpEntity resEntity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		// 200
		if (statusCode == HttpStatus.OK.value()) {

			String res;
			try {
				res = EntityUtils.toString(resEntity);
				XmlMapper xmlMapper = new XmlMapper();
				JsonNode readTree = xmlMapper.readTree(res.getBytes());

				JsonNode tab = readTree.get("values").get("TAB");
				boolean success = tab.get("SUCCESS").asBoolean();
				String result = tab.get("RESULT").asText();
				logger.debug("OAFService.callSAPUpdateRequest() ::  success ::" + success);
				logger.debug("OAFService.callSAPUpdateRequest() ::  result :: " + result);
				logger.debug("OAFService.callSAPUpdateRequest() :: response as json :: " + readTree);
//                defaultResponse = this.objectMapper.treeToValue(responseAsNode, OAFResponse.class);
				// todo take decision based on xml response.
				logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				logger.debug("OAFService.callSAPUpdateRequest() ::  SAP service response = " + res);

				// Setting sapResponse
				sapServiceCallResponse = result;

				/*
				 * OAFResponse oafRes = generateResponse(true, "", ""); return new
				 * ResponseEntity<>(oafRes, HttpStatus.OK);
				 */
				if (success) {
					isSapUpdateFail = false;
					logger.info("OAFService.callSAPUpdateRequest() ::  SAP update request successfully completed.");
					logger.debug(
							"OAFService.callSAPUpdateRequest() ::  SAP update request successfully completed, URL :: "
									+ url);
					OAFResponse oafResponse = generateResponse(success, "", result);
					return new ResponseEntity<>(oafResponse, HttpStatus.OK);
				} else {
					isSapUpdateFail = true;
					logger.error(
							"OAFService.callSAPUpdateRequest() :: Error code - 108 : SAP update request fail, Response :: "
									+ res);
					logger.debug("OAFService.callSAPUpdateRequest() :: SAP update request fail, URL :: " + url);
					OAFResponse oafResponse = generateResponse(success, "108", result);
					return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
				}
//                OAFResponse oafRes = generateResponse(false, "500", "under dev");
//                return new ResponseEntity<>(oafRes, HttpStatus.OK);

			} catch (IOException e) {
				logger.error(
						"OAFService.callSAPUpdateRequest() :: Error code - 104 : UpdateSAP service is unable to read SAP response",
						e);
				OAFResponse oafRes = generateResponse(false, "104", "UpdateSAP service is unable to read SAP response");
				// 500
				return new ResponseEntity<>(oafRes, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		// 401
		else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
			logger.debug("OAFService.callSAPUpdateRequest() :: OAF SAP Response status  :: " + statusCode);
			logger.error(
					"OAFService.callSAPUpdateRequest() :: Error code - 401 : Authentication to SAP service failed");
			OAFResponse oafRes = generateResponse(false, "401", "Authentication to SAP service failed");
			return new ResponseEntity<>(oafRes, HttpStatus.UNAUTHORIZED);

		}
		// 404
		else if (statusCode == HttpStatus.NOT_FOUND.value()) {
			logger.debug("OAFService.callSAPUpdateRequest() :: SAP service is down. Status : ", statusCode);
			logger.error("OAFService.callSAPUpdateRequest() :: Error code - 404 : SAP service is down.");
			OAFResponse oafRes = generateResponse(false, "404", "SAP service is down.");
			return new ResponseEntity<>(oafRes, HttpStatus.NOT_FOUND);
		} else {
			logger.debug("OAFService.callSAPUpdateRequest() :: SAP service response = " + true);
			logger.error("OAFService.callSAPUpdateRequest() :: Error code - 500 : Failed to calling SAP service.");
			String message = "Failed to calling SAP service.";
			OAFResponse oafRes = generateResponse(false, "500", message);
			return new ResponseEntity<>(oafRes, HttpStatus.valueOf(statusCode));
		}
	}

	/**
	 * This method is responsible to get SAP Authentication as String
	 *
	 * @return
	 */
	private String getSAPAuthentication() {
		return Base64.getEncoder().encodeToString((OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_USERNAME)
				+ ":" + OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_PASSWORD)).getBytes());
	}

	/**
	 * This method is responsible to generate SAP API url.
	 *
	 * @param execution
	 * @param status
	 * @return
	 */
	private String generateSAPServiceUrl(DelegateExecution execution, String status) {
		StringBuilder url = new StringBuilder(OAF_SAP_SERVICE_BASE_URL);
		url.append(OAF_SAP_SERVICE_URL_CONTEXT);
		url.append(execution.getVariable(OAFConstants.MAIL_TEMPLATE_CONTENT_INQUIRY)).append('.')
				.append(execution.getVariable(OAFConstants.MAIL_TEMPLATE_CONTENT_QUOTE)).append('.');

		if (status != null && status.equalsIgnoreCase(OAFStatus.COMPLETE.toString())) {
			url.append(SAP_APPROVED);
		}
		if (status != null && status.equalsIgnoreCase(OAFStatus.REJECT.toString())) {
			url.append(SAP_REJECTED);
		}

		url.append(OAF_SAP_SERVICE_URL_QUERY_PARAM);
		return url.toString();
	}

	/**
	 * This method is responsible to send mail on SAP service fail.
	 *
	 * @param execution
	 */
	private void sendSAPServiceFailMail(DelegateExecution execution) {

		logger.debug("OAFService.sendSAPServiceFailMail()");

		String toEmailAssignee = TEMP_INIT_MAIL;
		OAFEmailTemplateContent emailTemplateContent = null;
		emailTemplateContent = OAFEmailTemplateUtil.initMail(execution, emailTemplateContent);
		logger.debug("In " + QUOTE_FAILURE_FINAL_APPROVED + " case");

		// Template Mail-10 - failure final approved
		String mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
				OAF_MAIL_TEMPLATE_10_FAILURE_FOR_FINAL_APPROVAL);

		if ((!mailTemplateProcessedContent.isEmpty() || mailTemplateProcessedContent != null)) {
			try {
				OAFEmail email = new OAFEmail();
				if (!toEmailAssignee.isEmpty()) {
					email.setTo(toEmailAssignee);
					email.setCc(null);
					String subjectMail = "SAP update failure of Quote " + emailTemplateContent.getInquiry() + "/"
							+ emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
					email.setSubject(subjectMail);
					email.setTemplateProcessedContent(mailTemplateProcessedContent);
					email.setAttachedDocumetIdentityList(null);
					// Sending failure final approved Mail-10
					logger.debug("OAFService.sendSAPServiceFailMail() :: Sending Email to : " + email.getTo());
					OAFFreeMarkerUtil.sendNotification(email);
					if (logger.isDebugEnabled()) {
						logger.debug("SAP update Service fail mail sent.");
					}

				} else {
					logger.debug("OAFService.sendSAPServiceFailMail() ::  " + toEmailAssignee + " user not Found");
				}
			} catch (Exception e) {
				logger.error("OAFService.sendSAPServiceFailMail() ::  Failed to send email notification  : "
						+ e.getMessage());
			}
		} else {
			logger.error(
					"OAFService.sendSAPServiceFailMail ::  Failed to send email notification  : Email template parsing error");
		}
	}

	/**
	 * This method is responsible to update call SAP Service and Update Status in
	 * DB.
	 *
	 * @param execution
	 * @param status
	 * @param rejectTaskId
	 */
	public void callSAPUpdateServiceByExecution(DelegateExecution execution, String status, String rejectTaskId) {
		// call sap service
		String processInstanceId = execution.getProcessInstanceId();
		if (this.callSAPService(execution, status, rejectTaskId)) {
			logger.debug("OAFService.callSAPUpdateService() ::  SAP service successfully called.");
			// if success then update in database too..
//            String processInstanceId = execution.getProcessInstanceId();
			ResponseEntity responseEntity = this.updateProcessStatus(processInstanceId, status, rejectTaskId);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				logger.debug("OAFService.callSAPUpdateService() ::  DB process status successfully updated.");
			} else {
				logger.debug("OAFService.callSAPUpdateService() ::  Unable to update db process status :: "
						+ responseEntity.getBody());
			}
		} else {
			this.sendSAPServiceFailMail(execution);
			// send sap service fail mail to X user.
			// to do change flow of execution.
			// to do hold task
		}

	}

	/**
	 * This method is responsible to call this.callSAPUpdateServiceByExecution.
	 *
	 * @param delegateTask
	 * @param status
	 * @param rejectTaskId
	 */
	public void callSAPUpdateServiceByTask(DelegateTask delegateTask, String status, String rejectTaskId) {
		logger.debug("OAFService.callSAPUpdateService(DelegateTask delegateTask, String status, String rejectTaskId)");
		this.callSAPUpdateServiceByExecution(delegateTask.getExecution(), status, rejectTaskId);
	}

	/**
	 * This method is responsible to Call SAP service and Update status in DB if
	 * Status is COMPLETE. Call from execution listener.
	 *
	 * @param execution
	 * @param status
	 */
	public void callSAPUpdateServiceByExecution(DelegateExecution execution, String status) {
		String processInstanceId = execution.getProcessInstanceId();
		if (OAF_SAP_SERVICE_SAP_SEND != null && Boolean.parseBoolean(OAF_SAP_SERVICE_SAP_SEND)) {
			if (status.equalsIgnoreCase(OAFStatus.COMPLETE.toString())) {

				// call sap service
				if (this.callSAPService(execution, status)) {
					logger.debug("OAFService.callSAPUpdateServiceByExecution() :: SAP service successfully called.");
					// if success then update in database too..
//            String processInstanceId = execution.getProcessInstanceId();
					ResponseEntity responseEntity = this.updateProcessStatus(processInstanceId, status, "0");
					if (responseEntity.getStatusCode() == HttpStatus.OK) {
						logger.debug(
								"OAFService.callSAPUpdateServiceByExecution() :: DB process status successfully updated.");
					} else {
						logger.debug(
								"OAFService.callSAPUpdateServiceByExecution() :: Unable to update db process, status :: "
										+ responseEntity.getBody());
					}
					execution.setVariable(SAP_SERVICE_CALL_FAIL, false);
				} else {
					execution.setVariable(SAP_SERVICE_CALL_FAIL, true);
				}

			} else if (status.equalsIgnoreCase(OAFStatus.REJECT.toString())) {
				// call sap service
				if (this.callSAPService(execution, status)) {
					logger.debug("OAFService.callSAPUpdateServiceByExecution() :: SAP service successfully called.");
					execution.setVariable(SAP_SERVICE_CALL_FAIL, false);
				} else {
					execution.setVariable(SAP_SERVICE_CALL_FAIL, true);
				}
				logger.info("OAFService.callSAPUpdateService() :::  " + SAP_SERVICE_CALL_FAIL + " :: "
						+ execution.getVariable(SAP_SERVICE_CALL_FAIL));
			}
		} else {
			execution.setVariable(SAP_SERVICE_CALL_FAIL, false);
			ResponseEntity responseEntity = this.updateProcessStatus(processInstanceId, status, "0");
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				logger.debug("OAFService.callSAPUpdateServiceByExecution() :: DB process status successfully updated.");
			} else {
				logger.debug("OAFService.callSAPUpdateServiceByExecution() :: Unable to update db process, status :: "
						+ responseEntity.getBody());
			}
			logger.info("OAFService.callSAPUpdateService() :::  " + SAP_SERVICE_CALL_FAIL + " :: "
					+ execution.getVariable(SAP_SERVICE_CALL_FAIL));
		}
	}

	/**
	 * This method is responsible to create 'restartProcess' restAPI Request.
	 *
	 * @param processInstanceId
	 * @param inquiry
	 * @return
	 */
	public ResponseEntity createRestartProcessRequest(String processInstanceId, String inquiry) {
		logger.debug("OAFService.createRestartProcessRequest() :: Input Data  :: 1). processInstanceId = "
				+ processInstanceId + " \n" + "2). inquiry = " + inquiry);

		String url = OAF_ADAPTER_BASE_URL + OAF_RESTART_PROCESS_BASE_URL;

		logger.debug("OAFService.createRestartProcessRequest() :: oaf-adapter url : " + url);
		if ((processInstanceId == null || StringUtils.isEmpty(processInstanceId))
				|| (inquiry == null || StringUtils.isEmpty(inquiry))) {
			logger.error(
					"OAFService.createRestartProcessRequest() :: Error code - 105 : Required parameter not found.");
			OAFResponse oafResponse = this.generateResponse(false, "105", "Required parameter not found");
			return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
		} else {
			return callPostRequest(url, processInstanceId, inquiry);
		}
	}
}
