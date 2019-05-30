package com.activiti.extension.oaf.freemarker.task.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.mail.util.ByteArrayDataSource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is responsible to do common operations or has static methods
 * 
 * @author Keval Bhatt
 *
 */
public class OAFCommonUtil implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFCommonUtil.class);
	public static String dateFormatReg = DEFAULT_DATE_TIME_FORMAT;

	private static HistoryService historyService =  Context.getProcessEngineConfiguration().getHistoryService();
	/**
	 * Return the time difference in minute
	 * 
	 * @param startDateTime
	 * @return
	 */
	public static int getDateDiffernceInMin(long startDateTime) {
		Date startDate = new Date(startDateTime);
		Date endDate = new Date();
		long duration = endDate.getTime() - startDate.getTime();
		long diffInMin = TimeUnit.MILLISECONDS.toMinutes(duration);
		return (int) diffInMin;
	}

	/**
	 * Return the time difference in hour
	 * 
	 * @param startDateTime
	 * @return
	 */
	public static int getDateDiffernceInHour(long startDateTime) {
		Date startDate = new Date(startDateTime);
		Date endDate = new Date();
		long duration = endDate.getTime() - startDate.getTime();
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		return (int) diffInHours;
	}

	/**
	 * Return the time difference in day
	 * 
	 * @param startDateTime
	 * @return
	 */
	public static int getDateDiffernceInDay(long startDateTime) {
		Date startDate = new Date(startDateTime);
		Date endDate = new Date();
		long duration = endDate.getTime() - startDate.getTime();
		long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
		return (int) diffInDays;
	}

	/**
	 * Convert the date into string format
	 * 
	 * @param dateToConvert
	 * @return
	 */
	public static String converDateIntoString(Date dateToConvert) {
		String formatedDate = new SimpleDateFormat(dateFormatReg).format(dateToConvert);

		return formatedDate;
	}

	/**
	 * Convert the String to date format
	 * 
	 * @param stringToConvert
	 * @return
	 */
	public static Date convertStringIntoDate(String stringToConvert) {
		try {
			Date date = new SimpleDateFormat(dateFormatReg).parse(stringToConvert);
			return date;
		} catch (ParseException e) {

			return null;
		}
	}

	/**
	 * Responsible to change the default task name to custom task names
	 * 
	 * @param delegateTask
	 * @param oafForm
	 */
	public static void changeTaskNames(DelegateTask delegateTask, String oafForm) {
		UIQuotationDetail uiQuotationDetail = convertJsonStringToQuote(oafForm);
		String inq = uiQuotationDetail.getHeaderSegment().getInq().getValue();
		String quote = uiQuotationDetail.getHeaderSegment().getQuo().getValue();
		// Changing Task Name Start
		switch (String.valueOf(delegateTask.getExecution().getVariable(SEND_EMAIL_NOTIFICATION_FOR))) {
		case LEVEL1_APPROVAL1_ID:
			delegateTask.setName(LEVEL1_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL1_APPROVAL2_ID:
			delegateTask.setName(LEVEL1_APPROVAL2_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL2_APPROVAL1_ID:
			delegateTask.setName(LEVEL2_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL2_APPROVAL2_ID:
			delegateTask.setName(LEVEL2_APPROVAL2_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL3_APPROVAL1_ID:
			delegateTask.setName(LEVEL3_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL4_APPROVAL1_ID:
			delegateTask.setName(LEVEL4_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		case LEVEL5_APPROVAL1_ID:
			delegateTask.setName(LEVEL5_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;

		case LEVEL6_APPROVAL1_ID:
			delegateTask.setName(LEVEL6_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;

		case LEVEL7_APPROVAL1_ID:
			delegateTask.setName(LEVEL7_APPROVAL1_DESIGNATION + " (Inquiry: " + inq + "- Quote: " + quote + ")");
			break;
		}
	}

	/**
	 * Map the json to POJO class
	 * 
	 * @param inputJson
	 * @return
	 */
	public static UIQuotationDetail convertJsonStringToQuote(String inputJson) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(inputJson, UIQuotationDetail.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Initialize or set the execution status variables like Approved, Reject, etc
	 * 
	 * @param execution
	 * @param oafForm
	 */
	public static void initApprovalStatusOneTime(DelegateExecution execution, String oafForm) {
		if (execution.getVariable(INIT_STATUS_LABELS_FTIME) != null) {
			UIQuotationDetail uiQuotationDetail = convertJsonStringToQuote(oafForm);
			// Init Inq and quote (required)
			String inq = uiQuotationDetail.getHeaderSegment().getInq().getValue();
			String quote = uiQuotationDetail.getHeaderSegment().getQuo().getValue();
			execution.setVariable(MAIL_TEMPLATE_CONTENT_INQUIRY, inq);
			execution.setVariable(MAIL_TEMPLATE_CONTENT_QUOTE, quote);

			// Init statues variables
			execution.setVariable(OAF_APPROVAL_STATUS_APPROVED, OAF_APPROVAL_STATUS_APPROVED_PROPERTY);
			execution.setVariable(OAF_APPROVAL_STATUS_REJECTED, OAF_APPROVAL_STATUS_REJECTED_PROPERTY);
			execution.setVariable(OAF_APPROVAL_STATUS_SKIP, OAF_APPROVAL_STATUS_SKIP_APPROVAL_PROPERTY);
			execution.setVariable(OAF_APPROVAL_STATUS_AUTO_APPROVED, OAF_APPROVAL_STATUS_AUTO_APPROVAL_PROPERTY);
			execution.setVariable(OAF_APPROVAL_STATUS_AUTO_REJECTED, OAF_APPROVAL_STATUS_AUTO_REJECTION_PROPERTY);
			execution.setVariable(OAF_APPROVAL_STATUS_FINAL_COMPLETED, OAF_APPROVAL_STATUS_FINAL_COMPLETED_PROPERTY);
			execution.removeVariable(INIT_STATUS_LABELS_FTIME);
		}
	}

	/**
	 * Change the autoapproval rejection status
	 * 
	 * @param userId
	 * @param execution
	 * @param historicTaskInstance
	 */
	public static void changeAutoApprovalRejectionStatus(long userId, DelegateExecution execution,
			HistoricTaskInstance historicTaskInstance, boolean isChangeApprovalStatus,
			boolean isChangeRejectionStatus) {
		String autoAppExVarNameforStatus = "";
		String isApprovedVar = "";
		String taskName = historicTaskInstance.getName();
		if (taskName != null) {
			taskName = taskName.substring(0, taskName.lastIndexOf("(")).toString().trim();
		}
		if (taskName.equals(LEVEL1_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL1_APPROVAL1_STATUS;
			isApprovedVar = LEVEL1_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL1_APPROVAL2_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL1_APPROVAL2_STATUS;
			isApprovedVar = LEVEL1_APPROVAL2_IS_APPROVED;
		} else if (taskName.equals(LEVEL2_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL2_APPROVAL1_STATUS;
			isApprovedVar = LEVEL2_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL2_APPROVAL2_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL2_APPROVAL2_STATUS;
			isApprovedVar = LEVEL2_APPROVAL2_IS_APPROVED;
		} else if (taskName.equals(LEVEL3_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL3_APPROVAL1_STATUS;
			isApprovedVar = LEVEL3_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL4_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL4_APPROVAL1_STATUS;
			isApprovedVar = LEVEL4_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL5_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL5_APPROVAL1_STATUS;
			isApprovedVar = LEVEL5_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL6_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL6_APPROVAL1_STATUS;
			isApprovedVar = LEVEL6_APPROVAL1_IS_APPROVED;
		} else if (taskName.equals(LEVEL7_APPROVAL1_DESIGNATION)) {
			autoAppExVarNameforStatus = LEVEL7_APPROVAL1_STATUS;
			isApprovedVar = LEVEL7_APPROVAL1_IS_APPROVED;
		}
		if (!autoAppExVarNameforStatus.isEmpty()) {
			if (isChangeApprovalStatus) {
				if (execution.getVariable(OAF_APPROVAL_STATUS_AUTO_APPROVED) != null) {
					execution.setVariable(autoAppExVarNameforStatus,
							execution.getVariable(OAF_APPROVAL_STATUS_AUTO_APPROVED));
					execution.setVariable(isApprovedVar, true);
				} else {
					execution.setVariable(autoAppExVarNameforStatus, "-");
				}
			} else if (isChangeRejectionStatus) {
				if (execution.getVariable(OAF_APPROVAL_STATUS_AUTO_REJECTED) != null) {
					execution.setVariable(autoAppExVarNameforStatus,
							execution.getVariable(OAF_APPROVAL_STATUS_AUTO_REJECTED));
				} else {
					execution.setVariable(autoAppExVarNameforStatus, "-");
				}
			}

		}
	}

	/**
	 * Get the auto approval difference
	 * 
	 * @param taskLongCreateTime
	 * @return
	 */
	public static int getAutoApproveDifference(long taskLongCreateTime) {
		if (OAF_AUTO_APPROVAL_MIN_ENALBLE != null && Boolean.parseBoolean(OAF_AUTO_APPROVAL_MIN_ENALBLE) == true) {
			return getDateDiffernceInMin(taskLongCreateTime);
		} else if (OAF_AUTO_APPROVAL_HOUR_ENALBLE != null
				&& Boolean.parseBoolean(OAF_AUTO_APPROVAL_HOUR_ENALBLE) == true) {
			return getDateDiffernceInHour(taskLongCreateTime);
		} else if (OAF_AUTO_APPROVAL_DAY_ENALBLE != null
				&& Boolean.parseBoolean(OAF_AUTO_APPROVAL_DAY_ENALBLE) == true) {
			return getDateDiffernceInDay(taskLongCreateTime);
		} else {
			return -1;
		}
	}

	/**
	 * Get the auto approval duration
	 * 
	 * @return
	 */
	public static int getAutoApproveDuration() {
		if (OAF_AUTO_APPROVAL_MIN_ENALBLE != null && Boolean.parseBoolean(OAF_AUTO_APPROVAL_MIN_ENALBLE) == true) {
			return Integer.parseInt(OAF_AUTO_APPROVAL_MIN_VALUE);
		} else if (OAF_AUTO_APPROVAL_HOUR_ENALBLE != null
				&& Boolean.parseBoolean(OAF_AUTO_APPROVAL_HOUR_ENALBLE) == true) {
			return Integer.parseInt(OAF_AUTO_APPROVAL_HOUR_VALUE);
		} else if (OAF_AUTO_APPROVAL_DAY_ENALBLE != null
				&& Boolean.parseBoolean(OAF_AUTO_APPROVAL_DAY_ENALBLE) == true) {
			return Integer.parseInt(OAF_AUTO_APPROVAL_DAY_VALUE);
		} else {
			return -1;
		}
	}

	/**
	 * Convert the json array into OBJECT for tokenizing the string
	 * 
	 * @param obj
	 * @return
	 */
	public static JSONArray jsonArrStringToJsonArryObject(Object obj) {
		JSONArray jsonArray = new JSONArray();
		if (obj != null) {
			Object o = null;
			try {
				o = new JSONTokener(obj.toString()).nextValue();
			} catch (JSONException e) {
				logger.debug("OAFCommonUtil.jsonArrStringToJsonArryObject() :: Exception while parsing JSON : " + e);
			}
			if (o instanceof JSONArray) {
				jsonArray = (JSONArray) o;
			}
		}

		return jsonArray;

	}

	/**
	 * Sort the json array in revers order
	 * 
	 * @param jsonArray
	 * @return
	 */
	public static JSONArray sortJsonArrayInReverseOrder(JSONArray jsonArray) {
		JSONArray newJsonArray = new JSONArray();
		if (jsonArray.length() >= 1) {
			for (int i = jsonArray.length() - 1; i >= 0; i--) {
				newJsonArray.put(jsonArray.get(i));
			}
		}
		return newJsonArray;
	}

	/**
	 * Check the current and parameter date contains weekend
	 * 
	 * @param taskCreateTime
	 * @return
	 */
	public static boolean isAutoApprDtContainsWeekend(Date taskCreateTime) {
		Date endDate = new Date();
		LocalDate startLocalDate = taskCreateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String weekend = OAF_AUTO_APPROVAL_WEEKEND_DAYS;
		// Split weekend date from property file
		String[] weekenddays = weekend.split(", ");

		Set<DayOfWeek> weekendSet = new HashSet<DayOfWeek>();
		for (String day : weekenddays) {

			if (day.equalsIgnoreCase("MONDAY")) {
				weekendSet.add(DayOfWeek.MONDAY);
			}
			if (day.equalsIgnoreCase("TUESDAY")) {
				weekendSet.add(DayOfWeek.TUESDAY);
			}
			if (day.equalsIgnoreCase("WEDNESDAY")) {
				weekendSet.add(DayOfWeek.WEDNESDAY);
			}
			if (day.equalsIgnoreCase("THURSDAY")) {
				weekendSet.add(DayOfWeek.THURSDAY);
			}
			if (day.equalsIgnoreCase("FRIDAY")) {
				weekendSet.add(DayOfWeek.FRIDAY);
			}
			if (day.equalsIgnoreCase("SATURDAY")) {
				weekendSet.add(DayOfWeek.SATURDAY);
			}
			if (day.equalsIgnoreCase("SUNDAY")) {
				weekendSet.add(DayOfWeek.SUNDAY);
			}
		}
		LocalDate ld = startLocalDate;
		while (ld.isBefore(endLocalDate)) {
			if (weekendSet.contains(ld.getDayOfWeek())) {
				// If weekend day, return true .
				return true;
			}
			// Prepare for next loop.
			ld = ld.plusDays(1); // Increment to next day.
		}

		return false;
	}

	/**
	 * Get the auto approval level enable variable name
	 * 
	 * @param taskId
	 * @param execution
	 * @return
	 */
	public static String getAutoApprovalLevelEnableName(String taskId, DelegateExecution execution) {
		String autoApprovalLevelName = null;
		if (taskId.equals(execution.getVariable(lEVEL1_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL1_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(lEVEL1_APPROVAL2_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL1_APPROVAL2_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL2_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL2_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL2_APPROVAL2_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL2_APPROVAL2_ENABLE;
		} else if (taskId.equals(execution.getVariable(lEVEL3_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL3_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL4_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL4_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL5_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL5_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL6_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL6_APPROVAL1_ENABLE;
		} else if (taskId.equals(execution.getVariable(LEVEL7_APPROVAL1_TASK_ID).toString())) {
			autoApprovalLevelName = OAF_AUTO_APPROVAL_LEVEL7_APPROVAL1_ENABLE;
		}
		return autoApprovalLevelName;
	}

	/**
	 * Get the final approval rest interval
	 * 
	 * @return
	 */
	public static int getFinalApprovalResetInterval() {
		if (OAF_FINAL_APPROVAL_RESET_INTERVAL_ENABLE != null
				&& Boolean.parseBoolean(OAF_FINAL_APPROVAL_RESET_INTERVAL_ENABLE) == true) {
			return Integer.parseInt(OAF_FINAL_APPROVAL_RESET_INTERVAL_DAYS);
		} else if (OAF_FINAL_APPROVAL_RESET_INTERVAL_ENABLE != null
				&& Boolean.parseBoolean(OAF_FINAL_APPROVAL_RESET_INTERVAL_ENABLE) == false) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * To do auto reject or reset case
	 * 
	 * @param historicTaskInstance
	 * @param taskService
	 * @param execution
	 * @param taskId
	 * @param userId
	 */
	public static void doAutoRejectCaseOrResetCase(HistoricTaskInstance historicTaskInstance, TaskService taskService,
			DelegateExecution execution, String taskId, long userId) {
		if (OAF_FINAL_APPROVAL_RESET_AUTO_CASE_ENABLE != null
				&& Boolean.parseBoolean(OAF_FINAL_APPROVAL_RESET_AUTO_CASE_ENABLE)) {
			execution.setVariable(IS_FINAL_RESET_ENABLE, true);
			execution.removeVariable(IS_REMINDER_ENABLED);
			logger.debug("OAFTaskService.doAutoRejectCaseOrResetCase():: Reminder task Name  :: "
					+ historicTaskInstance.getName() + " is set as auto reset..");

		} else if (OAF_FINAL_APPROVAL_RESET_AUTO_REJECT_ENABLE != null
				&& Boolean.parseBoolean(OAF_FINAL_APPROVAL_RESET_AUTO_REJECT_ENABLE)) {
			// Change the status of approval
			changeAutoApprovalRejectionStatus(userId, execution, historicTaskInstance, false, true);
			// Comment following block to disable auto rejection
			// Auto rejection Start
			execution.setVariable("form" + historicTaskInstance.getFormKey() + "outcome", "Reject");
			taskService.complete(taskId);
			execution.setVariable(IS_FINAL_AUTO_REJECTION_ENABLE, true);
			execution.removeVariable(IS_REMINDER_ENABLED);
			logger.debug("OAFTaskService.doAutoRejectCaseOrResetCase():: Reminder task Name  :: "
					+ historicTaskInstance.getName() + " is auto rejected..");
			// Auto rejection End
		} else {
			execution.setVariable(IS_REMINDER_ENABLED, false);
			logger.debug(
					"OAFTaskService.doAutoRejectCaseOrResetCase():: not able to read property for auto reset or rejection case");
		}
	}

	/**
	 * Set sap failure email content for retrying the request
	 * 
	 * @param emailTemplateContent
	 * @param retrySapFailUrl
	 * @param sapFailReason
	 * @return
	 */
	public static void setSapFailureEmailContent(OAFEmailTemplateContent emailTemplateContent, String retrySapFailUrl,
			String sapFailReason) {
		if (OAF_SERVICE_SAP_FAIL_RETRY_ENABLE != null && Boolean.parseBoolean(OAF_SERVICE_SAP_FAIL_RETRY_ENABLE)) {
			emailTemplateContent.setSapFailEmail(true);
			emailTemplateContent.setRetrySapFailUrl(retrySapFailUrl);
			emailTemplateContent.setSapFailReason(sapFailReason);
		} else {
			emailTemplateContent.setSapFailEmail(false);
		}
	}

	/**
	 * This method is responsible to get nodeId from nodeRef.
	 * 
	 * @param nodeRef
	 * @return
	 */
	public static String getNodeId(String nodeRef) {
		nodeRef = nodeRef.replaceAll("://", "/");
		return nodeRef.substring(nodeRef.lastIndexOf("/") + 1);
	}

	/**
	 * @param emailTemplateContent
	 * @param pdfDownloadUrl
	 */
	public static void setPdfDownloadEmailContent(OAFEmailTemplateContent emailTemplateContent,
			DelegateExecution execution) {
		if (execution.getVariable(ACS_PDF_GENERATED_NODE_ID) != null) {
			String pdfFileName = emailTemplateContent.getInquiry() + "_" + emailTemplateContent.getQuote();
			pdfFileName = pdfFileName + "_" + java.time.LocalDate.now().toString() + ".pdf";
			String nodeId = (String) execution.getVariable(ACS_PDF_GENERATED_NODE_ID);
			String downloadUrl = OAF_APW_EMAIL_DOWNLOAD_URL + nodeId + "/" + pdfFileName;			
			logger.debug("OAFCommonUtil.setPdfDownloadEmailContent() :: Setting pdf download link in email...");			
			emailTemplateContent.setPdfDownloadEnable(true);
			emailTemplateContent.setPdfFileName(pdfFileName);
			emailTemplateContent.setPdfDownloadLink(downloadUrl);
			logger.debug("OAFCommonUtil.setPdfDownloadEmailContent() :: Pdf download link is attached");
		} else {
			emailTemplateContent.setPdfDownloadEnable(false);
		}
	}

	public static ByteArrayDataSource generatePdfFromContent(DelegateExecution execution) {
		ByteArrayDataSource bads = null;

		// Generate PDF
		// Store PDF In ACS
		String comments = (String) execution.getVariable(VIEWCOMMENTS);
		String oafForm = (String) execution.getVariable(OAFFORM);
		Map<String, Object> getting_pdf_data = OAFPdfUtil.getting_pdf_data(oafForm);
		if (comments != null) {
			JSONArray commentJsonArray = new JSONArray(comments);
			//commentJsonArray = OAFCommonUtil.sortJsonArrayInReverseOrder(commentJsonArray);
			getting_pdf_data.put(VIEWCOMMENTS, commentJsonArray);
		}
		bads = OAFPdfUtil.storeAndGetGeneratedPdf(getting_pdf_data, oafForm, execution);

		return bads;
	}
	
	public static boolean isTaskCompleted(String taskId) {
		try {
			HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
					.taskId(taskId).singleResult();
			if(historicTaskInstance.getEndTime()!=null) {
				return true;
			}else {
				return false;
			}
		}
		catch(Exception e) {
			logger.error("OAFCommonUtil.isTaskCompleted() :: Exception :: "+e);
			return true;
		}
				
	}

}
