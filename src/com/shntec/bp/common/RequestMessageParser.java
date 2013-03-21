package com.shntec.bp.common;

import javax.servlet.http.HttpServletRequest;

import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;

import com.shntec.bp.util.ShntecLogger;

public class RequestMessageParser {

	// Success
	final static public int ERROR_SUCCESS = 0;
	// JSON message format is not correct
	final static public int ERROR_INVALID_FORMAT = 1;
	// JSON message can not be parsed as defined object
	final static public int ERROR_INVALID_OBJECT = 2;

	//
	private int actionCode = JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION;
	//
	private String actionAuthentication = null;
	
	private int errorCode = ERROR_SUCCESS;

	// Request JSON object
	private JSONObject requestJsonObject = null;
	
	private JSONObject headerJsonObject = null;
	
	// Parameter JSON object
	private JSONObject parameterJsonObject = null;
	
	// Original JSON message text
	private String requestMessage = null;
	
	// Original request object
	private HttpServletRequest request = null;
	
	public RequestMessageParser () {
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public int getActionCode() {
		return actionCode;
	}
	
	public String getActionAuthentication () {
		return actionAuthentication;
	}
	
	public JSONObject getParameterJsonObject() {
		return parameterJsonObject;
	}

	public JSONObject getRequestJsonObject() {
		return requestJsonObject;
	}
	
	public String getRequestMessage ( ) {
		
		return requestMessage;
	
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean parse(String RequestMessage) {
	
		boolean isSuccess = false;

		try {
			requestJsonObject = new JSONObject(RequestMessage); 
		}
		catch(JSONException ex) {
			ShntecLogger.logger.error("Parse input JSON message failed.");
			ShntecLogger.logger.error(ex.toString());
			errorCode = ERROR_INVALID_FORMAT;
			return isSuccess;
		}

		// Parse fixed element (header)
		try {
			headerJsonObject = requestJsonObject.getJSONObject(JSONMessageConstant.JSON_KEY_ACTION_HEADER);
		} catch (JSONException ex) {
			ShntecLogger.logger.error("Parse action header failed.");
			ShntecLogger.logger.error(ex.toString());
			errorCode = ERROR_INVALID_OBJECT;
			return isSuccess;
		}

		// Parse action code
		try {
			actionCode = headerJsonObject.getInt(JSONMessageConstant.JSON_KEY_ACTION_CODE);
		}
		catch (JSONException ex) {
			ShntecLogger.logger.error("Parse action code element failed.");
			ShntecLogger.logger.error(ex.toString());
			errorCode = ERROR_INVALID_OBJECT;
			return isSuccess;
		}

		// Parse action authentication ( optional )
		try {
			actionAuthentication = headerJsonObject.getString(JSONMessageConstant.JSON_KEY_AUTHENTICATION);
		}
		catch(JSONException ex) {
			// If action authentication missed, set it to null;
			actionAuthentication = null;
		}

		// Parse request parameter ( optional )		
		try {
			parameterJsonObject = requestJsonObject.getJSONObject(JSONMessageConstant.JSON_KEY_PARAMETER);
		}
		catch (JSONException ex) {
			// If action has no parameter, set it to null;
			parameterJsonObject = null;
		}
		
		// All parse is successful
		this.requestMessage = RequestMessage;
		
		isSuccess = true;
		
		return isSuccess;		
	}
	
}
