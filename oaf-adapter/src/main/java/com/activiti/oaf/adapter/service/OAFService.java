package com.activiti.oaf.adapter.service;

import com.activiti.oaf.adapter.bean.OrderApproval;
import com.activiti.oaf.adapter.constants.OAFAction;
import com.activiti.oaf.adapter.constants.OAFConstants;
import com.activiti.oaf.adapter.constants.OAFStatus;
import com.activiti.oaf.adapter.model.*;
import com.activiti.oaf.adapter.repository.OrderApprovalRepository;
import com.activiti.oaf.adapter.util.OAFOperation;
import com.activiti.oaf.adapter.util.OAFUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Bean for OAFService related operations
 * @author Pradip Patel
 */
@Service
public class OAFService {

    private static final Logger logger = LoggerFactory.getLogger(OAFService.class);
    @Autowired
    private OrderApprovalRepository orderApprovalRepository;
    @Autowired
    private OAFProcessService orderApprovalProcessService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OAFOperation oafOperation;

    /**
     * This method is responsible to get OrderApproval
     * @param processInstanceId
     * @return {@link OrderApproval
     */
    private OrderApproval getUniqueOrderApprovalByProcessInstanceId(String processInstanceId) {
        return this.orderApprovalRepository.findUniqueOrderApprovalByProcessInstanceId(processInstanceId);
    }

    /**
     * This method is responsible to get OrderApproval based on following parameters :
     * @param processInstanceId
     * @param inquiry
     * @param status
     * @return {@link OrderApproval
     */
    private OrderApproval getOrderApprovalByProcessInstanceIdInquiryNoAndStatus(String processInstanceId, String inquiry, String status) {
        return this.orderApprovalRepository.findOrderApprovalByProcessInstanceIdInquiryNoAndStatus(processInstanceId, inquiry, status);
    }

    /**
     * This method is responsible to save {@link OrderApproval}
     * @param {@link OrderApproval}
     * @return {@link OrderApproval}
     */
    private OrderApproval saveOrderApproval(OrderApproval orderApproval) {
        return this.orderApprovalRepository.saveAndFlush(orderApproval);
    }

    /**
     * This method is responsible to retrieve {@link List<OrderApproval>} based on status
     * @param status : {@link OAFStatus}
     * @return {@link List<OrderApproval>}
     */
    public List<OrderApproval> getOrderApprovals(String status) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFService.getOrderApprovalsByStatus");
        }
        return this.orderApprovalRepository.getOrderApprovalsByStatus(status);
    }

    /**
     * This method is responsible to generate {@link OrderApproval} based on : {@link UIQuotationDetail}
     * @param quotationDetail
     * @return : {@link OrderApproval}
     * @throws Exception
     */
    private OrderApproval generateNewQuotation(UIQuotationDetail quotationDetail) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFService.generateNewQuotation");
        }
        OrderApproval orderApproval = new OrderApproval();

        orderApproval.setInquiryNumber(quotationDetail.getHeaderSegment().getInq().getValue());
        orderApproval.setQuotationNumber(quotationDetail.getHeaderSegment().getQuo().getValue());
        orderApproval.setCreated(new Date());
        orderApproval.setProcessInstanceId("0");
        orderApproval.setVersion(1);
        orderApproval.setAction(OAFAction.NEW.toString());
        orderApproval.setStatus(OAFStatus.WAIT.toString());

        List<UIAttachment> uiAttachments = this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments(), orderApproval);
        if (uiAttachments == null) {
            orderApproval.setAttachmentFail(true);
        } else {
            quotationDetail.setAttachments(uiAttachments);
            orderApproval.setAttachmentFail(false);
        }
//        quotationDetail.setAttachments(this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments()));

//        try {
        String orderApprovalAsString = this.objectMapper.writeValueAsString(quotationDetail);
        orderApproval.setJsonPayload(orderApprovalAsString);
//        } catch (JsonProcessingException e) {
//            if (logger.isDebugEnabled()) {
//            logger.error("Error code - 101 : Input json is wrong. Please validate json and try again", e);
//            logger.error("Invalid json received :: ", e);
//            }
//            e.printStackTrace();
//        }
        return orderApproval;
    }

    /**
     * This method is responsible to save new {@link OrderApproval}
     * @param {@link OrderApproval}
     * @return {@link OrderApproval}
     */
    private OrderApproval saveNewQuotation(OrderApproval orderApproval) {
        return this.orderApprovalRepository.saveAndFlush(orderApproval);
    }

    /**
     * This method is responsible to save updated {@link OrderApproval}
     * @param {@link OrderApproval}
     * @return {@link OrderApproval}
     */
    public OrderApproval saveUpdatedQuotation(OrderApproval orderApproval) {
        return this.orderApprovalRepository.saveAndFlush(orderApproval);
    }

    /**
     * This method is responsible to get existing {@link OrderApproval} based {@link UIQuotationDetail} inquiry Number.
     * @param {@link UIQuotationDetail}
     * @return {@link OrderApproval}
     */
    private OrderApproval getExistingOrderApproval(UIQuotationDetail quotationDetail) {
        return this.getQuoteByInquiryNo(
                quotationDetail.getHeaderSegment().getInq().getValue());
    }

    /**
     * This method is responsible to get {@link OrderApproval} by InquiryNumber
     * @param inquiryNo
     * @return {@link OrderApproval}
     */
    private OrderApproval getQuoteByInquiryNo(String inquiryNo) {
        return this.orderApprovalRepository.findOrderApprovalByInquiryNo(inquiryNo);

    }

    /**
     * This Method is responsible to update existing {@link OrderApproval} based on {@link OAFAction} and {@link OAFStatus} and return {@link OrderApproval}
     * @param existingOrderApproval  : {@link OrderApproval}
     * @param quotationDetail        : {@link UIQuotationDetail}
     * @param action                 : {@link OAFAction}
     * @param status                 : {@link OAFStatus}
     * @return                       : {@link OrderApproval}
     * @throws Exception
     */
    private OrderApproval getUpdatedExistingOrderApproval(OrderApproval existingOrderApproval, UIQuotationDetail quotationDetail, String action, String status) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("In OAFService.getUpdatedExistingOrderApproval");
        }
        existingOrderApproval.setModified(new Date());
        existingOrderApproval.setStatus(status);
        existingOrderApproval.setAction(action);
