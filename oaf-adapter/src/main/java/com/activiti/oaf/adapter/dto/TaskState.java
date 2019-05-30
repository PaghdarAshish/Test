/**
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.oaf.adapter.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

/**
 * Task states values enumeration.
 */
public enum TaskState {
    ACTIVE("active"),
    COMPLETED("completed"),
    ALL("all");

    private String text;

    TaskState(final String text) {
        this.text = text;
    }

    /**
     * Get TaskState from text.
     * 
     * @param inputState input text
     * @return the corresponding TaskState, or null
     */
    @JsonCreator
    public static TaskState fromText(final String inputState) {
        if (!StringUtils.isEmpty(inputState)) {
            for (TaskState state : values()) {
                if(state.getText().equals(inputState)) {
                    return state;
                }
            }
        }

        return null;
    }

    @JsonValue
    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return text;
    }
}
