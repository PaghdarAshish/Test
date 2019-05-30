package com.activiti.extension.api;

import com.activiti.extension.bean.service.OAFService;
import com.activiti.extension.oaf.model.UIQuotationDetail;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * RestController for OAF.
 * @author Pradip Patel
 */

@RestController
@RequestMapping("/enterprise")
@Api(value = "manageOAF", tags = "Store OAF Json to APS and allow APS to process OAF in Activiti")
public class OAFController {

    @Autowired
    private OAFService orderApprovalService;

    @RequestMapping(value = "/manageOAF", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Persist OAF json at APS database, update status for individual record", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request completed successfully"),
            @ApiResponse(code = 401, message = "You are not authorized process this request"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<?> manageOAF(@Valid @RequestBody UIQuotationDetail quotationDetail) {
        return this.orderApprovalService.manageOAF(quotationDetail);
    }


    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
        return this.orderApprovalService.updateProcessStatus(processInstanceId, status, rejectTaskId);
    }

//    @RequestMapping(value = "/sendAttachmentFailEmail", method = RequestMethod.POST, produces = "com.activiti.extension.oaf.model.OAFResponse")
    @RequestMapping(value = "/sendAttachmentFailEmail", method = RequestMethod.POST)
    public ResponseEntity<?> attachmentFail(@RequestParam("inquiryNumber") String inquiryNumber, @RequestParam("quotationNumber") String quotationNumber) {
        return this.orderApprovalService.sendAttachmentEmail(inquiryNumber,quotationNumber);
    }


    @RequestMapping(value = {"/restartProcess"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity restartProcessRequest(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("inquiry") String inquiry) {
        return this.orderApprovalService.createRestartProcessRequest(processInstanceId, inquiry);
    }
}