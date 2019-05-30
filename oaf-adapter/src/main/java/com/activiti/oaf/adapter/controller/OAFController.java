package com.activiti.oaf.adapter.controller;

import com.activiti.oaf.adapter.bean.OrderApproval;
import com.activiti.oaf.adapter.constants.OAFConstants;
import com.activiti.oaf.adapter.constants.OAFStatus;
import com.activiti.oaf.adapter.model.OAFResponse;
import com.activiti.oaf.adapter.model.UIQuotationDetail;
import com.activiti.oaf.adapter.service.OAFProcessService;
import com.activiti.oaf.adapter.service.OAFService;
import com.activiti.oaf.adapter.util.OAFUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * RestController for OAF.
 * @author Pradip Patel
 */
@RestController
@Api(value = "manageOAF", tags = "Manage OAF data into DB")
public class OAFController implements OAFConstants {

    public static final String KSCHL_UNIQUE_FIELD_VALUE = "YKES";
    @Value("${oaf.manage-oaf.start-workflow.enable}")
    private Boolean START_WORKFLOW;
    @Autowired
    private OAFService orderApprovalService;
    @Autowired
    private OAFProcessService orderApprovalProcessService;
    @Autowired
    private ObjectMapper objectMapper;
    private Logger logger = LoggerFactory.getLogger(OAFController.class);

    @PostMapping(value = {"/manageOAF"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Persist OAF json into database, update status for individual record", response = OAFResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request completed successfully"),
            @ApiResponse(code = 401, message = "You are not authorized process this request"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity manageOAF(@Valid @RequestBody UIQuotationDetail quotationDetail) {
        // set status ::
        // new, restart, update
        if (logger.isDebugEnabled()) {
            logger.debug("OAFController.manageOAF");
            logger.debug("Input Json payload :: \n" + OAFUtil.convertQuoteToJsonString(quotationDetail));
        }
        return this.orderApprovalService.manageOAF(quotationDetail, START_WORKFLOW);
    }

    @GetMapping(value = {"manageOAF"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity manageOAFGet() {
        Map<String, String> response = new HashMap<>(1);
        response.put("status", "OAF adapter application is working");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = {"updateStatus"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update status of individual record based on process instance id", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request completed successfully"),
            @ApiResponse(code = 400, message = "Required parameter not found ,\nInvalid status value found, please provide correct value ,\n No record found for provided processInstanceId value"),
            @ApiResponse(code = 401, message = "You are not authorized process this request"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", dataType = "String", required = true, paramType = "form"),
            @ApiImplicitParam(name = "status", dataType = "String", required = true, paramType = "form", allowableValues = "COMPLETE, REJECT")}
    )
    public ResponseEntity updateStatus(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") String status, @RequestParam("rejectTaskId") String rejectTaskId) {
/*
        Map<String, String> response = new HashMap<>();

        if ((processInstanceId == null || StringUtils.isEmpty(processInstanceId)) || (status == null || StringUtils.isEmpty(status)) || rejectTaskId == null)
        {
            logger.error("required parameter not found.");
            response.put("success", "false");
            response.put("errorCode","400");
            response.put("message", "Required parameter not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else{
            long count = Arrays.stream(OAFStatus.values()).filter(oafStatus -> oafStatus.toString().equals(status.toUpperCase())).count();
            if (count == 0) {
                response.put("success", "false");
                response.put("errorCode","400");
                response.put("message", "Invalid status value found, please provide correct value");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            OrderApproval orderApproval = this.orderApprovalService.updateStatus(processInstanceId, status,rejectTaskId);
            if (orderApproval == null) {
                response.put("success", "false");
                response.put("errorCode","400");
                response.put("message", "No record found for provided processInstanceId");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                response.put("success", "true");
                response.put("errorCode","");
                response.put("message", "quote successfully updated");
                return new ResponseEntity<>(response, HttpStatus.OK);

            }
    }*/
        return this.orderApprovalService.updateStatus(processInstanceId, status, rejectTaskId);
    }

    @ApiIgnore
    @GetMapping(value = "sap/bc/zalfstatus/{inquiry}.{quote}.{status}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity sapMockController(@PathVariable("inquiry") String inquiry, @PathVariable("quote") String quote, @PathVariable("status") String status, @RequestParam("sap-client") String sapClient, @RequestParam("saml2") String saml2) {
        //    /sap/bc/zalfstatus/
        //    ?sap-client=210&saml2=disabled
        if (logger.isDebugEnabled()) {
            logger.debug("path :: " + "/sap/bc/zalfstatus/" + inquiry + "." + quote + "." + status + "?sap-client=" + sapClient + "&saml2=" + saml2);
            logger.debug("Input Data  :: 1). inquiry = " + inquiry + " \n" +
                    "2). quote = " + quote + " \n" +
                    "3). status = " + status + " \n" +
                    "4). sap-client = " + sapClient + " \n" +
                    "5). saml2 = " + saml2);
        }
        return this.orderApprovalService.updateQuoteInSAP(inquiry, quote, status);
    }

    @PostMapping(value = {"restartProcess"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity autoRestartProcessRequest(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("inquiry") String inquiry) {
        return this.orderApprovalService.createAutoRestartRequest(processInstanceId, inquiry, OAFStatus.START.toString(),START_WORKFLOW);
    }
    
    @ApiIgnore
    @GetMapping(value = "findByInquiry/{inquiry}")
    public List<OrderApproval> findByInquiry(@PathVariable("inquiry") String inquiry) {
        if (logger.isDebugEnabled()) {
            logger.debug("path :: " + "/findByInquiry/" + inquiry);
            logger.debug("Input Data  :: 1). inquiry = " + inquiry);
        }
        return this.orderApprovalService.getOrderApprovalByInquiry(inquiry);
    }
}
