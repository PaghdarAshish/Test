package com.activiti.oaf.adapter.service;

import com.activiti.oaf.adapter.bean.OrderApproval;
import com.activiti.oaf.adapter.constants.OAFStatus;
import com.activiti.oaf.adapter.dto.TaskQueryRepresentation;
import com.activiti.oaf.adapter.dto.TaskSort;
import com.activiti.oaf.adapter.dto.TaskState;
import com.activiti.oaf.adapter.model.OAFResponse;
import com.activiti.oaf.adapter.model.UIOtherCharges;
import com.activiti.oaf.adapter.model.UIQuotationDetail;
import com.activiti.oaf.adapter.model.runtime.CreateProcessInstanceRepresentation;
import com.activiti.oaf.adapter.model.runtime.RestVariable;
import com.activiti.oaf.adapter.repository.OrderApprovalRepository;
import com.activiti.oaf.adapter.util.OAFUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.activiti.oaf.adapter.controller.OAFController.KSCHL_UNIQUE_FIELD_VALUE;
import static org.apache.http.Consts.UTF_8;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

/**
 * This Bean is responsible to perform OAF process related operation.
 *
 * @Author Pradip Patel
 */
@Service
public class OAFProcessService {

    private Logger logger = LoggerFactory.getLogger(OAFProcessService.class);
    @Autowired
    private OrderApprovalRepository orderApprovalRepository;
    @Autowired
    private CreateProcessInstanceRepresentation representation;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${oaf.process-definition.key}")
    private String PROCESS_DEFINITION_KEY;
    @Value("${oaf.process.business.key}")
    private String PROCESS_BUSINESS_KEY;
    @Value("${oaf.process.variable.tonnage}")
    private String TONNAGE;
    @Value("${oaf.process.variable.gm}")
    private String GM;
    @Value("${oaf.process.variable.sales-office}")
    private String SALES_OFFICE;
    @Value("${oaf.process.variable.engineering-service}")
    private String ENGINEERING_SERVICE;
    @Value("${oaf.process.variable.reset-process}")
    private String IS_PROCESS_RESET;
    @Value("${oaf.process.variable.json-payload}")
    private String JSON_PAYLOAD;
    @Value("${oaf.aps.username}")
    private String APS_USERNAME;
    @Value("${oaf.aps.password}")
    private String APS_PASSWORD;
    @Value("${oaf.aps.enterprise.api.base-url}")
    private String APS_ENTERPRISE_BASE_URL;
    @Value("${oaf.aps.process-instance.url}")
    private String APS_PROCESS_INSTANCE_URL;
    @Value("${oaf.aps.process-instance.variables.url}")
    private String APS_PROCESS_INSTANCE_VARIABLES_URL;
    @Value("${oaf.aps.process-instance.task.url}")
    private String APS_PROCESS_INSTANCE_TASK_URL;
    @Value("${oaf.aps.process-instance.query.url}")
    private String APS_PROCESS_INSTANCE_QUERY_URL;
    @Value("${oaf.aps.task.action.complete.url}")
    private String APS_TASK_ACTION_COMPLETE_URL;
    @Value("${oaf.aps.send-attachment-fail-email.url}")
    private String APS_SEND_ATTACHMENT_FAIL_EMAIL_URL;

