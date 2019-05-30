package com.activiti.extension.oaf.model;

import java.util.Date;

public class OAFHolidays {

	private Long id;

	private String event_name;

	private String created_by;

	private String modified_by;

	Date start_date;

	Date end_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getModified_by() {
		return modified_by;
	}

	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	@Override
	public String toString() {
		return "OAFHolidays [id=" + id + ", event_name=" + event_name + ", created_by=" + created_by + ", modified_by="
				+ modified_by + ", start_date=" + start_date + ", end_date=" + end_date + "]";
	}
	
	

}
