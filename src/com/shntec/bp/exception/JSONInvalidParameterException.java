package com.shntec.bp.exception;

import com.shntec.bp.common.ShntecErrorCode;

public class JSONInvalidParameterException extends ShntecException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4854970932901135214L;
	
	private String parameterName = null;

	public JSONInvalidParameterException() {
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_INVALID_PARAMETER);
	}

	public JSONInvalidParameterException(String arg0) {
		super(arg0);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_INVALID_PARAMETER);
	}

	public JSONInvalidParameterException(Throwable arg0) {
		super(arg0);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_INVALID_PARAMETER);
	}

	public JSONInvalidParameterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		setErrorCode(ShntecErrorCode.SHNTEC_ERROR_CODE_INVALID_PARAMETER);
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

}
