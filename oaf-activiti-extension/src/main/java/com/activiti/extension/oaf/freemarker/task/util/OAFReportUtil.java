package com.activiti.extension.oaf.freemarker.task.util;

import com.activiti.extension.bean.service.OAFService;
import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.model.OAFReportDetails;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible to update the report
 * @author Keval Bhatt
 *
 */
public class OAFReportUtil implements OAFConstants {
	private static Logger logger = LoggerFactory.getLogger(OAFReportUtil.class);
	private static HistoryService historyService =  Context.getProcessEngineConfiguration().getHistoryService();
	private static IdentityService identityService =  Context.getProcessEngineConfiguration().getIdentityService();
	private static ObjectMapper objMapper = new ObjectMapper();
	private static String reportUrl =OAF_ADAPTER_BASE_URL+ OAF_API_SAVE_REPORT_BASE_URL;
	private static String contentType = CONTENT_JSON_VALUE;
	
	/**
	 * To get the common elements for report
	 * @param execution
	 * @param oafForm
	 * @return
	 */
	public static OAFReportDetails getReportCommonElements(DelegateExecution execution,String oafForm) {
		UIQuotationDetail uiQuotationDetail =OAFCommonUtil.convertJsonStringToQuote(oafForm);
		OAFReportDetails reportDetails = new OAFReportDetails();
		if(uiQuotationDetail!=null) {
			//Inq number
			String inq= "";
			inq = (uiQuotationDetail.getHeaderSegment().getInq() != null && ! uiQuotationDetail.getHeaderSegment().getInq().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getInq().getValue() : "-";
			
			//Quote number
			String quote= "";
			quote = (uiQuotationDetail.getHeaderSegment().getQuo() != null && !uiQuotationDetail.getHeaderSegment().getQuo().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getQuo().getValue() : "-";
			
			//CompanyCode
			String companyCode = "";
			companyCode = (uiQuotationDetail.getHeaderSegment().getCompanycode() != null && !uiQuotationDetail.getHeaderSegment().getCompanycode().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getCompanycode().getValue() : "-";
			
			//Quote create date
			Date  proceDate=  historyService.createProcessInstanceHistoryLogQuery((String)execution.getProcessInstanceId()).singleResult().getStartTime();
			String quoteDate = "-";
			if (proceDate != null) {
				quoteDate = new SimpleDateFormat("dd-MM-yyyy").format(proceDate);

			}			
			// Sold to party
			String sTP= "";
			sTP = (uiQuotationDetail.getHeaderSegment().getCltname() != null && !uiQuotationDetail.getHeaderSegment().getCltname().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getCltname().getValue() : "-";
			
			// DQ number
			String dQ="" ;
			dQ = (uiQuotationDetail.getHeaderSegment().getBstnk() != null && !uiQuotationDetail.getHeaderSegment().getBstnk().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getBstnk().getValue() : "-";
			
			// Tonnage
			String tonnage= "";
			tonnage = (uiQuotationDetail.getHeaderSegment().getProjmt() != null && ! uiQuotationDetail.getHeaderSegment().getProjmt().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getProjmt().getValue() : "-";
			
			// Elapsed Day			
			int diff = OAFCommonUtil.getDateDiffernceInDay(proceDate.getTime());
			
			// Sales Office
			String salesOffice= "";
			
			salesOffice = (uiQuotationDetail.getHeaderSegment().getDistrict() != null && !uiQuotationDetail.getHeaderSegment().getDistrict().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getDistrict().getValue() : "-";
			// Sales Engineer
			
			String salesEngineer= "";																											
			salesEngineer = (uiQuotationDetail.getHeaderSegment().getSname() != null && !uiQuotationDetail.getHeaderSegment().getSname().getValue().trim().isEmpty())? uiQuotationDetail.getHeaderSegment().getSname().getValue() : "-";
			
			
			reportDetails.setCompany_code(companyCode);
			reportDetails.setInq(inq);
			reportDetails.setQuote(quote);
			reportDetails.setDq(dQ);
			reportDetails.setSold_to_party(sTP);
			reportDetails.setTonnage(tonnage);
			reportDetails.setSales_office(salesOffice);
			reportDetails.setSales_engineer(salesEngineer);
			reportDetails.setElapsed_day(String.valueOf(diff));
			reportDetails.setCreation_date(proceDate.getTime());
			reportDetails.setProcess_id((String)execution.getProcessInstanceId());
			return reportDetails;
		}
		else {
			return null;
		}		
	}

	
	/**
	 * Set the pending approval description
	 * @param execution
	 * @param reportDetails
	 */
	public static void setPendingApprovalDesc( DelegateExecution execution, OAFReportDetails reportDetails) {
		
		List<HistoricTaskInstance> historicTasks =historyService.createHistoricTaskInstanceQuery().executionId(execution.getProcessInstanceId()).unfinished().list();
		String pendingWith = "";
		for(HistoricTaskInstance historicTask : historicTasks) {
			String userId = historicTask.getAssignee();
			User user = identityService.createUserQuery().userId(userId).singleResult();
			pendingWith +=user.getFirstName();
			pendingWith +=",";
		}
		reportDetails.setPending_with(pendingWith);
	}
	
	/**
	 * To set the approval description
	 * @param appDate
	 * @param reportDetails
	 * @return
	 */
	public static OAFReportDetails setApprovedDesc(String appDate,OAFReportDetails reportDetails) {
		if(reportDetails != null) {
			reportDetails.setApproved_date(appDate);
			return reportDetails;
		}else {
			return null;
		}		
	}

	/**
	 * To set the rejection description
	 * @param emailTemplateContent
	 * @param reportDetails
	 */
	public static void setRejectionApprovalDesc(OAFEmailTemplateContent emailTemplateContent, OAFReportDetails reportDetails) {
		reportDetails.setRejection_date(emailTemplateContent.getRejectedDate());
		reportDetails.setRejection_reason(emailTemplateContent.getRejectedReason());
		reportDetails.setRejection_user(emailTemplateContent.getRejectedByUserName());		
	}
	
	// Update report for pending approval
	/**
	 * Send the request and update the report for pending approval
	 * @param taskId
	 * @param pendingWith
	 * @param emailTemplateContent
	 * @param execution
	 * @return
	 */
	public static int updatePendingReportDetail(String taskId,String pendingWith,OAFEmailTemplateContent emailTemplateContent, DelegateExecution execution) {
		try {
			String oafForm = (String)execution.getVariable(OAFFORM);
			OAFReportDetails reportDetails = getReportCommonElements(execution, oafForm);
			reportDetails.setQuote_type(QUOTE_TYPE_PENDING);
			setLastApprovalInfo(execution, reportDetails, emailTemplateContent);
			//setPendingApprovalDesc(execution, reportDetails);
			reportDetails.setPending_with(pendingWith);
			//Set taskId
			reportDetails.setTask_id(taskId);
			//reportDetails = setPendingApprovalDesc(pendingWith, lastAppDate, lastAppUser, reportDetails);
			String json = objMapper.writeValueAsString(reportDetails);
			logger.debug("OAFReportUtil.updatePendingReportDetail() ::  updating oaf report... ");
			int status= OAFService.sendJsonPostRequest(reportUrl, json, contentType);
			logger.debug("OAFReportUtil.updatePendingReportDetail() ::  report successfully updated... ");
			return status;
		}
		catch(JsonProcessingException e) {			
			logger.error("OAFReportUtil.updatePendingReportDetail() :: JsonProcessingException : "+e);
			return 0;
		}
		catch(Exception e) {
			logger.error("OAFReportUtil.updatePendingReportDetail() :: Exception : "+e);
			return 0;
		}				
	}
	
	/**
	 * Set the last approval information
	 * @param execution
	 * @param reportDetails
	 * @param emailTemplateContent
	 */
	public static void setLastApprovalInfo(DelegateExecution execution, OAFReportDetails reportDetails, OAFEmailTemplateContent emailTemplateContent)
	{
		String lastApproveUser = "";
		String lastAppDate = null;
		JSONObject jsonObj = emailTemplateContent.getApprovalInformaiton();
		JSONArray jsonArray = jsonObj.getJSONArray(MAIL_TEMPLATE_CONTENT_APPROVALINFO_JSON);
		JSONObject temJson = null;
		if(jsonArray.length() == 0) {
			
			lastApproveUser ="-";
			lastAppDate = "-";
		}
		else {
			temJson = jsonArray.getJSONObject(0);
			lastApproveUser = temJson.getString(KEY_CMT_NAME);			
			lastAppDate = temJson.getString(KEY_CMT_DATE);			
		}
		
		reportDetails.setLast_approval_date(lastAppDate);
		reportDetails.setLast_approval_user(lastApproveUser);				
	}

	// Update report for rejection approval
	
	/**
	 * Send the request and update the report for rejection approval
	 * @param emailTemplateContent
	 * @param execution
	 * @return
	 */
	public static int updateRejectedReportDetail(OAFEmailTemplateContent emailTemplateContent, DelegateExecution execution) {
		try {
			String oafForm = (String)execution.getVariable(OAFFORM);
			OAFReportDetails reportDetails = getReportCommonElements(execution, oafForm);
			reportDetails.setQuote_type(QUOTE_TYPE_REJECTION);
			setRejectionApprovalDesc(emailTemplateContent, reportDetails);			
			String json = objMapper.writeValueAsString(reportDetails);
			logger.debug("OAFReportUtil.updateRejectedReportDetail() ::  updating oaf report... ");
			int status= OAFService.sendJsonPostRequest(reportUrl, json, contentType);
			logger.debug("OAFReportUtil.updateRejectedReportDetail() ::  report successfully updated... ");
			return status;
		}
		catch(JsonProcessingException e) {			
			logger.error("OAFReportUtil.updateRejectedReportDetail() :: JsonProcessingException : "+e);
			return 0;
		}
		catch(Exception e) {
			logger.error("OAFReportUtil.updateRejectedReportDetail() :: Exception : "+e);
			return 0;
		}				
	}

	// Update report for final approve report
	/**
	 * Send the request and update the report for final  approval quote
	 * @param emailTemplateContent
	 * @param execution
	 * @return
	 */
	public static int updateFinalApprovedReportDetail(OAFEmailTemplateContent emailTemplateContent, DelegateExecution execution) {
		try {
			String oafForm = (String)execution.getVariable(OAFFORM);
			OAFReportDetails reportDetails = getReportCommonElements(execution, oafForm);
			reportDetails.setQuote_type(QUOTE_TYPE_APPROVAL);
			setFinalApproveDesc(emailTemplateContent, reportDetails);			
			String json = objMapper.writeValueAsString(reportDetails);
			logger.debug("OAFReportUtil.updateFinalApprovedReportDetail() ::  updating oaf report... ");
			int status= OAFService.sendJsonPostRequest(reportUrl, json, contentType);
			logger.debug("OAFReportUtil.updateFinalApprovedReportDetail() ::  report successfully updated... ");
			return status;
		}
		catch(JsonProcessingException e) {			
			logger.error("OAFReportUtil.updateFinalApprovedReportDetail() :: JsonProcessingException : "+e);
			return 0;
		}
		catch(Exception e) {
			logger.error("OAFReportUtil.updateFinalApprovedReportDetail() :: Exception : "+e);
			return 0;
		}				
	}


	/**
	 * Send the request and update the report for final approval description
	 * @param emailTemplateContent
	 * @param reportDetails
	 */
	public static void setFinalApproveDesc(OAFEmailTemplateContent emailTemplateContent, OAFReportDetails reportDetails) {
		String finalAppDate = OAFCommonUtil.converDateIntoString(new Date());
		reportDetails.setApproved_date(finalAppDate);		
				
	}
	
}