    /**
     * This method is responsible to start process
     *
     * @param orderApproval
     * @param isProcessReset
     * @return {@link OAFResponse}
     */
    public OAFResponse startProcess(OrderApproval orderApproval, boolean isProcessReset) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.startProcess");
            logger.debug("OAF ID :: " + orderApproval.getId());
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_URL == null || APS_USERNAME == null || APS_PASSWORD == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url, oaf.aps.username, oaf.aps.password]");
//            return null;
        }
        StringBuilder startProcessURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_URL);
        if (logger.isDebugEnabled()) {
            logger.debug("Start Process url :: " + startProcessURL.toString());
        }
        String processInstanceId = null;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            String payload = null;
            try {
                payload = generateRequestJsonPayload(orderApproval, isProcessReset);
            } catch (JsonProcessingException e) {
                logger.error("Error code - 101 : Input json is wrong. Please validate json and try again", e);
                return generateResponse(false, "101", "Input json is wrong. Please validate json and try again");
            }
            byte[] encoding;
            // use httpClient (no need to close it explicitly)
            HttpPost httpPost = new HttpPost(startProcessURL.toString());
            try {
                httpPost.setEntity(new StringEntity(payload));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error code - 102 : Unsupported encoding found while calling APS service", e);
                //500
                return generateResponse(false, "102", "Unsupported encoding found while calling APS service");
            }
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPost.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
//            HttpResponse response = this.getNodeRefByDocGuid(startProcessURL.toString(), payload);
            int statusCode = response.getStatusLine().getStatusCode();
            String resAsString = null;
            JsonNode jsonNode = null;
            if (statusCode == HttpStatus.SC_OK) {

                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    jsonNode = new ObjectMapper().readTree(resAsString);
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                    return generateResponse(false, "104", "Adepter service is unable to read APS response");
                }

//                String resAsString = new BasicResponseHandler().handleEntity(response.getEntity());
                if (logger.isDebugEnabled()) {
                    logger.debug("StartProcess response = " + resAsString);
                }
