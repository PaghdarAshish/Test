package com.activiti.extension.oaf.freemarker.task.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFAttachedDocumentList;

/**
 * This class is responsible to difference the attachments link based on type of
 * user
 * 
 * @author Avani Purohit
 */
public class OAFEmailAttachmentsUtill implements OAFConstants {
	private static Logger logger = LoggerFactory.getLogger(OAFEmailAttachmentsUtill.class);

	public static OAFAttachedDocumentList gettingListOfAttchmentByCategory(String attachmentDataString,
			DelegateExecution execution) {
		logger.debug("OAFEmailAttachmentsUtill.gettingListOfAttchmentByCategory() :: inside method of list getting");
		JSONArray getAttachmentArray;
		List<JSONObject> categoryAList = new ArrayList();
		List<JSONObject> categoryBList = new ArrayList();
		List<JSONObject> categoryCList = new ArrayList();
		List<JSONObject> listAC = new ArrayList();
		List<JSONObject> listBC = new ArrayList();
		JSONObject attachmentObject = new JSONObject(attachmentDataString);
		getAttachmentArray = attachmentObject.getJSONArray(ATTACHMENTS);
		for (int i = 0; i < getAttachmentArray.length(); i++) {
			JSONObject objects = getAttachmentArray.getJSONObject(i);
			if (objects.get(CATEGORY).equals(CATEGORYA)) {
				categoryAList.add(objects);
			} else if (objects.get(CATEGORY).equals(CATEGORYB)) {
				categoryBList.add(objects);
			} else {
				categoryCList.add(objects);
			}
		}
		for (int i = 0; i < categoryAList.size(); i++)
			listAC.add(categoryAList.get(i));
		for (int i = 0; i < categoryCList.size(); i++)
			listAC.add(categoryCList.get(i));
		for (int i = 0; i < categoryBList.size(); i++)
			listBC.add(categoryBList.get(i));
		for (int i = 0; i < categoryCList.size(); i++)
			listBC.add(categoryCList.get(i));
		OAFAttachedDocumentList assignAttachmentListToApprover = assignAttachmentListToApprover(listAC, listBC,
				execution);
		if (assignAttachmentListToApprover != null) {
			return assignAttachmentListToApprover;
		} else {
			return null;
		}
	}

