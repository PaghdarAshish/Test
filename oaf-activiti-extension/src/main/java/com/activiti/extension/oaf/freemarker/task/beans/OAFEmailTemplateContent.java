package com.activiti.extension.oaf.freemarker.task.beans;

import org.activiti.engine.impl.util.json.JSONObject;

/**
 * @author Keval Bhatt This bean is responsible to get and set the email
 *         template content.
 *
 */
public class OAFEmailTemplateContent {

	private String inquiry;
	private String quote;
	private String salesOffice;
	private String project;
	private String client;
	private String location;
	private String weightMt;	
	private String salesComments;
	private String revision;
	private String dq;
	private String vkbur;
	private String regionFlag;
	private String marginCPP;
	private String engineeringServices;
	private boolean isUserDecisionTableFailed;
	private boolean isApprovalEmail;
	private String approveTaskUrl;
	private String rejectTaskUrl;
	private String viewTaskUrl;
	private boolean isSapFailEmail;
	private String retrySapFailUrl;
	private String sapFailReason;
	private String rejectedByUserName;
	private String rejectedDate;
	private String rejectedReason;
	private String initMail;
	private JSONObject approvalInformaiton;
	private JSONObject approvalPendingInformaiton;
	private int daysElapsedFromSubmission;
	private JSONObject downloadLinks;
	private boolean isPdfDownloadEnable;
	private String pdfFileName;
	private String pdfDownloadLink;
	public JSONObject getDownloadLinks() {
		return downloadLinks;
	}

	public void setDownloadLinks(JSONObject downloadLinks) {
		this.downloadLinks = downloadLinks;
	}

	public String getInquiry() {
		return inquiry;
	}

	public void setInquiry(String inquiry) {
		this.inquiry = inquiry;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getSalesOffice() {
		return salesOffice;
	}

	public void setSalesOffice(String salesOffice) {
		this.salesOffice = salesOffice;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWeightMt() {
		return weightMt;
	}

	public void setWeightMt(String weightMt) {
		this.weightMt = weightMt;
	}

	public String getSalesComments() {
		return salesComments;
	}

	public void setSalesComments(String salesComments) {
		this.salesComments = salesComments;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getDq() {
		return dq;
	}

	public void setDq(String dq) {
		this.dq = dq;
	}

	
	public String getVkbur() {
		return vkbur;
	}

	public void setVkbur(String vkbur) {
		this.vkbur = vkbur;
	}

	public String getRegionFlag() {
		return regionFlag;
	}

	public void setRegionFlag(String regionFlag) {
		this.regionFlag = regionFlag;
	}


	public String getMarginCPP() {
		return marginCPP;
	}

	public void setMarginCPP(String marginCPP) {
		this.marginCPP = marginCPP;
	}

	public String getEngineeringServices() {
		return engineeringServices;
	}

	public void setEngineeringServices(String engineeringServices) {
		this.engineeringServices = engineeringServices;
	}

	public boolean isUserDecisionTableFailed() {
		return isUserDecisionTableFailed;
	}

	public void setUserDecisionTableFailed(boolean isUserDecisionTableFailed) {
		this.isUserDecisionTableFailed = isUserDecisionTableFailed;
	}

	public boolean isApprovalEmail() {
		return isApprovalEmail;
	}

	public String getApproveTaskUrl() {
		return approveTaskUrl;
	}

	public void setApproveTaskUrl(String approveTaskUrl) {
		this.approveTaskUrl = approveTaskUrl;
	}

	public String getRejectTaskUrl() {
		return rejectTaskUrl;
	}

	public void setRejectTaskUrl(String rejectTaskUrl) {
		this.rejectTaskUrl = rejectTaskUrl;
	}

	public String getViewTaskUrl() {
		return viewTaskUrl;
	}

	public void setViewTaskUrl(String viewTaskUrl) {
		this.viewTaskUrl = viewTaskUrl;
	}

	public boolean isSapFailEmail() {
		return isSapFailEmail;
	}

	public void setSapFailEmail(boolean isSapFailEmail) {
		this.isSapFailEmail = isSapFailEmail;
	}

	public String getSapFailReason() {
		return sapFailReason;
	}

	public void setSapFailReason(String sapFailReason) {
		this.sapFailReason = sapFailReason;
	}

	public String getRetrySapFailUrl() {
		return retrySapFailUrl;
	}

	public void setRetrySapFailUrl(String retrySapFailUrl) {
		this.retrySapFailUrl = retrySapFailUrl;
	}

	public String getRejectedByUserName() {
		return rejectedByUserName;
	}

	public void setRejectedByUserName(String rejectedByUserName) {
		this.rejectedByUserName = rejectedByUserName;
	}

	public String getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(String rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public String getRejectedReason() {
		return rejectedReason;
	}

	public String getInitMail() {
		return initMail;
	}

	public void setInitMail(String initMail) {
		this.initMail = initMail;
	}

	public void setRejectedReason(String rejectedReason) {
		this.rejectedReason = rejectedReason;
	}

	public void setApprovalEmail(boolean isApprovalEmail) {
		this.isApprovalEmail = isApprovalEmail;
	}

	public JSONObject getApprovalInformaiton() {
		return approvalInformaiton;
	}

	public void setApprovalInformaiton(JSONObject approvalInformaiton) {
		this.approvalInformaiton = approvalInformaiton;
	}

	public JSONObject getApprovalPendingInformaiton() {
		return approvalPendingInformaiton;
	}

	public void setApprovalPendingInformaiton(JSONObject approvalPendingInformaiton) {
		this.approvalPendingInformaiton = approvalPendingInformaiton;
	}

	public int getDaysElapsedFromSubmission() {
		return daysElapsedFromSubmission;
	}

	public void setDaysElapsedFromSubmission(int daysElapsedFromSubmission) {
		this.daysElapsedFromSubmission = daysElapsedFromSubmission;
	}

	public boolean isPdfDownloadEnable() {
		return isPdfDownloadEnable;
	}

	public void setPdfDownloadEnable(boolean isPdfDownloadEnable) {
		this.isPdfDownloadEnable = isPdfDownloadEnable;
	}

	public String getPdfFileName() {
		return pdfFileName;
	}

	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}

	public String getPdfDownloadLink() {
		return pdfDownloadLink;
	}

	public void setPdfDownloadLink(String pdfDownloadLink) {
		this.pdfDownloadLink = pdfDownloadLink;
	}

	
}
