package com.activiti.extension.oaf.freemarker.task.beans;

/**
 * Bean for adding entire document as an attachment in email
 * @author Avani Purohit
 *
 */
public class OAFAttachedDocumentIdentification {

	String nodeRef;
	String fileName;
	public String getNodeRef() {
		return nodeRef;
	}
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "AttachedDocumentIdentification [nodeRef=" + nodeRef + ", fileName=" + fileName + "]";
	}
	
	
}
