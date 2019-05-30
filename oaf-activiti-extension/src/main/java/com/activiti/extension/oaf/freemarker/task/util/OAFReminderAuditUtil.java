package com.activiti.extension.oaf.freemarker.task.util;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.bean.service.OAFService;
import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.model.OAFReminderAudit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is class is responsible to create reminder audit and send the request to update reminder audit
 * 
 * @author Keval Bhatt
 *
 */
public class OAFReminderAuditUtil implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFReminderAuditUtil.class);
	private static HistoryService historyService = Context.getProcessEngineConfiguration().getHistoryService();

	private static ObjectMapper objMapper = new ObjectMapper();
	private static String reminderAuditUrl = OAF_ADAPTER_BASE_URL + OAF_API_SAVE_REMINDER_AUDIT_BASE_URL;
	private static String contentType = CONTENT_JSON_VALUE;

	/**
	 * Send post request and update the reminder audit table
	 * @param execution
	 * @param toEmailAssignee
	 * @param subject
	 * @return
	 */
	public static int saveReminderAudit(DelegateExecution execution, String toEmailAssignee, String subject) {
		try {
			HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
					.taskId(execution.getVariable(EMAIL_TASK_ID).toString()).unfinished().singleResult();
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(historicTaskInstance.getProcessInstanceId()).unfinished().singleResult();

			OAFReminderAudit reminderAudit = new OAFReminderAudit();
			reminderAudit.setBccEmail(null);
			reminderAudit.setCcEmail(null);
			reminderAudit.setToEmail(toEmailAssignee);
			reminderAudit.setTask_id(execution.getVariable(EMAIL_TASK_ID).toString());
			reminderAudit.setTask_name(historicTaskInstance.getName());
			reminderAudit.setProcess_instance_id(historicTaskInstance.getProcessInstanceId());
			reminderAudit.setProcess_name(historicProcessInstance.getName());
			reminderAudit.setSubject(subject);
			reminderAudit.setReminder_date(OAFCommonUtil.converDateIntoString(historicTaskInstance.getCreateTime()));
			String json = objMapper.writeValueAsString(reminderAudit);
			logger.debug("OAFReminderAuditUtil.saveReminderAudit() ::  updating reminder audit... ");
			int status = OAFService.sendJsonPostRequest(reminderAuditUrl, json, contentType);
			logger.debug("OAFReminderAuditUtil.saveReminderAudit() ::  reminder audit successfully updated... ");
			return status;

		} catch (JsonProcessingException e) {
			logger.error("OAFReminderAuditUtil.saveReminderAudit() :: JsonProcessingException : " + e);
			return 0;
		} catch (Exception e) {
			logger.error("OAFReminderAuditUtil.saveReminderAudit() :: Exception : " + e);
			return 0;
		}
	}
}
