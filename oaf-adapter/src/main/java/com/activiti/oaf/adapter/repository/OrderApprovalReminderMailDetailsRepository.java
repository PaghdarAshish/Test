package com.activiti.oaf.adapter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.activiti.oaf.adapter.bean.OrderApprovalReminderMailDetails;

/**
 * Repository for reminder audit
 * 
 * @author Keval Bhatt
 *
 */
public interface OrderApprovalReminderMailDetailsRepository
		extends JpaRepository<OrderApprovalReminderMailDetails, Integer> {

	@Query("From OrderApprovalReminderMailDetails reminder_audit where reminder_audit.task_name like %:inqNumber%")
	List<OrderApprovalReminderMailDetails> searchRemindersByInqNumber(@Param("inqNumber") String inqNumber);
}
