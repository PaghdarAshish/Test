package com.activiti.extension.oaf.freemarker.task.service;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.impl.util.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.util.OAFCommonUtil;

/**
 * This class is responsible to set the comment in json.
 * 
 * @author Keval Bhatt, Avani Purohit
 *
 */
public class SetCommentsTaskListener implements TaskListener, OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(SetCommentsTaskListener.class);

	/*
	 * This method will call the methods to set comments
	 */
	@Override
	public void notify(DelegateTask task) {
		logger.debug("SetCommentsTaskListener.notify()");
		DelegateExecution execution = task.getExecution();
		JSONArray commentJsonArr = new JSONArray();
		if (execution.getVariable(ISFROMREJECTSCENARIO) != null) {
			if (!String.valueOf(execution.getVariable(VIEWCOMMENTS).toString()).isEmpty()) {
				Object obj = execution.getVariable(VIEWCOMMENTS);
				commentJsonArr = OAFCommonUtil.jsonArrStringToJsonArryObject(obj);
			}

			logger.debug(
					"SetCommentsTaskListener.notify() :: Appending previous comments :: " + commentJsonArr.toString());
			logger.debug("SetCommentsTaskListener.notify() :: Cleanning level variables :: ");
			// Remove unncessary variables and clean the levels
			execution.removeVariable(ISFROMREJECTSCENARIO);
			// Level1Approval1
			execution.removeVariable(LEVEL1_APPROVAL1_ID);
			execution.removeVariable(LEVEL1_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL1_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL1_APPROVAL1_IS_REJECTED_FLAG);
			execution.removeVariable(lEVEL1_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL1_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL1_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL1_APPROVAL1_STATUS);
			// Level1Approval2
			execution.removeVariable(LEVEL1_APPROVAL2_ID);
			execution.removeVariable(LEVEL1_APPROVAL2_IS_APPROVED);
			execution.removeVariable(LEVEL1_APPROVAL2_IS_REJECTED);
			execution.removeVariable(LEVEL1_APPROVAL2_IS_REJECTED_FLAG);
			execution.removeVariable(lEVEL1_APPROVAL2_TASK_ID);
			execution.removeVariable(LEVEL1_APPROVAL2_COMMENT);
			execution.removeVariable(LEVEL1_APPROVAL2_SALES_COMMENT);
			execution.removeVariable(LEVEL1_APPROVAL2_STATUS);
			// Level2Approval1
			execution.removeVariable(LEVEL2_APPROVAL1_ID);
			execution.removeVariable(LEVEL2_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL2_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL2_APPROVAL1_IS_REJECTED_FLAG);
			execution.removeVariable(LEVEL2_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL2_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL2_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL2_APPROVAL1_STATUS);
			// Level2Approval2
			execution.removeVariable(LEVEL2_APPROVAL2_ID);
			execution.removeVariable(LEVEL2_APPROVAL2_IS_APPROVED);
			execution.removeVariable(LEVEL2_APPROVAL2_IS_REJECTED);
			execution.removeVariable(LEVEL2_APPROVAL2_IS_REJECTED_FLAG);
			execution.removeVariable(LEVEL2_APPROVAL2_TASK_ID);
			execution.removeVariable(LEVEL2_APPROVAL2_COMMENT);
			execution.removeVariable(LEVEL2_APPROVAL2_SALES_COMMENT);
			execution.removeVariable(LEVEL2_APPROVAL2_STATUS);
			// Level3Approval1
			execution.removeVariable(LEVEL3_APPROVAL1_ID);
			execution.removeVariable(LEVEL3_APPROVAL1_IS_APPROVED);
			execution.removeVariable(lEVEL3_APPROVAL1_IS_REJECTED);
			execution.removeVariable(lEVEL3_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL3_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL3_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL3_APPROVAL1_STATUS);
			// Level4Approval1
			execution.removeVariable(LEVEL4_APPROVAL1_ID);
			execution.removeVariable(LEVEL4_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL4_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL4_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL4_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL4_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL4_APPROVAL1_STATUS);
			// Level5Approval1
			execution.removeVariable(LEVEL5_APPROVAL1_ID);
			execution.removeVariable(LEVEL5_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL5_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL5_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL5_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL5_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL5_APPROVAL1_STATUS);
			// Level6Approval1
			execution.removeVariable(LEVEL6_APPROVAL1_ID);
			execution.removeVariable(LEVEL6_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL6_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL6_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL6_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL6_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL6_APPROVAL1_STATUS);
			// Level7Approval1
			execution.removeVariable(LEVEL7_APPROVAL1_ID);
			execution.removeVariable(LEVEL7_APPROVAL1_IS_APPROVED);
			execution.removeVariable(LEVEL7_APPROVAL1_IS_REJECTED);
			execution.removeVariable(LEVEL7_APPROVAL1_TASK_ID);
			execution.removeVariable(LEVEL7_APPROVAL1_COMMENT);
			execution.removeVariable(LEVEL7_APPROVAL1_SALES_COMMENT);
			execution.removeVariable(LEVEL7_APPROVAL1_STATUS);

			execution.removeVariable(CURRENT_LEVEL);
			execution.removeVariable(LEVEL1_PENDING_WITH);
			execution.removeVariable(LEVEL2_PENDING_WITH);

			execution.removeVariable(SAP_SERVICE_CALL_FAIL);
			execution.removeVariable(IS_FINAL_AUTO_REJECTION_ENABLE);

		} else {
			commentJsonArr = (createCommentList(task));
			// commentJsonArray.put(createCommentList(task));
		}
		if (commentJsonArr != null) {
			execution.setVariable(VIEWCOMMENTS, commentJsonArr.toString());
			execution.setVariable(COMMENTJSON, commentJsonArr.toString());
			logger.debug("SetCommentsTaskListener.notify() :: viewComment list" + execution.getVariable(VIEWCOMMENTS));
			execution.setVariable(ADD_COMMENTS, null);
			execution.setVariable(ADD_SALES_COMMENTS, null);
		}

	}

	/**
	 * To create comment list
	 * 
	 * @param task
	 * @return JSONArray of comments
	 */
	public JSONArray createCommentList(DelegateTask task) {
		logger.info("SetCommentsTaskListener.createCommentList()");
		DelegateExecution execution = task.getExecution();
		JSONArray commentJsonArr = null;
		if (execution.getVariable(COMMENTJSON) == null || execution.getVariable(COMMENTJSON).toString().isEmpty()) {
			commentJsonArr = new JSONArray();
		} else {
			Object variable = execution.getVariable(COMMENTJSON);
			Object o = null;
			try {
				o = new JSONTokener(variable.toString()).nextValue();
			} catch (JSONException e) {
				logger.error("SetCommentsTaskListener.createCommentList() :: JSONException :: " + e);
			}
			if (o instanceof JSONArray) {
				commentJsonArr = (JSONArray) o;
			}
		}
		JSONObject jsonObject = new JSONObject();
		String taskName = task.getName();
		// int counter =
		// Integer.parseInt(execution.getVariable(COMMENT_COUNTETR).toString());
		// String commentCounter = String.valueOf(counter);
		if (taskName != null) {
			taskName = taskName.substring(0, taskName.lastIndexOf("(")).toString().trim();
		}
		if (LEVEL1_APPROVAL1_DESIGNATION.equals(taskName)) {
			// If level1Approval2 has rejected then comment of level1App1 should not be
			// display (Parralel task condition)
			if (execution.getVariable(LEVEL1_APPROVAL2_IS_REJECTED) == null) {

				jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL1_APPROVAL1_NAME));
				jsonObject.put(KEY_CMT_DESIGNATION, LEVEL1_APPROVAL1_DESIGNATION);
				jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
				if (task.getExecution().getVariable(LEVEL1_APPROVAL1_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL1_APPROVAL1_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS,
							task.getExecution().getVariable(LEVEL1_APPROVAL1_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL1_APPROVAL1_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
				}
				// Comment for Sales

				if (task.getExecution().getVariable(LEVEL1_APPROVAL1_SALES_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL1_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
							task.getExecution().getVariable(LEVEL1_APPROVAL1_SALES_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL1_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
				}

				if (task.getExecution().getVariable(LEVEL1_APPROVAL1_STATUS) != null) {
					jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL1_APPROVAL1_STATUS));
				} else {
					jsonObject.put(KEY_CMT_STATUS, "-");
				}

				commentJsonArr.put(jsonObject);

			}

		} else if (LEVEL1_APPROVAL2_DESIGNATION.equals(taskName)) {
			// If level1Approval1 has rejected then comment of level1App2 should not be
			// display (Parralel task condition)
			if (execution.getVariable(LEVEL1_APPROVAL1_IS_REJECTED) == null) {
				jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL1_APPROVAL2_NAME));
				jsonObject.put(KEY_CMT_DESIGNATION, LEVEL1_APPROVAL2_DESIGNATION);
				jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));

				if (task.getExecution().getVariable(LEVEL1_APPROVAL2_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL1_APPROVAL2_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS,
							task.getExecution().getVariable(LEVEL1_APPROVAL2_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL1_APPROVAL2_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
				}
				// Comment for Sales

				if (task.getExecution().getVariable(LEVEL1_APPROVAL2_SALES_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL1_APPROVAL2_SALES_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
							task.getExecution().getVariable(LEVEL1_APPROVAL2_SALES_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL1_APPROVAL2_SALES_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
				}

				if (task.getExecution().getVariable(LEVEL1_APPROVAL2_STATUS) != null) {
					jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL1_APPROVAL2_STATUS));
				} else {
					jsonObject.put(KEY_CMT_STATUS, "-");
				}

				commentJsonArr.put(jsonObject);

			}
		} else if (LEVEL2_APPROVAL1_DESIGNATION.equals(taskName)) {
			// If level2Approval2 has rejected then comment of level2App1 should not be
			// display (Parallel task condition)
			if (execution.getVariable(LEVEL2_APPROVAL2_IS_REJECTED) == null) {
				jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL2_APPROVAL1_NAME));
				jsonObject.put(KEY_CMT_DESIGNATION, LEVEL2_APPROVAL1_DESIGNATION);
				jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));

				if (task.getExecution().getVariable(LEVEL2_APPROVAL1_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL2_APPROVAL1_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS,
							task.getExecution().getVariable(LEVEL2_APPROVAL1_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL2_APPROVAL1_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
				}

				// Comment for Sales

				if (task.getExecution().getVariable(LEVEL2_APPROVAL1_SALES_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL2_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
							task.getExecution().getVariable(LEVEL2_APPROVAL1_SALES_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL2_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
				}

				if (task.getExecution().getVariable(LEVEL2_APPROVAL1_STATUS) != null) {
					jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL2_APPROVAL1_STATUS));
				} else {
					jsonObject.put(KEY_CMT_STATUS, "-");
				}
				commentJsonArr.put(jsonObject);
			}

		} else if (LEVEL2_APPROVAL2_DESIGNATION.equals(taskName)) {
			// If level2Approval1 has rejected then comment of level2App2 should not be
			// display (Parallel task condition)
			if (execution.getVariable(LEVEL2_APPROVAL1_IS_REJECTED) == null) {
				jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL2_APPROVAL2_NAME));
				jsonObject.put(KEY_CMT_DESIGNATION, LEVEL2_APPROVAL2_DESIGNATION);
				jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
				if (task.getExecution().getVariable(LEVEL2_APPROVAL2_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL2_APPROVAL2_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS,
							task.getExecution().getVariable(LEVEL2_APPROVAL2_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL2_APPROVAL2_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
				}
				// Comment for Sales

				if (task.getExecution().getVariable(LEVEL2_APPROVAL2_SALES_COMMENT) != null && !String
						.valueOf(task.getExecution().getVariable(LEVEL2_APPROVAL2_SALES_COMMENT)).trim().isEmpty()) {
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
							task.getExecution().getVariable(LEVEL2_APPROVAL2_SALES_COMMENT).toString());
				} else {
					task.getExecution().setVariable(LEVEL2_APPROVAL2_SALES_COMMENT, NO_COMMENTS);
					jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
				}

				if (task.getExecution().getVariable(LEVEL2_APPROVAL2_STATUS) != null) {
					jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL2_APPROVAL2_STATUS));
				} else {
					jsonObject.put(KEY_CMT_STATUS, "-");
				}
				commentJsonArr.put(jsonObject);
			}

		} else if (LEVEL3_APPROVAL1_DESIGNATION.equals(taskName)) {
			jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL3_APPROVAL1_NAME));
			jsonObject.put(KEY_CMT_DESIGNATION, LEVEL3_APPROVAL1_DESIGNATION);
			jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
			if (task.getExecution().getVariable(LEVEL3_APPROVAL1_COMMENT) != null
					&& !String.valueOf(task.getExecution().getVariable(LEVEL3_APPROVAL1_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS, task.getExecution().getVariable(LEVEL3_APPROVAL1_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL3_APPROVAL1_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
			}
			// Comment for Sales

			if (task.getExecution().getVariable(LEVEL3_APPROVAL1_SALES_COMMENT) != null && !String
					.valueOf(task.getExecution().getVariable(LEVEL3_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
						task.getExecution().getVariable(LEVEL3_APPROVAL1_SALES_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL3_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
			}

			if (task.getExecution().getVariable(LEVEL3_APPROVAL1_STATUS) != null) {
				jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL3_APPROVAL1_STATUS));
			} else {
				jsonObject.put(KEY_CMT_STATUS, "-");
			}
			commentJsonArr.put(jsonObject);

		} else if (LEVEL4_APPROVAL1_DESIGNATION.equals(taskName)) {
			jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL4_APPROVAL1_NAME));
			jsonObject.put(KEY_CMT_DESIGNATION, LEVEL4_APPROVAL1_DESIGNATION);
			jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
			if (task.getExecution().getVariable(LEVEL4_APPROVAL1_COMMENT) != null
					&& !String.valueOf(task.getExecution().getVariable(LEVEL4_APPROVAL1_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS, task.getExecution().getVariable(LEVEL4_APPROVAL1_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL4_APPROVAL1_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
			}
			// Comment for Sales

			if (task.getExecution().getVariable(LEVEL4_APPROVAL1_SALES_COMMENT) != null && !String
					.valueOf(task.getExecution().getVariable(LEVEL4_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
						task.getExecution().getVariable(LEVEL4_APPROVAL1_SALES_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL4_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
			}

			if (task.getExecution().getVariable(LEVEL4_APPROVAL1_STATUS) != null) {
				jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL4_APPROVAL1_STATUS));
			} else {
				jsonObject.put(KEY_CMT_STATUS, "-");
			}
			commentJsonArr.put(jsonObject);
		} else if (LEVEL5_APPROVAL1_DESIGNATION.equals(taskName)) {
			jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL5_APPROVAL1_NAME));
			jsonObject.put(KEY_CMT_DESIGNATION, LEVEL5_APPROVAL1_DESIGNATION);

			jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
			if (task.getExecution().getVariable(LEVEL5_APPROVAL1_COMMENT) != null
					&& !String.valueOf(task.getExecution().getVariable(LEVEL5_APPROVAL1_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS, task.getExecution().getVariable(LEVEL5_APPROVAL1_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL5_APPROVAL1_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
			}
			// Comment for Sales

			if (task.getExecution().getVariable(LEVEL5_APPROVAL1_SALES_COMMENT) != null && !String
					.valueOf(task.getExecution().getVariable(LEVEL5_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
						task.getExecution().getVariable(LEVEL5_APPROVAL1_SALES_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL5_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
			}

			if (task.getExecution().getVariable(LEVEL5_APPROVAL1_STATUS) != null) {
				jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL5_APPROVAL1_STATUS));
			} else {
				jsonObject.put(KEY_CMT_STATUS, "-");
			}
			commentJsonArr.put(jsonObject);
		} else if (LEVEL6_APPROVAL1_DESIGNATION.equals(taskName)) {
			jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL6_APPROVAL1_NAME));
			jsonObject.put(KEY_CMT_DESIGNATION, LEVEL6_APPROVAL1_DESIGNATION);
			jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
			if (task.getExecution().getVariable(LEVEL6_APPROVAL1_COMMENT) != null
					&& !String.valueOf(task.getExecution().getVariable(LEVEL6_APPROVAL1_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS, task.getExecution().getVariable(LEVEL6_APPROVAL1_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL6_APPROVAL1_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
			}
			// Comment for Sales

			if (task.getExecution().getVariable(LEVEL6_APPROVAL1_SALES_COMMENT) != null && !String
					.valueOf(task.getExecution().getVariable(LEVEL6_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
						task.getExecution().getVariable(LEVEL6_APPROVAL1_SALES_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL6_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
			}

			if (task.getExecution().getVariable(LEVEL6_APPROVAL1_STATUS) != null) {
				jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL6_APPROVAL1_STATUS));
			} else {
				jsonObject.put(KEY_CMT_STATUS, "-");
			}
			commentJsonArr.put(jsonObject);
		} else if (LEVEL7_APPROVAL1_DESIGNATION.equals(taskName)) {
			jsonObject.put(KEY_CMT_NAME, task.getExecution().getVariable(LEVEL7_APPROVAL1_NAME));
			jsonObject.put(KEY_CMT_DESIGNATION, LEVEL7_APPROVAL1_DESIGNATION);
			jsonObject.put(KEY_CMT_DATE, OAFCommonUtil.converDateIntoString(new Date()));
			if (task.getExecution().getVariable(LEVEL7_APPROVAL1_COMMENT) != null
					&& !String.valueOf(task.getExecution().getVariable(LEVEL7_APPROVAL1_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS, task.getExecution().getVariable(LEVEL7_APPROVAL1_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL7_APPROVAL1_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS, NO_COMMENTS);
			}
			// Comment for Sales

			if (task.getExecution().getVariable(LEVEL7_APPROVAL1_SALES_COMMENT) != null && !String
					.valueOf(task.getExecution().getVariable(LEVEL7_APPROVAL1_SALES_COMMENT)).trim().isEmpty()) {
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES,
						task.getExecution().getVariable(LEVEL7_APPROVAL1_SALES_COMMENT).toString());
			} else {
				task.getExecution().setVariable(LEVEL7_APPROVAL1_SALES_COMMENT, NO_COMMENTS);
				jsonObject.put(KEY_CMT_COMMENTS_FOR_SALES, NO_COMMENTS);
			}

			if (task.getExecution().getVariable(LEVEL7_APPROVAL1_STATUS) != null) {
				jsonObject.put(KEY_CMT_STATUS, task.getExecution().getVariable(LEVEL7_APPROVAL1_STATUS));
			} else {
				jsonObject.put(KEY_CMT_STATUS, "-");
			}
			commentJsonArr.put(jsonObject);
		} else {
			logger.info("SetCommentsTaskListener.createCommentList() ::: Task not found for adding comments");
		}
		if (commentJsonArr != null) {
			return commentJsonArr;
		} else {
			return null;
		}

	}

}
