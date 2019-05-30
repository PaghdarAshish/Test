package com.activiti.extension.oaf.freemarker.task.beans;

import org.activiti.engine.impl.util.json.JSONObject;

import java.util.List;

/**
 * @author Avani Purohit This class is used to get and set the list of
 *         attachment links
 *
 */
public class OAFAttchmentDownloadLink {

	List<JSONObject> level1Approval1DownloadLinks;
	List<JSONObject> level1Approval2DownloadLinks;
	List<JSONObject> level2Approval1DownloadLinks;
	List<JSONObject> level2Approval2DownloadLinks;
	List<JSONObject> level3Approval1DownloadLinks;
	List<JSONObject> level4Approval1DownloadLinks;
	List<JSONObject> level5Approval1DownloadLinks;
	List<JSONObject> level6Approval1DownloadLinks;
	List<JSONObject> level7Approval1DownloadLinks;

	public List<JSONObject> getLevel1Approval1DownloadLinks() {
		return level1Approval1DownloadLinks;
	}

	public void setLevel1Approval1DownloadLinks(List<JSONObject> level1Approval1DownloadLinks) {
		this.level1Approval1DownloadLinks = level1Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel1Approval2DownloadLinks() {
		return level1Approval2DownloadLinks;
	}

	public void setLevel1Approval2DownloadLinks(List<JSONObject> level1Approval2DownloadLinks) {
		this.level1Approval2DownloadLinks = level1Approval2DownloadLinks;
	}

	public List<JSONObject> getLevel2Approval1DownloadLinks() {
		return level2Approval1DownloadLinks;
	}

	public void setLevel2Approval1DownloadLinks(List<JSONObject> level2Approval1DownloadLinks) {
		this.level2Approval1DownloadLinks = level2Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel2Approval2DownloadLinks() {
		return level2Approval2DownloadLinks;
	}

	public void setLevel2Approval2DownloadLinks(List<JSONObject> level2Approval2DownloadLinks) {
		this.level2Approval2DownloadLinks = level2Approval2DownloadLinks;
	}

	public List<JSONObject> getLevel3Approval1DownloadLinks() {
		return level3Approval1DownloadLinks;
	}

	public void setLevel3Approval1DownloadLinks(List<JSONObject> level3Approval1DownloadLinks) {
		this.level3Approval1DownloadLinks = level3Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel4Approval1DownloadLinks() {
		return level4Approval1DownloadLinks;
	}

	public void setLevel4Approval1DownloadLinks(List<JSONObject> level4Approval1DownloadLinks) {
		this.level4Approval1DownloadLinks = level4Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel5Approval1DownloadLinks() {
		return level5Approval1DownloadLinks;
	}

	public void setLevel5Approval1DownloadLinks(List<JSONObject> level5Approval1DownloadLinks) {
		this.level5Approval1DownloadLinks = level5Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel6Approval1DownloadLinks() {
		return level6Approval1DownloadLinks;
	}

	public void setLevel6Approval1DownloadLinks(List<JSONObject> level6Approval1DownloadLinks) {
		this.level6Approval1DownloadLinks = level6Approval1DownloadLinks;
	}

	public List<JSONObject> getLevel7Approval1DownloadLinks() {
		return level7Approval1DownloadLinks;
	}

	public void setLevel7Approval1DownloadLinks(List<JSONObject> level7Approval1DownloadLinks) {
		this.level7Approval1DownloadLinks = level7Approval1DownloadLinks;
	}

	@Override
	public String toString() {
		return "OAFAttchmentDownloadLink [level1GeneralSMDownloadLinks=" + level1Approval1DownloadLinks
				+ ", level1SeniorGMDownloadLinks=" + level1Approval2DownloadLinks + ", level2CreditMDDownloadLinks="
				+ level2Approval1DownloadLinks + ", level2SeniorFADownloadLinks=" + level2Approval2DownloadLinks
				+ ", level3SeniorFMDownloadLinks=" + level3Approval1DownloadLinks + ", level4FinanceDDownloadLinks="
				+ level4Approval1DownloadLinks + ", level5VicePDownloadLinks=" + level5Approval1DownloadLinks
				+ ", level6DownloadLinks=" + level6Approval1DownloadLinks + ", level7DownloadLinks="
				+ level7Approval1DownloadLinks + "]";
	}

}
