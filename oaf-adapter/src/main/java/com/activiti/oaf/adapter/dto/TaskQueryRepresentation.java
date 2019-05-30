/**
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.oaf.adapter.dto;

import java.util.Date;

/**
 * Representation class for a task query request 
 */
public class TaskQueryRepresentation extends PagedDataRepresentation {

    protected String taskId;

    protected Long appDefinitionId;

    protected String processInstanceId;

    protected String processDefinitionId;

    protected String text;

    protected TaskState state;

    protected TaskSort sort;

    protected String assignment;

    protected Date dueBefore;

    protected Date dueAfter;

    protected boolean includeProcessInstance;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getAppDefinitionId() {
        return appDefinitionId;
    }

    public void setAppDefinitionId(final Long appDefinitionId) {
        this.appDefinitionId = appDefinitionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(final String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(final String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(final TaskState state) {
        this.state = state;
    }

    public TaskSort getSort() {
        return sort;
    }

    public void setSort(final TaskSort sort) {
        this.sort = sort;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(final String assignment) {
        this.assignment = assignment;
    }

    public Date getDueBefore() {
        return dueBefore;
    }

    public void setDueBefore(final Date dueBefore) {
        this.dueBefore = dueBefore;
    }

    public Date getDueAfter() {
        return dueAfter;
    }

    public void setDueAfter(final Date dueAfter) {
        this.dueAfter = dueAfter;
    }

    public boolean isIncludeProcessInstance() {
        return includeProcessInstance;
    }

    public void setIncludeProcessInstance(final boolean includeProcessInstance) {
        this.includeProcessInstance = includeProcessInstance;
    }
}
