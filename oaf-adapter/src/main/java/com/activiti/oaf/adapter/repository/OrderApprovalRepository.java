package com.activiti.oaf.adapter.repository;


import com.activiti.oaf.adapter.bean.OrderApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This is used to perform on {@link OrderApproval} Entity
 */
public interface OrderApprovalRepository extends JpaRepository<OrderApproval, Long> {

    @Query("from OrderApproval o where o.processInstanceId = :process_instance_id")
    List<OrderApproval> findOrderApprovalByProcessInstanceId(@Param("process_instance_id") String processInstanceId);

    @Query("from OrderApproval o where o.processInstanceId = :process_instance_id AND o.id = :id")
    OrderApproval findOrderApprovalByProcessInstanceIdAndId(@Param("process_instance_id") String processInstanceId, @Param("id") Long id);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber and o1.quotationNumber = o.quotationNumber) AND o.status = :status")
    List<OrderApproval> getOrderApprovalsByStatusOld(@Param("status") String status);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber and o1.quotationNumber = o.quotationNumber) AND o.inquiryNumber = :inquiry_number and o.quotationNumber = :quotation_number ")
    OrderApproval findOrderApprovalByInquiryNoAndQuotationNo(@Param("inquiry_number") String inquiryNumber, @Param("quotation_number") String quotationNumber);

    @Query("from OrderApproval o where o.inquiryNumber = :inquiry_number and o.quotationNumber = :quotation_number and o.version = :version")
    OrderApproval findQuoteByInquiryNoQuotationNoAndVersion(@Param("inquiry_number") String inquiryNumber, @Param("quotation_number") String quotationNumber, @Param("version") Integer version);

    @Query("from OrderApproval o where o.inquiryNumber = :inquiry_number and o.version = :version")
    OrderApproval findQuoteByInquiryNoAndVersion(@Param("inquiry_number") String inquiryNumber, @Param("version") Integer version);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber and o1.quotationNumber = o.quotationNumber) AND o.processInstanceId = :process_instance_id")
    OrderApproval findUniqueOrderApprovalByProcessInstanceId(@Param("process_instance_id") String processInstanceId);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber) AND o.inquiryNumber = :inquiry_number")
    OrderApproval findOrderApprovalByInquiryNo(@Param("inquiry_number") String inquiryNumber);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber) AND o.inquiryNumber = :inquiry_number AND o.processInstanceId = :process_instance_id AND o.status = :status")
    OrderApproval findOrderApprovalByProcessInstanceIdInquiryNoAndStatus(@Param("process_instance_id") String processInstanceId, @Param("inquiry_number") String inquiryNumber, @Param("status") String status);

    @Query("from OrderApproval o where o.version = (SELECT MAX(version) from OrderApproval as o1 where o1.inquiryNumber = o.inquiryNumber) AND o.status = :status")
    List<OrderApproval> getOrderApprovalsByStatus(@Param("status") String status);
    
    @Query("from OrderApproval o where o.inquiryNumber = :inquiryNumber")
    List<OrderApproval> getOrderApprovalsByInquiryNumber(@Param("inquiryNumber") String inquiryNumber);
}
