package com.activiti.oaf.adapter.bean;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Bean or entity class for oaf report
 * @author Keval Bhatt
 *
 */
@Entity
@Table(name="order_approval_report")
public class OrderApprovalReportDetails {
	

	@Column(name="company_code")
	private String company_code;
	@Id	
	@Column(name="inq")
	private String inq;
	@Column(name="quote")
	private String quote;	
	@Column(name="quote_type")
	private String quote_type;	
	@Temporal(TemporalType.DATE)
	@Column(name = "creation_date")
	private Date creation_date;
	@Column(name="sold_to_party")
	private String sold_to_party;
	@Column(name="dq")
	private String dq;
	@Column(name="tonnage")
	private String tonnage;
	@Column(name="last_approval_date")
	private String last_approval_date;
	@Column(name="last_approval_user")
	private String last_approval_user;	
	@Column(name="rejection_date")
	private String rejection_date;
	@Column(name="rejection_user")
	private String rejection_user;
	@Column(name="rejection_reason")
	private String rejection_reason;
	@Column(name="pending_with")
	private String pending_with;	
	@Column(name="approved_date")
	private String approved_date;
	@Column(name="elapsed_day")
	private String elapsed_day;
	@Column(name="sales_office")
	private String sales_office;
	@Column(name="sales_engineer")
	private String sales_engineer;
	@Column(name="task_id")
	private String task_id;
	@Column(name="process_id")
	private String process_id;
	
	
	@Transient
	private Long fromDate;
	@Transient
	private Long toDate;
	
	@Column(name="report_create_datetime")
	@CreationTimestamp
	private Date report_create_datetime;
	
	@Column(name="report_update_datetime")
	@UpdateTimestamp
	private Date report_update_datetime;
	
	public String getCompany_code() {
		return company_code;
	}
	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}
	public String getInq() {
		return inq;
	}
	public void setInq(String inq) {
		this.inq = inq;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getQuote_type() {
		return quote_type;
	}
	public void setQuote_type(String quote_type) {
		this.quote_type = quote_type;
	}
	
	public Date getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}
	public String getSold_to_party() {
		return sold_to_party;
	}
	public void setSold_to_party(String sold_to_party) {
		this.sold_to_party = sold_to_party;
	}
	public String getDq() {
		return dq;
	}
	public void setDq(String dq) {
		this.dq = dq;
	}
	public String getTonnage() {
		return tonnage;
	}
	public void setTonnage(String tonnage) {
		this.tonnage = tonnage;
	}
	public String getLast_approval_date() {
		return last_approval_date;
	}
	public void setLast_approval_date(String last_approval_date) {
		this.last_approval_date = last_approval_date;
	}
	public String getLast_approval_user() {
		return last_approval_user;
	}
	public void setLast_approval_user(String last_approval_user) {
		this.last_approval_user = last_approval_user;
	}
	public String getRejection_date() {
		return rejection_date;
	}
	public void setRejection_date(String rejection_date) {
		this.rejection_date = rejection_date;
	}
	public String getRejection_user() {
		return rejection_user;
	}
	public void setRejection_user(String rejection_user) {
		this.rejection_user = rejection_user;
	}
	public String getRejection_reason() {
		return rejection_reason;
	}
	public void setRejection_reason(String rejection_reason) {
		this.rejection_reason = rejection_reason;
	}
	public String getPending_with() {
		return pending_with;
	}
	public void setPending_with(String pending_with) {
		this.pending_with = pending_with;
	}
	public String getApproved_date() {
		return approved_date;
	}
	public void setApproved_date(String approved_date) {
		this.approved_date = approved_date;
	}
	public String getElapsed_day() {
		return elapsed_day;
	}
	public void setElapsed_day(String elapsed_day) {
		this.elapsed_day = elapsed_day;
	}
	public String getSales_office() {
		return sales_office;
	}
	public void setSales_office(String sales_office) {
		this.sales_office = sales_office;
	}
	public String getSales_engineer() {
		return sales_engineer;
	}
	public void setSales_engineer(String sales_engineer) {
		this.sales_engineer = sales_engineer;
	}
	public Long getFromDate() {
		return fromDate;
	}
	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}
	public Long getToDate() {
		return toDate;
	}
	public void setToDate(Long toDate) {
		this.toDate = toDate;
	}
	
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getProcess_id() {
		return process_id;
	}
	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}
	
	@Override
	public String toString() {
		return "OrderApprovalReportDetails [company_code=" + company_code + ", inq=" + inq + ", quote=" + quote
				+ ", quote_type=" + quote_type + ", creation_date=" + creation_date + ", sold_to_party=" + sold_to_party
				+ ", dq=" + dq + ", tonnage=" + tonnage + ", last_approval_date=" + last_approval_date
				+ ", last_approval_user=" + last_approval_user + ", rejection_date=" + rejection_date
				+ ", rejection_user=" + rejection_user + ", rejection_reason=" + rejection_reason + ", pending_with="
				+ pending_with + ", approved_date=" + approved_date + ", elapsed_day=" + elapsed_day + ", sales_office="
				+ sales_office + ", sales_engineer=" + sales_engineer + ", task_id=" + task_id + ", process_id="
				+ process_id + ", fromDate=" + fromDate + ", toDate=" + toDate + ", report_create_datetime="
				+ report_create_datetime + ", report_update_datetime=" + report_update_datetime + "]";
	}
	
	
	
}
