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
 * Task sorting values enumeration.
 */
public enum TaskSort {
    CREATED_DESC("created-desc"),
    CREATED_ASC("created-asc"),
    DUE_DESC("due-desc"),
    DUE_ASC("due-asc");

    private String text;

    TaskSort(final String text) {
        this.text = text;
    }

    /**
     * Get TaskSort from text.
     * 
     * @param inputSort input text
     * @return the corresponding TaskSort, or null
     */
    @JsonCreator
    public static TaskSort fromText(String inputSort) {
        if (!StringUtils.isEmpty(inputSort)) {
            for (TaskSort sort : values()) {
                if (sort.getText().equals(inputSort)) {
                    return sort;
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

