package com.activiti.oaf.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.activiti.oaf.adapter.bean.OrderApprovalReportDetails;

/**
 * Repository for OAF report
 * 
 * @author Keval Bhatt
 *
 */
@Repository
public interface OrderApprovalReportDetailsRepository extends JpaRepository<OrderApprovalReportDetails, String> {

}