//        quotationDetail.setAttachments(this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments()));
        List<UIAttachment> uiAttachments = this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments(), existingOrderApproval);
        if (uiAttachments == null) {
            existingOrderApproval.setAttachmentFail(true);
        } else {
            quotationDetail.setAttachments(uiAttachments);
            existingOrderApproval.setAttachmentFail(false);
        }
        existingOrderApproval.setJsonPayload(OAFUtil.convertQuoteToJsonString(quotationDetail));
        return existingOrderApproval;
    }

    private OrderApproval updateExistingOrderApproval_temp1(OrderApproval existingOrderApproval, UIQuotationDetail quotationDetail, String action, String status) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("In OAFService.updateAndSaveOrderApproval");
        }
        existingOrderApproval.setModified(new Date());
        existingOrderApproval.setStatus(status);
        existingOrderApproval.setAction(action);
        List<UIAttachment> uiAttachments = this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments(), existingOrderApproval);
        if (uiAttachments == null) {
            existingOrderApproval.setAttachmentFail(true);
        } else {
            quotationDetail.setAttachments(uiAttachments);
            existingOrderApproval.setAttachmentFail(false);
        }
        existingOrderApproval.setJsonPayload(OAFUtil.convertQuoteToJsonString(quotationDetail));
        return this.orderApprovalRepository.saveAndFlush(existingOrderApproval);
    }

    /**
     *
     * @param quotationDetail :: input json payload DTO POJO
     * @param startWorkflow :: If true then process start immediately in same request.
     *                         If false then data is stored in db and scheduler is responsible to taken further action on that.
     * @return object of OAFResponse, if any error then response is as "success": "false"
 with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    public ResponseEntity<OAFResponse> manageOAF(UIQuotationDetail quotationDetail, Boolean startWorkflow) {
        OrderApproval orderApproval = null;

        String errorMessage = this.validateMandatoryFields(quotationDetail);

        if (errorMessage.length() > 0) {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "400", errorMessage);
            return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
        }

        OrderApproval existingOrderApproval = this.getExistingOrderApproval(quotationDetail);

        if (existingOrderApproval != null) {
            logger.info("Quote found");
            if (logger.isDebugEnabled()) {
                logger.debug("Quote already exist");
                logger.debug("Quote :: processID | Status :: " + existingOrderApproval.getProcessInstanceId() + " | " + existingOrderApproval.getStatus());
            }
        }

        if (quotationDetail.getHeaderSegment() != null && quotationDetail.getHeaderSegment().getErdat() != null && !quotationDetail.getHeaderSegment().getErdat().getValue().isEmpty()) {
            quotationDetail.getHeaderSegment().getErdat().setValue(oafOperation.getFormattedDate(OAFConstants.ATTACHMENT_PRE_DATE_FORMAT, OAFConstants.ATTACHMENT_POST_DATE_FORMAT, quotationDetail.getHeaderSegment().getErdat().getValue()));
        }

        if (quotationDetail.getAttachments() != null) {
            quotationDetail.getAttachments().stream().filter(attachment -> attachment.getDate() != null && !attachment.getDate().isEmpty()).forEach(attachment -> {
                attachment.setDate(oafOperation.getFormattedDate(OAFConstants.ATTACHMENT_PRE_DATE_FORMAT, OAFConstants.ATTACHMENT_POST_DATE_FORMAT, attachment.getDate()));
            });
        }

        //set attachment default category.
        this.oafOperation.setAttachmentDefaultCategory(quotationDetail);

        if (existingOrderApproval == null) {
            //start New process
            return performStartOperation(quotationDetail, startWorkflow);

        } else {
            // if found

            // check if quoteNumber is same then update/restart case.
            String quotationNumber = quotationDetail.getHeaderSegment().getQuo().getValue();
            if (quotationNumber.equalsIgnoreCase(existingOrderApproval.getQuotationNumber())) {

                if (existingOrderApproval.getStatus().equalsIgnoreCase(OAFStatus.START.toString())) {

                    boolean isProcessCancelled = false;
                    //if yes then update stop status to existing OA and start new one.
                    if (!existingOrderApproval.getProcessInstanceId().equals("0")) {
                        boolean processExist = this.orderApprovalProcessService.isProcessExist(existingOrderApproval.getProcessInstanceId());
                        if (!processExist) {
                            String processInstanceId = existingOrderApproval.getProcessInstanceId();
                            ResponseEntity<OAFResponse> responseEntity = performStopStartOperation(existingOrderApproval, quotationDetail, startWorkflow);

                            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                                existingOrderApproval.setStatus(OAFStatus.STOP.toString());
                                existingOrderApproval.setProcessInstanceId(processInstanceId);
                                this.saveUpdatedQuotation(existingOrderApproval);
                            }
                            return responseEntity;
                        }
                        /*if (!processExist) {
                            //
                            existingOrderApproval.setStatus(OAFStatus.STOP.toString());
                            OrderApproval updatedQuotation = this.saveUpdatedQuotation(existingOrderApproval);
                            if (updatedQuotation != null && updatedQuotation.getStatus().equalsIgnoreCase(OAFStatus.STOP.toString())) {
                                return performStopStartOperation(updatedQuotation, quotationDetail, startWorkflow);
                            }
                        }*/

