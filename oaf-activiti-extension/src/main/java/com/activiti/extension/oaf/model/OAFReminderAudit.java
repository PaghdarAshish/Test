package com.activiti.extension.oaf.model;

/**
 * Bean class for reminder audit
 * @author Keval Bhatt
 *
 */
public class OAFReminderAudit {

	private String reminder_date;
	
	private String subject;
	
	private String toEmail;
	
	private String ccEmail;
	
	private String bccEmail;
	
	private String process_instance_id;
	
	private String task_id;
	
	private String process_name;
	
	private String task_name;

	public String getReminder_date() {
		return reminder_date;
	}

	public void setReminder_date(String reminder_date) {
		this.reminder_date = reminder_date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public String getBccEmail() {
		return bccEmail;
	}

	public void setBccEmail(String bccEmail) {
		this.bccEmail = bccEmail;
	}

	public String getProcess_instance_id() {
		return process_instance_id;
	}

	public void setProcess_instance_id(String process_instance_id) {
		this.process_instance_id = process_instance_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	
	
}
