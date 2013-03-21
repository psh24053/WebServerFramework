package com.shntec.bp.common;

public abstract class ShntecBaseAction {

	protected int actionCode = JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION;
	
	protected String actionName = null;
	
	protected String actionDescription = null;
	
	protected boolean needAuthenticationCheck = false;
	
	public abstract ResponseMessageGenerator handleAction( RequestMessageParser parser );

	public int getActionCode() {
		
		return actionCode;
		
	}
	
	public String getActionName() {
		
		return actionName;
		
	}
	
	public String getActionDescription () {
		
		return actionDescription;
		
	}
	
}
