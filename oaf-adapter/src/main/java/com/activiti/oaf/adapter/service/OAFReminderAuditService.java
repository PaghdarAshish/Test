package com.activiti.oaf.adapter.service;

import com.activiti.oaf.adapter.bean.OrderApprovalReminderMailDetails;
import com.activiti.oaf.adapter.repository.OrderApprovalReminderMailDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This is service class responsible to save reminder audit
 * @author Keval Bhatt
 *
 */
@Service
public class OAFReminderAuditService {
	
	private static final Logger logger = LoggerFactory.getLogger(OAFReminderAuditService.class);
	
	@Autowired
	private OrderApprovalReminderMailDetailsRepository mailDetailsRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	/**
	 * Save the reminder audit
	 * @param approvalReminderMailDetails
	 * @return
	 */
	public int saveReminderDetail(OrderApprovalReminderMailDetails approvalReminderMailDetails) {
		try {
			mailDetailsRepository.save(approvalReminderMailDetails);			
			return 1;
		} catch (Exception e) {
			logger.error("OAFReminderAuditService.saveReminderDetail() :: exception : "+ e);			
			return 0;
		}
	}

	
	/**
	 * Search the reminder audit based on inqnumber
	 * @param inqNumber
	 * @return
	 */
	public List<OrderApprovalReminderMailDetails> searchRemindersByInqNumber(String inqNumber) {
		return this.mailDetailsRepository.searchRemindersByInqNumber(inqNumber);
	}
}
