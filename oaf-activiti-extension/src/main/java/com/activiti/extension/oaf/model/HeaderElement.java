package com.activiti.extension.oaf.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean class for store value and update status
 * @author Pradip Patel
 */
public class HeaderElement implements Serializable {

    private String value;
    private Boolean update;

    @Override
    public String toString() {
        return "HeaderElement{" +
                "value='" + value + '\'' +
                ", update=" + update +
                '}';
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderElement that = (HeaderElement) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, update);
    }
}
