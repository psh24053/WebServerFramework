/**
 * 
 */
package com.shntec.bp.exception;

import com.shntec.bp.common.ShntecErrorCode;

/**
 * @author 1
 *
 */
public class ShntecException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int errorCode = ShntecErrorCode.SHNTEC_ERROR_CODE_SUCCESS;
	
	private String moreInformation = null;

	/**
	 * 
	 */
	public ShntecException() {
		// TODO Auto-generated constructor stub
	}
	
	public ShntecException(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @param arg0
	 */
	public ShntecException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public ShntecException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ShntecException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setErrorCode (int errorCode) {
		this.errorCode = errorCode;
	}
	public int getErrorCode () {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return ShntecErrorCode.getErrorMessage(errorCode);
	}
	
	public void setMoreInformation(String moreInformation) {
		this.moreInformation = moreInformation;
	}
	
	public String getMoreInformation() {
		return moreInformation;
	}

}
