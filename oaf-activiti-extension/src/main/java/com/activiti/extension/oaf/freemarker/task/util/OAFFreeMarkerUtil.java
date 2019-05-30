package com.activiti.extension.oaf.freemarker.task.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.activiti.extension.oaf.constants.OAFConstants;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmail;
import com.activiti.extension.oaf.freemarker.task.beans.OAFEmailTemplateContent;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

/**
 * This class is responsible to send an email
 * @author Keval Bhatt
 */
public class OAFFreeMarkerUtil implements OAFConstants {
	@Autowired
	private static Environment environment;

	private static Logger logger = LoggerFactory.getLogger(OAFFreeMarkerUtil.class);

	/*
	 * =================== START - Methods For Process Template Content - START
	 * ==============================
	 */

	/**
	 * To get string writer content of processed template data
	 *
	 * @param template     configured freemarker template object
	 * @param templateData dynamic data to be filled in freemarker template
	 * @return processed freemarker template data
	 * @throws TemplateException Error if not found template
	 * @throws IOException       Error if Signals that an I/O exception of some sort
	 *                           has occurred
	 */
	public static String getTemplateProcessedContent(Template template, Map<String, Object> templateData)
			throws TemplateException, IOException {
		// write the freemarker output to a StringWriter
		StringWriter stringWriter = new StringWriter();
		template.setOutputEncoding(KEY_MAIL_UTF_8);
		template.process(templateData, stringWriter);

		// get the String from the StringWriter
		return stringWriter.toString();
	}
	/*
	 * =================== END - Methods For Process Template Content - END
	 * ==============================
	 */

	/**
	 * To get template name based on configured template name
	 *
	 * @param TemplateName .ftl file name for template
	 * @return content of freemarker template
	 */
	public static Template getFreemarkerTemplateContent(String templateName) {
		Template template = null;
		try {
			template = freemarkerConfiguration().getTemplate(templateName);
		} catch (TemplateNotFoundException e) {
			logger.error("OAFFreeMarkerUtil.getFreemarkerTemplate() -> Error if not found template : ", e.getMessage());
		} catch (MalformedTemplateNameException e) {
			logger.error(
					"OAFFreeMarkerUtil.getFreemarkerTemplate() -> Error if template name given was not malformed : ",
					e.getMessage());
		} catch (ParseException e) {
			logger.error(
					"OAFFreeMarkerUtil.getFreemarkerTemplate() :: Error if signals syntactical/lexical errors in template : ",
					e.getMessage());
		} catch (IOException e) {
			logger.error(
					"OAFFreeMarkerUtil.getFreemarkerTemplate() :: Error if Signals that an I/O exception of some sort has occurred : ",
					e.getMessage());
		}

		return template;
	}

	/**
	 * To configure freeMarker Template with version and template loading path
	 *
	 * @return freemarker configuration
	 */
	public static Configuration freemarkerConfiguration() {
		Configuration templateConfiguration = new Configuration(Configuration.VERSION_2_3_23);

		templateConfiguration.setDefaultEncoding(KEY_MAIL_UTF_8);
		templateConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		templateConfiguration.setLogTemplateExceptions(false);
		templateConfiguration.setClassForTemplateLoading(OAFFreeMarkerUtil.class, KEY_MAIL_RESOURCE_PATH);
		return templateConfiguration;
	}

