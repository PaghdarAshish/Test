/**
 * Copyright 2005-2018 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.oaf.adapter.model.runtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Pojo representing a variable used in REST-service which definies it's name,
 * variable, scope and type.
 * 
 * @author Frederik Heremans [APS Default class]
 */
public class RestVariable {

    public enum RestVariableScope {
        LOCAL, GLOBAL
    };

    private String name;
    private String type;
    private RestVariableScope variableScope;
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonInclude(Include.NON_NULL)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public RestVariableScope getVariableScope() {
        return variableScope;
    }

    public void setVariableScope(RestVariableScope variableScope) {
        this.variableScope = variableScope;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @JsonInclude(Include.NON_NULL)
    public String getScope() {
        String scope = null;
        if (variableScope != null) {
            scope = variableScope.name().toLowerCase();
        }
        return scope;
    }

    public void setScope(String scope) {
        setVariableScope(getScopeFromString(scope));
    }

    public static RestVariableScope getScopeFromString(String scope) {
        if (scope != null) {
            for (RestVariableScope s : RestVariableScope.values()) {
                if (s.name().equalsIgnoreCase(scope)) {
                    return s;
                }
            }
            return null;
        } else {
            return null;
        }
    }
}
