package com.activiti.extension.api;

import com.activiti.extension.oaf.constants.OAFConstants;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * This class is responsible to download the document or file from ACS based on the noderef parameter
 * @author Keval Bhatt
 *
 */
@RestController
@RequestMapping("/enterprise/oaf-download-document")
public class OAFDownloadDocumentEndpoint implements OAFConstants {

	private static Logger logger = LoggerFactory.getLogger(OAFDownloadDocumentEndpoint.class);

	/**
	 * This method is responsible to get the noderef and will download or generate bytes of file
	 * @param nodeRef
	 * @param document
	 * @param httpServletResponse
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void downoadDoc(@RequestParam("nodeRef") String nodeRef, @RequestParam("document") String document,
			HttpServletResponse httpServletResponse) {

		HttpClient client = HttpClientBuilder.create().build();
		String url = ACS_BASE_URL + ACS_DOWNLOAD_CONTENT_URL + nodeRef + "?a=true";
		logger.debug("OAFDownloadDocumentEndpoint.downoadDoc() :: Download ACS url " + url);

		HttpGet request = new HttpGet(url);
		// Create Authentication token of ACS username and ACS Password 
		String encoding = Base64.getEncoder().encodeToString((ACS_USERNAME + ":" + ACS_PASSWORD).getBytes());
		request.addHeader(AUTHORIZATION, BASIC + encoding);
		HttpResponse response = null;
		try {
			// Send the request to ACS and get the response
			response = client.execute(request);
			logger.debug("OAFDownloadDocumentEndpoint.downoadDoc() :: ACS response " + response);
		} catch (IOException e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: IOException " + e);
		}
		catch(Exception e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: Exception " + e);
		}
		InputStream inputStream = null;
		try {
			inputStream = response.getEntity().getContent();
			logger.debug("OAFDownloadDocumentEndpoint.downoadDoc() :: inputStream is in response " + response);
		} catch (UnsupportedOperationException | IOException e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: UnsupportedOperationException or IOException " + e.getMessage());
		}
		catch(Exception e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: Exception " + e);
		}
		String contentType = response.getEntity().getContentType().getValue();
		long length = response.getEntity().getContentLength();
		httpServletResponse.setContentType(contentType);
		httpServletResponse.setContentLength((int) length);
		String extension = MimeTypes.getExtension(contentType);
		document = document + extension;
		httpServletResponse.setHeader(ACS_CONTENT_DISPOSITION, ACS_ATTCHMENT + ACS_FILE_NAME + document);
		try {
			IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
		} catch (IOException e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: IOException " + e);
		}
		catch(Exception e) {
			logger.error("OAFDownloadDocumentEndpoint.downoadDoc() :: Exception " + e);
		}
		finally {
			if (inputStream != null) {
				IOUtils.closeQuietly(inputStream);
			}
			logger.debug("OAFDownloadDocumentEndpoint.downoadDoc() :: Content downloaded ");
		}
	}
}
