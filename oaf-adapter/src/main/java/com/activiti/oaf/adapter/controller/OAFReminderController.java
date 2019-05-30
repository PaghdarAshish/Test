package com.activiti.oaf.adapter.controller;

import static com.activiti.oaf.adapter.constants.OAFConstants.ERROR;
import static com.activiti.oaf.adapter.constants.OAFConstants.SUCCESS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.activiti.oaf.adapter.bean.OrderApprovalReminderMailDetails;
import com.activiti.oaf.adapter.service.OAFReminderAuditService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This controller class is responsible to get the request for reminder audit
 * and save it in db table
 * 
 * @author Keval Bhatt
 *
 */
@RestController
public class OAFReminderController {

	@Autowired
	private OAFReminderAuditService oafReminderAuditService;

	private Logger logger = LoggerFactory.getLogger(OAFReminderController.class);

	/**
	 * This method is responsible to get the requested data and save it
	 * 
	 * @param approvalReminderMailDetails
	 * @return
	 */
	@PostMapping(value = { "/saveReminderAudit" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String saveReportRecord(@RequestBody OrderApprovalReminderMailDetails approvalReminderMailDetails) {
		JsonObject responseJson = new JsonObject();
		int responseCode = oafReminderAuditService.saveReminderDetail(approvalReminderMailDetails);
		if (responseCode > 0) {
			responseJson.addProperty(SUCCESS, "Reminder audit updated");
		} else {
			responseJson.addProperty(ERROR, "Reminder audit failed to update , please check the log ");
		}
		return new Gson().toJson(responseJson, JsonObject.class);

	}

	/**
	 * This method is responsible to return the reminder details based on the
	 * inqnumber
	 * 
	 * @param inqNumber
	 * @return
	 */
	@GetMapping(value = "/getReminderAudit", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getReminderAudit(@RequestParam String inqNumber) {
		try {
			List<OrderApprovalReminderMailDetails> results = oafReminderAuditService
					.searchRemindersByInqNumber(inqNumber);
			return new Gson().toJson(results, List.class);
		} catch (Exception e) {
			JsonObject errorJson = new JsonObject();
			logger.error("OAFReportController.getReminderAudit()::: Exception :" + e);
			errorJson.addProperty(ERROR, "There is a problem with getting audit, please check the log.");
			return new Gson().toJson(errorJson, JsonObject.class);

		}
	}

}
