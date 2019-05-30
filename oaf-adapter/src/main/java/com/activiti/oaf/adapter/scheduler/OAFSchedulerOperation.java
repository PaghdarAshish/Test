package com.activiti.oaf.adapter.scheduler;

import com.activiti.oaf.adapter.bean.OrderApproval;
import com.activiti.oaf.adapter.constants.OAFStatus;
import com.activiti.oaf.adapter.model.OAFResponse;
import com.activiti.oaf.adapter.service.OAFProcessService;
import com.activiti.oaf.adapter.service.OAFService;
import com.activiti.oaf.adapter.util.OAFOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class contains all methods related to Scheduler operation.
 * @author Pradip Patel
 */
@Component
public class OAFSchedulerOperation {

    private static final Logger logger = LoggerFactory.getLogger(OAFSchedulerOperation.class);

    @Autowired
    private OAFProcessService orderApprovalProcessService;

    @Autowired
    private OAFService orderApprovalService;

    /**
     * This method is responsible to retrieve all waiting OAF request.
     */
    void processQuotes() {

        List<OrderApproval> waitOrderApprovals = this.orderApprovalService.getOrderApprovals(OAFStatus.WAIT.toString());
        if (waitOrderApprovals != null && !waitOrderApprovals.isEmpty()) {
            for (OrderApproval orderApproval : waitOrderApprovals) {
                this.performOperation(orderApproval);
            }
        }
    }

    /**
     * This method is responsible to Mark process as STOP in database which was cancelled.
     * @param orderApproval
     */
    private void performCancelledStopOperation(OrderApproval orderApproval) {

        if (!orderApproval.getProcessInstanceId().equals("0")) {
            boolean processExist = this.orderApprovalProcessService.isProcessExist(orderApproval.getProcessInstanceId());
            if (!processExist) {
                //
                orderApproval.setStatus(OAFStatus.STOP.toString());
                OrderApproval updatedQuotation = this.orderApprovalService.saveUpdatedQuotation(orderApproval);
                if (logger.isDebugEnabled()) {
                    logger.debug("Process marked as STOP in DB successfully, ProcessId :: " + updatedQuotation.getProcessInstanceId());
                }
            }
        }
    }

    /**
     * This method is responsible to take action based OAF request action.
     * @param orderApproval
     */
    private void performOperation(OrderApproval orderApproval) {
        String action = orderApproval.getAction();

        switch (action) {
            case "NEW":
                logger.info("Start process scheduler called");
                this.startProcess(orderApproval);
                break;
            case "UPDATE":
                logger.info("update process scheduler called");
                this.updateProcess(orderApproval);
                break;
            case "RESTART":
                logger.info("Restart process scheduler called");
                this.restartProcess(orderApproval);
                break;
            case "RE_INITIATE":
                logger.info("Re-initiate process scheduler called");
                this.reInitiateProcess(orderApproval);
                break;
            case "AUTO_RESTART":
                logger.info("AutoRestart process scheduler called");
                this.autoRestartProcess(orderApproval);
                break;
        }
    }

    /**
     * This method is responsible to do appropriate changes DB and mark as re-initiate.
     * @param orderApproval
     */
    private void reInitiateProcess(OrderApproval orderApproval) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFSchedulerOperation.reInitiateProcess");
            logger.debug(orderApproval.toString());
        }
        this.updateProcessWithAllData(orderApproval, true);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