	public static OAFAttachedDocumentList assignAttachmentListToApprover(List<JSONObject> listAC,
			List<JSONObject> listBC, DelegateExecution execution) {
		String approverName = String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR));
		OAFAttachedDocumentList list = new OAFAttachedDocumentList();
		if (approverName.equals(LEVEL1_APPROVAL1_ID)) {
			list.setLevel1GeneralSMDocument(listBC);
		} else if (approverName.equals(LEVEL1_APPROVAL2_ID)) {
			list.setLevel1SeniorGMDocument(listAC);
		} else if (approverName.equals(LEVEL2_APPROVAL1_ID)) {
			list.setLevel2CreditMDocument(listBC);
		} else if (approverName.equals(LEVEL3_APPROVAL1_ID)) {
			list.setLevel2SeniorFMDocument(listAC);
		} else if (approverName.equals(LEVEL2_APPROVAL2_ID)) {
			list.setLevel2SeniorFADocument(listBC);
		} else if (approverName.equals(LEVEL4_APPROVAL1_ID)) {
			list.setLevel3FinanceDDocument(listAC);
		} else {
			list.setLevel4VicePDocument(listAC);
		}
		return list;
	}

	/**
	 * This method is used to convert List in to JSONObject
	 * 
	 * @param list
	 * @return
	 */
	public static JSONObject converAttchmentListIntoJson(List<JSONObject> list) {
		logger.debug("OAFEmailAttachmentsUtill.converAttchmentListIntoJson()");
		JSONObject finalAttachmentJsonObject = new JSONObject();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String incre = String.valueOf(i);
				finalAttachmentJsonObject.put(incre, list.get(i));
			}
		}
		if (finalAttachmentJsonObject != null) {
			return finalAttachmentJsonObject;
		} else {
			return null;
		}
	}

	/**
	 * This method is used for getting list of attachments link by category
	 * 
	 * @param attachmentDataString contains value of json
	 * @param execution
	 * @param currentTaskName
	 * @return
	 */
	public static List<JSONObject> gettingListOfAttachmentLinkByCategory(String attachmentDataString,
			DelegateExecution execution, String currentTaskName) {
		JSONArray getAttachmentArray = null;
		List<JSONObject> categoryAList = new ArrayList<>();
		List<JSONObject> categoryBList = new ArrayList<>();
		List<JSONObject> categoryCList = new ArrayList<>();
		List<JSONObject> listAttachments = new ArrayList<>();

		JSONObject attachmentObject = null;
		if (attachmentDataString != null) {
			attachmentObject = new JSONObject(attachmentDataString);
		}
		if (attachmentObject.has(ATTACHMENTS)) {
			getAttachmentArray = attachmentObject.getJSONArray(ATTACHMENTS);
		}

		if (getAttachmentArray.length() > 0) {
			for (int i = 0; i < getAttachmentArray.length(); i++) {
				JSONObject objects = getAttachmentArray.getJSONObject(i);
				if (objects.get(CATEGORY).equals(CATEGORYA)) {
					categoryAList.add(objects);
				} else if (objects.get(CATEGORY).equals(CATEGORYB)) {
					categoryBList.add(objects);
				} else {
					categoryCList.add(objects);
				}
			}
		}

		// get the list of documents for attachment based on the category allowed for
		// the current task owner user.
		if (execution.getVariable(USER_CATEGORY) != null) {
			JSONObject userCatagory = new JSONObject(String.valueOf(execution.getVariable(USER_CATEGORY)));
			Iterator keys = userCatagory.keys();
			while (keys.hasNext()) {
				Object key = keys.next();
				if (currentTaskName.contains(key.toString())) {
					logger.debug(
							"OAFEmailAttachmentsUtill.gettingListOfAttachmentLinkByCategory() : Category available for the current task owner : "
									+ userCatagory.getString(key.toString()));
					String currentUserCatagory = userCatagory.getString(key.toString());
					String[] currentUserCatagorys = null;

					if (currentUserCatagory.contains(",")) {
						currentUserCatagorys = currentUserCatagory.split(",");

					} else {
						currentUserCatagorys = new String[1];
						currentUserCatagorys[0] = currentUserCatagory;
					}

					getListOfAttachmentsByCategory(categoryAList, categoryBList, categoryCList, listAttachments,
							currentUserCatagorys);

					break;
				}
			}
		}

		logger.debug(
				"OAFEmailAttachmentsUtill.gettingListOfAttachmentLinkByCategory() : List of documents for attachment : "
						+ listAttachments);
		if (listAttachments != null) {
			return listAttachments;
		} else {
			return null;
		}

	}

	/**
	 * Get the list of document details based on the given categories from and
	 * combine into the another list
	 * 
	 * @param categoryAList        is an object of type List of JSONObjects which
	 *                             contains all the documents of Category A
	 * @param categoryBList        is an object of type List of JSONObjects which
	 *                             contains all the documents of Category B
	 * @param categoryCList        is an object of type List of JSONObjects which
	 *                             contains all the documents of Category c
	 * @param listAttachments      is an object of type List of JSONObjects which
	 *                             contains all the documents of the current task
	 *                             onwer user's alloweded categories
	 * @param currentUserCatagorys is an object of type string array which contains
	 *                             all the category values alloweded for the current
	 *                             task owner user
	 */
	private static void getListOfAttachmentsByCategory(List<JSONObject> categoryAList, List<JSONObject> categoryBList,
			List<JSONObject> categoryCList, List<JSONObject> listAttachments, String[] currentUserCatagorys) {
		for (String category : currentUserCatagorys) {
			if (category.equals(CATEGORYA)) {
				if (categoryAList != null) {
					for (int i = 0; i < categoryAList.size(); i++)
						listAttachments.add(categoryAList.get(i));
				}
			}
			if (category.equals(CATEGORYB)) {
				if (categoryBList != null) {
					for (int i = 0; i < categoryBList.size(); i++)
						listAttachments.add(categoryBList.get(i));
				}
			}
			if (category.equals(CATEGORYC)) {
				if (categoryCList != null) {
					for (int i = 0; i < categoryCList.size(); i++)
						listAttachments.add(categoryCList.get(i));
				}
			}
		}
	}

	/**
	 * This method is used to get attachments links for specific user.
	 * 
	 * @param gettingListOfAttachmentLinkByCategory
	 * @param execution
	 * @return
	 */
	public static JSONObject getAttachmentLinkForSpecificUser(List<JSONObject> gettingListOfAttachmentLinkByCategory,
			DelegateExecution execution) {
		logger.debug("OAFEmailAttachmentsUtill.getAttachmentLinkForSpecificUser()");
		String approverName = String.valueOf(execution.getVariable(SEND_EMAIL_NOTIFICATION_FOR));
		logger.debug("OAFEmailAttachmentsUtill.assignDownloadLinkToUser() :: Current approval name ::" + approverName);
		List<JSONObject> downloadLinks = new ArrayList();
		JSONObject converAttchmentListIntoJson = new JSONObject();
		downloadLinks = gettingListOfAttachmentLinkByCategory;
		if (converAttchmentListIntoJson != null) {
			converAttchmentListIntoJson = converAttchmentListIntoJson(downloadLinks);
			return converAttchmentListIntoJson;
		} else {
			return null;
		}
	}

	/**
	 * This method is used to get Link for specific user.
	 * 
	 * @param attachmentLinkForSpecificUser
	 * @return
	 */
	public static JSONObject getLinks(JSONObject attachmentLinkForSpecificUser) {
		JSONObject downloadLinks = new JSONObject();
		JSONArray linkArray = new JSONArray();
		String url = "";
		if (attachmentLinkForSpecificUser.length() > 0) {
			for (int i = 0; i < attachmentLinkForSpecificUser.length(); i++) {
				String incre = String.valueOf(i);
				if (!attachmentLinkForSpecificUser.getJSONObject(incre).getString(DOWNLOADURL).trim().isEmpty()) {
					url = OAF_APW_EMAIL_DOWNLOAD_URL
							+ attachmentLinkForSpecificUser.getJSONObject(incre).getString(DOWNLOADURL) + "/"
							+ attachmentLinkForSpecificUser.getJSONObject(incre).getString(DOCNAME);
					linkArray.put(new JSONObject().put(DOCUMENT_DOWNLOAD_LINK, url).put(DOCNAME,
							attachmentLinkForSpecificUser.getJSONObject(incre).getString(DOCNAME)));
				} else {
					logger.debug("OAFEmailAttachmentsUtill.getLinks() :: Download link is empty for this "
							+ attachmentLinkForSpecificUser.getJSONObject(incre).getString(DOCNAME)
							+ " document, not adding in email");
				}
			}
		}
		downloadLinks.put(MAIL_TEMPLATE_CONTENT_DOWNLOAD_LINKS_JSON, linkArray);
		if (downloadLinks != null) {
			logger.debug("OAFEmailAttachmentsUtill.getLinks() :: got downloadLinks JSONObject");
			return downloadLinks;
		} else {
			return null;
		}
	}
}