	/**
	 * Responsible to dynamic data to template
	 *
	 * @param execution
	 * @return
	 */
	public static Map<String, Object> commonDynamicDataForQuote(DelegateExecution execution,
			OAFEmailTemplateContent emailTemplateContent) {
		Map<String, Object> templateData = new HashMap<String, Object>();

		templateData.put(MAIL_TEMPLATE_CONTENT_INQUIRY, emailTemplateContent.getInquiry());
		templateData.put(MAIL_TEMPLATE_CONTENT_QUOTE, emailTemplateContent.getQuote());
		templateData.put(MAIL_TEMPLATE_CONTENT_SALEOFFICE, emailTemplateContent.getSalesOffice());
		templateData.put(MAIL_TEMPLATE_CONTENT_PROJECT, emailTemplateContent.getProject());
		templateData.put(MAIL_TEMPLATE_CONTENT_CLIENT, emailTemplateContent.getClient());
		templateData.put(MAIL_TEMPLATE_CONTENT_LOCATION, emailTemplateContent.getLocation());
		templateData.put(MAIL_TEMPLATE_CONTENT_WEIGHT, emailTemplateContent.getWeightMt());
		templateData.put(MAIL_TEMPLATE_CONTENT_SALESCOMMENTS, emailTemplateContent.getSalesComments());
		templateData.put(MAIL_TEMPLATE_CONTENT_REVISION, emailTemplateContent.getRevision());
		templateData.put(MAIL_TEMPLATE_CONTENT_DQ, emailTemplateContent.getDq());

		templateData.put(MAIL_TEMPLATE_CONTENT_APPROVALINFO, emailTemplateContent.getApprovalInformaiton());
		templateData.put(MAIL_TEMPLATE_CONTENT_APPROVALPENDINGINFO,
				emailTemplateContent.getApprovalPendingInformaiton());

		if (emailTemplateContent.isApprovalEmail()) {
			templateData.put(MAIL_TEMPLATE_CONTENT_ISAPPROVALEMAIL, emailTemplateContent.isApprovalEmail());
			templateData.put(MAIL_TEMPLATE_CONTENT_APPROVETASK_URL, emailTemplateContent.getApproveTaskUrl());
			templateData.put(MAIL_TEMPLATE_CONTENT_REJECTTASK_URL, emailTemplateContent.getRejectTaskUrl());
			templateData.put(MAIL_TEMPLATE_CONTENT_VIEWTASK_URL, emailTemplateContent.getViewTaskUrl());
			templateData.put(MAIL_TEMPLATE_CONTENT_DOWNLOAD_LINKS, emailTemplateContent.getDownloadLinks());

		}
		if(emailTemplateContent.isSapFailEmail()) {
			templateData.put(MAIL_TEMPLATE_CONTENT_ISSAPFAILEMAIL, emailTemplateContent.isSapFailEmail());
			templateData.put(MAIL_TEMPLATE_CONTENT_SAPFAIL_REASON, emailTemplateContent.getSapFailReason());
			templateData.put(MAIL_TEMPLATE_CONTENT_RETRY_SAP_URL, emailTemplateContent.getRetrySapFailUrl());
		}
		if (emailTemplateContent.getRejectedByUserName() != null
				&& !emailTemplateContent.getRejectedByUserName().isEmpty()) {
			templateData.put(MAIL_TEMPLATE_CONTENT_REJECTEDBY, emailTemplateContent.getRejectedByUserName());
		}
		if(emailTemplateContent.isUserDecisionTableFailed()) {
			templateData.put(MAIL_TEMPLATE_CONTENT_VKBUR, emailTemplateContent.getVkbur());
			templateData.put(MAIL_TEMPLATE_CONTENT_REGION, emailTemplateContent.getRegionFlag());
			templateData.put(MAIL_TEMPLATE_CONTENT_MARGIN_CPP, emailTemplateContent.getMarginCPP());
			templateData.put(MAIL_TEMPLATE_CONTENT_ENG_SERVICES, emailTemplateContent.getEngineeringServices());
			templateData.put(MAIL_TEMPLATE_CONTENT_DECISION_TABLE_OUTCOME, "Fail");
		}
		
		templateData.put(MAIL_TEMPLATE_CONTENT_DAYSELAPSED, emailTemplateContent.getDaysElapsedFromSubmission());
		if(emailTemplateContent.isPdfDownloadEnable()) {
			templateData.put(MAIL_TEMPLATE_CONTENT_ISPDF_DOWNLOAD_ENABLE, emailTemplateContent.isPdfDownloadEnable());
			templateData.put(MAIL_TEMPLATE_CONTENT_PDF_DOWNLOAD_LINK, emailTemplateContent.getPdfDownloadLink());
			templateData.put(MAIL_TEMPLATE_CONTENT_PDF_NAME, emailTemplateContent.getPdfFileName());
		}
		return templateData;

	}

	public static void sendNotification(OAFEmail email) {

		logger.debug("OAFFreeMarkerUtil.sendNotification() :: Setting email body content ");

		Properties property = getSMTPEmailProperty();
		Session session = Session.getDefaultInstance(property);

		String from = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_FROM);

		InternetAddress fromAddress = getFromAddress(from);

		/* Multiple Users */
		List<String> toAddresses = new ArrayList<String>(Arrays.asList(email.getTo().split(",")));
		InternetAddress[] getTosAddresses = geListOfEmailAddresses(toAddresses);

		InternetAddress[] getCssAddresses = null;
		if (email.getCc() != null) {
			List<String> ccAddresses = new ArrayList<String>(Arrays.asList(email.getCc().split(",")));
			getCssAddresses = geListOfEmailAddresses(ccAddresses);
		}