//        this.completeProcessHoldTask(orderApproval.getProcessInstanceId(), processName);
        this.completeHoldTask(orderApproval.getRejectTaskId(), processName);

    }

    /**
     * This method is responsible to do complete HoldTask of running process.
     * @param processInstanceId : process instance id
     * @param processName       : Name of running process.
     */
    private void completeProcessHoldTask(String processInstanceId, String processName) {

        //query
//       POST http://localhost:9090/activiti-app/api/enterprise/tasks/query
        //complete
        //PUT http://localhost:9090/activiti-app/api/enterprise/tasks/{taskId}/action/complete

        OAFResponse oafResponse = this.orderApprovalProcessService.getProcessActiveTask(processInstanceId);
        if (oafResponse.getSuccess().equalsIgnoreCase("false")) {
            logger.error("Error code - 210 : Unable to retrieve active task of processId - {" + processInstanceId + "}");
        } else {
            String taskId = oafResponse.getMessage();
            oafResponse.setMessage("");
            OAFResponse oafCompleteTaskResponse = this.orderApprovalProcessService.completeActiveTask(taskId);
            if (oafCompleteTaskResponse.getSuccess().equalsIgnoreCase("true")) {
                logger.info("Task successfully completed, Process Name ::  " + processName);
            } else {
                logger.error("Error code - 211 : Unable to complete hold task, Invalid taskId ::" + taskId);
            }
        }


    }
    /**
     * This method is responsible to do complete HoldTask based on rejectTaskId and processName.
     * @param rejectTask        : Reject Task Id
     * @param processName       : Name of running process.
     */
    private void completeHoldTask(String rejectTask, String processName) {

        //complete
        //PUT http://localhost:9090/activiti-app/api/enterprise/tasks/{taskId}/action/complete
        OAFResponse oafCompleteTaskResponse = this.orderApprovalProcessService.completeActiveTask(rejectTask);
        if (oafCompleteTaskResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Task successfully completed, Process Name ::  " + processName);
        } else {
            logger.error("Error code - 211 : Unable to complete hold task, Invalid taskId ::" + rejectTask);
        }
    }

    /**
     * This method is responsible to restart process using orderApproval.
     * @param orderApproval
     */
    private void restartProcess(OrderApproval orderApproval) {

        if (logger.isDebugEnabled()) {
            logger.debug("this.orderApprovalProcessService.restartProcess");
            logger.debug("OrderApproval BEFORE START Process");
            logger.debug(orderApproval.toString());
        }

        OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, true);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Process successfully restarted, Name ::  " + processName);

            if (logger.isDebugEnabled()) {
                logger.debug("this.orderApprovalProcessService.killProcess");
                logger.debug("OrderApproval BEFORE Kill Process");
                logger.debug(orderApproval.toString());
            }
            OrderApproval preOrderApproval = OAFOperation.generatePreviousOrderApprovalVersion(orderApproval);
            preOrderApproval = this.orderApprovalService.getPreviousOrderApproval(preOrderApproval);
            if (preOrderApproval != null) {
                OAFResponse killProcess = this.orderApprovalProcessService.killProcess(preOrderApproval);
                if (killProcess.getSuccess().equalsIgnoreCase("true")) {
                    logger.info("previous workflow stopped, id :: " + preOrderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("previous workflow stopped, id :: " + preOrderApproval.getProcessInstanceId());
                    }
                } else {
//                logger.error("Unable to stop workflow, id :: " + orderApproval.getProcessInstanceId());
                    logger.error("Error code - 205 : Unable to cancel process - {" + preOrderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                }
            }
        } else {
            logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
        }

    }

    /**
     * This method is responsible to auto-restart process using orderApproval.
     * @param orderApproval
     */
    private void autoRestartProcess(OrderApproval orderApproval) {

        if (logger.isDebugEnabled()) {
            logger.debug("this.orderApprovalProcessService.restartProcess");
            logger.debug("OrderApproval BEFORE START Process");
            logger.debug(orderApproval.toString());
        }

        OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Process successfully auto restarted, Name ::  " + processName);

            if (logger.isDebugEnabled()) {
                logger.debug("this.orderApprovalProcessService.killProcess");
                logger.debug("OrderApproval BEFORE Kill Process");
                logger.debug(orderApproval.toString());
            }
            OrderApproval preOrderApproval = OAFOperation.generatePreviousOrderApprovalVersion(orderApproval);
            preOrderApproval = this.orderApprovalService.getPreviousOrderApproval(preOrderApproval);
            if (preOrderApproval != null) {
                OAFResponse killProcess = this.orderApprovalProcessService.killProcess(preOrderApproval);
                if (killProcess.getSuccess().equalsIgnoreCase("true")) {
                    logger.info("previous workflow stopped, id :: " + preOrderApproval.getProcessInstanceId());
                    if (logger.isDebugEnabled()) {
                        logger.debug("previous workflow stopped, id :: " + preOrderApproval.getProcessInstanceId());
                    }
                } else {
//                logger.error("Unable to stop workflow, id :: " + orderApproval.getProcessInstanceId());
                    logger.error("Error code - 205 : Unable to cancel process - {" + preOrderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                }
            }
        } else {
            logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
        }

    }

    /**
     * This method is responsible to update process using orderApproval.
     * @param orderApproval
     */
    private void updateProcess(OrderApproval orderApproval) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFSchedulerOperation.updateProcess");
            logger.debug(orderApproval.toString());
        }
        OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, false);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Process successfully updated, Name ::  " + processName);
        } else {
            logger.error("Error code - 204 : Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
        }
    }

    /**
     * This method is responsible to update process with all data as well as orderApproval.
     * @param orderApproval
     */
    private void updateProcessWithAllData(OrderApproval orderApproval, boolean isProcessReInitiated) {
//        boolean decisionFailed = this.orderApprovalProcessService.getIsLevelUsersDecisionFailed(orderApproval.getProcessInstanceId());
        OAFResponse oafResponse = this.orderApprovalProcessService.updateProcess(orderApproval, isProcessReInitiated);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Process successfully updated, Name ::  " + processName);
        } else {
            logger.error("Error code - 204 : Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
        }
    }

    /**
     * This method is responsible to start process using orderApproval.
     * @param orderApproval
     */
    private void startProcess(OrderApproval orderApproval) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFSchedulerOperation.startProcess");
            logger.debug(orderApproval.toString());
        }
        OAFResponse oafResponse = this.orderApprovalProcessService.startProcess(orderApproval, false);
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
            logger.info("Process successfully started, Name ::  " + processName);
        } else {
            logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.");
        }
    }

    /*private void sendAttachmentFailEmailToInitiator(OrderApproval orderApproval) {
        if (!tempInitiatorEmail.isEmpty()) {
            OAFResponse oafResponse = this.orderApprovalProcessService.sendAttachmentFailEmail(orderApproval);
            if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                logger.info("Mail successfully send to initiator.");
            } else {
                logger.error("Error code - 212 : Unable to send email to initiator.");
            }
        } else {
//            logger.error("Unable to find process initiator email id, please check configuration.");
            logger.error("Error code - 214 : Unable to find process initiator email id, Please check input json/ configuration.");
        }
    }*/
}