//                        isProcessCancelled = !(this.orderApprovalProcessService.isProcessExist(existingOrderApproval.getProcessInstanceId()));
                    }

                    //verify changes in unique Fields
                    boolean isCompositeFieldsModified = this.oafOperation.verifyQuoteCompositeFields(quotationDetail, existingOrderApproval);
                    if (isCompositeFieldsModified) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Quote CompositeFieldsModified :: " + isCompositeFieldsModified);
                        }
                        //todo
                        return processRestartOperation(quotationDetail, startWorkflow, existingOrderApproval);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Need to Start workflow :: " + startWorkflow);
                        }

                        boolean highlightFields = this.oafOperation.verifyQuoteFieldsForHighlight(existingOrderApproval, quotationDetail);
                        if (highlightFields) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Quote highlightFields :: " + highlightFields);
                                logger.debug("quotationDetail = " + quotationDetail);
                            }
                            //todo
                            return performUpdateOperation(quotationDetail, startWorkflow, existingOrderApproval);
                        }
                    }
                    if (startWorkflow) {
                        if (!existingOrderApproval.getProcessInstanceId().equals("0")) {
                            logger.info("Process already started, ProcessId :: " + existingOrderApproval.getProcessInstanceId());
                        }
                    }
//            logger.info("Quote exist!");
                    // No change in json payload, Process is already running, so no action taken.
                    String message = "No change in json payload, Process is already running, so no action taken.";
                    logger.info(message);
                    OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", message);
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);

                } else if (existingOrderApproval.getStatus().equalsIgnoreCase(OAFStatus.COMPLETE.toString())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("performCompletedStartOperation");
                    }
                    return performCompletedStartOperation(existingOrderApproval, quotationDetail, startWorkflow);

                } else if (existingOrderApproval.getStatus().equalsIgnoreCase(OAFStatus.WAIT.toString())) {

                    if (existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.NEW.toString())) {
                        return performWaitExistingOperation(quotationDetail, startWorkflow, existingOrderApproval, OAFAction.NEW);
                    } else {

                        boolean isCompositeFieldsModified = this.oafOperation.verifyQuoteCompositeFields(quotationDetail, existingOrderApproval);
                        if (isCompositeFieldsModified) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Quote CompositeFieldsModified :: " + isCompositeFieldsModified);
                            }
                            return performWaitExistingOperation(quotationDetail, startWorkflow, existingOrderApproval, OAFAction.RESTART);