		sendAnEmail(getTosAddresses, fromAddress, getCssAddresses, email, session);
		logger.debug("OAFFreeMarkerUtil.sendNotification() :: Email successfully sent to : " + email.getTo());
	}

	public static void sendNotificationFromService(OAFEmail email) {

		logger.debug("OAFFreeMarkerUtil.sendNotification() :: Setting email body content ");

		Properties property = getCustomSMTPEmailProperty();
		Session session = Session.getDefaultInstance(property);

		String from = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_FROM);

		InternetAddress fromAddress = getFromAddress(from);

		/* Multiple Users */
		List<String> toAddresses = new ArrayList<String>(Arrays.asList(email.getTo().split(",")));
		InternetAddress[] getTosAddresses = geListOfEmailAddresses(toAddresses);

		InternetAddress[] getCssAddresses = null;
		if (email.getCc() != null) {
			List<String> ccAddresses = new ArrayList<String>(Arrays.asList(email.getCc().split(",")));
			getCssAddresses = geListOfEmailAddresses(ccAddresses);
		}

		if (sendAnEmail(getTosAddresses, fromAddress, getCssAddresses, email, session)) {
			logger.debug("OAFFreeMarkerUtil.sendNotification() :: Email successfully sent To : " + email.getTo());
		} else {
			logger.debug("OAFFreeMarkerUtil.sendNotification() :: Email failed to sent To : " + email.getTo()
					+ " : please check the log ");
		}

	}

	private static InternetAddress getFromAddress(String from) {
		InternetAddress fromAddress = null;
		try {
			fromAddress = new InternetAddress(from);
		} catch (AddressException e) {
			logger.error("OAFFreeMarkerUtil.getFromAddress() :: Error while setting FROM address in InternetAddress ",
					e.getMessage());
		}
		return fromAddress;
	}

	private static InternetAddress[] geListOfEmailAddresses(List<String> emailAddresses) {
		InternetAddress[] internetAddress = new InternetAddress[emailAddresses.size()];
		int index = 0;
		for (String emailAddress : emailAddresses) {
			InternetAddress internetAddressTemp = new InternetAddress();
			internetAddressTemp.setAddress(emailAddress);
			internetAddress[index] = internetAddressTemp;
			index++;
		}
		return internetAddress;
	}

	private static Properties getSMTPEmailProperty() {
		// Creates a Session with the following properties.
		Properties property = new Properties();

		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_HOST),
				processEngineConfiguration().getMailServerHost());
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_PROTOCOL),
				OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_SMTP));
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_START_TTLS),
				processEngineConfiguration().getMailServerUseTLS());
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_PORT),
				processEngineConfiguration().getMailServerPort());
		return property;
	}

	private static Properties getCustomSMTPEmailProperty() {
		// Creates a Session with the following properties.
		Properties property = new Properties();

		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_HOST), "localhost");
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_PROTOCOL),
				OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_SMTP));
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_START_TTLS), true);
		property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_PORT), 25);
