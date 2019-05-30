package com.activiti.oaf.adapter.controller;

import static com.activiti.oaf.adapter.constants.OAFConstants.APPROVED;
import static com.activiti.oaf.adapter.constants.OAFConstants.APPROVED_DATE;
import static com.activiti.oaf.adapter.constants.OAFConstants.COMMON_ELEMENTS;
import static com.activiti.oaf.adapter.constants.OAFConstants.COMPANY_CODE;
import static com.activiti.oaf.adapter.constants.OAFConstants.CREATION_DATE;
import static com.activiti.oaf.adapter.constants.OAFConstants.DQ;
import static com.activiti.oaf.adapter.constants.OAFConstants.ELAPSED_DAY;
import static com.activiti.oaf.adapter.constants.OAFConstants.ERROR;
import static com.activiti.oaf.adapter.constants.OAFConstants.INQ;
import static com.activiti.oaf.adapter.constants.OAFConstants.LAST_APPROVAL_DATE;
import static com.activiti.oaf.adapter.constants.OAFConstants.LAST_APPROVAL_USER;
import static com.activiti.oaf.adapter.constants.OAFConstants.PENDING;
import static com.activiti.oaf.adapter.constants.OAFConstants.PENDING_WITH;
import static com.activiti.oaf.adapter.constants.OAFConstants.PROCESS_ID;
import static com.activiti.oaf.adapter.constants.OAFConstants.QUOTE;
import static com.activiti.oaf.adapter.constants.OAFConstants.REJECTED;
import static com.activiti.oaf.adapter.constants.OAFConstants.REJECTION_DATE;
import static com.activiti.oaf.adapter.constants.OAFConstants.REJECTION_REASON;
import static com.activiti.oaf.adapter.constants.OAFConstants.REJECTION_USER;
import static com.activiti.oaf.adapter.constants.OAFConstants.SALES_ENGINEER;
import static com.activiti.oaf.adapter.constants.OAFConstants.SALES_OFFICE;
import static com.activiti.oaf.adapter.constants.OAFConstants.SOLD_TO_PARTY;
import static com.activiti.oaf.adapter.constants.OAFConstants.SUCCESS;
import static com.activiti.oaf.adapter.constants.OAFConstants.TASK_ID;
import static com.activiti.oaf.adapter.constants.OAFConstants.TONNAGE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

import com.activiti.oaf.adapter.bean.OrderApprovalReportDetails;
import com.activiti.oaf.adapter.service.OAFReportDetailsService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This controller class is responsible to save, update and delete report data
 * 
 * @author Keval Bhatt
 *
 */
@RestController
public class OAFReportController {

	@Autowired
	private OAFReportDetailsService orderApprovalReportDetailsService;
	private Logger logger = LoggerFactory.getLogger(OAFReportController.class);