//                        return processRestartOperation(quotationDetail, startWorkflow, existingOrderApproval);
                        } else if ((isCompositeFieldsModified == false) && existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.RESTART.toString())) {

                            return performWaitExistingOperation(quotationDetail, startWorkflow, existingOrderApproval, OAFAction.RESTART);

                        } else {
                            boolean highlightFields = this.oafOperation.verifyQuoteFieldsForHighlight(existingOrderApproval, quotationDetail);
                            if (highlightFields) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Quote highlightFields :: " + highlightFields);
                                    logger.debug("quotationDetail = " + quotationDetail);
                                }
                                return performWaitExistingOperation(quotationDetail, startWorkflow, existingOrderApproval, OAFAction.UPDATE);
                            }
                        }
                        String message = "No change in json payload, so no action taken.";
                        logger.info(message);
                        OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", message);
                        return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                    }

                } else if (existingOrderApproval.getStatus().equalsIgnoreCase(OAFStatus.REJECT.toString())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("performRejectReInitiateOperation");
                    }
                    return performRejectReInitiateOperation(quotationDetail, startWorkflow, existingOrderApproval);
                } else if (existingOrderApproval.getStatus().equalsIgnoreCase(OAFStatus.FAIL.toString())) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("OAFStatus.FAIL");
                    }
                    if (existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.RESTART.toString())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("processRestartOperation");
                        }
                        return processRestartOperation(quotationDetail, startWorkflow, existingOrderApproval);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("performFailStartOperation");
                        }
                        return performFailStartOperation(quotationDetail, startWorkflow, existingOrderApproval, OAFAction.NEW);
                    }

                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("performRejectReInitiateOperation");
                    }
                    return performStartOperation(quotationDetail, startWorkflow);
                }

            } else {
                //stop previous version and start new ..
                // else restart process with latest data.
                return processRestartOperation(quotationDetail, startWorkflow, existingOrderApproval);
            }
        }
    }

    /**
     * This method is responsible to validate mandatory fields in manageOAF json Payload.
     * @param quotationDetail : {@link UIQuotationDetail}
     * @return Error message if any.
     */
    private String validateMandatoryFields(UIQuotationDetail quotationDetail) {
        StringBuilder message = new StringBuilder();
        if (quotationDetail == null || quotationDetail.getHeaderSegment() == null) {
            message.append("Invalid Input json");
            return message.toString();
        }
        UIHeaderSegment headerSegment = quotationDetail.getHeaderSegment();
        if (isFieldNotExist(headerSegment.getProjmt())) {
            message.append((message.length() == 0) ? "projmt" : ", projmt");
        }
        if (isFieldNotExist(headerSegment.getMarginCPP())) {
            message.append((message.length() == 0) ? "marginCPP" : ", marginCPP");
        }
        if (headerSegment.getVkbur() == null) {
            message.append((message.length() == 0) ? "vkbur" : ", vkbur");
        }
        if (isFieldNotExist(headerSegment.getKst())) {
            message.append((message.length() == 0) ? "kst" : ", kst");
        }
        if (message.length() > 0) {
            return "Required property not found :: " + message.toString();
        } else {
            return "";
        }
    }

    /**
     * This method is responsible to check is {@link HeaderElement} not exist
     * @param headerElement
     * @return true/false
     */
    private boolean isFieldNotExist(HeaderElement headerElement) {
        return headerElement == null || headerElement.getValue().trim().isEmpty();
    }

    /**
     * This method is responsible to create start process request/ start process based on startWorkflow parameter.
     * @param quotationDetail      : manageOAF request json payload.
     * @param startWorkflow        : if true then process is immediately start else it only store start request data in DB
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performStartOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow) {
        OrderApproval orderApproval;
        try {

            orderApproval = this.generateNewQuotation(quotationDetail);
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveNewQuotation(orderApproval);
            }
        } catch (Exception e) {
            //
            logger.error("Error code - 201 : Unable to create data into database", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to create data into database");
//                return responseEntity(oafResponse);
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("New Quote saved");
        if (logger.isDebugEnabled()) {
            logger.debug("New Quote saved");
            logger.debug("OrderApproval.getId() = " + orderApproval.getId());
            logger.debug("orderApproval.getInquiryNumber() = " + orderApproval.getInquiryNumber());
//            logger.debug("OrderApproval = " + orderApproval);
        }
        if (startWorkflow) {

            // start new process
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                logger.error("Error code - " + oafResponse.getErrorCode() + " : Unable to start process. Please check input json and decision tables.");
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
            } else {
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                }
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }
    }

    /**
     * This method is responsible to create start process request/ start process based on startWorkflow parameter which was previously completed.
     * @param existingOrderApproval : existingOrderApproval
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performCompletedStartOperation(OrderApproval existingOrderApproval, UIQuotationDetail quotationDetail, Boolean startWorkflow) {
        OrderApproval orderApproval;
        try {
            existingOrderApproval.setProcessInstanceId("0");
            orderApproval = this.getUpdatedOrderApproval(existingOrderApproval, quotationDetail, OAFAction.NEW.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveUpdatedQuotation(orderApproval);
            }

        } catch (Exception e) {
            //
            logger.error("Error code - 201 : Unable to create data into database", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to create data into database");
//                return responseEntity(oafResponse);
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("New Quote saved");
        if (logger.isDebugEnabled()) {
            logger.debug("New Quote saved");
            logger.debug("OrderApproval.getId() = " + orderApproval.getId());
            logger.debug("orderApproval.getInquiryNumber() = " + orderApproval.getInquiryNumber());
//            logger.debug("OrderApproval = " + orderApproval);
        }
        if (startWorkflow) {

            // start new process
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
//                logger.error("Unable to start process. Please check input json and decision tables.");
                logger.error("Error code - " + oafResponse.getErrorCode() + " : Unable to start process. Please check input json and decision tables.");
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
            } else {
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                }
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }
    }

    /**
     * This method is responsible to create start process request/ start process based on startWorkflow parameter which was previously failed.
     * @param existingOrderApproval : existingOrderApproval
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performFailStartOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow, OrderApproval existingOrderApproval, OAFAction action) {
        OrderApproval orderApproval;
        try {
            // update quote with restart action
            orderApproval = this.getUpdatedExistingOrderApproval(existingOrderApproval,
                    quotationDetail, action.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveNewQuotation(orderApproval);
            }
        } catch (Exception e) {
            //
            logger.error("Error code - 201 : Unable to create data into database", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to create data into database");
//                return responseEntity(oafResponse);
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Quote updated");
        if (logger.isDebugEnabled()) {
            logger.debug("Quote updated");
            logger.debug("OrderApproval.getId() = " + orderApproval.getId());
            logger.debug("orderApproval.getInquiryNumber() = " + orderApproval.getInquiryNumber());
//            logger.debug("OrderApproval = " + orderApproval);
        }
        if (startWorkflow) {

            // start new process
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                logger.error("Error code - " + oafResponse.getErrorCode() + " : Unable to start process. Please check input json and decision tables.");
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
            } else {
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                }
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }
    }

    /**
     * This method is responsible to create update process request/ update process based on startWorkflow parameter.
     * @param existingOrderApproval : existingOrderApproval
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performUpdateOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow, OrderApproval existingOrderApproval) {
        OrderApproval orderApproval;
        try {
            // update quote with update action
            orderApproval = this.getUpdatedOrderApproval(existingOrderApproval, quotationDetail, OAFAction.UPDATE.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveUpdatedQuotation(orderApproval);
            }

        } catch (Exception e) {
            logger.error("Error code - 201 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("update data into database successful, id :: " + orderApproval.getProcessInstanceId());
        if (logger.isDebugEnabled()) {
            logger.debug("Updated payload stored, processId :: " + orderApproval.getProcessInstanceId());
        }
        if (startWorkflow) {
            // update existing process variable
            OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, false);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
                logger.error("Error code - 202 : Unable to update data into database - " + orderApproval.getProcessInstanceId());
                // 500
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }
//                oafResponse.setMessage("Process successfully updated.");
                oafResponse.setMessage("Process has been mark as update.");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {
            logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            if (logger.isDebugEnabled()) {
                logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            }
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "Process has been mark as update.");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }
    }

    /**
     * This method is responsible to create re_initiate process request/ update process for re_initiate based on startWorkflow parameter which was previously completed.
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @param existingOrderApproval : existingOrderApproval
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performRejectReInitiateOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow, OrderApproval existingOrderApproval) {
        OrderApproval orderApproval;
        try {
            // update quote with update action
            orderApproval = this.getUpdatedExistingOrderApproval(existingOrderApproval, quotationDetail, OAFAction.RE_INITIATE.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveNewQuotation(orderApproval);
            }


        } catch (Exception e) {
            logger.error("Error code - 202 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("update data into database successful, id :: " + orderApproval.getProcessInstanceId());
        if (logger.isDebugEnabled()) {
            logger.debug("Updated payload stored, processId :: " + orderApproval.getProcessInstanceId());
        }

        if (startWorkflow) {
//            boolean decisionFailed = this.orderApprovalProcessService.getIsLevelUsersDecisionFailed(orderApproval.getProcessInstanceId());
            // update existing process variable
            OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, true);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
                logger.error("Error code - 202 : Unable to update data into database - " + orderApproval.getProcessInstanceId());
                // 500
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }

                if (orderApproval.getRejectTaskId().equalsIgnoreCase("0")) {
                    logger.error("Error code - 211 : Unable to complete hold task, Invalid taskId - " + 0);
                    OAFResponse rejectTaskResponse = this.orderApprovalProcessService.generateResponse(false, "211", "Unable to complete hold task, Invalid taskId - 0");
                    return new ResponseEntity<>(rejectTaskResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    OAFResponse oafCompleteTaskResponse = this.orderApprovalProcessService.completeActiveTask(orderApproval.getRejectTaskId());
                    if (oafCompleteTaskResponse.getSuccess().equalsIgnoreCase("true")) {
                        String processName = new StringBuilder(orderApproval.getInquiryNumber()).append("-").append(orderApproval.getQuotationNumber()).toString();
                        logger.info("Task successfully completed, Process Name ::  " + processName);
                        return new ResponseEntity<>(oafCompleteTaskResponse, HttpStatus.OK);
                    } else {
                        logger.error("Error code - 211 : Unable to complete hold task, Invalid taskId - " + 0);
                        OAFResponse rejectTaskResponse = this.orderApprovalProcessService.generateResponse(false, "211", "Unable to complete hold task, Invalid taskId - 0");
                        return new ResponseEntity<>(rejectTaskResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

            }
        } else {
            logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            if (logger.isDebugEnabled()) {
                logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            }
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }

    }

    /**
     * This method is responsible to create wait with existing action process request/  perform existing process Action based on startWorkflow parameter.
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @param existingOrderApproval : existingOrderApproval
     * @param markAsRestart         : if true then process will be restart
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performWaitExistingOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow, OrderApproval existingOrderApproval, boolean markAsRestart) {
        OrderApproval orderApproval;
        try {
            // update quote with update action
            orderApproval = this.updateExistingOrderApproval_temp1(existingOrderApproval,
                    quotationDetail, existingOrderApproval.getAction(), OAFStatus.WAIT.toString());
        } catch (Exception e) {
            logger.error("Error code - 202 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "202", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("update data into database successful, id :: " + orderApproval.getProcessInstanceId());
        if (logger.isDebugEnabled()) {
            logger.debug("Updated payload stored, processId :: " + orderApproval.getProcessInstanceId());
        }

        if (existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.NEW.toString())) {
            if (startWorkflow) {

                // start new process
                OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

                if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                    logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
                    return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
                } else {
                    String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                    logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                    }
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                }
            } else {
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }

        } else if (existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.RESTART.toString())) {
            if (startWorkflow) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Need to Start workflow :: " + startWorkflow);
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, true);
                if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
//                        logger.info("workflow restarted, id :: " + startProcessRes.getProcessInstanceId());
                    String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                    logger.info("workflow restarted :: " + processName);
                    if (logger.isDebugEnabled()) {
//                            logger.debug("Restarted workflow, id :: " + startProcessRes.getProcessInstanceId());
                        logger.debug("workflow restarted :: " + processName);
//                            logger.debug("Restarted workflow, payload :: " + startProcessRes.getJsonPayload());

                    }
                    oafResponse = this.orderApprovalProcessService.killProcess(existingOrderApproval);
                    if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                        logger.info("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                        if (logger.isDebugEnabled()) {
                            logger.debug("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                        }
                        return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                    } else {
                        logger.error("Error code - 213 : Unable to stop workflow, id : " + existingOrderApproval.getProcessInstanceId());
                        return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                    }
                } else {
//                    logger.error("Unable to start workflow for inquiry Number :: " + existingOrderApproval.getInquiryNumber());
                    logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
//                        logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                    return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                }
            } else {
                logger.info("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }

        } else {
            if (startWorkflow) {
                // update existing process variable
                OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, false);

                if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
                    logger.error("Error code - 202 : Unable to update data into database - " + orderApproval.getProcessInstanceId());
                    // 500
                    return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                    }
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                }
            } else {
                logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        }
    }

    /**
     * This method is responsible to create wait with existing action process request/  perform existing process Action based on startWorkflow parameter.
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @param existingOrderApproval : existingOrderApproval
     * @param action         : {@link OAFAction}
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performWaitExistingOperation(UIQuotationDetail quotationDetail, Boolean startWorkflow, OrderApproval existingOrderApproval, OAFAction action) {
        OrderApproval orderApproval;
        try {
            // update quote with update action
            orderApproval = this.getUpdatedExistingOrderApproval(existingOrderApproval, quotationDetail, action.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveUpdatedQuotation(orderApproval);
            }


        } catch (Exception e) {
            logger.error("Error code - 202 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "202", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("update data into database successful, id :: " + orderApproval.getProcessInstanceId());
        if (logger.isDebugEnabled()) {
            logger.debug("Updated payload stored, processId :: " + orderApproval.getProcessInstanceId());
        }

//        if (existingOrderApproval.getAction().equalsIgnoreCase(OAFAction.NEW.toString())) {
        /*if (action.equals(OAFAction.NEW)) {
        } else*/
        if (action.equals(OAFAction.RESTART)) {
            if (startWorkflow) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Need to Start workflow :: " + startWorkflow);
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, true);
                if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
