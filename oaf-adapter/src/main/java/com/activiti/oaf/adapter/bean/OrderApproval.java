package com.activiti.oaf.adapter.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * OrderApproval Entity is responsible to store manageOAF restApi json payload as wll as inquiryNumber, Quotation Number, processInstanceId with its version.
 * @author Pradip Patel
 */

@Entity
@Table(
        name = "order_approval"
)
public class OrderApproval implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(
            name = "id"
    )
    private Long id;
    @Lob
    @Column(
            name = "json_payload"
    )
    private String jsonPayload;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "created"
    )
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "modified"
    )
    private Date modified;
    @Column(
            name = "status"
    )
    private String status;
    @Column(
            name = "process_instance_id",
            length = 20
    )
    private String processInstanceId = "0";
    @Column(
            name = "quotation_number"
    )
    private String quotationNumber;
    @Column(
            name = "inquiry_number"
    )
    private String inquiryNumber;
    @Column(
            name = "version"
    )
    private Integer version;

    @Column(
            name = "action"
    )
    private String action;
    @Column(
            name = "reject_task_id",
            length = 20
    )
    private String rejectTaskId= "0";
    @Column(
            name = "attachment_fail",nullable = false
    )
    private boolean attachmentFail;

    @Transient
    private String foundAttachments="";

    public OrderApproval() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(String jsonPayload) {
        this.jsonPayload = jsonPayload;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public void setQuotationNumber(String quotationNumber) {
        this.quotationNumber = quotationNumber;
    }

    public String getInquiryNumber() {
        return inquiryNumber;
    }

    public void setInquiryNumber(String inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRejectTaskId() {
        return rejectTaskId;
    }

    public void setRejectTaskId(String rejectTaskId) {
        this.rejectTaskId = rejectTaskId;
    }

    public boolean isAttachmentFail() {
        return attachmentFail;
    }

    public void setAttachmentFail(boolean attachmentFail) {
        this.attachmentFail = attachmentFail;
    }

    public String getFoundAttachments() {
        return foundAttachments;
    }

    public void setFoundAttachments(String foundAttachments) {
        this.foundAttachments = foundAttachments;
    }

    @Override
    public String toString() {
        return "OrderApproval{" +
                "id=" + id +
                ", jsonPayload='" + jsonPayload + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", status='" + status + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", quotationNumber='" + quotationNumber + '\'' +
                ", inquiryNumber='" + inquiryNumber + '\'' +
                ", version=" + version +
                ", action='" + action + '\'' +
                ", rejectTaskId='" + rejectTaskId + '\'' +
                ", attachmentFail=" + attachmentFail +
                '}';
    }
}