package com.activiti.oaf.adapter.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activiti.oaf.adapter.bean.OrderApprovalReportDetails;
import com.activiti.oaf.adapter.constants.OAFConstants;
import com.activiti.oaf.adapter.repository.OrderApprovalReportDetailsRepository;

/**
 * This is service class responsible to save the report detail and get the
 * report detail
 * 
 * @author Keval Bhatt
 *
 */
@Service
public class OAFReportDetailsService implements OAFConstants {

	private static final Logger logger = LoggerFactory.getLogger(OAFReportDetailsService.class);

	@Autowired
	private OrderApprovalReportDetailsRepository approvalReportDetailsRepository;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Save the report detail
	 * 
	 * @param orderApprovalReportDetails
	 * @return
	 */
	public int saveReportData(OrderApprovalReportDetails orderApprovalReportDetails) {
		try {
			approvalReportDetailsRepository.save(orderApprovalReportDetails);
			return 1;
		} catch (Exception e) {
			logger.error("OAFReportDetailsService.saveReportData() :: exception : " + e);
			return 0;
		}
	}

	/**
	 * This class is responsible to delete the report detail by id
	 * 
	 * @param id
	 * @return
	 */
	public int deleteReportByInqNumber(String inqNumber) {
		try {
			approvalReportDetailsRepository.delete(inqNumber);
			return 1;
		} catch (Exception e) {
			logger.error("OAFReportDetailsService.deleteReportById() :: exception : " + e);
			return 0;
		}
	}

	/**
	 * This method is used to get the report detail based on the searched fields
	 * 
	 * @param company_code
	 * @param quote_type
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<OrderApprovalReportDetails> findByReportDetail(String company_code, String quote_type, Date fromDate,
			Date toDate) {
		StringBuilder hqlReportDetailBuilder = new StringBuilder();
		hqlReportDetailBuilder.append("select report from OrderApprovalReportDetails report");
		if (toDate != null)
			toDate = addEndTime(toDate);
		if (fromDate != null && toDate != null) {
			hqlReportDetailBuilder.append(" where (report.creation_date between :fromDate and :toDate)");
		}

		List<String> companyCodesList = null;
		if (company_code != null && !company_code.isEmpty()) {
			companyCodesList = new ArrayList<>();
			companyCodesList = Arrays.asList(company_code.split(","));
			if (hqlReportDetailBuilder.toString().contains("where")) {
				hqlReportDetailBuilder.append(" and report.company_code in :company_code");
			} else {
				hqlReportDetailBuilder.append(" where report.company_code in :company_code");
			}
		}
		if (quote_type != null && !quote_type.isEmpty()) {
			if (hqlReportDetailBuilder.toString().contains("where")) {
				hqlReportDetailBuilder.append(" and report.quote_type in :quote_type");
			} else {
				hqlReportDetailBuilder.append(" where report.quote_type in :quote_type");
			}
		}
		hqlReportDetailBuilder.append(" order by report.creation_date desc");
		logger.info("OAFReportDetailsService.findByReportDetail() :: HQL : " + hqlReportDetailBuilder);
		List<OrderApprovalReportDetails> reportDetails = executeQueryToGetFilteredReportDetail(companyCodesList,
				quote_type, fromDate, toDate, hqlReportDetailBuilder.toString());
		if (reportDetails != null) {
			logger.info("OAFReportDetailsService.findByReportDetail() :: =========reportDetails START ======= ");
			logger.info(reportDetails.toString());
			logger.info("OAFReportDetailsService.findByReportDetail() :: =========reportDetails END======= ");
			return reportDetails;
		} else {
			return null;
		}
	}

	/**
	 * The method is responsible to execute the query to get the report detail
	 * 
	 * @param companyCodesList
	 * @param quote_type
	 * @param fromDate
	 * @param toDate
	 * @param hqlReportDetailBuilder
	 * @return
	 */
	private List<OrderApprovalReportDetails> executeQueryToGetFilteredReportDetail(List<String> companyCodesList,
			String quote_type, Date fromDate, Date toDate, String hqlReportDetailBuilder) {
		TypedQuery<OrderApprovalReportDetails> createQuery = entityManager.createQuery(hqlReportDetailBuilder,
				OrderApprovalReportDetails.class);
		try {
			if (fromDate != null && toDate != null) {
				createQuery.setParameter(FROMDATE, fromDate);
				createQuery.setParameter(TODATE, toDate);
			}

			if (companyCodesList != null) {
				createQuery.setParameter(COMPANY_CODE, companyCodesList);
			}
			if (!quote_type.isEmpty()) {
				createQuery.setParameter(QUOTE_TYPE, quote_type);
			}
			List<OrderApprovalReportDetails> resultList = createQuery.getResultList();
			logger.info("OAFReportDetailsService.findByReportDetail() :: =========resultList======= " + resultList);
			return resultList;
		} catch (Exception e) {
			logger.error("OAFReportDetailsService.findByReportDetail() :: Exception :: " + e);
			return null;
		}

	}

	/**
	 * This method is responsible to addEndtime
	 * 
	 * @param date
	 * @return
	 */
	public static Date addEndTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
		return cal.getTime();
	}

}
