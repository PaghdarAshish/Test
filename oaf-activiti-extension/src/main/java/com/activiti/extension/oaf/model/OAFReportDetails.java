package com.activiti.extension.oaf.model;

/**
 * Bean class for report
 * @author Keval Bhatt
 *
 */
public class OAFReportDetails {
		
	private String company_code;
	private String inq;
	private String quote;	
	private String quote_type;
	private Long creation_date;
	private String sold_to_party;
	private String dq;
	private String tonnage;
	private String last_approval_date;
	private String last_approval_user;
	private String rejection_date;
	private String rejection_user;
	private String rejection_reason;
	private String pending_with;
	private String approved_date;
	private String elapsed_day;
	private String sales_office;
	private String sales_engineer;	
	private String task_id;	
	private String process_id;
	
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
	
	public Long getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(Long creation_date) {
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
		return "OAFReportDetails [company_code=" + company_code + ", inq=" + inq + ", quote=" + quote + ", quote_type="
				+ quote_type + ", creation_date=" + creation_date + ", sold_to_party=" + sold_to_party + ", dq=" + dq
				+ ", tonnage=" + tonnage + ", last_approval_date=" + last_approval_date + ", last_approval_user="
				+ last_approval_user + ", rejection_date=" + rejection_date + ", rejection_user=" + rejection_user
				+ ", rejection_reason=" + rejection_reason + ", pending_with=" + pending_with + ", approved_date="
				+ approved_date + ", elapsed_day=" + elapsed_day + ", sales_office=" + sales_office
				+ ", sales_engineer=" + sales_engineer + ", task_id=" + task_id + ", process_id=" + process_id + "]";
	}
	
	
}