//                System.out.println(jsonNode);
                JsonNode id = jsonNode.get("id");
                processInstanceId = id.asText();
                // Check decision table failed or not
                JsonNode variables = jsonNode.path("variables");
                boolean isLevelUsersDecisionFailed = checkDecisionTableFailed(variables);


                if (processInstanceId != null && isLevelUsersDecisionFailed) {
//                    orderApproval.setProcessInstanceId(processInstanceId);
//                    orderApproval.setStatus(OAFStatus.WAIT.toString());
                    orderApproval.setStatus(OAFStatus.FAIL.toString());
                    orderApproval.setModified(new Date());
                    logger.info("OAF process started, Id :: " + processInstanceId);
                    this.orderApprovalRepository.saveAndFlush(orderApproval);
                    return generateResponse(false, "", "");
                } else if (processInstanceId != null) {
                    orderApproval.setProcessInstanceId(processInstanceId);
                    orderApproval.setStatus(OAFStatus.START.toString());
//                orderApproval.setStatus(OAFStatus.RESTARTED.toString());
                    orderApproval.setModified(new Date());
                    logger.info("OAF process started, Id :: " + processInstanceId);
                    this.orderApprovalRepository.saveAndFlush(orderApproval);
                    return generateResponse(true, "", "");
                } else {
                    logger.error("Unable to start process. Please check input json and decision tables.");
                    if (logger.isDebugEnabled()) {
//                        logger.debug("Unable to start process for data :: " + payload);
                    }
                    return generateResponse(false, "203", "Unable to start process. Please check input json and decision tables");
                }
            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            }
            // *
            else {
//                String msg = "APS service response :: " + statusCode;
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 203 : Unable to start process. Please check input json and decision tables.", e);
            return generateResponse(false, "203", "Unable to start process. Please check input json and decision tables");
        }
    }

    /**
     * This method is responsible to find document based guid
     *
     * @param uri
     * @param json
     * @return org.apache.http.HttpResponse
     * @throws IOException
     */
    private HttpResponse getNodeRefByDocGuid(String uri, String json) throws IOException {
        byte[] encoding;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            // use httpClient (no need to close it explicitly)

            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPost.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            return httpClient.execute(httpPost);
        }
    }


    /**
     * This method is responsible to stop running process
     *
     * @param orderApproval
     * @return com.activiti.oaf.adapter.model.OAFResponse
     */
    public OAFResponse killProcess(OrderApproval orderApproval) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.killProcess");
            logger.debug("OAF ID :: " + orderApproval.getId());
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_URL == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url]");
        }
        String processInstanceId = orderApproval.getProcessInstanceId();
        StringBuilder cancelProcessURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_URL).append("/").append(processInstanceId);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            byte[] encoding;
            HttpDelete httpDelete = new HttpDelete(cancelProcessURL.toString());
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpDelete.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpDelete);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
            boolean isProcessCancelled = false;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String resAsString;
                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                    return generateResponse(false, "104", "Adepter service is unable to read APS response");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Cancel process response = " + resAsString);
                }
                isProcessCancelled = true;
                if (processInstanceId != null && isProcessCancelled) {
                    orderApproval.setProcessInstanceId(processInstanceId);
                    orderApproval.setStatus(OAFStatus.STOP.toString());
                    orderApproval.setModified(new Date());
                    logger.info("OAF process cancelled, Id :: " + processInstanceId);
                    this.orderApprovalRepository.saveAndFlush(orderApproval);
                    //200
                    return this.generateResponse(true, "", "");
                } else {
                    logger.error("Error code - 205 : Unable to cancel process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                    //500
                    return this.generateResponse(false, "205", "Unable to cancel process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                }

            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            } else {
                String msg = "APS service response :: " + statusCode;
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }
        } catch (IOException e) {
            logger.error("Error code - 205 : Unable to cancel process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.", e);
            return generateResponse(false, "205", "Unable to cancel process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
        }
    }

    /**
     * This method is responsible to update running process
     *
     * @param orderApproval
     * @param isProcessReInitiated : if true then all variables will be updated, else process marked updated fields.
     * @return com.activiti.oaf.adapter.model.OAFResponse
     */
    public OAFResponse updateProcess(OrderApproval orderApproval, boolean isProcessReInitiated) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.updateProcess");
            logger.debug("OAF ID :: " + orderApproval.getId());
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_URL == null || APS_PROCESS_INSTANCE_VARIABLES_URL == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url, oaf.aps.process-instance.variables.url]");
        }
        String processInstanceId = orderApproval.getProcessInstanceId();
        StringBuilder updateProcessURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_URL).append("/").append(processInstanceId).append(APS_PROCESS_INSTANCE_VARIABLES_URL);

        if (logger.isDebugEnabled()) {
            logger.debug("updateProcess URL :: " + updateProcessURL.toString());
        }

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            String payload;
            try {
                if (isProcessReInitiated) {
                    payload = generateReInitiatedRequestJsonPayload(orderApproval);
                } else {
                    payload = generateUpdateRequestJsonPayload(orderApproval);
                }
            } catch (JsonProcessingException e) {
                logger.error("Error code - 101 : Input json is wrong. Please validate json and try again", e);
                return generateResponse(false, "101", "Input json is wrong. Please validate json and try again");
            }
            byte[] encoding;
            HttpPut httpPut = new HttpPut(updateProcessURL.toString());
            try {
                httpPut.setEntity(new StringEntity(payload));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error code - 102 : Unsupported encoding found while calling APS service", e);
                //500
                return generateResponse(false, "102", "Unsupported encoding found while calling APS service");
            }
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPut.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response;
            try {
                response = httpClient.execute(httpPut);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
            List<RestVariable> restVariables;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String resAsString;
                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Update process response ::  " + resAsString);
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    restVariables = objectMapper.readValue(resAsString, new TypeReference<List<RestVariable>>() {
                    });

                    if (processInstanceId != null && !restVariables.isEmpty()) {
                        orderApproval.setProcessInstanceId(processInstanceId);
                        orderApproval.setStatus(OAFStatus.START.toString());
                        orderApproval.setModified(new Date());
                        logger.info("OAF process updated, Id :: " + processInstanceId);
                        this.orderApprovalRepository.saveAndFlush(orderApproval);
                        return generateResponse(true, "", "");

                    } else {
                        logger.error("Error code - 204 : Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                        return generateResponse(false, "204", "Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
                    }
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                    return generateResponse(false, "104", "Adepter service is unable to read APS response");
                }

            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            }
            // *
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 204 : Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.", e);
            return generateResponse(false, "204", "Unable to update process - {" + orderApproval.getProcessInstanceId() + "}, Please check input json and decision tables.");
        }
    }

    /**
     * This method is responsible to check is process is exist or not.
     *
     * @param processInstanceId
     * @return
     */
    public boolean isProcessExist(String processInstanceId) {

        boolean isProcessExist = false;
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.isProcessExist");
            logger.debug("Process ID :: " + processInstanceId);
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_URL == null || APS_USERNAME == null || APS_PASSWORD == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url, oaf.aps.username, oaf.aps.password]");
//            return null;
        }
        StringBuilder startProcessURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_URL).append("/").append(processInstanceId);
        if (logger.isDebugEnabled()) {
            logger.debug("Process Info url :: " + startProcessURL.toString());
        }
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            byte[] encoding;
            // use httpClient (no need to close it explicitly)
            HttpGet httpGet = new HttpGet(startProcessURL.toString());
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpGet.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
            }
