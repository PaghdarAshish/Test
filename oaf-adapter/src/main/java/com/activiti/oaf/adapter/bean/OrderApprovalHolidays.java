package com.activiti.oaf.adapter.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * This bean will use to create holiday calendar (Not Worked now)
 * 
 * @author Keval Bhatt
 *
 */

//@Table(name="order_approval_holidays")
//@Entity
public class OrderApprovalHolidays {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "event_name")
	private String event_name;

	@Column(name = "created_by")
	private String created_by;

	@Column(name = "modified_by")
	private String modified_by;

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	Date start_date;

	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	Date end_date;

	@Column(name = "holiday_create_datetime")
	@CreationTimestamp
	private Date holiday_create_datetime;

	@Column(name = "holiday_update_datetime")
	@UpdateTimestamp
	private Date holiday_update_datetime;

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

	public Date getHoliday_create_datetime() {
		return holiday_create_datetime;
	}

	public void setHoliday_create_datetime(Date holiday_create_datetime) {
		this.holiday_create_datetime = holiday_create_datetime;
	}

	public Date getHoliday_update_datetime() {
		return holiday_update_datetime;
	}

	public void setHoliday_update_datetime(Date holiday_update_datetime) {
		this.holiday_update_datetime = holiday_update_datetime;
	}

	@Override
	public String toString() {
		return "OrderApprovalHolidaysMasterData [id=" + id + ", event_name=" + event_name + ", created_by=" + created_by
				+ ", modified_by=" + modified_by + ", start_date=" + start_date + ", end_date=" + end_date
				+ ", holiday_create_datetime=" + holiday_create_datetime + ", holiday_update_datetime="
				+ holiday_update_datetime + "]";
	}

}
