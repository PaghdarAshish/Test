package com.activiti.extension.bean.service;

import com.activiti.domain.idm.User;
import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.util.OAFCommonUtil;
import com.activiti.service.api.UserService;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * This is Spring service responsible to complete the tasks
 * 
 * @author Keval Bhatt
 *
 */
@Service(value = "oafTasks")
public class OAFTaskService implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFTaskService.class);
	@Autowired
	TaskService taskService;

	@Autowired
	UserService userService;
	@Autowired
	HistoryService historyService;

	/**
	 * This method is responsible to complete the low priority If task not completed
	 * yet
	 * 
	 * @param execution
	 */
	public void completeLevel1Approval1TaskIfNot(DelegateExecution execution) {
		try {
			if (execution.getVariable(lEVEL1_APPROVAL1_TASK_ID) != null) {
				String taskId = (String) execution.getVariable(lEVEL1_APPROVAL1_TASK_ID);
				HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
						.taskId(taskId).singleResult();
				if (historicTaskInstance.getEndTime() == null) {
					if (execution.getVariable(OAF_APPROVAL_STATUS_SKIP) != null) {
						execution.setVariable(LEVEL1_APPROVAL1_STATUS, execution.getVariable(OAF_APPROVAL_STATUS_SKIP));
						execution.setVariable(LEVEL1_APPROVAL1_IS_APPROVED, true);
					} else {
						execution.setVariable(LEVEL1_APPROVAL1_STATUS, "-");
					}
					execution.setVariable(LEVEL1_APPROVAL1_IS_APPROVED, true);
					taskService.complete(taskId);
					logger.info("OAFTaskService.completeLevel1Approval1TaskIfNot()::  Task name "
							+ historicTaskInstance.getName() + " with id "
							+ (String) execution.getVariable(lEVEL1_APPROVAL1_TASK_ID) + " successfully completed");

				} else {
					logger.info(
							"OAFTaskService.completeLevel1Approval1TaskIfNot():: Can not complete task because task with id "
									+ (String) execution.getVariable(lEVEL1_APPROVAL1_TASK_ID) + " Already Completed");
				}

			} else {
				logger.info("OAFTaskService.completeLevel1Approval1TaskIfNot():: " + lEVEL1_APPROVAL1_TASK_ID
						+ " not found");
			}
		} catch (ActivitiObjectNotFoundException notFound) {
			logger.error(
					"OAFTaskService.completeLevel1Approval1TaskIfNot():: Can not complete task because task with id "
							+ (String) execution.getVariable(lEVEL1_APPROVAL1_TASK_ID) + " Already Completed");
		} catch (Exception e) {
			logger.error("OAFTaskService.completeLevel1Approval1TaskIfNot():: Exception :: " + e);
		}

	}

	/**
	 * Reject the task forcefully
	 * @param taskId
	 * @param userId
	 * @param execution
	 */
	public void rejectTask(String taskId, long userId, DelegateExecution execution) {
		try {
			if ((taskId != null || !taskId.isEmpty())) {
				HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
						.taskId(taskId).singleResult();
				if (historicTaskInstance.getEndTime() == null) {
					logger.debug("OAFTaskService.rejectTask()::  Reject task Name  :: "
							+ historicTaskInstance.getName());
					HashMap<String, Object> variables = new HashMap<String, Object>();
					variables.put("outcome", new String("Reject"));
					taskService.complete(taskId, variables);
					logger.debug("OAFTaskService.rejectTask()::  Task Successfully Rejected  :: "
							+ historicTaskInstance.getName());
				} else {
					logger.info("OAFTaskService.rejectTask()::  rejection task with id " + taskId
							+ " is already completed");
				}
			} else {
				logger.info("OAFTaskService.rejectTask():: rejection task id is empty or null");
			}
		} catch (Exception e) {
			logger.error("OAFTaskService.rejectTask():: Exception :: " + e);
		}
	}

	/**
	 * This method is responsible to auto approve (based on weekend), enable reminder,  enable auto reset scenario
	 * 
	 * @param taskId
	 * @param taskCreateTime
	 * @param userId
	 * @param execution
	 */
	public void checkTimeAndCompleteTask(String taskId, String taskCreateTime, long userId,
			DelegateExecution execution) {
		try {

			if ((taskId != null || !taskId.isEmpty()) && (taskCreateTime != null || !taskCreateTime.isEmpty())) {
				HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
						.taskId(taskId).singleResult();
				Long taskLongCreateTime = Long.parseLong(taskCreateTime);
				if (historicTaskInstance.getEndTime() == null) {
					if (execution.getVariable(IS_FINAL_LEVEL) != null
							&& (Boolean) execution.getVariable(IS_FINAL_LEVEL) == true) {
						int counter = 1;
						if (execution.getVariable(OAF_FINAL_APPROVAL_RESET_COUNTER) != null) {
							counter = (Integer) execution.getVariable(OAF_FINAL_APPROVAL_RESET_COUNTER);
							counter++;
							execution.setVariable(OAF_FINAL_APPROVAL_RESET_COUNTER, counter);
						} else {
							execution.setVariable(OAF_FINAL_APPROVAL_RESET_COUNTER, counter);
						}

						int intervalDay = OAFCommonUtil.getFinalApprovalResetInterval();

						if (intervalDay == 0) {							
							OAFCommonUtil.doAutoRejectCaseOrResetCase(historicTaskInstance, taskService, execution, taskId, userId);						
							logger.debug("OAFTaskService.checkTimeAndCompleteTask()::  Reminder task Name  :: "
									+ historicTaskInstance.getName()
									+ ", It is final level approval task. (interval time read from property is disabled)");
						} else if (intervalDay > 0) {
							logger.debug("OAFTaskService.checkTimeAndCompleteTask():: Final approval reset counter : "
									+ counter);
							if (counter >= intervalDay) {							
								OAFCommonUtil.doAutoRejectCaseOrResetCase(historicTaskInstance, taskService, execution, taskId, userId);							
								logger.debug("OAFTaskService.checkTimeAndCompleteTask()::  Reminder task Name  :: "
										+ historicTaskInstance.getName()
										+ ", It is final level approval task. (Interval time read from property file)");									
							} else {
								execution.setVariable(IS_FINAL_RESET_ENABLE, false);
								execution.setVariable(IS_REMINDER_ENABLED, false);
							}
						}
					} else {

						logger.debug("OAFTaskService.checkTimeAndCompleteTask()::  Reminder task Name  :: "
								+ historicTaskInstance.getName());

						// If between date contains weekend day then skip the auto approval and send
						// reminder
						boolean checkIsdayWeekend = OAFCommonUtil
								.isAutoApprDtContainsWeekend(historicTaskInstance.getCreateTime());

						// In the case of parallel process tasks update the separated variable for
						// different level
						String autoApprovalVarname = OAFCommonUtil.getAutoApprovalLevelEnableName(taskId, execution);

						if (checkIsdayWeekend && execution.getVariable(autoApprovalVarname) == null) {
							logger.debug("OAFTaskService.checkTimeAndCompleteTask() :: checkIsdayWeekend :: "
									+ checkIsdayWeekend);
							// This block will only execute once to skip the auto approve after weekend is
							// over so user will get notify again and able to complete the task
							// Send reminder
							execution.setVariable(IS_REMINDER_ENABLED, true);
							User user = userService.getUser(userId);
							execution.setVariable(REMINDER_APPROVAL_EMAIL_ADDRESS, user.getEmail());
							execution.setVariable(autoApprovalVarname, false);

						} else {
							int diff = OAFCommonUtil.getAutoApproveDifference(taskLongCreateTime);
							int autoApproveDuration = OAFCommonUtil.getAutoApproveDuration();
							if (diff == -1) {
								logger.error(
										"OAFTaskService.checkTimeAndCompleteTask() ::: Error while returnning property for auto approval for getting difference ");
							}
							if (autoApproveDuration == -1) {
								logger.error(
										"OAFTaskService.checkTimeAndCompleteTask():::  Error while returnning property for auto approval for getting auto approval duration ");
							} else {
								if ((diff >= autoApproveDuration) && (!historicTaskInstance.getId().isEmpty())) {
									execution.setVariable(IS_REMINDER_ENABLED, false);
									boolean isAutoApprovedAllow = true;
									if (taskId.equals(execution.getVariable(lEVEL1_APPROVAL1_TASK_ID).toString())) {
										if (execution.getVariable(LEVEL1_APPROVAL1_SKIP_AUTO_APPROVE) != null
												&& (Boolean) execution
														.getVariable(LEVEL1_APPROVAL1_SKIP_AUTO_APPROVE)) {
											isAutoApprovedAllow = false;
										}
									}
									if (taskId.equals(execution.getVariable(lEVEL1_APPROVAL2_TASK_ID).toString())) {
										execution.setVariable(LEVEL1_APPROVAL1_SKIP_AUTO_APPROVE, true);
									}
									if (isAutoApprovedAllow) {
										// Change the status of approval
										OAFCommonUtil.changeAutoApprovalRejectionStatus(userId, execution, historicTaskInstance,true,false);										
										taskService.complete(taskId);
										execution.removeVariable(autoApprovalVarname);
										logger.debug("OAFTaskService.checkTimeAndCompleteTask():: Reminder Task name "
												+ historicTaskInstance.getName() + " with id " + taskId
												+ " successfully completed");
									} else {
										logger.debug("OAFTaskService.checkTimeAndCompleteTask():: Reminder Task name "
												+ historicTaskInstance.getName() + " with id " + taskId
												+ " auto approve is skiped");
									}

								} else {
									execution.setVariable(IS_REMINDER_ENABLED, true);
									User user = userService.getUser(userId);
									execution.setVariable(REMINDER_APPROVAL_EMAIL_ADDRESS, user.getEmail());

								}
							}
						}

					}

				} else {
					logger.debug("OAFTaskService.checkTimeAndCompleteTask()::  Reminder task Name  :: "
							+ historicTaskInstance.getName() + " is already completed");
				}

			} else {
				logger.info(
						"OAFTaskService.checkTimeAndCompleteTask():: Reminder taskId or taskCreateTime is empty or null");
			}

		} catch (Exception e) {
			logger.error("OAFTaskService.checkTimeAndCompleteTask():: Exception :: " + e
					+ (taskId != null ? " TaskId :: " + taskId : ""));
		}
	}

}
