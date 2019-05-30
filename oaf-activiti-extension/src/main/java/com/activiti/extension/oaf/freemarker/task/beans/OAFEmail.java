package com.activiti.extension.oaf.freemarker.task.beans;

import javax.mail.internet.MimeBodyPart;

import org.activiti.engine.impl.util.json.JSONObject;

/**
 * @author Keval Bhatt
 * This bean is responsible to set and get the email content like to,cc,subject,etc.
 *
 */
public class OAFEmail {
	private String to;
	private String cc;
	private String subject;
	private String templateProcessedContent;
	private JSONObject attachedDocumetIdentityList;
	private MimeBodyPart attachedSingleFile;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplateProcessedContent() {
		return templateProcessedContent;
	}

	public void setTemplateProcessedContent(String templateProcessedContent) {
		this.templateProcessedContent = templateProcessedContent;
	}

	public JSONObject getAttachedDocumetIdentityList() {
		return attachedDocumetIdentityList;
	}

	public void setAttachedDocumetIdentityList(JSONObject list) {
		this.attachedDocumetIdentityList = list;
	}

	public MimeBodyPart getAttachedSingleFile() {
		return attachedSingleFile;
	}

	public void setAttachedSingleFile(MimeBodyPart attachedSingleFile) {
		this.attachedSingleFile = attachedSingleFile;
	}
	
	

}