//            HttpResponse response = this.getNodeRefByDocGuid(startProcessURL.toString(), payload);
            int statusCode = response.getStatusLine().getStatusCode();
            String resAsString = null;
            JsonNode jsonNode = null;
            if (statusCode == HttpStatus.SC_OK) {

                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    jsonNode = new ObjectMapper().readTree(resAsString);
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                }

//                String resAsString = new BasicResponseHandler().handleEntity(response.getEntity());
                if (logger.isDebugEnabled()) {
                    logger.debug("Current process response = " + resAsString);
                }
                JsonNode ended = jsonNode.get("ended");
                if (ended.isNull()) {
                    isProcessExist = true;
                }
            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                isProcessExist = false;
            }
            // *
            else {
//                String msg = "APS service response :: " + statusCode;
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 203 : Unable to get process information.", e);
        }
        return isProcessExist;
    }

    /**
     * This method is responsible to get active task of process.
     *
     * @param processInstanceId
     * @return
     */
    public OAFResponse getProcessActiveTask(String processInstanceId) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.getProcessActiveTask");
            logger.debug("OAF PROCESS ID :: " + processInstanceId);
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_TASK_URL == null || APS_PROCESS_INSTANCE_QUERY_URL == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url,oaf.aps.process-instance.task.url, oaf.aps.process-instance.query.url]");
        }
        StringBuilder queryProcessTaskURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_TASK_URL).append(APS_PROCESS_INSTANCE_QUERY_URL);
        if (logger.isDebugEnabled()) {
            logger.debug("queryProcessTask URL :: " + queryProcessTaskURL.toString());
        }

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            String payload;
            try {
                payload = generateQueryJsonPayload(processInstanceId);
            } catch (JsonProcessingException e) {
                logger.error("Error code - 101 : Input json is wrong. Please validate json and try again", e);
                return generateResponse(false, "101", "Input json is wrong. Please validate json and try again");
            }
            byte[] encoding;
            HttpPost httpPost = new HttpPost(queryProcessTaskURL.toString());
            try {
                httpPost.setEntity(new StringEntity(payload));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error code - 102 : Unsupported encoding found while calling APS service", e);
                //500
                return generateResponse(false, "102", "Unsupported encoding found while calling APS service");
            }
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPost.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String resAsString;
                String taskId = "";
                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    if (logger.isDebugEnabled()) {
                        logger.debug("process task query response ::  " + resAsString);
                    }
                /*if (logger.isDebugEnabled()) {
                    logger.debug("responseAsString = " + responseAsString);
                }*/
                    JsonNode jsonNode = new ObjectMapper().readTree(resAsString);
                    JsonNode size = jsonNode.get("size");
                    int noRes = size.asInt();
                    if (noRes > 0) {
                        JsonNode taskIdNode = jsonNode.get("data").get(0).get("id");
                        taskId = taskIdNode.asText();
                        if (logger.isDebugEnabled()) {
                            logger.debug("taskId = " + taskId);
                        }
                    }
                    if (taskId.length() > 0) {
//                        return generateResponse(true, "", "");
                        return generateResponse(true, "", taskId);
                    } else {
                        logger.error("Error code - 210 : Unable to retrieve active task of processId - {" + processInstanceId + "}");
                        return generateResponse(false, "210", "Unable to retrieve active task of processId - {" + processInstanceId + "}");
                    }
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                    return generateResponse(false, "104", "Adepter service is unable to read APS response");
                }

            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            }
            // *
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 210 : Unable to retrieve active task of processId - {" + processInstanceId + "}");
            return generateResponse(false, "210", "Unable to retrieve active task of processId - {" + processInstanceId + "}");
        }
    }

    /**
     * This method is responsible to complete running process active task.
     *
     * @param taskId
     * @return
     */
    public OAFResponse completeActiveTask(String taskId) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.completeActiveTask");
            logger.debug("OAF Task ID :: " + taskId);
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_TASK_URL == null || APS_TASK_ACTION_COMPLETE_URL == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.task.url, oaf.aps.task.action.complete.url]");
        }
        StringBuilder queryProcessTask = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_TASK_URL).append("/").append(taskId).append(APS_TASK_ACTION_COMPLETE_URL);

        if (logger.isDebugEnabled()) {
            logger.debug("completeActiveTask URL :: " + queryProcessTask);
        }
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            byte[] encoding;
            HttpPut httpPut = new HttpPut(queryProcessTask.toString());
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPut.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response;
            try {
                response = httpClient.execute(httpPut);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                logger.info("Task successfully completed, TaskId ::  " + taskId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Task successfully completed, TaskId ::  " + taskId);
                }
                return generateResponse(true, "", "");
            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            }
            // *
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 211 : Unable to complete hold task, taskId ::" + taskId, e);
            return generateResponse(false, "211", "Unable to complete hold task, taskId - " + taskId);
        }
    }

    /**
     * This method is responsible to generate query as json to find ActiveTask.
     *
     * @param processInstanceId
     * @return java.lang.String
     * @throws JsonProcessingException
     */
    private String generateQueryJsonPayload(String processInstanceId) throws JsonProcessingException {
        TaskQueryRepresentation queryRepresentation = new TaskQueryRepresentation();
        queryRepresentation.setProcessInstanceId(processInstanceId);
        queryRepresentation.setState(TaskState.ACTIVE);
        queryRepresentation.setSort(TaskSort.CREATED_DESC);
        queryRepresentation.setPage(0);
        queryRepresentation.setSize(1);

        return new ObjectMapper().writeValueAsString(queryRepresentation);

    }

    /**
     * This method is responsible to create json payload for start new process
     *
     * @param orderApproval
     * @param isProcessReset
     * @return java.lang.String
     * @throws JsonProcessingException
     */
    private String generateRequestJsonPayload(OrderApproval orderApproval, boolean isProcessReset) throws JsonProcessingException {
        String processName = orderApproval.getInquiryNumber() + "-" + orderApproval.getQuotationNumber();
        if (logger.isDebugEnabled()) {
            logger.debug("Representation :: " + representation);
        }
        UIQuotationDetail quote = OAFUtil.convertJsonStringToQuote(orderApproval.getJsonPayload());
        RestVariable tonnage = new RestVariable();
        tonnage.setName(TONNAGE);
        if (quote.getHeaderSegment().getProjmt() != null) {
            tonnage.setValue(quote.getHeaderSegment().getProjmt().getValue());
        } else {
            tonnage.setValue("");
        }

        RestVariable gm = new RestVariable();
        gm.setName(GM);
        if (quote.getHeaderSegment().getMarginCPP() != null) {
            gm.setValue(quote.getHeaderSegment().getMarginCPP().getValue());
        } else {
            gm.setValue("");
        }

        RestVariable salesOffice = new RestVariable();
        salesOffice.setName(SALES_OFFICE);
        if (quote.getHeaderSegment().getVkbur() != null) {
            salesOffice.setValue(quote.getHeaderSegment().getVkbur().getValue());
        } else {
            salesOffice.setValue("");
        }

        RestVariable engineeringService = new RestVariable();
        engineeringService.setName(ENGINEERING_SERVICE);
        boolean isEngineeringServiceAvailable = false;
        for (UIOtherCharges otherCharge : quote.getOtherCharges()) {
            /*
             * only check existing other changes element if value for KSCHL is YKES
             */
            /*if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl())) {
                isEngineeringServiceAvailable = true;
                break;
            }*/
            //updated code
            if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl())) {
                if (otherCharge.getTotalAmount() != null) {
                    try {
                        isEngineeringServiceAvailable = !Double.valueOf(otherCharge.getTotalAmount()).equals(0.0);
                    } catch (NumberFormatException ne) {
                        isEngineeringServiceAvailable = false;
                    }
                    break;
                }
            }
        }
