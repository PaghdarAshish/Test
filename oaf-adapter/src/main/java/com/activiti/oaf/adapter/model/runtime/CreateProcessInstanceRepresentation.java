/**
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.oaf.adapter.model.runtime;

import java.util.List;

/**
 * [APS Default class]
 */
public class CreateProcessInstanceRepresentation extends CompleteFormRepresentation {
    private String processDefinitionId;
    private String processDefinitionKey;
    private String name;
    private String businessKey;
    private List<RestVariable> variables;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    public String getBusinessKey() {
        return businessKey;
    }
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    public List<RestVariable> getVariables() {
        return variables;
    }
    public void setVariables(List<RestVariable> variables) {
        this.variables = variables;
    }

}
