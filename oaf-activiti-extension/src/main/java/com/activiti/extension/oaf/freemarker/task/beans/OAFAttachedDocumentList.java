package com.activiti.extension.oaf.freemarker.task.beans;

import org.activiti.engine.impl.util.json.JSONObject;

import java.util.List;

/**
 * @author Avani Purohit
 * This class is used to get and set the attached document list.
 *
 */
public class OAFAttachedDocumentList {

	List<JSONObject> level1GeneralSMDocument;
	List<JSONObject> level1SeniorGMDocument;
	List<JSONObject> level2CreditMDocument;
	List<JSONObject> level2SeniorFADocument;
	List<JSONObject> level2SeniorFMDocument;
	List<JSONObject> level3FinanceDDocument;
	List<JSONObject> level4VicePDocument;

	public List<JSONObject> getLevel1GeneralSMDocument() {
		return level1GeneralSMDocument;
	}

	public void setLevel1GeneralSMDocument(List<JSONObject> level1GeneralSMDocument) {
		this.level1GeneralSMDocument = level1GeneralSMDocument;
	}

	public List<JSONObject> getLevel1SeniorGMDocument() {
		return level1SeniorGMDocument;
	}

	public void setLevel1SeniorGMDocument(List<JSONObject> level1SeniorGMDocument) {
		this.level1SeniorGMDocument = level1SeniorGMDocument;
	}

	public List<JSONObject> getLevel2CreditMDocument() {
		return level2CreditMDocument;
	}

	public void setLevel2CreditMDocument(List<JSONObject> level2CreditMDocument) {
		this.level2CreditMDocument = level2CreditMDocument;
	}

	public List<JSONObject> getLevel2SeniorFADocument() {
		return level2SeniorFADocument;
	}

	public void setLevel2SeniorFADocument(List<JSONObject> level2SeniorFADocument) {
		this.level2SeniorFADocument = level2SeniorFADocument;
	}

	public List<JSONObject> getLevel2SeniorFMDocument() {
		return level2SeniorFMDocument;
	}

	public void setLevel2SeniorFMDocument(List<JSONObject> level2SeniorFMDocument) {
		this.level2SeniorFMDocument = level2SeniorFMDocument;
	}

	public List<JSONObject> getLevel3FinanceDDocument() {
		return level3FinanceDDocument;
	}

	public void setLevel3FinanceDDocument(List<JSONObject> level3FinanceDDocument) {
		this.level3FinanceDDocument = level3FinanceDDocument;
	}

	public List<JSONObject> getLevel4VicePDocument() {
		return level4VicePDocument;
	}

	public void setLevel4VicePDocument(List<JSONObject> level4VicePDocument) {
		this.level4VicePDocument = level4VicePDocument;
	}

	@Override
	public String toString() {
		return "OAFAttachedDocumentIdentification [level1GeneralSMDocument=" + level1GeneralSMDocument
				+ ", level1SeniorGMDocument=" + level1SeniorGMDocument + ", level2CreditMDocument="
				+ level2CreditMDocument + ", level2SeniorFADocument=" + level2SeniorFADocument
				+ ", level2SeniorFMDocument=" + level2SeniorFMDocument + ", level3FinanceDDocument="
				+ level3FinanceDDocument + ", level4VicePDocument=" + level4VicePDocument + "]";
	}

}