//        engineeringService.setValue(quote.getHeaderSegment().getKst().getValue());
        engineeringService.setValue(isEngineeringServiceAvailable);

        RestVariable jsonPayload = new RestVariable();
        jsonPayload.setName(JSON_PAYLOAD);
        jsonPayload.setValue(orderApproval.getJsonPayload());

        if (logger.isDebugEnabled()) {
            logger.debug("orderApproval.getJsonPayload() = " + orderApproval.getJsonPayload());
        }
        representation.setName(processName);
        representation.setProcessDefinitionKey(PROCESS_DEFINITION_KEY);
        representation.setBusinessKey(PROCESS_BUSINESS_KEY);
        List<RestVariable> restVariables = new ArrayList<RestVariable>() {{
            add(tonnage);
            add(gm);
            add(salesOffice);
            add(engineeringService);
            add(jsonPayload);
        }};

        RestVariable isOAFReset = new RestVariable();
        isOAFReset.setName(IS_PROCESS_RESET);
        if (isProcessReset) {
            isOAFReset.setValue(true);
        } else {
            isOAFReset.setValue(false);
        }
        restVariables.add(isOAFReset);
        representation.setVariables(restVariables);


        return new ObjectMapper().writeValueAsString(representation);
    }

    /**
     * This method is responsible to generate payload for update request.
     *
     * @param orderApproval
     * @return
     * @throws JsonProcessingException
     */
    private String generateUpdateRequestJsonPayload(OrderApproval orderApproval) throws JsonProcessingException {

        List<RestVariable> updatedVariableList = new ArrayList<>();
        RestVariable jsonPayload = new RestVariable();
        jsonPayload.setName(JSON_PAYLOAD);
        jsonPayload.setValue(orderApproval.getJsonPayload());
        updatedVariableList.add(jsonPayload);
        if (logger.isDebugEnabled()) {
            logger.debug("orderApproval.getId() = " + orderApproval.getId());
        }
        return new ObjectMapper().writeValueAsString(updatedVariableList);
    }

    /**
     * This method is responsible to generate payload for re-initiate request.
     *
     * @param orderApproval
     * @return
     * @throws JsonProcessingException
     */
    private String generateReInitiatedRequestJsonPayload(OrderApproval orderApproval) throws JsonProcessingException {

        List<RestVariable> updatedVariableList = new ArrayList<>();

        UIQuotationDetail quote = OAFUtil.convertJsonStringToQuote(orderApproval.getJsonPayload());
        RestVariable tonnage = new RestVariable();
        tonnage.setName(TONNAGE);
        if (quote.getHeaderSegment().getProjmt() != null) {
            tonnage.setValue(quote.getHeaderSegment().getProjmt().getValue());
        } else {
            tonnage.setValue("");
        }
        updatedVariableList.add(tonnage);

        RestVariable gm = new RestVariable();
        gm.setName(GM);
        if (quote.getHeaderSegment().getMarginCPP() != null) {
            gm.setValue(quote.getHeaderSegment().getMarginCPP().getValue());
        } else {
            gm.setValue("");
        }
        updatedVariableList.add(gm);

        RestVariable salesOffice = new RestVariable();
        salesOffice.setName(SALES_OFFICE);
        if (quote.getHeaderSegment().getVkbur() != null) {
            salesOffice.setValue(quote.getHeaderSegment().getVkbur().getValue());
        } else {
            salesOffice.setValue("");
        }
        updatedVariableList.add(salesOffice);

        RestVariable engineeringService = new RestVariable();
        engineeringService.setName(ENGINEERING_SERVICE);
        boolean isEngineeringServiceAvailable = false;
        for (UIOtherCharges otherCharge : quote.getOtherCharges()) {
            /*
             * only check existing other changes element if value for KSCHL is YKES
             */
            /*if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl())) {
                isEngineeringServiceAvailable = true;
                break;
            }*/
            //updated code
            if (KSCHL_UNIQUE_FIELD_VALUE.equalsIgnoreCase(otherCharge.getKschl())) {
                if (otherCharge.getTotalAmount() != null) {
                    try {
                        isEngineeringServiceAvailable = !Double.valueOf(otherCharge.getTotalAmount()).equals(0.0);
                    } catch (NumberFormatException ne) {
                        isEngineeringServiceAvailable = false;
                    }
                    break;
                }
            }
        }
        engineeringService.setValue(isEngineeringServiceAvailable);
        updatedVariableList.add(engineeringService);

        RestVariable isOAFRestart = new RestVariable();
        isOAFRestart.setName(IS_PROCESS_RESET);
        isOAFRestart.setValue(true);
        updatedVariableList.add(isOAFRestart);

        RestVariable jsonPayload = new RestVariable();
        jsonPayload.setName(JSON_PAYLOAD);
        jsonPayload.setValue(orderApproval.getJsonPayload());
        updatedVariableList.add(jsonPayload);
        if (logger.isDebugEnabled()) {
            logger.debug("orderApproval.getId() = " + orderApproval.getId());
        }
        return new ObjectMapper().writeValueAsString(updatedVariableList);
    }

    /**
     * This method is responsible to generate response based on parameter.
     *
     * @param success   : true/false
     * @param errorCode : error code
     * @param message   : error message
     * @return com.activiti.oaf.adapter.model.OAFResponse
     */
    OAFResponse generateResponse(boolean success, String errorCode, String message) {
        OAFResponse oafResponse = new OAFResponse();
        oafResponse.setErrorCode(errorCode);
        oafResponse.setMessage(message);
        oafResponse.setSuccess(String.valueOf(success));
        return oafResponse;
    }

    //unused method
    public OAFResponse sendAttachmentFailEmail(OrderApproval orderApproval) {
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.sendAttachmentFailEmail");
            logger.debug("OAF ID :: " + orderApproval.getId());
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_SEND_ATTACHMENT_FAIL_EMAIL_URL == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url]");
        }
        StringBuilder sendAttachmentFailEmailURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_SEND_ATTACHMENT_FAIL_EMAIL_URL);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            byte[] encoding;
            HttpPost httpPost = new HttpPost(sendAttachmentFailEmailURL.toString());
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpPost.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            httpPost.setHeader(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("inquiryNumber", orderApproval.getInquiryNumber()));
            form.add(new BasicNameValuePair("quotationNumber", orderApproval.getQuotationNumber()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
                return generateResponse(false, "400", "Connection to APS service from Adepter service failed");
            }
            boolean isMailSend = false;
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String resAsString;
                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    OAFResponse oafResponse = this.objectMapper.readValue(resAsString, OAFResponse.class);
                    if (oafResponse.getSuccess().equalsIgnoreCase("true")) {
                        isMailSend = true;
                    }
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                    return generateResponse(false, "104", "Adepter service is unable to read APS response");
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("sendAttachmentFailEmail response = " + resAsString);
                }


                if (isMailSend) {
//                    logger.info("Mail successfully send to initiator.");
                    if (logger.isDebugEnabled()) {
                        logger.debug("Mail successfully send to initiator");
                    }
                    //200
                    return this.generateResponse(true, "", "");
                } else {
                    logger.error("Error code - 212 : Unable to send email to initiator.");
                    //500
                    return this.generateResponse(false, "212", "Unable to send email to initiator.");
                }

            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
                return generateResponse(false, "401", "Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
                return generateResponse(false, "404", "APS service configuration seems to be wrong. APS service not found");
            }

            // 400 & 500
            else if (statusCode == HttpStatus.SC_BAD_REQUEST || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {

                logger.error("Error code - 212 : Unable to send email to initiator.");
                //500
                return this.generateResponse(false, "212", "Unable to send email to initiator.");
            } else {
                String msg = "APS service response :: " + statusCode;
                logger.error("Error code - 206 : " + msg);
                return generateResponse(false, "206", msg);
            }
        } catch (IOException e) {
            logger.error("Error code - 212 : Unable to send email to initiator.", e);
            //500
            return this.generateResponse(false, "212", "Unable to send email to initiator.");
        }
    }

    /**
     * This method is responsible to check Decision table failed.
     *
     * @param variables
     * @return true/ false
     */
    private boolean checkDecisionTableFailed(JsonNode variables) {
        if (variables.isArray()) {
            for (JsonNode variable : variables) {
                JsonNode name = variable.path("name");
                if (name.asText().equals("isLevelUsersDecisionFailed")) {
                    JsonNode value = variable.path("value");
                    if (value.asBoolean()) {
                        // Decision table failed, Stop status will be set
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method is responsible to check level user decision table failed.
     *
     * @param processInstanceId
     * @return true/ false
     */
    public boolean getIsLevelUsersDecisionFailed(String processInstanceId) {

        boolean isLevelUsersDecisionFailed = false;
        if (logger.isDebugEnabled()) {
            logger.debug("OAFProcessService.getIsLevelUsersDecisionFailed");
            logger.debug("Process ID :: " + processInstanceId);
        }
        if (APS_ENTERPRISE_BASE_URL == null || APS_PROCESS_INSTANCE_URL == null || APS_USERNAME == null || APS_PASSWORD == null) {
            logger.debug("Required configuration not found, [oaf.aps.enterprise.api.base-url, oaf.aps.process-instance.url, oaf.aps.username, oaf.aps.password]");
//            return null;
        }
        StringBuilder startProcessURL = new StringBuilder(APS_ENTERPRISE_BASE_URL).append(APS_PROCESS_INSTANCE_URL).append("/").append(processInstanceId);
        if (logger.isDebugEnabled()) {
            logger.debug("Process Info url :: " + startProcessURL.toString());
        }
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            byte[] encoding;
            // use httpClient (no need to close it explicitly)
            HttpGet httpGet = new HttpGet(startProcessURL.toString());
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            String userPass = APS_USERNAME + ":" + APS_PASSWORD;
            encoding = Base64.getEncoder().encode(userPass.getBytes(UTF_8));
            httpGet.setHeader(AUTHORIZATION, "Basic " + new String(encoding));
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
            } catch (IOException e) {
                logger.error("Error code - 400 : Connection to APS service from Adepter service failed", e);
                //400
            }
//            HttpResponse response = this.getNodeRefByDocGuid(startProcessURL.toString(), payload);
            int statusCode = response.getStatusLine().getStatusCode();
            String resAsString = null;
            JsonNode jsonNode = null;
            if (statusCode == HttpStatus.SC_OK) {

                try {
                    resAsString = EntityUtils.toString(response.getEntity());
                    jsonNode = new ObjectMapper().readTree(resAsString);
                } catch (IOException e) {
                    logger.error("Error code - 104 : Adepter service is unable to read APS response", e);
                    //500
                }

//                String resAsString = new BasicResponseHandler().handleEntity(response.getEntity());
                if (logger.isDebugEnabled()) {
                    logger.debug("Current process response = " + resAsString);
                }
//                System.out.println(jsonNode);
                JsonNode id = jsonNode.get("id");
                processInstanceId = id.asText();
                // Check decision table failed or not
                JsonNode variables = jsonNode.path("variables");
                isLevelUsersDecisionFailed = checkDecisionTableFailed(variables);
                return isLevelUsersDecisionFailed;

            }
            //401
            else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Error code - 401 : Authentication to APS service from Adepter service failed");
            }
            //404
            else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                logger.error("Error code - 404 : APS service configuration seems to be wrong. APS service not found");
            }
            // *
            else {
//                String msg = "APS service response :: " + statusCode;
                if (logger.isDebugEnabled()) {
                    logger.debug("statusCode :: " + statusCode);
                }
                String msg = "Unable to process request, Please try again";
                logger.error("Error code - 206 : " + msg);
            }

        } catch (IOException e) {
            logger.error("Error code - 203 : Unable to get process information.", e);
        }
        return isLevelUsersDecisionFailed;
    }
}