	/**
	 * This method is used to get the requested data and save it in table
	 * 
	 * @param orderApprovalReportDetails
	 * @return
	 */
	@PostMapping(value = { "/saveReportRecord" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String saveReportRecord(@RequestBody OrderApprovalReportDetails orderApprovalReportDetails) {
		JsonObject responseJson = new JsonObject();
		int responseCode = orderApprovalReportDetailsService.saveReportData(orderApprovalReportDetails);
		if (responseCode > 0) {
			responseJson.addProperty(SUCCESS, "Report successfully updated");
		} else {
			responseJson.addProperty(ERROR, "Report failed to update , please check the log ");
		}
		return new Gson().toJson(responseJson, JsonObject.class);

	}

	/**
	 * This method get the inqnumber and delete the record if it exists
	 * 
	 * @param inqNumber
	 * @return
	 */
	@GetMapping(value = "/deleteReportEntry", produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteEntry(@RequestParam String inqNumber) {
		JsonObject responseJson = new JsonObject();
		if (inqNumber != null && !inqNumber.isEmpty()) {
			logger.debug("OAFReportController.deleteEntry() :: InqNumber :: " + inqNumber);
			int responseCode = orderApprovalReportDetailsService.deleteReportByInqNumber(inqNumber);
			if (responseCode > 0) {
				responseJson.addProperty(SUCCESS, "Reprot with inqNumber " + inqNumber + " is successfully deleted");
			} else {
				responseJson.addProperty(ERROR, "Reprot with inqNumber " + inqNumber
						+ " is failed to delete, it might be already deleted or check the log");
			}
		} else {
			responseJson.addProperty(ERROR, "Request parameter inqNumber is empty");
		}
		return new Gson().toJson(responseJson, JsonObject.class);
	}

	@PostMapping(value = "/getOAFReportDetail", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getReportsDetails(@RequestBody OrderApprovalReportDetails orderApprovalReportDetails) {
		try {
			logger.info("OAFController.getReportsDetails() :: JSON :: " + orderApprovalReportDetails.toString());
			String company_code = "";
			if (orderApprovalReportDetails.getCompany_code() != null) {
				company_code = String.valueOf(orderApprovalReportDetails.getCompany_code());
			}
			String quote_type = "";
			if (orderApprovalReportDetails.getQuote_type() != null) {
				quote_type = String.valueOf(orderApprovalReportDetails.getQuote_type());
			}
			Date fromDate = null;
			if (orderApprovalReportDetails.getFromDate() != null) {
				fromDate = new Date(orderApprovalReportDetails.getFromDate());
			}
			Date toDate = null;
			if (orderApprovalReportDetails.getToDate() != null) {
				toDate = new Date(orderApprovalReportDetails.getToDate());
			}
			List<OrderApprovalReportDetails> reportDetails = orderApprovalReportDetailsService
					.findByReportDetail(company_code, quote_type, fromDate, toDate);

			if (reportDetails != null) {
				HashMap<String, JsonArray> finalReportGson = convertToHashMapGson(reportDetails);
				return new Gson().toJson(finalReportGson, HashMap.class);
			} else {
				JsonObject errorJson = new JsonObject();
				errorJson.addProperty(ERROR, "There is a problem with getting report, please check the log.");
				return new Gson().toJson(errorJson, JsonObject.class);

			}

		} catch (Exception e) {
			JsonObject errorJson = new JsonObject();
			logger.error("OAFReportController.getReportsDetails()::: Exception :" + e);
			errorJson.addProperty(ERROR, "There is a problem with getting report, please check the log.");
			return new Gson().toJson(errorJson, JsonObject.class);

		}
	}

	/**
	 * This method will return HashMap data of report
	 * 
	 * @param reportDetails
	 * @return
	 */
	private HashMap<String, JsonArray> convertToHashMapGson(List<OrderApprovalReportDetails> reportDetails) {
		HashMap<String, JsonArray> reportGsonHashMap = new HashMap<String, JsonArray>();

		JsonObject commonJson = null;
		JsonObject approvalJson = null;
		JsonObject pendingJson = null;
		JsonObject rejectionJson = null;
		JsonArray approvalJsonArr = new JsonArray();
		JsonArray pendingJsonArr = new JsonArray();
		JsonArray rejectionJsonArr = new JsonArray();
		String formatedDate = "";
		for (OrderApprovalReportDetails reports : reportDetails) {
			commonJson = new JsonObject();
			approvalJson = new JsonObject();
			pendingJson = new JsonObject();
			rejectionJson = new JsonObject();
			// To check all nulls
			if (reports.getCompany_code() != null) {
				commonJson.addProperty(COMPANY_CODE, reports.getCompany_code());
			} else {
				commonJson.addProperty(COMPANY_CODE, "-");
			}
			if (reports.getInq() != null) {
				commonJson.addProperty(INQ, reports.getInq());
			} else {
				commonJson.addProperty(INQ, "-");
			}
			if (reports.getQuote() != null) {
				commonJson.addProperty(QUOTE, reports.getQuote());
			} else {
				commonJson.addProperty(QUOTE, "-");
			}
			if (reports.getDq() != null) {
				commonJson.addProperty(DQ, reports.getDq());
			} else {
				commonJson.addProperty(DQ, "-");
			}
			if (reports.getCreation_date() != null) {
				formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(reports.getCreation_date());
				commonJson.addProperty(CREATION_DATE, formatedDate);

				commonJson.addProperty(CREATION_DATE, formatedDate);
			} else {
				commonJson.addProperty(CREATION_DATE, "-");
			}
			if (reports.getSold_to_party() != null) {
				commonJson.addProperty(SOLD_TO_PARTY, reports.getSold_to_party());
			} else {
				commonJson.addProperty(SOLD_TO_PARTY, "-");
			}
			if (reports.getTonnage() != null) {
				commonJson.addProperty(TONNAGE, reports.getTonnage());
			} else {
				commonJson.addProperty(TONNAGE, "-");
			}
			if (reports.getElapsed_day() != null) {
				commonJson.addProperty(ELAPSED_DAY, reports.getElapsed_day());
			} else {
				commonJson.addProperty(ELAPSED_DAY, "-");
			}
			if (reports.getSales_office() != null) {
				commonJson.addProperty(SALES_OFFICE, reports.getSales_office());
			} else {
				commonJson.addProperty(SALES_OFFICE, "-");
			}

			if (reports.getSales_engineer() != null) {
				commonJson.addProperty(SALES_ENGINEER, reports.getSales_engineer());
			} else {
				commonJson.addProperty(SALES_ENGINEER, "-");
			}

			if (reports.getProcess_id() != null) {
				commonJson.addProperty(PROCESS_ID, reports.getProcess_id());
			} else {
				commonJson.addProperty(PROCESS_ID, "-");
			}
			if (reports.getTask_id() != null) {
				commonJson.addProperty(TASK_ID, reports.getTask_id());
			} else {
				commonJson.addProperty(TASK_ID, "");
			}
			if (reports.getQuote_type().equalsIgnoreCase("Approval")) {
				approvalJson.add(COMMON_ELEMENTS, commonJson);
				if (reports.getApproved_date() != null) {
					approvalJson.addProperty(APPROVED_DATE, reports.getApproved_date().toString());
				} else {
					approvalJson.addProperty(APPROVED_DATE, "-");
				}
				approvalJsonArr.add(approvalJson);
			}
			if (reports.getQuote_type().equalsIgnoreCase("Rejection")) {
				rejectionJson.add(COMMON_ELEMENTS, commonJson);
				if (reports.getRejection_date() != null) {
					rejectionJson.addProperty(REJECTION_DATE, reports.getRejection_date().toString());
				} else {
					rejectionJson.addProperty(REJECTION_DATE, "-");
				}
				if (reports.getRejection_reason() != null) {
					rejectionJson.addProperty(REJECTION_REASON, reports.getRejection_reason());
				} else {
					rejectionJson.addProperty(REJECTION_REASON, "-");
				}
				if (reports.getRejection_user() != null) {
					rejectionJson.addProperty(REJECTION_USER, reports.getRejection_user());
				} else {
					rejectionJson.addProperty(REJECTION_USER, "-");
				}
				rejectionJsonArr.add(rejectionJson);
			}
			if (reports.getQuote_type().equalsIgnoreCase("Pending")) {
				pendingJson.add(COMMON_ELEMENTS, commonJson);

				if (reports.getPending_with() != null) {
					pendingJson.addProperty(PENDING_WITH, reports.getPending_with());
				} else {
					pendingJson.addProperty(PENDING_WITH, "-");
				}
				if (reports.getLast_approval_date() != null) {
					pendingJson.addProperty(LAST_APPROVAL_DATE, reports.getLast_approval_date());
				} else {
					pendingJson.addProperty(LAST_APPROVAL_DATE, "-");
				}
				if (reports.getLast_approval_user() != null) {
					pendingJson.addProperty(LAST_APPROVAL_USER, reports.getLast_approval_user());
				} else {
					pendingJson.addProperty(LAST_APPROVAL_USER, "-");
				}
				pendingJsonArr.add(pendingJson);
			}
		}

		reportGsonHashMap.put(APPROVED, approvalJsonArr);
		reportGsonHashMap.put(PENDING, pendingJsonArr);
		reportGsonHashMap.put(REJECTED, rejectionJsonArr);

		return reportGsonHashMap;
	}
}
