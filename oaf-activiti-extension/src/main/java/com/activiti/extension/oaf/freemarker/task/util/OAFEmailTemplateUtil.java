package com.activiti.extension.oaf.freemarker.task.util;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.model.HeaderElement;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * This class is responsible to set the email template and has some static
 * methods
 * 
 * @author Keval Bhatt
 *
 */
public class OAFEmailTemplateUtil implements OAFConstants {
	private static Logger logger = LoggerFactory.getLogger(OAFEmailTemplateUtil.class);
	private static HistoryService historyService = Context.getProcessEngineConfiguration().getHistoryService();

	/**
	 * This method will use to parse the email template
	 * 
	 * @param execution
	 * @param emailTemplateContent
	 * @param templateName
	 * @return
	 */
	public static String parseTemplate(DelegateExecution execution, OAFEmailTemplateContent emailTemplateContent,
			String templateName) {
		String templateProcessedContent = "";
		try {
			templateProcessedContent = templateForCommonEmailData(execution, emailTemplateContent, templateName);
			if (templateProcessedContent != null && !templateProcessedContent.isEmpty()) {
				return templateProcessedContent;
			} else {
				return null;
			}

		} catch (TemplateException | IOException e) {
			logger.debug("OAFEmailTemplateUtil.parseTemplate() ::  Not able to parse template  " + templateName
					+ "  to send an email notification " + e.getMessage());
			return null;
		}

	}

	// Initialize email template content Start

