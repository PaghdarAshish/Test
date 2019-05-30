package com.activiti.extension.oaf.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean class store Attachment information.
 * @author Pradip Patel
 */
public class UIAttachment implements Serializable {

    private String docName;
    private String category;
    private String guid;
    private String date;
    private String downloadURL;

    @Override
    public String toString() {
        return "UIAttachment{" +
                "docName='" + docName + '\'' +
                ", category='" + category + '\'' +
                ", guid='" + guid + '\'' +
                ", date='" + date + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
    }

    //getter

    public String getDocName() {
        return docName;
    }

    public String getCategory() {
        return category;
    }

    public String getGuid() {
        return guid;
    }

    public String getDate() {
        return date;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
//setter


    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIAttachment that = (UIAttachment) o;
        return Objects.equals(docName, that.docName) &&
                Objects.equals(category, that.category) &&
                Objects.equals(guid, that.guid) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docName, category, guid, date);
    }
}
