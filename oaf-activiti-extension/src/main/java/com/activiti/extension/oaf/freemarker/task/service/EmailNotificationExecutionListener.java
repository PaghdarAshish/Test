package com.activiti.extension.oaf.freemarker.task.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.util.ByteArrayDataSource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.util.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.freemarker.task.util.OAFCommonUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFEmailTemplateUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFFreeMarkerUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFPdfUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFReportUtil;

/**
 * 
 * This class is responsible to send email notification based on the type of
 * email case This is APS execution listener class
 * 
 * @author Keval Bhatt
 *
 */
public class EmailNotificationExecutionListener implements ExecutionListener, OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(EmailNotificationExecutionListener.class);
	private static OAFEmailTemplateContent emailTemplateContent;
	String initMail = "";

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		Boolean isEmailSend = false;
		if (!OAF_EMAIL_SEND.isEmpty() && OAF_EMAIL_SEND != null && OAF_EMAIL_SEND.equalsIgnoreCase("True")) {
			isEmailSend = true;
		}
		if (isEmailSend) {
			OAFEmail email = new OAFEmail();
			emailTemplateContent = OAFEmailTemplateUtil.initMail(execution, emailTemplateContent);
			if (TEMP_INIT_MAIL_IS_ENABLE != null && Boolean.parseBoolean(TEMP_INIT_MAIL_IS_ENABLE) == false) {
				initMail = emailTemplateContent.getInitMail();
				logger.debug("EmailNotificationExecutionListener.notify() :: Init email address from JSON Field :: "
						+ initMail);

			} else {

				initMail = TEMP_INIT_MAIL;
				logger.debug("EmailNotificationExecutionListener.notify() :: Init email address from property file :: "
						+ initMail);
			}
			String mailTemplateProcessedContent = "";
			switch (String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR))) {
			case QUOTE_SUBMISSION:

				logger.debug("In " + QUOTE_SUBMISSION + " case");

				// Template Mail-1 - Submission of quote
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_1_SUBMISSION_OF_QUOTE);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty() )) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Confirmation of submission for approval of Quote "
									+ emailTemplateContent.getInquiry() + "/" + emailTemplateContent.getRevision() + "/"
									+ emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Submission of quote Mail-1
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
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

			case QUOTE_FAILURE_SUBMISSION:

				logger.debug("In " + QUOTE_FAILURE_SUBMISSION + " case");

				// Template Mail-2 - Failure submission of quote
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_2_FAILURE_OF_SUBMISSION_OF_QUOTE);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Failure of submission for approval of Quote "
									+ emailTemplateContent.getInquiry() + "/" + emailTemplateContent.getRevision() + "/"
									+ emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Failure submission of quote Mail-2
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
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
			case INITIATOR_APPROVAL_CONFIRMATION:
				logger.debug("In " + INITIATOR_APPROVAL_CONFIRMATION + " case");
				// Template Mail-4 - Confirmation of approval
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_4_FOR_CONFIRMATION_OF_APPROVAL);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Progress of approval for " + emailTemplateContent.getInquiry() + "/"
									+ emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Confirmation Approval Mail-4
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
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
				// Update report
				String currentPendingName = "-";
				// To check current approval in case of parralel process we will have to remove
				// the name of the current approval in pending with field
				if ((execution.getVariable(CURRENT_LEVEL) != null)) {

					if (execution.getVariable(LEVEL1_PENDING_WITH) != null
							&& execution.getVariable(LEVEL1_PENDING_WITH).toString().contains(", ")) {
						String currentComName = "-";
						if ((execution.getVariable(CURRENT_LEVEL) == execution.getVariable(LEVEL1_APPROVAL1_ID))) {
							currentComName = (String) execution.getVariable(LEVEL1_APPROVAL1_NAME);
						} else {
							currentComName = (String) execution.getVariable(LEVEL1_APPROVAL2_NAME);
						}
						// Remove unncessary string
						List<String> items = new ArrayList<String>(
								Arrays.asList(execution.getVariable(LEVEL1_PENDING_WITH).toString().split(", ")));
						int index = 0;
						for (String name : items) {
							index++;
							if (name.equalsIgnoreCase(currentComName)) {
								index--;
								break;
							}

						}
						items.remove(index);
						currentPendingName = items.get(0);
						execution.setVariable(LEVEL1_PENDING_WITH, currentPendingName);
					} else if (execution.getVariable(LEVEL2_PENDING_WITH) != null
							&& execution.getVariable(LEVEL2_PENDING_WITH).toString().contains(", ")) {
						String currentComName = "";
						// String[] pendingNames =
						// execution.getVariable(LEVEL2_PENDING_WITH).toString().split(", ");
						if ((execution.getVariable(CURRENT_LEVEL) == execution.getVariable(LEVEL2_APPROVAL1_ID))) {
							currentComName = (String) execution.getVariable(LEVEL2_APPROVAL1_NAME);
						} else {
							currentComName = (String) execution.getVariable(LEVEL2_APPROVAL2_NAME);
						}
						// Remove unncessary string
						List<String> items = new ArrayList<String>(
								Arrays.asList(execution.getVariable(LEVEL2_PENDING_WITH).toString().split(", ")));
						int index = 0;
						for (String name : items) {
							index++;
							if (name.equalsIgnoreCase(currentComName)) {
								index--;
								break;
							}

						}
						items.remove(index);
						currentPendingName = items.get(0);

						execution.setVariable(LEVEL2_PENDING_WITH, currentPendingName);
					}

					else {
						currentPendingName = "-";
					}

				}

				OAFReportUtil.updatePendingReportDetail("-", currentPendingName, emailTemplateContent, execution);
				// Update report end
				break;
			case QUOTE_RESET_CASE_2:
				logger.debug("In " + QUOTE_RESET_CASE_2 + " case");

				// Template Mail-6 - Reset of approval case 2
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_6_RESET_OF_APPROVAL_CASE2);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Re-set of approval of Quote " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Reset of approval case 2 Mail-6
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

						} else {
							logger.debug(
									"EmailNotificationExecutionListener.notify() ::  " + initMail + " user not Found");
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
			case QUOTE_RESET_CASE_1:
				logger.debug("In " + QUOTE_RESET_CASE_1 + " case");

				// Template Mail-7 - Reset of approval case 1
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_7_RESET_OF_APPROVAL_CASE1);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Re-set of approval of Quote " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending Reset of approval case 1 Mail-7
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

						} else {
							logger.debug(
									"EmailNotificationExecutionListener.notify() ::  " + initMail + " user not Found");
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
			case QUOTE_REJECT:
				logger.debug("In " + QUOTE_REJECT + " case");
				// Template Mail-8 - Rejection of Quote
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_8_FOR_REJECTION_OF_QUOTE);

				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Rejection of approval of Quote " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);

							// Sending rejection quote Mail-8
							logger.debug("EmailNotificationExecutionListener.notify() :: Sending Email to : "
									+ email.getTo());
							OAFFreeMarkerUtil.sendNotification(email);

						} else {
							logger.debug(
									"EmailNotificationExecutionListener.notify() ::  " + initMail + " user not Found");
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
				// Update report
				OAFReportUtil.updateRejectedReportDetail(emailTemplateContent, execution);
				break;
			case QUOTE_FINAL_APPROVED:
				logger.debug("In " + QUOTE_FINAL_APPROVED + " case");

				boolean pdfAttachmentEnable = false;
				boolean pdfDownloadLinkEnable = false;
				boolean pdfGenerateEnable = false;
				ByteArrayDataSource bads = null;
				// Check is pdf attachment enable
				pdfGenerateEnable = (OAF_PDF_GENERATE_ENABLE != null
						&& Boolean.parseBoolean(OAF_PDF_GENERATE_ENABLE) == true) ? true : false;				
				
				if(pdfGenerateEnable) {
					//Generatepdf
					bads= OAFCommonUtil.generatePdfFromContent(execution);			
				}
				// Check is pdf send enable
				pdfAttachmentEnable = (OAF_PDF_MAIL_SEND != null
						&& Boolean.parseBoolean(OAF_PDF_MAIL_SEND) == true) ? true : false;
				
				// Check is pdf download link enable
				pdfDownloadLinkEnable = (OAF_PDF_LINK_DOWNLOAD != null
						&& Boolean.parseBoolean(OAF_PDF_LINK_DOWNLOAD) == true) ? true : false;
								
				// If enable attaching pdfdownloadlink
				if(pdfGenerateEnable && pdfDownloadLinkEnable) {
					
					OAFCommonUtil.setPdfDownloadEmailContent(emailTemplateContent, execution);
				}
				// Template Mail-9 - final approved
				mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
						OAF_MAIL_TEMPLATE_9_FOR_FINAL_APPROVAL);

				
				if ((mailTemplateProcessedContent != null && !mailTemplateProcessedContent.isEmpty())) {
					try {
						String toEmailAssignee = initMail;
						if (!toEmailAssignee.isEmpty()) {
							email.setTo(toEmailAssignee);
							email.setCc(null);
							String subjectMail = "Approval of approval for Quote " + emailTemplateContent.getInquiry()
									+ "/" + emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
							email.setSubject(subjectMail);
							email.setTemplateProcessedContent(mailTemplateProcessedContent);
							email.setAttachedDocumetIdentityList(null);
							// If enable attaching pdf in email
							if (pdfGenerateEnable &&  pdfAttachmentEnable) {
								if (bads != null) {
									logger.debug(
											"EmailNotificationExecutionListener.notify() :: setting pdf attachment in email...");
									OAFPdfUtil.setMimeBodyPartPDFFileForMail(bads, emailTemplateContent, email);
								}
							}							
							// Sending final approved Mail-9
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
				// Update report
				OAFReportUtil.updateFinalApprovedReportDetail(emailTemplateContent, execution);
				break;
			
			default:
				logger.debug("EmailNotificationExecutionListener.notify() :: In Default Case");
				break;
			}

			execution.removeVariable(SEND_EMAIL_NOTIFICATION_FOR);
		}
	}

}
