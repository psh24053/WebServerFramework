package com.shntec.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecActionHandler;
import com.shntec.bp.exception.ShntecException;
import com.shntec.bp.impl.ShntecBpEntry;
import com.shntec.bp.util.CommonUtil;
import com.shntec.bp.util.DatabaseManager;
import com.shntec.bp.util.ShntecConfigManager;
import com.shntec.bp.util.ShntecLogger;

/**
 * Servlet implementation class Main
 */
public class Main extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		ShntecLogger.logger.debug("Enter Main servlet initialization process ...");
		
		// Read properties and initialize.
		ShntecConfigManager.getInstance();
		
		// Initialize database manager
		DatabaseManager.getInstance();

		// Initialize action handler
		ShntecActionHandler.getInstance();
		
		// Initialize file storage hierarchy
		//FileStorageManager.getInstance().fileStorageInitialize();
		
		// Initialize Job Scheduler

		ShntecLogger.logger.debug("Leave Main servlet initialization process ...");	

	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {

		ShntecLogger.logger.debug("Enter Main servlet destory process ...");

		DatabaseManager.getInstance().closeDS();

		ShntecLogger.logger.debug("Leave Main servlet destory process ...");
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ShntecLogger.logger.debug("Enter Main servlet doGet() ...");
		
		doPost(request, response);
		
		ShntecLogger.logger.debug("Leave Main servlet doGet() ...");
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ShntecLogger.logger.debug("Enter Main servlet doPost() ...");

		// Read complete request content
		String requestMessage = CommonUtil.readUtf8RequestContent(request);
		if (null == requestMessage) {
			ShntecLogger.logger.error("No request message is received.");
			response.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"No request message is received.");
			return;
		}
		
		ResponseMessageGenerator responseGenerator = null;
		int responseStatus = HttpServletResponse.SC_OK;

		ShntecLogger.logger.info("Received request message: " 
				+ requestMessage + ", message length=" + requestMessage.length()
				+ ", received content length in byte: " + request.getContentLength());

		ShntecBpEntry mainEntry = new ShntecBpEntry();
		
		// Filter actions, default allow and forbid POS actions
		mainEntry.setDefaultAllow(true);
		
		try {
			responseGenerator = mainEntry.processRequest(requestMessage, request);
		}
		catch (ShntecException e) {
			ShntecLogger.logger.error(e.getMessage());
			response.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getErrorMessage());
			return;
		}
		
		// Send back response after finishing action handle
		response.setStatus(responseStatus);
		HashMap<String, String> httpResponseHeader = responseGenerator.getHttpResponseHeader();
		if ( httpResponseHeader != null) {
			Iterator<Entry<String, String>> iter = httpResponseHeader.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, String> mapEntry = iter.next(); 
				response.addHeader(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		if (responseGenerator.getResponseType() == ResponseMessageGenerator.RESPONSE_TYPE_JSON) {
			ShntecLogger.logger.info("Response message to be sent:" + responseGenerator.generate());
			CommonUtil.writeUtf8ResponseContent(response, responseGenerator.generate());
		}
		else if (responseGenerator.getResponseType() == ResponseMessageGenerator.RESPONSE_INPUT_STREAM){
			InputStream is = responseGenerator.getResponseInputStream();
			OutputStream os =  response.getOutputStream();
			
			if (is == null) {
				ShntecLogger.logger.error("Get input stream from response genrator failed");
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Get input stream from response genrator failed");
				return;
			}
			// read and write
			int ch = 0;

			while( -1 != ( ch = is.read()) ){
				os.write(ch);
			} 
			
			is.close();
			os.close();
		}
		
		ShntecLogger.logger.debug("Leave Main servlet doPost() ...");
	
	}

}
