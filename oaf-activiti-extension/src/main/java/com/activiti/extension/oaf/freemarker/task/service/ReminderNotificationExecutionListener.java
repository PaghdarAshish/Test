package com.activiti.extension.oaf.freemarker.task.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;
import com.activiti.extension.oaf.freemarker.task.util.OAFEmailTemplateUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFFreeMarkerUtil;
import com.activiti.extension.oaf.freemarker.task.util.OAFReminderAuditUtil;

/**
 * This class is responsible to send reminder email and also responsible to
 * update reminder audit
 * 
 * @author Keval Bhatt
 *
 */
public class ReminderNotificationExecutionListener implements ExecutionListener, OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(ReminderNotificationExecutionListener.class);
	private static OAFEmailTemplateContent emailTemplateContent;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		emailTemplateContent = OAFEmailTemplateUtil.initMail(execution, emailTemplateContent);
		String reminderEmail = (String) execution.getVariable(REMINDER_APPROVAL_EMAIL_ADDRESS);
		OAFEmail email = new OAFEmail();
		String mailTemplateProcessedContent = "";
		switch (String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR))) {
		case QUOTE_REMINDER_APPROVAL:
			logger.debug("In " + QUOTE_REMINDER_APPROVAL + " case");

			mailTemplateProcessedContent = OAFEmailTemplateUtil.parseTemplate(execution, emailTemplateContent,
					OAF_MAIL_TEMPLATE_5_REMINDER_FOR_APPROVAL);

			if ((!mailTemplateProcessedContent.isEmpty() || mailTemplateProcessedContent != null)) {
				try {
					String toEmailAssignee = reminderEmail;
					execution.removeVariable(REMINDER_APPROVAL_EMAIL_ADDRESS);
					if (!toEmailAssignee.isEmpty()) {
						email.setTo(toEmailAssignee);
						email.setCc(null);

						String subjectMail3 = "Reminder to approve Quote " + emailTemplateContent.getInquiry() + "/"
								+ emailTemplateContent.getRevision() + "/" + emailTemplateContent.getDq();
						email.setSubject(subjectMail3);
						email.setTemplateProcessedContent(mailTemplateProcessedContent);
						email.setAttachedDocumetIdentityList(null);

						if (OAF_REMINDER_AUDIT_ENABLE != null
								&& Boolean.parseBoolean(OAF_REMINDER_AUDIT_ENABLE) == true) {
							// Saving Reminder audit
							OAFReminderAuditUtil.saveReminderAudit(execution, toEmailAssignee, subjectMail3);
						}
						logger.debug(
								"ReminderNotificationExecutionListener.notify() :: Variables for reminder audit has been set: ");

						logger.debug("ReminderNotificationExecutionListener.notify() :: Sending Email to : "
								+ email.getTo());
						// Sending Approval Mail-8
						logger.debug("ReminderNotificationExecutionListener.notify() :: Sending Email to : "
								+ email.getTo());
						OAFFreeMarkerUtil.sendNotification(email);

					} else {
						logger.debug("ReminderNotificationExecutionListener.notify() ::  " + reminderEmail
								+ " user not Found");
					}
				} catch (Exception e) {
					logger.error(
							"ReminderNotificationExecutionListener.notify() ::  Failed to send email notification  : "
									+ e.getMessage());
				}
			} else {
				logger.error(
						"ReminderNotificationExecutionListener.notify() ::  Failed to send email notification  : Email template parsing error");
			}

			break;

		default:
			logger.debug("ReminderNotificationExecutionListener.notify() :: In Default Case");
			break;
		}

	}

}