//                        logger.info("workflow restarted, id :: " + startProcessRes.getProcessInstanceId());
                    String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                    logger.info("workflow restarted :: " + processName);
                    if (logger.isDebugEnabled()) {
//                            logger.debug("Restarted workflow, id :: " + startProcessRes.getProcessInstanceId());
                        logger.debug("workflow restarted :: " + processName);
//                            logger.debug("Restarted workflow, payload :: " + startProcessRes.getJsonPayload());

                    }
                    oafResponse = this.orderApprovalProcessService.killProcess(existingOrderApproval);
                    if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                        logger.info("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                        if (logger.isDebugEnabled()) {
                            logger.debug("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                        }
                        return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                    } else {
                        logger.error("Error code - 213 : Unable to stop workflow, id : " + existingOrderApproval.getProcessInstanceId());
                        return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                    }
                } else {
//                    logger.error("Unable to start workflow for inquiry Number :: " + existingOrderApproval.getInquiryNumber());
                    logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
//                        logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                    return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                }
            } else {
                logger.info("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                oafResponse.setMessage("Process has been mark as restart.");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }

        } else if (action.equals(OAFAction.UPDATE)) {
            if (startWorkflow) {
                // update existing process variable
                OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, false);

                if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
                    logger.error("Error code - 202 : Unable to update data into database - " + orderApproval.getProcessInstanceId());
                    // 500
                    return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                    }
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                }
            } else {
                logger.info("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("Process successfully updated, processId :: " + orderApproval.getProcessInstanceId());
                }
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                oafResponse.setMessage("Process has been mark as update.");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {

            if (startWorkflow) {
                // start new process
                OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

                if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                    logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
                    return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
                } else {
                    String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                    logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                    }
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                }
            } else {
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }

        }
    }
    /**
     * This method is responsible to create restart process request/ restart process Action based on startWorkflow parameter.
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @param existingOrderApproval : existingOrderApproval
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> processRestartOperation(UIQuotationDetail quotationDetail, Boolean
            startWorkflow, OrderApproval existingOrderApproval) {
        OrderApproval orderApproval;

        try {
            // update quote with restart action
            orderApproval = this.getUpdatedOrderApproval(existingOrderApproval, quotationDetail, OAFAction.RESTART.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveUpdatedQuotation(orderApproval);
            }

        } catch (Exception e) {
            logger.error("Error code - 202 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (startWorkflow) {
            if (logger.isDebugEnabled()) {
                logger.debug("Need to Start workflow :: " + startWorkflow);
            }
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, true);
            if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
//                        logger.info("workflow restarted, id :: " + startProcessRes.getProcessInstanceId());
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("workflow restarted :: " + processName);
                if (logger.isDebugEnabled()) {
//                            logger.debug("Restarted workflow, id :: " + startProcessRes.getProcessInstanceId());
                    logger.debug("workflow restarted :: " + processName);
//                            logger.debug("Restarted workflow, payload :: " + startProcessRes.getJsonPayload());

                }
                oafResponse = this.orderApprovalProcessService.killProcess(existingOrderApproval);
                if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                    logger.info("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                    }
//                    oafResponse.setMessage("Request successfully processed.");
//                    oafResponse.setMessage("Process successfully updated.");
                    oafResponse.setMessage("Process has been mark as restart.");
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                } else {
                    logger.error("Error code - 213 : Unable to stop workflow, id :: " + existingOrderApproval.getProcessInstanceId());
                    return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                }
            } else {

//                logger.error("Unable to start workflow for inquiry Number :: " + existingOrderApproval.getInquiryNumber());
                logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
//                        logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
            }
        } else {
            logger.info("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            if (logger.isDebugEnabled()) {
                logger.debug("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            }
//            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "Process successfully updated.");
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "Process has been mark as restart.");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }

    }

    /**
     * This method is responsible to return updated {@link OrderApproval} based on action and status
     * @param existingOrderApproval  : {@link OrderApproval}
     * @param quotationDetail        : {@link UIQuotationDetail}
     * @param action                 : {@link OAFAction}
     * @param status                 : {@link OAFStatus}
     * @return                       : {@link OrderApproval}
     */
    private OrderApproval getUpdatedOrderApproval(OrderApproval existingOrderApproval, UIQuotationDetail quotationDetail, String action, String status) {
        if (logger.isDebugEnabled()) {
            logger.debug("In OAFService.getUpdatedOrderApproval");
        }
        OrderApproval orderApproval = new OrderApproval();

        orderApproval.setAction(action);
        orderApproval.setCreated(new Date());
        orderApproval.setInquiryNumber(existingOrderApproval.getInquiryNumber());

        List<UIAttachment> uiAttachments = this.oafOperation.generateAttachmentDownloadLinks(quotationDetail.getAttachments(), orderApproval);
        if (uiAttachments == null) {
            orderApproval.setAttachmentFail(true);
        } else {
            quotationDetail.setAttachments(uiAttachments);
            orderApproval.setAttachmentFail(false);
        }
        orderApproval.setJsonPayload(OAFUtil.convertQuoteToJsonString(quotationDetail));
        orderApproval.setProcessInstanceId(existingOrderApproval.getProcessInstanceId());
//        orderApproval.setProcessInstanceId("0");

//        orderApproval.setQuotationNumber(existingOrderApproval.getQuotationNumber());
        orderApproval.setQuotationNumber(quotationDetail.getHeaderSegment().getQuo().getValue());

//        orderApproval.setStatus(existingOrderApproval.getStatus());
//        orderApproval.setStatus(OAFStatus.WAIT.name());
        orderApproval.setStatus(status);
        orderApproval.setVersion(existingOrderApproval.getVersion() + 1);

//        return this.orderApprovalRepository.saveAndFlush(orderApproval);
        return orderApproval;
    }

    /**
     * This method is responsible to create auto-restart process request/ auto-restart process Action based on startWorkflow parameter.
     * @param quotationDetail       : manageOAF request json payload.
     * @param startWorkflow         : if true then process is immediately start else it only store start request data in DB
     * @param existingOrderApproval : existingOrderApproval
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> processAutoRestartOperation(UIQuotationDetail quotationDetail, Boolean
            startWorkflow, OrderApproval existingOrderApproval) {
        OrderApproval orderApproval;

        try {
            // update quote with restart action
//            orderApproval = this.getUpdatedExistingOrderApproval(existingOrderApproval,quotationDetail, OAFAction.AUTO_RESTART.toString(), OAFStatus.WAIT.toString());
            orderApproval = this.getUpdatedOrderApproval(existingOrderApproval, quotationDetail, OAFAction.AUTO_RESTART.toString(), OAFStatus.WAIT.toString());

//            getUpdatedExistingOrderApproval
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveNewQuotation(orderApproval);
            }

        } catch (Exception e) {
            logger.error("Error code - 202 : Unable to update data into database - {" + existingOrderApproval.getId() + "}", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to update data into database - {" + existingOrderApproval.getId() + "}");
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (startWorkflow) {
            if (logger.isDebugEnabled()) {
                logger.debug("Need to Start workflow :: " + startWorkflow);
            }
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);
            if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
//                        logger.info("workflow restarted, id :: " + startProcessRes.getProcessInstanceId());
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("workflow restarted :: " + processName);
                if (logger.isDebugEnabled()) {
//                            logger.debug("Restarted workflow, id :: " + startProcessRes.getProcessInstanceId());
                    logger.debug("workflow auto-restarted :: " + processName);
//                            logger.debug("Restarted workflow, payload :: " + startProcessRes.getJsonPayload());

                }
                oafResponse = this.orderApprovalProcessService.killProcess(existingOrderApproval);
                if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                    logger.info("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("previous workflow stopped, id :: " + existingOrderApproval.getProcessInstanceId());
                    }
//                    oafResponse.setMessage("Request successfully processed.");
//                    oafResponse.setMessage("Process successfully updated.");
                    oafResponse.setMessage("Process has been mark as auto-restart.");
                    return new ResponseEntity<>(oafResponse, HttpStatus.OK);
                } else {
                    logger.error("Error code - 213 : Unable to stop workflow, id :: " + existingOrderApproval.getProcessInstanceId());
                    return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
                }
            } else {

//                logger.error("Unable to start workflow for inquiry Number :: " + existingOrderApproval.getInquiryNumber());
                logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
//                        logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
                return new ResponseEntity<>(oafResponse, getHttpStatus(oafResponse));
            }
        } else {
            logger.info("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            if (logger.isDebugEnabled()) {
                logger.debug("Process data successfully updated, processId :: " + orderApproval.getProcessInstanceId());
            }
//            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "Process successfully updated.");
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "Process has been mark as auto-restart.");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }

    }

    /**
     * This method is responsible to get {@link HttpStatus} based on {@link OAFResponse}
     * @param oafResponse
     * @return
     */
    private HttpStatus getHttpStatus(OAFResponse oafResponse) {
        if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
            String code = oafResponse.getErrorCode();
            if (code.equals("400")) {
                return HttpStatus.valueOf(Integer.parseInt(code));
            } else if (code.equals("401")) {
                return HttpStatus.valueOf(Integer.parseInt(code));
            } else if (code.equals("404")) {
                return HttpStatus.valueOf(Integer.parseInt(code));
            } else {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

        } else {
            return HttpStatus.OK;
        }
    }

    /**
     * This method is responsible to get Previous version based on {@link OrderApproval}
     * @param preOrderApproval
     * @return : {@link OrderApproval} : previous OrderApproval version.
     */
    public OrderApproval getPreviousOrderApproval(OrderApproval preOrderApproval) {
        return this.orderApprovalRepository.findQuoteByInquiryNoAndVersion(preOrderApproval.getInquiryNumber(), preOrderApproval.getVersion());
    }

    /**
     * This method is responsible to update status of individual record based on process instance id.
     * @param processInstanceId : Running process id
     * @param status  {@link OAFStatus}
     * @param rejectTaskId : rejectTaskId in case of process rejection or "0" in non rejection scenario.
     * @return object of OAFResponse, if any error then response is as "success": "false"
     *  with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    public ResponseEntity updateStatus(String processInstanceId, String status, String rejectTaskId) {

        if ((processInstanceId == null || StringUtils.isEmpty(processInstanceId)) || (status == null || StringUtils.isEmpty(status)) || rejectTaskId == null) {
            logger.error("Error code - 105 : Required parameter not found.");
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "105", "Required parameter not found");
            return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
        } else {
            long count = Arrays.stream(OAFStatus.values()).filter(oafStatus -> oafStatus.toString().equals(status.toUpperCase())).count();
            if (count == 0) {
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "109", "Invalid status value found, please provide correct value");
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            }
            OrderApproval approval = this.getUniqueOrderApprovalByProcessInstanceId(processInstanceId);
            if (approval == null) {
                logger.error("No record found for processInstanceId ::" + processInstanceId);
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "110", "No record found for provided processInstanceId");
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                OAFStatus oafStatus = OAFStatus.valueOf(status.toUpperCase());
                approval.setStatus(oafStatus.toString());
                approval.setRejectTaskId(rejectTaskId);
                approval = this.saveOrderApproval(approval);
                logger.info("quote successfully updated");
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        }
    }

    /**
     * This is MOCK SAP service, which is responsible to provide success result in case of Approval or Rejection.
     * @param inquiry : InquiryNumber
     * @param quote   : Quotation Number
     * @param status  : A(Approve)/R(Reject)
     * @return object of OAFResponse, if any error then response is as "success": "false"
     *  with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    public ResponseEntity updateQuoteInSAP(String inquiry, String quote, String status) {
        if (logger.isDebugEnabled()) {
            logger.debug("Input Data  :: 1). inquiry = " + inquiry + " \n" +
                    "2). quote = " + quote + " \n" +
                    "3). status = " + status);
        }
        String xmlApprovalQuoteNotExist = "<?xml version=\"1.0\"?><asx:abap xmlns:asx=\"http://www.sap.com/abapxml\" version=\"1.0\"><asx:values><TAB><SUCCESS>false</SUCCESS><RESULT>Quotation " + quote + " does not exist</RESULT></TAB></asx:values></asx:abap>";

        String xmlRejectionUpdateFail = "<?xml version=\"1.0\"?><asx:abap xmlns:asx=\"http://www.sap.com/abapxml\" version=\"1.0\"><asx:values><TAB><SUCCESS>false</SUCCESS><RESULT>Failed updating Rejection status</RESULT></TAB></asx:values></asx:abap>";

        String xmlApprovalUpdateSuccess = "<?xml version=\"1.0\"?><asx:abap xmlns:asx=\"http://www.sap.com/abapxml\" version=\"1.0\"><asx:values><TAB><SUCCESS>true</SUCCESS><RESULT>Quotation " + quote + " updated Approval status</RESULT></TAB></asx:values></asx:abap>";

        String xmlRejectionUpdateSuccess = "<?xml version=\"1.0\"?><asx:abap xmlns:asx=\"http://www.sap.com/abapxml\" version=\"1.0\"><asx:values><TAB><SUCCESS>true</SUCCESS><RESULT>Quotation " + quote + " updated Rejection status</RESULT></TAB></asx:values></asx:abap>";

        if (status.equalsIgnoreCase("A")) {
            return new ResponseEntity<>(xmlApprovalUpdateSuccess, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(xmlApprovalUpdateSuccess, HttpStatus.OK);
        }

    }

    /**
     * This method is responsible to createAutoRequest for running process [Auto Restart]
     * @param processInstanceId : Running process instance id
     * @param inquiry           : InquiryNumber
     * @param status            : START
     * @param startWorkflow     : If true then process start immediately in same request.
     *             If false then data is stored in db and scheduler is responsible to taken further action on that.
     * @return object of OAFResponse,
     *    if any error then response is as "success": "false" with error code and appropriate message.
     *    If no error then response is "success":"true".
     */
    public ResponseEntity createAutoRestartRequest(String processInstanceId, String inquiry, String status, Boolean startWorkflow) {
        OrderApproval runningOrderApproval = this.getOrderApprovalByProcessInstanceIdInquiryNoAndStatus(processInstanceId, inquiry, status);
        if (runningOrderApproval == null) {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "110", "No record found for provided processInstanceId | inquiry :: " + processInstanceId + " | " + inquiry);
            return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
        } else {
            UIQuotationDetail quotationDetail = OAFUtil.convertJsonStringToQuote(runningOrderApproval.getJsonPayload());
//            return processRestartOperation(quotationDetail, startWorkflow, runningOrderApproval);
            return processAutoRestartOperation(quotationDetail, startWorkflow, runningOrderApproval);
        }
    }

    /**
     * This method is responsible to create start process request/ start process based on startWorkflow parameter which was stop previously.
     * @param quotationDetail      : manageOAF request json payload.
     * @param startWorkflow        : if true then process is immediately start else it only store start request data in DB
     * @return {@link OAFResponse} if any error then response is as "success": "false" with error code and appropriate message.
     *  If no error then response is "success":"true".
     */
    private ResponseEntity<OAFResponse> performStopStartOperation(OrderApproval existingOrderApproval, UIQuotationDetail quotationDetail, Boolean startWorkflow) {
        OrderApproval orderApproval;
        try {
            existingOrderApproval.setProcessInstanceId("0");
            orderApproval = this.getUpdatedOrderApproval(existingOrderApproval, quotationDetail, OAFAction.NEW.toString(), OAFStatus.WAIT.toString());
            if (orderApproval != null && orderApproval.isAttachmentFail()) {

                logger.error("Error code - 111 : Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
                //500
                OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "111", "Unable to find all attachments, found :: " + orderApproval.getFoundAttachments() + " , Please check input json/ configuration and try again.");
//                return responseEntity(oafResponse);
                return new ResponseEntity<>(oafResponse, HttpStatus.BAD_REQUEST);
            } else {
                orderApproval = this.saveUpdatedQuotation(orderApproval);
            }

        } catch (Exception e) {
            //
            logger.error("Error code - 201 : Unable to create data into database", e);
            //500
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(false, "201", "Unable to create data into database");
//                return responseEntity(oafResponse);
            return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("updated Quote saved");
        if (logger.isDebugEnabled()) {
            logger.debug("updated Quote saved");
            logger.debug("OrderApproval.getId() = " + orderApproval.getId());
            logger.debug("orderApproval.getInquiryNumber() = " + orderApproval.getInquiryNumber());
//            logger.debug("OrderApproval = " + orderApproval);
        }
        if (startWorkflow) {

            // start new process
            OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);

            if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
//                    logger.error("Unable to start workflow using data -> :: " + orderApproval.getJsonPayload());
//                logger.error("Unable to start process. Please check input json and decision tables.");
                logger.error("Error code - " + oafResponse.getErrorCode() + " : Unable to start process. Please check input json and decision tables.");
                return new ResponseEntity<>(oafResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//                    return new ResponseEntity<>(orderApproval, HttpStatus.BAD_REQUEST);
            } else {
                String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
                logger.info("New workflow started :: " + processName);
//                    logger.info("New workflow started, id :: " + startProcessRes.getProcessInstanceId());
                if (logger.isDebugEnabled()) {
                    logger.debug("New workflow started :: " + processName);
//                        logger.debug("New workflow started, payload :: " + startProcessRes.getJsonPayload());

                }
                return new ResponseEntity<>(oafResponse, HttpStatus.OK);
            }
        } else {
            OAFResponse oafResponse = this.orderApprovalProcessService.generateResponse(true, "", "");
            return new ResponseEntity<>(oafResponse, HttpStatus.OK);
        }
    }
    
    /**
     * This method is responsible to get OrderApproval based on inputted inquiry number
     * @param inquiryNumber
     * @return list of OrderApprovals
     */
    public List<OrderApproval> getOrderApprovalByInquiry(String inquiryNumber) {
        return this.orderApprovalRepository.getOrderApprovalsByInquiryNumber(inquiryNumber);
    }
}