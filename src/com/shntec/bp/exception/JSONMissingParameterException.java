package com.shntec.bp.exception;

import com.shntec.bp.common.ShntecErrorCode;

public class JSONMissingParameterException extends ShntecException {
	
	private String parameterName = null;
	
	public JSONMissingParameterException() {
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
	}

	public JSONMissingParameterException(String arg0) {
		super(arg0);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
	}

	public JSONMissingParameterException(Throwable arg0) {
		super(arg0);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
	}

	public JSONMissingParameterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

}