	/**
	 * This method will initialize all required data for email template
	 * 
	 * @param execution
	 * @param emailTemplateContent
	 * @return
	 */
	public static OAFEmailTemplateContent initMail(DelegateExecution execution,
			OAFEmailTemplateContent emailTemplateContent) {
		// Initialize historyService

		emailTemplateContent = new OAFEmailTemplateContent();
		String oafForm = (String) execution.getVariable(OAFFORM);
		UIQuotationDetail uiQuotationDetail = OAFCommonUtil.convertJsonStringToQuote(oafForm);

		// Inq
		String inq = "";
		inq = (uiQuotationDetail.getHeaderSegment().getInq() != null
				&& !uiQuotationDetail.getHeaderSegment().getInq().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getInq().getValue()
						: "-";

		// Quote
		String quote = "";
		quote = (uiQuotationDetail.getHeaderSegment().getQuo() != null
				&& !uiQuotationDetail.getHeaderSegment().getQuo().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getQuo().getValue()
						: "-";

		// SalesOffice
		String salesOffice = "";
		salesOffice = (uiQuotationDetail.getHeaderSegment().getDistrict() != null
				&& !uiQuotationDetail.getHeaderSegment().getDistrict().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getDistrict().getValue()
						: "-";

		// Project
		String project = "";
		project = (uiQuotationDetail.getHeaderSegment().getCltname() != null
				&& !uiQuotationDetail.getHeaderSegment().getCltname().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getCltname().getValue()
						: "-";

		// Client
		String client = "";
		client = (uiQuotationDetail.getHeaderSegment().getCustname() != null
				&& !uiQuotationDetail.getHeaderSegment().getCustname().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getCustname().getValue()
						: "-";

		// Location
		String location = "";
		location = (uiQuotationDetail.getHeaderSegment().getCltloc() != null
				&& !uiQuotationDetail.getHeaderSegment().getCltloc().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getCltloc().getValue()
						: "-";

		// WeightMt
		String weightMt = "";
		weightMt = (uiQuotationDetail.getHeaderSegment().getProjmt() != null
				&& !uiQuotationDetail.getHeaderSegment().getProjmt().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getProjmt().getValue()
						: "-";

		// SalesComment
		String salesComment = "";
		salesComment = (uiQuotationDetail.getHeaderSegment().getJustificationForAboveDiscount() != null
				&& !uiQuotationDetail.getHeaderSegment().getJustificationForAboveDiscount().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getJustificationForAboveDiscount().getValue()
						: "-";

		// Revision
		String revision = "";
		revision = (uiQuotationDetail.getHeaderSegment().getRevno() != null
				&& !uiQuotationDetail.getHeaderSegment().getRevno().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getRevno().getValue()
						: "-";

		// dQ
		String dQ = "";
		dQ = (uiQuotationDetail.getHeaderSegment().getBstnk() != null
				&& !uiQuotationDetail.getHeaderSegment().getBstnk().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getBstnk().getValue()
						: "-";

		// initMail
		String initMail = "";
		HeaderElement smtpaddr = uiQuotationDetail.getHeaderSegment().getSmtpaddr();
		initMail = (smtpaddr != null && smtpaddr.getValue() != null && !smtpaddr.getValue().trim().isEmpty())
				? uiQuotationDetail.getHeaderSegment().getSmtpaddr().getValue()
				: TEMP_INIT_MAIL;

		// vkbur
		String vkbur = "";
		vkbur = (uiQuotationDetail.getHeaderSegment().getVkbur() != null
				&& !uiQuotationDetail.getHeaderSegment().getVkbur().getValue().trim().isEmpty())
						? uiQuotationDetail.getHeaderSegment().getVkbur().getValue()
						: "-";

		// engineeringServices
		String engineeringServices = "";
		engineeringServices = (execution.getVariable(ENGINEERING_SERVICES) != null)
				? execution.getVariable(ENGINEERING_SERVICES).toString()
				: "-";

		// isUserDecisionTableFailed
		boolean isUserDecisionTableFailed = true;
		isUserDecisionTableFailed = (execution.getVariable(IS_LEVEL_USERS_DECISIONTABLE_FAILED) != null)
				? (Boolean)execution.getVariable(IS_LEVEL_USERS_DECISIONTABLE_FAILED)
				: true;
		
		// regionFlag
		String regionFlag = "";
		regionFlag = (execution.getVariable(REGION_FLAG) != null)
				? execution.getVariable(REGION_FLAG).toString()
				: "-";
		// marginCPP
		String marginCPP = "";
		marginCPP =(uiQuotationDetail.getHeaderSegment().getMarginCPP() != null
				&& !uiQuotationDetail.getHeaderSegment().getMarginCPP().getValue().trim().isEmpty())
				? uiQuotationDetail.getHeaderSegment().getMarginCPP().getValue()
				: "-";
		emailTemplateContent.setInquiry(inq);
		emailTemplateContent.setQuote(quote);
		emailTemplateContent.setSalesOffice(salesOffice);
		emailTemplateContent.setProject(project);
		emailTemplateContent.setClient(client);
		emailTemplateContent.setLocation(location);
		emailTemplateContent.setWeightMt(weightMt);
		emailTemplateContent.setSalesComments(salesComment);
		emailTemplateContent.setRevision(revision);
		emailTemplateContent.setDq(dQ);
		emailTemplateContent.setInitMail(initMail);
		emailTemplateContent.setVkbur(vkbur);
		emailTemplateContent.setEngineeringServices(engineeringServices);
		emailTemplateContent.setUserDecisionTableFailed(isUserDecisionTableFailed);		
		emailTemplateContent.setRegionFlag(regionFlag);
		emailTemplateContent.setMarginCPP(marginCPP);
		emailTemplateTaskPendingAndApporvedByCheck(execution, emailTemplateContent);

		if (execution.getProcessInstanceId() != null) {
			Date proceDate = historyService
					.createProcessInstanceHistoryLogQuery((String) execution.getProcessInstanceId()).singleResult()
					.getStartTime();
			int diff = OAFCommonUtil.getDateDiffernceInDay(proceDate.getTime());

			emailTemplateContent.setDaysElapsedFromSubmission(diff);

		} else {
			emailTemplateContent.setDaysElapsedFromSubmission(0);
		}
		emailTemplateCheckRejectedBy(execution, emailTemplateContent);

		return emailTemplateContent;

	}

	/**
	 * This method is responsible to check the rejected user information
	 * 
	 * @param execution
	 * @param emailTemplateContent
	 */
	public static void emailTemplateCheckRejectedBy(DelegateExecution execution,
			OAFEmailTemplateContent emailTemplateContent) {
		String rejectedByUserName = "";
		String rejectedReason = "";
		String rejectedDate = "";
		String rejectedLevelfrom = "";
		Date taskCompleteTime = null;
		String taskCompleteDate = "";
		// HistoricTaskInstance taskInstance = null;
		if (execution.getVariable(LEVEL1_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL1_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(lEVEL1_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL1_APPROVAL1_COMMENT);
			rejectedByUserName = (String) execution.getVariable(LEVEL1_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL1_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL1_APPROVAL2_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL1_APPROVAL2_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(lEVEL1_APPROVAL2_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL1_APPROVAL2_COMMENT);
			rejectedByUserName = (String) execution.getVariable(LEVEL1_APPROVAL2_NAME);
			rejectedLevelfrom = LEVEL1_APPROVAL2_ID;
		}
		if (execution.getVariable(LEVEL2_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL2_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL2_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL2_APPROVAL1_COMMENT);
			rejectedByUserName = (String) execution.getVariable(LEVEL2_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL2_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL2_APPROVAL2_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL2_APPROVAL2_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL2_APPROVAL2_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL2_APPROVAL2_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL2_APPROVAL2_NAME);
			rejectedLevelfrom = LEVEL2_APPROVAL2_ID;
		}
		if (execution.getVariable(lEVEL3_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(lEVEL3_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(lEVEL3_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL3_APPROVAL1_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL3_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL3_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL4_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL4_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL4_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL4_APPROVAL1_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL4_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL4_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL5_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL5_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL5_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL5_APPROVAL1_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL5_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL5_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL6_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL6_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL6_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL6_APPROVAL1_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL6_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL6_APPROVAL1_ID;
		}
		if (execution.getVariable(LEVEL7_APPROVAL1_IS_REJECTED) != null
				&& (Boolean) execution.getVariable(LEVEL7_APPROVAL1_IS_REJECTED) == true) {
			taskCompleteTime = historyService.createHistoricTaskInstanceQuery()
					.taskId((String) execution.getVariable(LEVEL7_APPROVAL1_TASK_ID)).singleResult().getEndTime();
			// Set rejected information
			taskCompleteDate = OAFCommonUtil.converDateIntoString(taskCompleteTime);
			rejectedDate = taskCompleteDate;
			rejectedReason = (String) execution.getVariable(LEVEL7_APPROVAL1_COMMENT);

			rejectedByUserName = (String) execution.getVariable(LEVEL7_APPROVAL1_NAME);
			rejectedLevelfrom = LEVEL7_APPROVAL1_ID;
		}
		if (!rejectedByUserName.isEmpty()) {
			logger.info("OAFEmailTemplateUtil.emailTemplateCheckRejectedBy() :: rejected by : " + rejectedByUserName
					+ " from level :  " + rejectedLevelfrom);
			emailTemplateContent.setRejectedByUserName(rejectedByUserName);
			emailTemplateContent.setRejectedDate(rejectedDate);
			emailTemplateContent.setRejectedReason(rejectedReason);
		}

	}

	/**
	 * This method is responsible to check the pending and approved user information
	 * and store it in a jsonarray
	 * 
	 * @param execution
	 * @param emailTemplateContent
	 */
	public static void emailTemplateTaskPendingAndApporvedByCheck(DelegateExecution execution,
			OAFEmailTemplateContent emailTemplateContent) {

		// Json for Approval Info Start
		JSONArray jsonArrayApprovalInfo = new JSONArray();

		JSONObject jsonApprovalInfoObj = new JSONObject();
		Object obj = execution.getVariable(VIEWCOMMENTS);
		jsonArrayApprovalInfo = OAFCommonUtil.jsonArrStringToJsonArryObject(obj);
		jsonArrayApprovalInfo = OAFCommonUtil.sortJsonArrayInReverseOrder(jsonArrayApprovalInfo);
		jsonApprovalInfoObj.put(MAIL_TEMPLATE_CONTENT_APPROVALINFO_JSON, jsonArrayApprovalInfo);

		emailTemplateContent.setApprovalInformaiton(jsonApprovalInfoObj);

		// Json for Approval Info End

		// Json for pending Approval Start
		JSONArray jsonArrayPendingApprovalInfo = new JSONArray();
		// Level 1 Start
		if ((execution.getVariable(LEVEL1_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL1_APPROVAL1_IS_APPROVED) == null)) {

			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL1_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL1_APPROVAL1_DESIGNATION));

		}
		if ((execution.getVariable(LEVEL1_APPROVAL2_ID) != null)
				&& (execution.getVariable(LEVEL1_APPROVAL2_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL1_APPROVAL2_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL1_APPROVAL2_DESIGNATION));

		}
		// Level 1 End
		// Level 2 Start
		if ((execution.getVariable(LEVEL2_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL2_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL2_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL2_APPROVAL1_DESIGNATION));
		}

		if ((execution.getVariable(LEVEL2_APPROVAL2_ID) != null)
				&& (execution.getVariable(LEVEL2_APPROVAL2_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL2_APPROVAL2_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL2_APPROVAL2_DESIGNATION));
		}
		// Level 2 End

		// Level 3 Start
		if ((execution.getVariable(LEVEL3_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL3_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL3_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL3_APPROVAL1_DESIGNATION));
		}
		// Level 3 End

		// Level 4 Start
		if ((execution.getVariable(LEVEL4_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL4_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL4_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL4_APPROVAL1_DESIGNATION));
		}
		// Level 4 End

		// Level 5 Start
		if ((execution.getVariable(LEVEL5_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL5_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL5_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL5_APPROVAL1_DESIGNATION));
		}
		// Level 5 End

		// Level 6 Start
		if ((execution.getVariable(LEVEL6_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL6_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL6_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL6_APPROVAL1_DESIGNATION));
		}
		// Level 6 End

		// Level 7 Start
		if ((execution.getVariable(LEVEL7_APPROVAL1_ID) != null)
				&& (execution.getVariable(LEVEL7_APPROVAL1_IS_APPROVED) == null)) {
			jsonArrayPendingApprovalInfo.put(new JSONObject()
					.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME,
							(String) execution.getVariable(LEVEL7_APPROVAL1_NAME))
					.put(MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION, LEVEL7_APPROVAL1_DESIGNATION));
		}
		// Level 7 End

		JSONObject jsonPendingApprovalObj = new JSONObject();
		jsonPendingApprovalObj.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGINFO_JSON, jsonArrayPendingApprovalInfo);
		emailTemplateContent.setApprovalPendingInformaiton(jsonPendingApprovalObj);
		// Json approval pending information for stencil
		execution.setVariable(OAF_VIEW_PENDING_APPROVALS, jsonArrayPendingApprovalInfo.toString());
		// Json for pending ApprovalEnd

	}

	// Initialize email template content End

	/**
	 * This method is responsible to set the data in email template
	 * 
	 * @param execution
	 * @param emailTemplateContent
	 * @param templateName
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static String templateForCommonEmailData(DelegateExecution execution,
			OAFEmailTemplateContent emailTemplateContent, String templateName) throws TemplateException, IOException {
		Template template = OAFFreeMarkerUtil.getFreemarkerTemplateContent(templateName);
		return template != null ? OAFFreeMarkerUtil.getTemplateProcessedContent(template,
				OAFFreeMarkerUtil.commonDynamicDataForQuote(execution, emailTemplateContent)) : null;
	}

}