//        property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_HOST),processEngineConfiguration().getMailServerHost());
//        property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_START_TTLS),processEngineConfiguration().getMailServerUseTLS());
//        property.put(OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_MAIL_PORT),processEngineConfiguration().getMailServerPort());
		return property;
	}

	private static boolean sendAnEmail(InternetAddress[] toAddresses, InternetAddress fromAddress,
			InternetAddress[] ccAddresses, OAFEmail email, Session session) {
		// Create an Internet mail msg.
		MimeMessage message = new MimeMessage(session);

		// set an email header
		serHeader(message);

		// set an email from address
		setFrom(fromAddress, message);

		// set an email to address
		setTo(toAddresses, message);

		// set an email cc address if not null
		if (ccAddresses != null) {
			setCc(ccAddresses, message);
		}

		// set an email subject
		setSubject(email.getSubject(), message);

		// set an sent date
		setSentDate(message);

		// Set an email msg text/html
		MimeBodyPart messagePart = getEmailBody(email.getTemplateProcessedContent());

		// Set an email attachment file
		List<MimeBodyPart> attachmentPart = null;
		if (email.getAttachedDocumetIdentityList() != null) {
			attachmentPart = getEmailAttachment(message, email.getAttachedDocumetIdentityList());
		}
		if (email.getAttachedSingleFile() != null) {
			attachmentPart = new ArrayList<>();
			attachmentPart.add(email.getAttachedSingleFile());
		}

		// Create Multipart E-Mail
		Multipart multipart = createMimeMultipartContent(messagePart, attachmentPart);

		// Setting email body and sending Transport email
		if (setContent(message, multipart) && sendTransportMail(message)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * To set an email header
	 *
	 * @param message MIME style email message. It implements the Message abstract
	 *                class and the MimePart interface
	 */
	private static void serHeader(MimeMessage message) {
		try {
			message.addHeader(KEY_MAIL_CONTENT_TYPE, KEY_MAIL_CONTENT_VALUE);
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.serHeader() :: Error while setting HEADER in an email : " + e.getMessage(),
					e.toString());
		}
	}

	/**
	 * To set an email from address
	 *
	 * @param fromAddress string email address
	 * @param message     MIME style email message. It implements the Message
	 *                    abstract class and the MimePart interface
	 */
	private static void setFrom(InternetAddress fromAddress, MimeMessage message) {
		try {
			message.setFrom(fromAddress);
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setFrom() :: Error while setting FROM in an email", e.getMessage());
		}
	}

	/* ------------ Multiple Users ------------ */

	/**
	 * To set an Internet email CC address using the syntax of RFC822
	 *
	 * @param ccAddresses string email address of cc
	 * @param message     MIME style email message. It implements the Message
	 *                    abstract class and the MimePart interface
	 */
	private static void setCc(InternetAddress[] ccAddresses, MimeMessage message) {
		try {
			message.setRecipients(Message.RecipientType.CC, ccAddresses);
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setCss() :: Error while setting CC in an email", e.getMessage());
		}
	}

	/**
	 * To set an Internet email TO address using the syntax of RFC822
	 *
	 * @param toAddress string email address of to
	 * @param message   MIME style email message. It implements the Message abstract
	 *                  class and the MimePart interface
	 */
	private static void setTo(InternetAddress[] toAddress, MimeMessage message) {
		try {
			message.addRecipients(Message.RecipientType.TO, toAddress);
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setTo() :: Error while setting TO in an email", e.getMessage());
		}
	}
	/* ------------ Multiple Users ------------ */

	/**
	 * To set an email sent date
	 *
	 * @param message MIME style email message. It implements the Message abstract
	 *                class and the MimePart interface
	 */
	private static void setSentDate(MimeMessage message) {
		try {
			message.setSentDate(new Date());
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setSubject() :: Error while setting SENT DATE in an email", e.getMessage());
		}
	}

	/**
	 * To set an email subject
	 *
	 * @param subject string content for subject
	 * @param message MIME style email message. It implements the Message abstract
	 *                class and the MimePart interface
	 */
	private static void setSubject(String subject, MimeMessage message) {
		try {
			message.setSubject(subject);
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setSubject() :: Error while setting SUBJECT in an email", e.getMessage());
		}
	}

	/**
	 * To get an email body text/html which added into MimeMultipart
	 *
	 * @param templateProcessedContent text/html template processed data from .ftl
	 *                                 file
	 * @return MimeBodyPart object that contain an email body
	 */
	private static MimeBodyPart getEmailBody(String templateProcessedContent) {
		// Set the email msg text.
		MimeBodyPart messagePart = new MimeBodyPart();
		try {
			messagePart.setContent(templateProcessedContent, KEY_MAIL_CONTENT_VALUE);
		} catch (MessagingException e) {
			logger.error(
					"OAFFreeMarkerUtil.getEmailBody() :: Error while setting template data(text/html) in an email body : ",
					e);
		}
		return messagePart != null ? messagePart : null;
	}

	/**
	 * To get an email attachment file and it's name which set into file datasource
	 * That file datasource added into MimeMultipart
	 *
	 * @param message                      attachment path
	 * @param generatedClaimCardPdfNodeRef
	 * @param pdfname
	 * @return MimeBodyPart object that contain an email attachment
	 */
	private static List<MimeBodyPart> getEmailAttachment(MimeMessage message, JSONObject attachmentJsonObject) {
		String[] noderef;
		String nodeRef[] = null;
		String acsUrlForGetContent;
		String fileName;
		List<MimeBodyPart> attachmentList = new ArrayList<>();
		for (int i = 0; i < attachmentJsonObject.length(); i++) {
			String incre = String.valueOf(i);
			fileName = attachmentJsonObject.getJSONObject(incre).getString(DOCNAME);
			noderef = attachmentJsonObject.getJSONObject(incre).getString(NODEREF).split("//");
			nodeRef = noderef[1].split("/");
			MimeBodyPart attachmentPart = new MimeBodyPart();
			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				byte[] b;
				acsUrlForGetContent = OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_BASE_URL)
						+ OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_NODE_URL) + nodeRef[1]
						+ OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_CONTENT_URL);
				logger.debug("acsUrlForGetContent : " + acsUrlForGetContent);
				HttpGet request = new HttpGet(acsUrlForGetContent);
				// do authentication of ACS
				String encoding = Base64.getEncoder()
						.encodeToString((OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_USERNAME) + ":"
								+ OAF_RESOURCE_BUNDLE_INSTANCE.getString(KEY_ACS_PASSWORD)).getBytes());

				logger.debug("encoding : " + encoding);
				request.setHeader(AUTHORIZATION, BASIC + encoding);

				HttpResponse response = httpclient.execute(request);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStr = response.getEntity().getContent();
					// generating bytearray of InputStream
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					String headerContentType = response.getEntity().getContentType().getValue();
					int reads = inputStr.read();
					while (reads != -1) {
						baos.write(reads);
						reads = inputStr.read();
					}
					b = baos.toByteArray();
					ByteArrayDataSource dsn = new ByteArrayDataSource(b, headerContentType);
					attachmentPart.setDataHandler(new DataHandler(dsn));
					attachmentPart.setFileName(fileName);
					attachmentList.add(attachmentPart);

				}
			} catch (MessagingException | IOException e) {
				logger.error(
						"OAFFreeMarkerUtil.getEmailAttachment() -> Error while setting file datasource content in an email attachment : ",
						e.getMessage());
			}
		}

		return attachmentList != null ? attachmentList : null;
	}

	/**
	 * To set an email content which text/html and attachment
	 *
	 * @param message   MIME style email message. It implements the Message abstract
	 *                  class and the MimePart interface
	 * @param multipart Multipart provides methods to retrieve and set its subparts.
	 */
	private static boolean setContent(MimeMessage message, Multipart multipart) {
		try {
			// testing
			message.setContent(multipart, KEY_MAIL_CONTENT_VALUE);
			return true;
		} catch (MessagingException e) {
			logger.error("OAFFreeMarkerUtil.setContent() :: Error while setting SET CONTENT in an email", e);
			return false;
		}
	}

	/**
	 * To send transport email that contain MIME message with credential
	 *
	 * @param message MIME style email message. It implements the Message abstract
	 *                class and the MimePart interface
	 */
	private static boolean sendTransportMail(MimeMessage message) {
		try {
			Transport.send(message, processEngineConfiguration().getMailServerUsername(),
					processEngineConfiguration().getMailServerPassword());
			return true;
		} catch (MessagingException e) {
			logger.error(
					"OAFFreeMarkerUtil.sentTransportMail() :: Error while setting SENT TRANSPORT MAIL in an email: "
							+ e.getMessage(),
					e);
			return false;
		}
	}

	/**
	 * To create Multipart E-Mail that add an email body(text/html) and attachment
	 *
	 * @param messagePart        an email body(text/html)
	 * @param attachmentPartList an email attachment(File Data Source)
	 * @return MimeMultipart object that contain an email body and attachment
	 */
	private static Multipart createMimeMultipartContent(MimeBodyPart messagePart,
			List<MimeBodyPart> attachmentPartList) {
		// Create Multipart E-Mail.
		Multipart multipart = new MimeMultipart();
		if (messagePart != null) {
			try {
				multipart.addBodyPart(messagePart);

			} catch (MessagingException e) {
				logger.error(
						"OAFFreeMarkerUtil.createMimeMultipartContent() :: Error while adding an email body(text/html) in multipart : ",
						e.getMessage());
			}
		}
		if (attachmentPartList != null) {
			try {
				for (MimeBodyPart attachment : attachmentPartList) {
					multipart.addBodyPart(attachment);
				}
			} catch (MessagingException e) {
				logger.error(
						"OAFFreeMarkerUtil.createMimeMultipartContent() :: Error while adding an email attachment(File Data Source) in multipart : ",
						e.getMessage());
			}
		}

		return multipart != null ? multipart : null;
	}

	/**
	 * ProcessEngineConfigurationImpl that helps to read app-properties related to
	 * an email
	 *
	 * @return processEngineConfiguration object to read property form
	 *         activiti-app.properties
	 */
	private static ProcessEngineConfigurationImpl processEngineConfiguration() {
		ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();
		return processEngineConfiguration != null ? processEngineConfiguration : null;
	}

}
