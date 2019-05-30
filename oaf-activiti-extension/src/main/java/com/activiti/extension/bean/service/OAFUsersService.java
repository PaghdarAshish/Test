package com.activiti.extension.bean.service;

import com.activiti.domain.idm.User;
import com.activiti.extension.oaf.constants.OAFApproverLevel;
import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.service.api.UserService;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible to set the userId in execution variable get by
 * email id
 * 
 * @author Keval Bhatt
 */
@Service(value = "oafUsers")
public class OAFUsersService implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFUsersService.class);

	@Autowired
	UserService userService;

	private static Map<OAFApproverLevel, Map<String, String>> appovers = new HashMap<>();
	Map<OAFApproverLevel, String> designation = new HashMap<>();

	@PostConstruct
	public void init() {
		Map<String, String> level1Approval1 = new HashMap<>();
		level1Approval1.put("email", LEVEL1_APPROVAL1_EMAILID);
		level1Approval1.put("id", LEVEL1_APPROVAL1_ID);
		level1Approval1.put("name", LEVEL1_APPROVAL1_NAME);
		level1Approval1.put("designation", LEVEL1_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level1Approval2 = new HashMap<>();
		level1Approval2.put("email", LEVEL1_APPROVAL2_EMAILID);
		level1Approval2.put("id", LEVEL1_APPROVAL2_ID);
		level1Approval2.put("name", LEVEL1_APPROVAL2_NAME);
		level1Approval2.put("designation", LEVEL1_APPROVAL2_DESIGNATION_KEY);

		Map<String, String> level2Approval1 = new HashMap<>();
		level2Approval1.put("email", LEVEL2_APPROVAL1_EMAILID);
		level2Approval1.put("id", LEVEL2_APPROVAL1_ID);
		level2Approval1.put("name", LEVEL2_APPROVAL1_NAME);
		level2Approval1.put("designation", LEVEL2_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level2Approval2 = new HashMap<>();
		level2Approval2.put("email", LEVEL2_APPROVAL2_EMAILID);
		level2Approval2.put("id", LEVEL2_APPROVAL2_ID);
		level2Approval2.put("name", LEVEL2_APPROVAL2_NAME);
		level2Approval2.put("designation", LEVEL2_APPROVAL2_DESIGNATION_KEY);

		Map<String, String> level3Approval1 = new HashMap<>();
		level3Approval1.put("email", LEVEL3_APPROVAL1_EMAILID);
		level3Approval1.put("id", LEVEL3_APPROVAL1_ID);
		level3Approval1.put("name", LEVEL3_APPROVAL1_NAME);
		level3Approval1.put("designation", LEVEL3_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level4Approval1 = new HashMap<>();
		level4Approval1.put("email", LEVEL4_APPROVAL1_EMAILID);
		level4Approval1.put("id", LEVEL4_APPROVAL1_ID);
		level4Approval1.put("name", LEVEL4_APPROVAL1_NAME);
		level4Approval1.put("designation", LEVEL4_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level5Approval1 = new HashMap<>();
		level5Approval1.put("email", LEVEL5_APPROVAL1_EMAILID);
		level5Approval1.put("id", LEVEL5_APPROVAL1_ID);
		level5Approval1.put("name", LEVEL5_APPROVAL1_NAME);
		level5Approval1.put("designation", LEVEL5_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level6Approval1 = new HashMap<>();
		level6Approval1.put("email", LEVEL6_APPROVAL1_EMAILID);
		level6Approval1.put("id", LEVEL6_APPROVAL1_ID);
		level6Approval1.put("name", LEVEL6_APPROVAL1_NAME);
		level6Approval1.put("designation", LEVEL6_APPROVAL1_DESIGNATION_KEY);

		Map<String, String> level7Approval1 = new HashMap<>();
		level7Approval1.put("email", LEVEL7_APPROVAL1_EMAILID);
		level7Approval1.put("id", LEVEL7_APPROVAL1_ID);
		level7Approval1.put("name", LEVEL7_APPROVAL1_NAME);
		level7Approval1.put("designation", LEVEL7_APPROVAL1_DESIGNATION_KEY);

		appovers.put(OAFApproverLevel.LEVEL1APPROVAL1, level1Approval1);
		appovers.put(OAFApproverLevel.LEVEL1APPROVAL2, level1Approval2);
		appovers.put(OAFApproverLevel.LEVEL2APPROVAL1, level2Approval1);
		appovers.put(OAFApproverLevel.LEVEL2APPROVAL2, level2Approval2);
		appovers.put(OAFApproverLevel.LEVEL3APPROVAL1, level3Approval1);
		appovers.put(OAFApproverLevel.LEVEL4APPROVAL1, level4Approval1);
		appovers.put(OAFApproverLevel.LEVEL5APPROVAL1, level5Approval1);
		appovers.put(OAFApproverLevel.LEVEL6APPROVAL1, level6Approval1);
		appovers.put(OAFApproverLevel.LEVEL7APPROVAL1, level7Approval1);

		designation.put(OAFApproverLevel.LEVEL1APPROVAL1, LEVEL1_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL1APPROVAL2, LEVEL1_APPROVAL2_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL2APPROVAL1, LEVEL2_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL2APPROVAL2, LEVEL2_APPROVAL2_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL3APPROVAL1, LEVEL3_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL4APPROVAL1, LEVEL4_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL5APPROVAL1, LEVEL5_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL6APPROVAL1, LEVEL6_APPROVAL1_DESIGNATION);
		designation.put(OAFApproverLevel.LEVEL7APPROVAL1, LEVEL7_APPROVAL1_DESIGNATION);

	}

	private void setApprovers(DelegateExecution execution, OAFApproverLevel approverLevel, String email) {

		// validating if email input is null
		if (email == null) {
			return;
		}

		User userDetails = this.userService.findUserByEmail(email);

		if (userDetails != null) {
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("email"), userDetails.getEmail());
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("id"), userDetails.getId());
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("name"), userDetails.getFullName());
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("designation"),
					designation.get(approverLevel));
		} else {
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("email"), null);
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("id"), null);
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("name"), null);
			execution.setVariable(OAFUsersService.appovers.get(approverLevel).get("designation"),
					designation.get(approverLevel));
		}

	}

	/**
	 * This method is responsible to set the userIds in execution variables for
	 * approvals
	 * 
	 * @param execution
	 */
	public void setUsers(DelegateExecution execution) {
		try {

			if (execution.getVariable(LEVEL1_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL1_APPROVAL1_ALLOW)) {
				String level1Approval1 = (String) execution.getVariable(LEVEL1_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL1APPROVAL1, level1Approval1);
				execution.removeVariable(LEVEL1_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL1 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL1_APPROVAL1_ID) + " :: "+ LEVEL1_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL1_APPROVAL1_NAME));						
			}
			else {
				execution.removeVariable(LEVEL1_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL1_APPROVAL2_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL1_APPROVAL2_ALLOW)) {
				String level1Approval2 = (String) execution.getVariable(LEVEL1_APPROVAL2_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL1APPROVAL2, level1Approval2);
				execution.removeVariable(LEVEL1_APPROVAL2_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL1 Approval2 ::  UserId :: "+ execution.getVariable(LEVEL1_APPROVAL2_ID) + " :: "+ LEVEL1_APPROVAL2_DESIGNATION +" :: "+execution.getVariable(LEVEL1_APPROVAL2_NAME));
				}
			else {
				execution.removeVariable(LEVEL1_APPROVAL2_ID);
			}
			if (execution.getVariable(LEVEL2_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL2_APPROVAL1_ALLOW)) {
				String level2Approval1 = (String) execution.getVariable(LEVEL2_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL2APPROVAL1, level2Approval1);
				execution.removeVariable(LEVEL2_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL2 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL2_APPROVAL1_ID) + " :: "+ LEVEL2_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL2_APPROVAL1_NAME));
			
			}
			else {
				execution.removeVariable(LEVEL2_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL2_APPROVAL2_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL2_APPROVAL2_ALLOW)) {
				String level2Approval2 = (String) execution.getVariable(LEVEL2_APPROVAL2_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL2APPROVAL2, level2Approval2);
				execution.removeVariable(LEVEL2_APPROVAL2_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL2 Approval2 ::  UserId :: "+ execution.getVariable(LEVEL2_APPROVAL2_ID) + " :: "+ LEVEL2_APPROVAL2_DESIGNATION +" :: "+execution.getVariable(LEVEL2_APPROVAL2_NAME));		
			}
			else {
				execution.removeVariable(LEVEL2_APPROVAL2_ID);
			}
			if (execution.getVariable(LEVEL3_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL3_APPROVAL1_ALLOW)) {
				String level3Approval1 = (String) execution.getVariable(LEVEL3_APPROVAL1_ID);
				execution.removeVariable(LEVEL3_APPROVAL1_ALLOW);
				this.setApprovers(execution, OAFApproverLevel.LEVEL3APPROVAL1, level3Approval1);
				logger.info("OAFUsers.setUsers():: LEVEL3 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL3_APPROVAL1_ID) + " :: "+ LEVEL3_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL3_APPROVAL1_NAME));
			}
			else {
				execution.removeVariable(LEVEL3_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL4_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL4_APPROVAL1_ALLOW)) {
				String level4Approval1 = (String) execution.getVariable(LEVEL4_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL4APPROVAL1, level4Approval1);
				execution.removeVariable(LEVEL4_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL4 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL4_APPROVAL1_ID) + " :: "+ LEVEL4_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL4_APPROVAL1_NAME));		
				
			}
			else {
				execution.removeVariable(LEVEL4_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL5_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL5_APPROVAL1_ALLOW)) {
				String level5Approval1 = (String) execution.getVariable(LEVEL5_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL5APPROVAL1, level5Approval1);
				execution.removeVariable(LEVEL5_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL5 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL5_APPROVAL1_ID) + " :: "+ LEVEL5_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL5_APPROVAL1_NAME));		
				}
			else {
				execution.removeVariable(LEVEL5_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL6_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL6_APPROVAL1_ALLOW)) {
				String level6Approval1 = (String) execution.getVariable(LEVEL6_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL6APPROVAL1, level6Approval1);
				execution.removeVariable(LEVEL6_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL6 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL6_APPROVAL1_ID) + " :: "+ LEVEL6_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL6_APPROVAL1_NAME));
			}else {
				execution.removeVariable(LEVEL6_APPROVAL1_ID);
			}
			if (execution.getVariable(LEVEL7_APPROVAL1_ALLOW) != null
					&& (Boolean) execution.getVariable(LEVEL7_APPROVAL1_ALLOW)) {
				String level7Approval1 = (String) execution.getVariable(LEVEL7_APPROVAL1_ID);
				this.setApprovers(execution, OAFApproverLevel.LEVEL7APPROVAL1, level7Approval1);
				execution.removeVariable(LEVEL7_APPROVAL1_ALLOW);
				logger.info("OAFUsers.setUsers():: LEVEL7 Approval1 ::  UserId :: "+ execution.getVariable(LEVEL7_APPROVAL1_ID) + " :: "+ LEVEL7_APPROVAL1_DESIGNATION +" :: "+execution.getVariable(LEVEL7_APPROVAL1_NAME));
			}
			else {
				execution.removeVariable(LEVEL7_APPROVAL1_ID);
			}
			logger.info("-------------------------------------------------------------------------------------------");

		} catch (Exception e) {
			logger.error("OAFUsers.setUsers():: Exception :: " + e);
		}

	}

}
