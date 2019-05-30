package com.activiti.extension.oaf.freemarker.task.service;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFAttachedDocumentList;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.freemarker.task.util.OAFCommonUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFEmailAttachmentsUtill;
import com.activiti.extension.oaf.freemarker.task.util.OAFEmailTemplateUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFFreeMarkerUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFReportUtil;

/**
 * This class is responsible to send email notification based on the type of
 * email case This is APS task listener class
 * @author Keval Bhatt
 *
 */
public class EmailNotificationTaskListener implements TaskListener, OAFConstants {
	private static Logger logger = LoggerFactory.getLogger(EmailNotificationTaskListener.class);
	private static OAFEmailTemplateContent emailTemplateContent;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		if (execution.getVariable(OAFFORM) != null) {
			String oafForm = (String) delegateTask.getExecution().getVariable(OAFFORM);
			OAFCommonUtil.changeTaskNames(delegateTask, oafForm);

			// Init statues, statues only will update one time
			OAFCommonUtil.initApprovalStatusOneTime(delegateTask.getExecution(), oafForm);

		}
		String initMail = "";
		// Changing Task Name End

		Boolean isEmailSend = false;
		Boolean isResendEmail = false;
		if (!OAF_EMAIL_SEND.isEmpty() && OAF_EMAIL_SEND != null && OAF_EMAIL_SEND.equalsIgnoreCase("True") && execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR)!=null) {
			isEmailSend = true;
		}
		// If mail resend request then the task is completed or not if completed then do not allow to send the mail
		if(isEmailSend) {
			String resendTaskId = "";
			// Level1 Resend Check
			boolean RESEND__MAIL_LEVEL1_APP2 = (execution.getVariable(IS_RESEND_MAIL_LEVEL1_APPROVAL2)!=null && (Boolean)execution.getVariable(IS_RESEND_MAIL_LEVEL1_APPROVAL2))?true:false;
			boolean RESEND__MAIL_LEVEL2_APP1 = (execution.getVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL1)!=null && (Boolean)execution.getVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL1))?true:false;
			boolean RESEND__MAIL_LEVEL2_APP2 = (execution.getVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL2)!=null && (Boolean)execution.getVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL2))?true:false;
			if(RESEND__MAIL_LEVEL1_APP2) {
				resendTaskId = String.valueOf(execution.getVariable(lEVEL1_APPROVAL2_TASK_ID));
				if(OAFCommonUtil.isTaskCompleted(resendTaskId)) {
					isEmailSend=false;
				}else {
					logger.debug("EmailNotificationTaskListener.notify() :: Resending email for case "+LEVEL1_APPROVAL2_ID);
					isEmailSend=true;
					isResendEmail = true;
				}
				execution.removeVariable(IS_RESEND_MAIL_LEVEL1_APPROVAL2);
			}else if(RESEND__MAIL_LEVEL2_APP1) {
				resendTaskId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL1_TASK_ID));
				if(OAFCommonUtil.isTaskCompleted(resendTaskId)) {
					isEmailSend=false;
				}else {
					logger.debug("EmailNotificationTaskListener.notify() :: Resending email for case "+LEVEL2_APPROVAL1_ID);
					isEmailSend=true;
					isResendEmail = true;
				}
				execution.removeVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL1);
			}
			else if(RESEND__MAIL_LEVEL2_APP2) {
				resendTaskId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL2_TASK_ID));
				if(OAFCommonUtil.isTaskCompleted(resendTaskId)) {
					isEmailSend=false;
				}else {
					System.out.println("hello");
					logger.debug("EmailNotificationTaskListener.notify() :: Resending email for case "+LEVEL2_APPROVAL2_ID);
					isEmailSend=true;
					isResendEmail = true;
				}
				execution.removeVariable(IS_RESEND_MAIL_LEVEL2_APPROVAL2);
			}
		}
		
		if (isEmailSend) {
			OAFEmail email = new OAFEmail();
			String taskId;
			String userId;
			String approveTaskUrl;
			String rejectTaskUrl;
			String viewTaskUrl;
			JSONObject converAttchmentListIntoJson = null;
			OAFAttachedDocumentList oafAttachedDocumentIdentification = null;
			String mailTemplateProcessedContent = "";
			JSONObject attachmentLinkForSpecificUser = null;
			JSONObject downloadLinks = null;
			List<JSONObject> gettingListOfAttachmentLinkByCategory = null;
			boolean isNeedToUpdateReport = false;
			
			emailTemplateContent = OAFEmailTemplateUtil.initMail(execution, emailTemplateContent);
			
			// Init Email address			
			if (TEMP_INIT_MAIL_IS_ENABLE != null && Boolean.parseBoolean(TEMP_INIT_MAIL_IS_ENABLE) == false) {
				initMail = emailTemplateContent.getInitMail();
				logger.debug("EmailNotificationTaskListener.notify() :: Init email address from JSON Field :: "
						+ initMail);

			} else {

				initMail = TEMP_INIT_MAIL;
				logger.debug("EmailNotificationTaskListener.notify() :: Init email address from property file :: "
						+ initMail);
			}
			// Init Email address end


			// oafAttachedDocumentIdentification=OAFEmailAttachmentsUtill.gettingListOfAttchmentByCategory(execution.getVariable(OAFFORM).toString(),
			// execution);

			StringBuilder pendingWith = new StringBuilder();

			// Check if email is for sap update failure if it does then do not set download links
			if(!String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR)).equalsIgnoreCase(QUOTE_FAILURE_FINAL_APPROVED)) {
				// Creating DownloadLinks start
				if (execution.getVariable(OAFFORM).toString() != null) {
					logger.debug(
							"EmailNotificationTaskListener.notify()  :: Getting list of attachments links by category ");
					gettingListOfAttachmentLinkByCategory = OAFEmailAttachmentsUtill.gettingListOfAttachmentLinkByCategory(
							execution.getVariable(OAFFORM).toString(), execution, delegateTask.getName());
				}
				if (gettingListOfAttachmentLinkByCategory != null) {
					logger.debug(
							"EmailNotificationTaskListener.notify()  :: Getting list of attachments by specific user ");
					attachmentLinkForSpecificUser = OAFEmailAttachmentsUtill
							.getAttachmentLinkForSpecificUser(gettingListOfAttachmentLinkByCategory, execution);
				}
				if (attachmentLinkForSpecificUser != null) {
					logger.debug("EmailNotificationTaskListener.notify()  :: Getting download links of attachments ");
					downloadLinks = OAFEmailAttachmentsUtill.getLinks(attachmentLinkForSpecificUser);
				}
				if (downloadLinks != null) {
					logger.debug("EmailNotificationTaskListener.notify()  :: setting download links in to email template ");
					emailTemplateContent.setDownloadLinks(downloadLinks);
				}
				// Creating DownloadLinks end

			}
			switch (String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR))) {
			case LEVEL1_APPROVAL1_ID:
				logger.debug("In " + LEVEL1_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL1_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(lEVEL1_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {

					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL1_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel1GeneralSMDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

							if (execution.getVariable(LEVEL1_PENDING_WITH) != null) {
								pendingWith.append(execution.getVariable(LEVEL1_PENDING_WITH));
								pendingWith.append(", " + execution.getVariable(LEVEL1_APPROVAL1_NAME));
							} else {
								pendingWith.append(execution.getVariable(LEVEL1_APPROVAL1_NAME));
							}
							execution.setVariable(LEVEL1_PENDING_WITH, pendingWith.toString());

						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL1_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
						isNeedToUpdateReport = true;

					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}

				break;
			case LEVEL1_APPROVAL2_ID:
				logger.debug("In " + LEVEL1_APPROVAL2_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL1_APPROVAL2_ID));
				taskId = String.valueOf(execution.getVariable(lEVEL1_APPROVAL2_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);
				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL1_APPROVAL2_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel1SeniorGMDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

							if (execution.getVariable(LEVEL1_PENDING_WITH) != null) {
								pendingWith.append(execution.getVariable(LEVEL1_PENDING_WITH));
								pendingWith.append(", " + execution.getVariable(LEVEL1_APPROVAL2_NAME));
							} else {
								pendingWith.append(execution.getVariable(LEVEL1_APPROVAL2_NAME));
							}
							execution.setVariable(LEVEL1_PENDING_WITH, pendingWith.toString());
						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL1_APPROVAL2_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;
			case LEVEL2_APPROVAL1_ID:
				logger.debug("In " + LEVEL2_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval

				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);
				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL2_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel2CreditMDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

							if (execution.getVariable(LEVEL2_PENDING_WITH) != null) {
								pendingWith.append(execution.getVariable(LEVEL2_PENDING_WITH));
								pendingWith.append(", " + execution.getVariable(LEVEL2_APPROVAL1_NAME));
							} else {
								pendingWith.append(execution.getVariable(LEVEL2_APPROVAL1_NAME));
							}
							execution.setVariable(LEVEL2_PENDING_WITH, pendingWith.toString());

						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL2_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
					isNeedToUpdateReport = true;
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}

				break;
			case LEVEL2_APPROVAL2_ID:
				logger.debug("In " + LEVEL2_APPROVAL2_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL2_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL2_APPROVAL2_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);

				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL2_APPROVAL2_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel2SeniorFADocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

							if (execution.getVariable(LEVEL2_PENDING_WITH) != null) {
								pendingWith.append(execution.getVariable(LEVEL2_PENDING_WITH));
								pendingWith.append(", " + execution.getVariable(LEVEL2_APPROVAL2_NAME));
							} else {
								pendingWith.append(execution.getVariable(LEVEL2_APPROVAL2_NAME));
							}
							execution.setVariable(LEVEL2_PENDING_WITH, pendingWith.toString());

						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL2_APPROVAL2_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;
			case LEVEL3_APPROVAL1_ID:
				logger.debug("In " + LEVEL3_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL3_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(lEVEL3_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);
				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);

				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL3_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel2SeniorFMDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);
							pendingWith.append(execution.getVariable(LEVEL3_APPROVAL1_NAME).toString());

						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL3_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;
			case LEVEL4_APPROVAL1_ID:
				logger.debug("In " + LEVEL4_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL4_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL4_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);
				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);

				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL4_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel3FinanceDDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);
							pendingWith.append(execution.getVariable(LEVEL4_APPROVAL1_NAME).toString());

						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL4_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;
			case LEVEL5_APPROVAL1_ID:
				logger.debug("In " + LEVEL5_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL5_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL5_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);
				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL5_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							// converAttchmentListIntoJson =
							// OAFEmailAttachmentsUtill.converAttchmentListIntoJson(oafAttachedDocumentIdentification.getLevel4VicePDocument());
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);
							pendingWith.append(execution.getVariable(LEVEL5_APPROVAL1_NAME).toString());
						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL5_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;

			case LEVEL6_APPROVAL1_ID:
				logger.debug("In " + LEVEL6_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL6_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL6_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);
				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL6_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);
							pendingWith.append(execution.getVariable(LEVEL6_APPROVAL1_NAME).toString());
						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL6_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;

			case LEVEL7_APPROVAL1_ID:
				logger.debug("In " + LEVEL7_APPROVAL1_ID + " case");

				// Creating and setting approve, reject and add comment link start
				userId = String.valueOf(execution.getVariable(LEVEL7_APPROVAL1_ID));
				taskId = String.valueOf(execution.getVariable(LEVEL7_APPROVAL1_TASK_ID));
				approveTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_APPROVE;
				rejectTaskUrl = OAF_APW_EMAIL_APPRROVAL_URL + userId + "/" + taskId + OAF_APW_EMAIL_OUTCOME_REJECT;
				viewTaskUrl = OAF_APW_EMAIL_TASK_VIEW + "0/" + taskId;
				emailTemplateContent.setApprovalEmail(true);
				emailTemplateContent.setApproveTaskUrl(approveTaskUrl);
				emailTemplateContent.setRejectTaskUrl(rejectTaskUrl);
				emailTemplateContent.setViewTaskUrl(viewTaskUrl);

				// Creating and setting approve, reject and add comment link end

				// Template Mail-3 - submission of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_3_FOR_SUBMISSION_OF_APPROVAL);
				if (( mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = String.valueOf(execution.getVariable(LEVEL7_APPROVAL1_EMAILID));
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail3 = "Quote submission for approval " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail3);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);

							email.setAttachedDocumetIdentityList(null);

							// Sending Approval Mail-3
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);
							pendingWith.append(execution.getVariable(LEVEL7_APPROVAL1_NAME).toString());
						} else {
							logger.debug("EmailNotificationTaskListener.notify() ::  " + LEVEL7_APPROVAL1_ID
									+ " user not Found or initiator user not found");
						}
					} catch (Exception e) {
						logger.error("EmailNotificationTaskListener.notify() ::  Failed to send email notification  : "
								+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationTaskListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				isNeedToUpdateReport = true;
				break;
				
				
			case QUOTE_FAILURE_FINAL_APPROVED:
				logger.debug("In " + QUOTE_FAILURE_FINAL_APPROVED + " case");								
				taskId = delegateTask.getId();	
				String retrySapFailUrl = OAF_APW_EMAIL_APPRROVAL_URL + taskId + OAF_APW_EMAIL_RETRY_SAPFAIL;
				String sapFailReason = "";
				if(execution.getVariable(SAP_SERVICE_CALL_RESPONSE)!=null) {
					sapFailReason = String.valueOf(execution.getVariable(SAP_SERVICE_CALL_RESPONSE));
					execution.removeVariable(SAP_SERVICE_CALL_RESPONSE);
				}
				else {
					sapFailReason = "-";
				}
				OAFCommonUtil.setSapFailureEmailContent(emailTemplateContent, retrySapFailUrl, sapFailReason);
				// Template Mail-10 - failure final approved
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_10_FAILURE_FOR_FINAL_APPROVAL);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "SAP update failure of Quote " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending failure final approved Mail-10
							logger.debug(
									"EmailNotificationTaskListener.notify() :: Sending Email to : " + email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

						} else {
							logger.debug("EmailNotificationExecutionListener.notify() ::  " + toEmailAssignee
									+ " user not Found");
						}
					} catch (Exception e) {
						logger.error(
								"EmailNotificationExecutionListener.notify() ::  Failed to send email notification  : "
										+ e.getMessage());
					}
				} else {
					logger.error(
							"EmailNotificationExecutionListener.notify() ::  Failed to send email notification  : Email template parsing error");
				}
				break;

			default:
				logger.debug("EmailNotificationTaskListener.notify() :: In Default Case");
				break;
			}

			if (isNeedToUpdateReport && !isResendEmail) {
				OAFReportUtil.updatePendingReportDetail(delegateTask.getId(), pendingWith.toString(),
						emailTemplateContent, execution);

			}			
		}
		execution.removeVariable(SEND_EMAIL_NOTIFICATION_FOR);
	}

}
