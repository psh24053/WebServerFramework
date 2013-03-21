package com.shntec.bp.impl;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import com.shntec.bp.common.NormalRequestDispatcher;
import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.exception.ShntecException;

import com.shntec.bp.util.ShntecLogger;

public class ShntecBpEntry {

	private boolean isDefaultAllow = true;

	private HashSet<Integer> allowedActions = null;
	private HashSet<Integer> forbiddenActions = null;

	public ShntecBpEntry() {
		allowedActions = new HashSet<Integer>();
		forbiddenActions = new HashSet<Integer>();
	}
	
	public void setDefaultAllow(boolean isDefaultAllow) {
		this.isDefaultAllow = isDefaultAllow;
	}
	
	public void addAllowedAction(int actionCode) {
		if (!allowedActions.contains(actionCode)) {
			allowedActions.add(actionCode);
		}
	}
	
	public void addForbiddenAction(int actionCode) {
		if (!forbiddenActions.contains(actionCode)) {
			forbiddenActions.add(actionCode);
		}
	}
	
	private boolean checkActionPermission(int actionCode){
		
		boolean isAllowed = isDefaultAllow;
		
		// Check allow list
		if (allowedActions.contains(actionCode)) {
			ShntecLogger.logger.debug("Action is allowed by allow filter: " + actionCode);
			return true;
		}
		
		// Check forbidden list
		if (forbiddenActions.contains(actionCode)) {
			ShntecLogger.logger.debug("Action is forbidden by forbidden filter: " + actionCode);
			return false;
		}
		
		if (isAllowed) {
			ShntecLogger.logger.debug("Action is allowed by default: " + actionCode);
		}
		else {
			ShntecLogger.logger.debug("Action is forbidden by default: " + actionCode);
		}
		
		return isAllowed;
	}
	
	// Based on JSON message
	public ResponseMessageGenerator processRequest(String jsonRequest, HttpServletRequest request) throws ShntecException {
		
		ShntecLogger.logger.debug("Enter ShntecBpEntry processRequest() ... ");
		
		// Parse received request message, check JSON message format  
		RequestMessageParser parser = new RequestMessageParser();
		
		if (!parser.parse(jsonRequest)) {
			throw new ShntecException(ShntecErrorCode.SHNTEC_ERROR_CODE_BAD_JSON);
		}
		
		parser.setRequest(request);
		
		// Check action filters
		if (!checkActionPermission(parser.getActionCode())) {
			throw new ShntecException(ShntecErrorCode.SHNTEC_ERROR_CODE_ACTION_FORBIDDEN);
		}
		
		NormalRequestDispatcher  dispatcher = null;
		ResponseMessageGenerator generator = null;
		
		dispatcher = new NormalRequestDispatcher();
		generator = dispatcher.dispatcherHandler(parser);
		
		if (null == generator) {
			ShntecLogger.logger.error("JSON request message process failed.");
			throw new ShntecException(ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR);
		}
		
		
		//jsonResponse = generator.generate();
		
		ShntecLogger.logger.debug("Leave ShntecBpEntry processRequest() ... ");

		return generator;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String requestMessage = "{cod: 1000, prm: { a: \"1\", b: \"2\"} }";
		String responseMessage = null;
		
		ShntecBpEntry entry = new ShntecBpEntry();
		
		try {
			responseMessage = entry.processRequest(requestMessage, null).generate();
		} catch (ShntecException e) {
			responseMessage = e.getErrorMessage();
		}
		
		ShntecLogger.logger.debug(responseMessage);

	}

}
