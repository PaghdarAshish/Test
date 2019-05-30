package com.activiti.extension.oaf.constants;

import java.util.ResourceBundle;

/**
 * This interface is responsible to store required constants 
 * @author Keval Bhatt, Pradip Patel, Avani Purohit
 *
 */
public interface OAFConstants {

	Long APS_TENANT_ID = 1L;

	String OAF_ADEPTER_CONTEXT = "oaf-adapter";
	String API_MANAGE_OAF = "/manageOAF";

	String ORDER_APPROVAL_FORM_RESOURCE_BUNDLE = "order-approval-form";
	ResourceBundle OAF_RESOURCE_BUNDLE_INSTANCE = ResourceBundle.getBundle(ORDER_APPROVAL_FORM_RESOURCE_BUNDLE);

	String SERVER = OAF_RESOURCE_BUNDLE_INSTANCE.getString("oaf-server");
	String KEY_OAF_ADAPTER_BASE_URL = SERVER + "oaf.adapter.base.url";
	String OAF_ADAPTER_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_ADAPTER_BASE_URL);

	String KEY_OAF_MANAGEOAF_BASE_URL = SERVER + "oaf.adapter.api.manage-oaf";
	String OAF_API_MANAGEOAF_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_MANAGEOAF_BASE_URL);

	String KEY_OAF_UPDATE_STATUS_BASE_URL = SERVER + "oaf.adapter.api.update-status";
	String OAF_API_UPDATE_STATUS_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_UPDATE_STATUS_BASE_URL);

	String KEY_OAF_RESTART_PROCESS_BASE_URL = SERVER + "oaf.adapter.api.restart-process";
	String OAF_RESTART_PROCESS_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_RESTART_PROCESS_BASE_URL);

	// Save Report
	String KEY_OAF_SAVE_REPORT_BASE_URL = SERVER + "oaf.adapter.api.save-report";
	String OAF_API_SAVE_REPORT_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAVE_REPORT_BASE_URL);

	// Save Reminder Audit
	String KEY_OAF_SAVE_REMINDER_AUDIT_BASE_URL = SERVER + "oaf.adapter.api.save-reminder-audit";
	String OAF_API_SAVE_REMINDER_AUDIT_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_SAVE_REMINDER_AUDIT_BASE_URL);

	// Save Reminder Audit
	String KEY_OAF_REMINDER_AUDIT_ENABLE = SERVER + "oaf.reminder.audit.enable";
	String OAF_REMINDER_AUDIT_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_REMINDER_AUDIT_ENABLE);

	// SAP start
	String KEY_OAF_SERVICE_SAP_SEND = SERVER + "oaf.service.sap.send";
	String OAF_SAP_SERVICE_SAP_SEND = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SERVICE_SAP_SEND);

	String KEY_OAF_SERVICE_SAP_FAIL_RETRY_ENABLE = SERVER + "oaf.service.sap.fail.retry.enable";
	String OAF_SERVICE_SAP_FAIL_RETRY_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SERVICE_SAP_FAIL_RETRY_ENABLE);
	
	String KEY_OAF_SERVICE_SAP_FAIL_RETRY_INTERVAL_SECONDS = SERVER + "oaf.service.sap.fail.retry.interval.seconds";
	String OAF_SERVICE_SAP_FAIL_RETRY_INTERVAL_SECONDS = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SERVICE_SAP_FAIL_RETRY_INTERVAL_SECONDS);
	
	String KEY_OAF_SERVICE_SAP_FAIL_RETRY_LIMIT = SERVER + "oaf.service.sap.fail.retry.limit";
	String OAF_SERVICE_SAP_FAIL_RETRY_LIMIT = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SERVICE_SAP_FAIL_RETRY_LIMIT);
	
	// SAP
	// Statues Start
	String INIT_STATUS_LABELS_FTIME = "initStatusLabelsFirstTime";
	String KEY_OAF_APPROVAL_STATUS_REJECTED = SERVER + "oaf.approval.status.rejected";
	String OAF_APPROVAL_STATUS_REJECTED_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_REJECTED);
	String KEY_OAF_APPROVAL_STATUS_APPROVED = SERVER + "oaf.approval.status.approved";
	String OAF_APPROVAL_STATUS_APPROVED_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_APPROVED);
	String KEY_OAF_APPROVAL_STATUS_SKIP_APPROVAL = SERVER + "oaf.approval.status.skip.approval";
	String OAF_APPROVAL_STATUS_SKIP_APPROVAL_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_SKIP_APPROVAL);
	
	String KEY_OAF_APPROVAL_STATUS_AUTO_APPROVAL = SERVER + "oaf.approval.status.auto.approval";
	String OAF_APPROVAL_STATUS_AUTO_APPROVAL_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_AUTO_APPROVAL);
	
	String KEY_OAF_APPROVAL_STATUS_AUTO_REJECTION = SERVER + "oaf.approval.status.auto.rejection";
	String OAF_APPROVAL_STATUS_AUTO_REJECTION_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_AUTO_REJECTION);
	
	String KEY_OAF_APPROVAL_STATUS_FINAL_COMPLETED = SERVER + "oaf.approval.status.final.completed";
	String OAF_APPROVAL_STATUS_FINAL_COMPLETED_PROPERTY = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_APPROVAL_STATUS_FINAL_COMPLETED);
	String LEVEL1_APPROVAL1_STATUS = "level1Approval1Status";
	String LEVEL1_APPROVAL2_STATUS = "level1Approval2Status";
	String LEVEL2_APPROVAL1_STATUS = "level2Approval1Status";
	String LEVEL2_APPROVAL2_STATUS = "level2Approval2Status";
	String LEVEL3_APPROVAL1_STATUS = "level3Approval1Status";
	String LEVEL4_APPROVAL1_STATUS = "level4Approval1Status";
	String LEVEL5_APPROVAL1_STATUS = "level5Approval1Status";
	String LEVEL6_APPROVAL1_STATUS = "level6Approval1Status";
	String LEVEL7_APPROVAL1_STATUS = "level7Approval1Status";
	// Execution variables key for statues
	String OAF_APPROVAL_STATUS_APPROVED = "oafApprovalStatusApproved";
	String OAF_APPROVAL_STATUS_REJECTED = "oafApprovalStatusRejected";
	String OAF_APPROVAL_STATUS_SKIP = "oafApprovalStatusSkip";
	String OAF_APPROVAL_STATUS_AUTO_APPROVED = "oafApprovalStatusAutoApproved";
	String OAF_APPROVAL_STATUS_AUTO_REJECTED = "oafApprovalStatusAutoRejected";
	String OAF_APPROVAL_STATUS_FINAL_COMPLETED = "oafApprovalStatusFinalCompleted";

	// Statues End

	// Auto approval enable_disable

	String OAF_AUTO_APPROVAL_LEVEL1_APPROVAL1_ENABLE = "oafAutoApprovalLevel1Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL1_APPROVAL2_ENABLE = "oafAutoApprovalLevel1Approval2_EN";
	String OAF_AUTO_APPROVAL_LEVEL2_APPROVAL1_ENABLE = "oafAutoApprovalLevel2Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL2_APPROVAL2_ENABLE = "oafAutoApprovalLevel2Approval2_EN";
	String OAF_AUTO_APPROVAL_LEVEL3_APPROVAL1_ENABLE = "oafAutoApprovalLevel3Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL4_APPROVAL1_ENABLE = "oafAutoApprovalLevel4Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL5_APPROVAL1_ENABLE = "oafAutoApprovalLevel5Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL6_APPROVAL1_ENABLE = "oafAutoApprovalLevel6Approval1_EN";
	String OAF_AUTO_APPROVAL_LEVEL7_APPROVAL1_ENABLE = "oafAutoApprovalLevel7Approval1_EN";

	// Auto Approval Read Proerty Start
	// Minute
	String KEY_OAF_AUTO_APPROVAL_MIN_ENALBLE = SERVER + "oaf.auto.approval.min.enable";
	String OAF_AUTO_APPROVAL_MIN_ENALBLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_MIN_ENALBLE);
	String KEY_OAF_AUTO_APPROVAL_MIN_VALUE = SERVER + "oaf.auto.approval.min.value";
	String OAF_AUTO_APPROVAL_MIN_VALUE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_MIN_VALUE);
	// Hour
	String KEY_OAF_AUTO_APPROVAL_HOUR_ENALBLE = SERVER + "oaf.auto.approval.hour.enable";
	String OAF_AUTO_APPROVAL_HOUR_ENALBLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_HOUR_ENALBLE);
	String KEY_OAF_AUTO_APPROVAL_HOUR_VALUE = SERVER + "oaf.auto.approval.hour.value";
	String OAF_AUTO_APPROVAL_HOUR_VALUE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_HOUR_VALUE);

	// Day
	String KEY_OAF_AUTO_APPROVAL_DAY_ENALBLE = SERVER + "oaf.auto.approval.day.enable";
	String OAF_AUTO_APPROVAL_DAY_ENALBLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_DAY_ENALBLE);
	String KEY_OAF_AUTO_APPROVAL_DAY_VALUE = SERVER + "oaf.auto.approval.day.value";
	String OAF_AUTO_APPROVAL_DAY_VALUE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_DAY_VALUE);

	// Weekend
	String KEY_OAF_AUTO_APPROVAL_WEEKEND_DAYS = SERVER + "oaf.auto.approval.weekend.days";
	String OAF_AUTO_APPROVAL_WEEKEND_DAYS = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_AUTO_APPROVAL_WEEKEND_DAYS);

	// Auto Approval Read Proerty End

	// Final Auto reset day duration Start
	String KEY_OAF_FINAL_APPROVAL_RESET_AUTO_CASE_ENABLE = SERVER + "oaf.approval.final.reset.auto.case";
	String OAF_FINAL_APPROVAL_RESET_AUTO_CASE_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_FINAL_APPROVAL_RESET_AUTO_CASE_ENABLE);

	String KEY_OAF_FINAL_APPROVAL_RESET_AUTO_REJECT_ENABLE = SERVER + "oaf.approval.final.reject.auto.case";
	String OAF_FINAL_APPROVAL_RESET_AUTO_REJECT_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_FINAL_APPROVAL_RESET_AUTO_REJECT_ENABLE);

	String KEY_OAF_FINAL_APPROVAL_DAY_RESET_INTERVAL_ENABLE = SERVER + "oaf.approval.final.reset.interval.enable";
	String OAF_FINAL_APPROVAL_RESET_INTERVAL_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_FINAL_APPROVAL_DAY_RESET_INTERVAL_ENABLE);

	String KEY_OAF_FINAL_APPROVAL_DAY_RESET_INTERVAL_DAYS = SERVER + "oaf.approval.final.reset.interval.days";
	String OAF_FINAL_APPROVAL_RESET_INTERVAL_DAYS = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_FINAL_APPROVAL_DAY_RESET_INTERVAL_DAYS);

	String OAF_FINAL_APPROVAL_RESET_COUNTER = "oafFinalApprovalResetCounter";
	
	String IS_FINAL_LEVEL = "isFinalLevel";
	String IS_FINAL_RESET_ENABLE = "isFinalResetEnable";
	String IS_FINAL_AUTO_REJECTION_ENABLE = "isFinalAutoRejectionEnable";
	// Final Auto reset day duration End
	// APW Email Approval URL start
	String KEY_APW_BASE_URL = SERVER + "oaf.apw.base.url";
	String KEY_APW_EMAIL_BASE_URL = SERVER + "oaf.apw.email.approval.url";
	String KEY_APW_TASK_DETAIL = SERVER + "oaf.apw.taskDetail.url";
	String KEY_APW_EMAIL_DOWNLOAD_URL = SERVER + "oaf.apw.email.document.download.url";

	String OAF_APW_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_APW_BASE_URL);
	String OAF_APW_EMAIL_APPRROVAL_URL = OAF_APW_BASE_URL
			+ OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_APW_EMAIL_BASE_URL);
	String OAF_APW_EMAIL_OUTCOME_APPROVE = "/approve";
	String OAF_APW_EMAIL_OUTCOME_REJECT = "/reject";
	String OAF_APW_EMAIL_RETRY_SAPFAIL = "/updatesap";
	String OAF_APW_EMAIL_TASK_VIEW = OAF_APW_BASE_URL + OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_APW_TASK_DETAIL);

	String OAF_APW_EMAIL_DOWNLOAD_URL = OAF_APW_BASE_URL
			+ OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_APW_EMAIL_DOWNLOAD_URL);
	// APW Email Approval URL end

	// EmailNotificaiton start
	String SEND_EMAIL_NOTIFICATION_FOR = "sendEmailNotificationFor";
	String QUOTE_REJECT = "quoteReject";
	String QUOTE_SUBMISSION = "quoteSubmission";
	String QUOTE_FAILURE_SUBMISSION = "quoteFailureSubmission";
	String QUOTE_RESET_CASE_2 = "quoteResetCase2";
	String QUOTE_RESET_CASE_1 = "quoteResetCase1";
	String INITIATOR_APPROVAL_CONFIRMATION = "initiApprovalConfirmation";
	String QUOTE_FINAL_APPROVED = "quoteFinalApproved";
	String QUOTE_FAILURE_FINAL_APPROVED = "quoteFailureFinalApproved";
	String QUOTE_REMINDER_APPROVAL = "quoteReminderApproval";
	String KEY_OAF_EMAIL_SEND = SERVER + "oaf.aps.sendmail";
	String OAF_EMAIL_SEND = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_EMAIL_SEND);

	// EmailNotificaiton end
	
	// Resend Mail Notification Start
	String IS_RESEND_MAIL_LEVEL1_APPROVAL2 ="isResendMailLevel1Approval2";
	String IS_RESEND_MAIL_LEVEL2_APPROVAL1 ="isResendMailLevel2Approval1";
	String IS_RESEND_MAIL_LEVEL2_APPROVAL2 ="isResendMailLevel2Approval2";
	// Resend Mail Notification End
	// Users id Start
	String LEVEL1_APPROVAL1_ID = "level1Approval1";
	String LEVEL1_APPROVAL2_ID = "level1Approval2";
	String LEVEL2_APPROVAL1_ID = "level2Approval1";
	String LEVEL2_APPROVAL2_ID = "level2Approval2";
	String LEVEL3_APPROVAL1_ID = "level3Approval1";
	String LEVEL4_APPROVAL1_ID = "level4Approval1";
	String LEVEL5_APPROVAL1_ID = "level5Approval1";
	String LEVEL6_APPROVAL1_ID = "level6Approval1";
	String LEVEL7_APPROVAL1_ID = "level7Approval1";
	// Users id End

	// PenddingWith Start

	String LEVEL1_PENDING_WITH = "level1PendingWith";
	String CURRENT_LEVEL = "currentLevel";
	String LEVEL2_PENDING_WITH = "level2PendingWith";

	// PendingWith End

	// Users EmailId Start
	String LEVEL1_APPROVAL1_EMAILID = "level1Approval1Email";
	String LEVEL1_APPROVAL2_EMAILID = "level1Approval2Email";
	String LEVEL2_APPROVAL1_EMAILID = "level2Approval1Email";
	String LEVEL2_APPROVAL2_EMAILID = "level2Approval2Email";
	String LEVEL3_APPROVAL1_EMAILID = "level3Approval1Email";
	String LEVEL4_APPROVAL1_EMAILID = "level4Approval1Email";
	String LEVEL5_APPROVAL1_EMAILID = "level5Approval1Email";
	String LEVEL6_APPROVAL1_EMAILID = "level6Approval1Email";
	String LEVEL7_APPROVAL1_EMAILID = "level7Approval1Email";
	// Users EmailId End

	// Level TaskId Start
	String lEVEL1_APPROVAL1_TASK_ID = "taskIdLevel1Approval1";
	String lEVEL1_APPROVAL2_TASK_ID = "taskIdLevel1Approval2";
	String LEVEL2_APPROVAL1_TASK_ID = "taskIdLevel2Approval1";
	String LEVEL2_APPROVAL2_TASK_ID = "taskIdLevel2Approval2";
	String lEVEL3_APPROVAL1_TASK_ID = "taskIdLevel3Approval1";
	String LEVEL4_APPROVAL1_TASK_ID = "taskIdLevel4Approval1";
	String LEVEL5_APPROVAL1_TASK_ID = "taskIdLevel5Approval1";
	String LEVEL6_APPROVAL1_TASK_ID = "taskIdLevel6Approval1";
	String LEVEL7_APPROVAL1_TASK_ID = "taskIdLevel7Approval1";
	// Level Taskid End

	String LEVEL1_APPROVAL1_SKIP_AUTO_APPROVE = "level1Approval1SkipAutoApprove";
	// Users To Approved By Start
	String LEVEL1_APPROVAL1_IS_APPROVED = "isLevel1Approval1Approved";
	String LEVEL1_APPROVAL2_IS_APPROVED = "isLevel1Approval2Approved";
	String LEVEL2_APPROVAL1_IS_APPROVED = "isLevel2Approval1Approved";
	String LEVEL2_APPROVAL2_IS_APPROVED = "isLevel2Approval2Approved";
	String LEVEL3_APPROVAL1_IS_APPROVED = "isLevel3Approval1Approved";
	String LEVEL4_APPROVAL1_IS_APPROVED = "isLevel4Approval1Approved";
	String LEVEL5_APPROVAL1_IS_APPROVED = "isLevel5Approval1Approved";
	String LEVEL6_APPROVAL1_IS_APPROVED = "isLevel6Approval1Approved";
	String LEVEL7_APPROVAL1_IS_APPROVED = "isLevel7Approval1Approved";

	// Users To Approved By End

	// Users To Rejected By Start
	String LEVEL1_APPROVAL1_IS_REJECTED = "isLevel1Approval1Rejected";
	String LEVEL1_APPROVAL2_IS_REJECTED = "isLevel1Approval2Rejected";
	String LEVEL2_APPROVAL1_IS_REJECTED = "isLevel2Approval1Rejected";
	String LEVEL2_APPROVAL2_IS_REJECTED = "isLevel2Approval2Rejected";
	String lEVEL3_APPROVAL1_IS_REJECTED = "isLevel3Approval1Rejected";
	String LEVEL4_APPROVAL1_IS_REJECTED = "isLevel4Approval1Rejected";
	String LEVEL5_APPROVAL1_IS_REJECTED = "isLevel5Approval1Rejected";
	String LEVEL6_APPROVAL1_IS_REJECTED = "isLevel6Approval1Rejected";
	String LEVEL7_APPROVAL1_IS_REJECTED = "isLevel7Approval1Rejected";

	String LEVEL1_APPROVAL1_IS_REJECTED_FLAG = "level1Approve1RejectedFlag";
	String LEVEL1_APPROVAL2_IS_REJECTED_FLAG = "level1Approve2RejectedFlag";
	String LEVEL2_APPROVAL1_IS_REJECTED_FLAG = "level2Approve1RejectedFlag";
	String LEVEL2_APPROVAL2_IS_REJECTED_FLAG = "level2Approve2RejectedFlag";

	// Users To Rejected By End

	// Password Authentication Start
	String AUTHORIZATION = "Authorization";
	String BASIC = "Basic ";
	// Password Authentication End

	// SMTP Email Config Start
	String KEY_MAIL_UTF_8 = "UTF-8";
	String KEY_MAIL_SMTP = SERVER + "oaf.smtp.email.smtp";
	String KEY_MAIL_HOST = SERVER + "oaf.smtp.email.host";
	String KEY_MAIL_PROTOCOL = SERVER + "oaf.smtp.email.transport.protocol";
	String KEY_MAIL_START_TTLS = SERVER + "oaf.smtp.email.start.ttls";
	String KEY_MAIL_PORT = SERVER + "oaf.smtp.email.start.port";
	String KEY_MAIL_FROM = SERVER + "oaf.smtp.email.from";
	String KEY_MAIL_CONTENT_TYPE = "Content-type";
	String KEY_MAIL_CONTENT_VALUE = "text/HTML; charset=UTF-8";
	String KEY_MAIL_RESOURCE_PATH = "/mail-content-templates";
	// SMTP Email Config End

	// ACS Config Start
	String KEY_ACS_BASE_URL = SERVER + "oaf.acs.base.url";
	String ACS_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_BASE_URL);
	// acs site name
	String KEY_ACS_SITE_NAME = SERVER + "oaf.acs.site.name";
	String ACS_SITE_NAME = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_SITE_NAME);
	String KEY_ACS_MAKE_FOLDER_URL = SERVER + "oaf.acs.createfolder.url";
	String ACS_MAKE_FOLDER_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_MAKE_FOLDER_URL);
	String KEY_ACS_CREATE_FOLDER_URL = SERVER + "oaf.acs.createfolder.url";
	String ACS_CREATE_FOLDER_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_CREATE_FOLDER_URL);
	String KEY_ACS_GET_NODEREF_URL = SERVER + "oaf.acs.getnoderef.url";
	String ACS_GET_NODEREF_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_GET_NODEREF_URL);
	String KEY_ACS_UPLOAD_CONTENT_URL = SERVER + "oaf.acs.upload.url";
	String ACS_UPLOAD_CONTENT_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_UPLOAD_CONTENT_URL);
	// ACS User Credential
	String KEY_ACS_USERNAME = SERVER + "oaf.acs.username";
	String ACS_USERNAME = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_USERNAME);
	String KEY_ACS_PASSWORD = SERVER + "oaf.acs.password";
	String ACS_PASSWORD = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_PASSWORD);
	String KEY_ACS_NODE_URL = SERVER + "oaf.acs.nodeurl";
	String ACS_NODE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_NODE_URL);
	String KEY_ACS_CONTENT_URL = SERVER + "oaf.acs.contenturl";
	String ACS_CONTENT_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_CONTENT_URL);
	String KEY_ACS_DOWNLOAD_CONTENT_URL = SERVER + "oaf.acs.downloadcontenturl";
	String ACS_DOWNLOAD_CONTENT_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_DOWNLOAD_CONTENT_URL);

	String ACS_CONTENT_DISPOSITION = "Content-Disposition";
	String ACS_ATTCHMENT = "attachment; ";
	String ACS_FILE_NAME = "filename=";
	String ACS_DOCUMENT_LIBRARY = "documentLibrary";
	String CONTENT_TYPE = "Content-type";
	String CONTENT_JSON_VALUE = "application/json";
	String ACS_FOLDER_CONTENT_TYPE = "cm:content";
	String ACS_DESCRIPTION_TEXT = "testfile";
	String ACS_DESCRIPTION = "description";
	String ACS_SITEID = "siteid";
	String ACS_UPLOAD_DIRECTORY = "uploaddirectory";
	String ACS_CONTAINERID = "containerid";
	String ACS_FILEDATA = "filedata";
	String ACS_CONTENT_TYPE_PDF = "application/pdf";
	String ACS_PDF_GENERATED_NODE_ID = "genPdfNodeId";	
	// ACS Config End

	String QUOTATION = "quotation";
	// Email Content common keys start
	String MAIL_TEMPLATE_CONTENT_INQUIRY = "inquiry";
	String MAIL_TEMPLATE_CONTENT_QUOTE = "quote";
	String MAIL_TEMPLATE_CONTENT_SALEOFFICE = "salesOffice";
	String MAIL_TEMPLATE_CONTENT_CLIENT = "client";
	String MAIL_TEMPLATE_CONTENT_PROJECT = "project";
	String MAIL_TEMPLATE_CONTENT_LOCATION = "location";
	String MAIL_TEMPLATE_CONTENT_WEIGHT = "weightMt";
	String MAIL_TEMPLATE_CONTENT_SALESCOMMENTS = "salesComments";
	String MAIL_TEMPLATE_CONTENT_REVISION = "revision";
	String MAIL_TEMPLATE_CONTENT_DQ = "dq";
	String MAIL_TEMPLATE_CONTENT_ISAPPROVALEMAIL = "isApprovalEmail";
	String MAIL_TEMPLATE_CONTENT_ISSAPFAILEMAIL = "isSapFailEmail";
	String MAIL_TEMPLATE_CONTENT_SAPFAIL_REASON = "sapFailReason";
	String MAIL_TEMPLATE_CONTENT_APPROVETASK_URL = "approveTaskUrl";
	String MAIL_TEMPLATE_CONTENT_REJECTTASK_URL = "rejectTaskUrl";
	String MAIL_TEMPLATE_CONTENT_VIEWTASK_URL = "viewTaskUrl";
	String MAIL_TEMPLATE_CONTENT_RETRY_SAP_URL = "retrySapUrl";
	String MAIL_TEMPLATE_CONTENT_APPROVALINFO = "approvalInformaiton";
	String MAIL_TEMPLATE_CONTENT_APPROVALINFO_JSON = "approvalInformaitonJson";
	String MAIL_TEMPLATE_CONTENT_APPROVALPENDINGINFO = "approvalPendingInformation";
	String MAIL_TEMPLATE_CONTENT_APPROVALPENDINGINFO_JSON = "approvalPendingInformationJson";
	String MAIL_TEMPLATE_CONTENT_APPROVALPENDINGNAME = "pendingApproval";
	String MAIL_TEMPLATE_CONTENT_APPROVALDESIGNATION = "designation";
	String MAIL_TEMPLATE_CONTENT_DAYSELAPSED = "daysElapsedFromSubmission";
	String MAIL_TEMPLATE_CONTENT_APPROVEDBY = "approvedBy";
	String MAIL_TEMPLATE_CONTENT_REJECTEDBY = "rejectedBy";
	String MAIL_TEMPLATE_CONTENT_APPROVALCOMMENT = "approvalComment";
	String MAIL_TEMPLATE_CONTENT_APPROVAEDDATE = "approvedDate";
	String MAIL_TEMPLATE_CONTENT_DOWNLOAD_LINKS = "downloadLinks";
	String MAIL_TEMPLATE_CONTENT_DOWNLOAD_LINKS_JSON = "downloadLinksJson";
	String MAIL_TEMPLATE_CONTENT_VKBUR = "vkbur";
	String MAIL_TEMPLATE_CONTENT_REGION = "regionFlag";
	String MAIL_TEMPLATE_CONTENT_MARGIN_CPP = "marginCPP";
	String MAIL_TEMPLATE_CONTENT_ENG_SERVICES = "engineeringServices";
	String MAIL_TEMPLATE_CONTENT_DECISION_TABLE_OUTCOME = "decOutcome";
	String MAIL_TEMPLATE_CONTENT_ISPDF_DOWNLOAD_ENABLE = "isPdfDownloadEnable";
	String MAIL_TEMPLATE_CONTENT_PDF_DOWNLOAD_LINK = "pdfDownloadLink";
	String MAIL_TEMPLATE_CONTENT_PDF_NAME = "pdfFileName";
	// Email Content common keys end
	// Mail Template Names Start
	String OAF_MAIL_TEMPLATE_1_SUBMISSION_OF_QUOTE = "oaf_mail_1_submission_of_quote.ftl";
	String OAF_MAIL_TEMPLATE_2_FAILURE_OF_SUBMISSION_OF_QUOTE = "oaf_mail_2_failure_of_submission_of_quote.ftl";
	String OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL = "oaf_mail_3_submission_of_approval.ftl";
	String OAF_MAIL_TEMPLATE_4_FOR_CONFIRMATION_OF_APPROVAL = "oaf_mail_4_confirmation_of_approval.ftl";
	String OAF_MAIL_TEMPLATE_5_REMINDER_FOR_APPROVAL = "oaf_mail_5_reminder_for_approval.ftl";
	String OAF_MAIL_TEMPLATE_6_RESET_OF_APPROVAL_CASE2 = "oaf_mail_6_reset_of_approval_case_2.ftl";
	String OAF_MAIL_TEMPLATE_7_RESET_OF_APPROVAL_CASE1 = "oaf_mail_7_reset_of_approval_case_1.ftl";
	String OAF_MAIL_TEMPLATE_8_FOR_REJECTION_OF_QUOTE = "oaf_mail_8_rejection_of_quote.ftl";
	String OAF_MAIL_TEMPLATE_9_FOR_FINAL_APPROVAL = "oaf_mail_9_final_approval.ftl";
	String OAF_MAIL_TEMPLATE_10_FAILURE_FOR_FINAL_APPROVAL = "oaf_mail_10_failure_of_final_approval.ftl";
	String OAF_MAIL_TEMPLATE_11_FAILURE_FOR_DOCUMENT_ATTACHMENT = "oaf_mail_11_failure_for_document_attachment.ftl";
	String OAF_PDF_GENERATOR_FTL = "oaf_pdfGenerator.ftl";
	// Mail Template Names End

	// Reminder start

	String REMINDER_APPROVAL_EMAIL_ADDRESS = "reminderApprovalEmailAddress";
	String IS_REMINDER_ENABLED = "isReminderEnabled";
	// Reminder End

	// OAF Users Allow Start
	String LEVEL1_APPROVAL1_ALLOW = "level1Approval1Allow";
	String LEVEL1_APPROVAL2_ALLOW = "level1Approval2Allow";
	String LEVEL2_APPROVAL1_ALLOW = "level2Approval1Allow";
	String LEVEL2_APPROVAL2_ALLOW = "level2Approval2Allow";
	String LEVEL3_APPROVAL1_ALLOW = "level3Approval1Allow";
	String LEVEL4_APPROVAL1_ALLOW = "level4Approval1Allow";
	String LEVEL5_APPROVAL1_ALLOW = "level5Approval1Allow";
	String LEVEL6_APPROVAL1_ALLOW = "level6Approval1Allow";
	String LEVEL7_APPROVAL1_ALLOW = "level7Approval1Allow";
	
	// OAF Users Allow End
	// OAF Users Name start
	String LEVEL1_APPROVAL1_NAME = "level1Approval1Name";
	String LEVEL1_APPROVAL2_NAME = "level1Approval2Name";
	String LEVEL2_APPROVAL1_NAME = "level2Approval1Name";
	String LEVEL2_APPROVAL2_NAME = "level2Approval2Name";
	String LEVEL3_APPROVAL1_NAME = "level3Approval1Name";
	String LEVEL4_APPROVAL1_NAME = "level4Approval1Name";
	String LEVEL5_APPROVAL1_NAME = "level5Approval1Name";
	String LEVEL6_APPROVAL1_NAME = "level6Approval1Name";
	String LEVEL7_APPROVAL1_NAME = "level7Approval1Name";
	// OAF Users Name end

	// OAF Users Comments start
	String LEVEL1_APPROVAL1_COMMENT = "level1Approval1Comment";
	String LEVEL1_APPROVAL2_COMMENT = "level1Approval2Comment";
	String LEVEL2_APPROVAL1_COMMENT = "level2Approval1Comment";
	String LEVEL2_APPROVAL2_COMMENT = "level2Approval2Comment";
	String LEVEL3_APPROVAL1_COMMENT = "level3Approval1Comment";
	String LEVEL4_APPROVAL1_COMMENT = "level4Approval1Comment";
	String LEVEL5_APPROVAL1_COMMENT = "level5Approval1Comment";
	String LEVEL6_APPROVAL1_COMMENT = "level6Approval1Comment";
	String LEVEL7_APPROVAL1_COMMENT = "level7Approval1Comment";

	// Comment for Sales
	String LEVEL1_APPROVAL1_SALES_COMMENT = "level1Approval1SalesComment";
	String LEVEL1_APPROVAL2_SALES_COMMENT = "level1Approval2SalesComment";
	String LEVEL2_APPROVAL1_SALES_COMMENT = "level2Approval1SalesComment";
	String LEVEL2_APPROVAL2_SALES_COMMENT = "level2Approval2SalesComment";
	String LEVEL3_APPROVAL1_SALES_COMMENT = "level3Approval1SalesComment";
	String LEVEL4_APPROVAL1_SALES_COMMENT = "level4Approval1SalesComment";
	String LEVEL5_APPROVAL1_SALES_COMMENT = "level5Approval1SalesComment";
	String LEVEL6_APPROVAL1_SALES_COMMENT = "level6Approval1SalesComment";
	String LEVEL7_APPROVAL1_SALES_COMMENT = "level7Approval1SalesComment";

	String NO_COMMENTS = "No Comments";
	String ADD_COMMENTS = "addComments";
	String ADD_SALES_COMMENTS = "addSalesComments";
	// OAF View Comment Variable
	String VIEWCOMMENTS = "viewComments";
	String OAF_VIEW_PENDING_APPROVALS = "oafviewpendingapproval";
	String ISFROMREJECTSCENARIO = "isFromRejectScenario";
	String PREVIOUSCOMMENTS = "previousComments";
	String ID = "Id";

	// OAF Users Comments End

	// OAF Users Designation key start
	String LEVEL1_APPROVAL1_DESIGNATION_KEY = "level1Approval1Designation";
	String LEVEL1_APPROVAL2_DESIGNATION_KEY = "level1Approval2Designation";
	String LEVEL2_APPROVAL1_DESIGNATION_KEY = "level2Approval1Designation";
	String LEVEL2_APPROVAL2_DESIGNATION_KEY = "level2Approval2Designation";
	String LEVEL3_APPROVAL1_DESIGNATION_KEY = "level3Approval1Designation";
	String LEVEL4_APPROVAL1_DESIGNATION_KEY = "level4Approval1Designation";
	String LEVEL5_APPROVAL1_DESIGNATION_KEY = "level5Approval1Designation";
	String LEVEL6_APPROVAL1_DESIGNATION_KEY = "level6Approval1Designation";
	String LEVEL7_APPROVAL1_DESIGNATION_KEY = "level7Approval1Designation";
	// OAF Users Designation key end

	// OAF Users Designation start
	String LEVEL1_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level1.approval1");
	String LEVEL1_APPROVAL2_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level1.approval2");
	String LEVEL2_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level2.approval1");
	String LEVEL2_APPROVAL2_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level2.approval2");
	String LEVEL3_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level3.approval1");
	String LEVEL4_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level4.approval1");
	String LEVEL5_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level5.approval1");
	String LEVEL6_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level6.approval1");
	String LEVEL7_APPROVAL1_DESIGNATION = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(SERVER + "oaf.approval.level7.approval1");
	// OAF Users Designation end

	// OAF User Task Date start
	String LEVEL1_APPROVAL1_DATE = "level1Approval1Date";
	String LEVEL1_APPROVAL2_DATE = "level1Approval2Date";
	String LEVEL2_APPROVAL1_DATE = "level2Approval1Date";
	String LEVEL2_APPROVAL2_DATE = "level2Approval2Date";
	String LEVEL3_APPROVAL1_DATE = "level3Approval1Date";
	String LEVEL4_APPROVAL1_DATE = "level4Approval1Date";
	String LEVEL5_APPROVAL1_DATE = "level5Approval1Date";
	String LEVEL6_APPROVAL1_DATE = "level6Approval1Date";
	String LEVEL7_APPROVAL1_DATE = "level7Approval1Date";
	// OAF User Task Date end

	// Decision Variable Start
	String VKBUR = "vkbur";
	String MARGINCPP = "marginCPP";
	String IS_LEVEL_USERS_DECISIONTABLE_FAILED="isLevelUsersDecisionFailed";
	String REGION_FLAG="regionFlag";
	String PROJECTMT = "projmt";
	String ENGINEERING_SERVICES = "engineeringServices";
	String YKES = "YKES";
	// Decisoin Variable End
	// Attachment Variables start
	String ATTACHMENTS = "attachments";
	String CATEGORY = "category";
	String CATEGORYA = "A";
	String CATEGORYB = "B";
	String CATEGORYC = "C";
	String OAFFORM = "oafForm";
	String NODEREF = "noderef";
	String DOCNAME = "docName";
	String DOCUMENT_DOWNLOAD_LINK = "documentDownloadLink";
	String DOCUMENT = "document";
	String DOWNLOADURL = "downloadURL";

	String QUOTE_TYPE_PENDING = "Pending";
	String QUOTE_TYPE_APPROVAL = "Approval";
	String QUOTE_TYPE_REJECTION = "Rejection";

	// Attachment Variables end

	// Reminder email variables start
	String REMINDER_EMAIL_TO = "email_to";
	String REMINDER_EMAIL_CC = "email_cc";
	String REMINDER_EMAIL_BCC = "email_bcc";
	String REMINDER_EMAIL_SUBJECT = "email_subject";
	String REMINDER_EMAIL_DATE = "email_date";
	String EMAIL_TASK_ID = "reminderTaskId";
	String REMINDER_EMAIL_TASK_ID = "email_task_id";
	String REMINDER_EMAIL_TASK_NAME = "email_task_name";
	String REMINDER_EMAIL_PROCESS_ID = "email_process_id";
	String REMINDER_EMAIL_PROCESS_NAME = "email_process_name";
	String REMINDER_EMAIL_ID = "email_id";
	// Reminder email variables end

	// Comment Variables Start
	String KEY_CMT_NAME = "name";
	String KEY_CMT_DESIGNATION = "designation";
	String KEY_CMT_DATE = "date";
	String KEY_CMT_COMMENTS = "comments";
	String KEY_CMT_COMMENTS_FOR_SALES = "commentsForSales";
	String COMMENTJSON = "commentJson";
	String KEY_CMT_STATUS = "status";
	// Comment Variables End
	String APPROVED_STATUS = "Approved";
	String REJECTED_STATUS = "Rejected";
	String TRUE = "True";
	String FALSE = "False";
	
	// Default date time format
	String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

	// String COMMENT_COUNTETR="commentCounter";

	String TEMP_INIT_MAIL_IS_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(SERVER + "oaf.temp.initmail.enable");
	String TEMP_INIT_MAIL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(SERVER + "oaf.temp.initmail");

	// To find the attachments based on the task owner allowed categories
	String USER_CATEGORY = "user_category";

	// Is PDF Generate true
	String KEY_OAF_PDF_GENERATE_ENABLE = SERVER + "oaf.pdf.generate.enable";
	String OAF_PDF_GENERATE_ENABLE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_PDF_GENERATE_ENABLE);

	String KEY_OAF_PDF_MAIL_SEND = SERVER + "oaf.pdf.mail.send";
	String OAF_PDF_MAIL_SEND = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_PDF_MAIL_SEND);

	String KEY_OAF_PDF_LINK_DOWNLOAD = SERVER + "oaf.pdf.mail.download.link.enable";
	String OAF_PDF_LINK_DOWNLOAD = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_PDF_LINK_DOWNLOAD);

	String CONTAINER="Container";
	String TRUCK="Truck";
	String KEY_PDF_NATURE = SERVER + "oaf.pdf.nature.value";
	String PDF_NATURE = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_PDF_NATURE);
	// SAP Properties
	String KEY_OAF_SAP_SERVICE_BASE_URL = SERVER + "oaf.sap.service.base-url";
	String OAF_SAP_SERVICE_BASE_URL = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_BASE_URL);
	String KEY_OAF_SAP_SERVICE_URL_CONTEXT = SERVER + "oaf.sap.service.url-context";
	String OAF_SAP_SERVICE_URL_CONTEXT = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_URL_CONTEXT);
	String KEY_OAF_SAP_SERVICE_URL_QUERY_PARAM = SERVER + "oaf.sap.service.url-query-param";
	String OAF_SAP_SERVICE_URL_QUERY_PARAM = OAF_RESOURCE_BUNDLE_INSTANCE
			.getString(KEY_OAF_SAP_SERVICE_URL_QUERY_PARAM);
	String KEY_OAF_SAP_SERVICE_USERNAME = SERVER + "oaf.sap.service.username";
	String OAF_SAP_SERVICE_USERNAME = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_USERNAME);
	String KEY_OAF_SAP_SERVICE_PASSWORD = SERVER + "oaf.sap.service.password";
	String OAF_SAP_SERVICE_PASSWORD = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_OAF_SAP_SERVICE_PASSWORD);

	String SAP_APPROVED = "A";
	String SAP_REJECTED = "R";
	String SAP_SERVICE_CALL_FAIL = "sapServiceCallFail";
	
	String SAP_SERVICE_CALL_RESPONSE = "sapServiceCallResponse";
}
