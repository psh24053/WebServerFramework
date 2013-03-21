package com.shntec.bp.common;

import java.util.HashMap;

import com.shntec.bp.util.ShntecLogger;

public class ShntecErrorCode {
	
	public final static int SHNTEC_ERROR_CODE_SUCCESS = 0;
	public final static String SHNTEC_ERROR_MESSAGE_SUCCESS = "操作执行成功。";
	
	public final static int SHNTEC_ERROR_CODE_SYSTEM_ERROR = 5000;
	public final static String SHNTEC_ERROR_MESSAGE_SYSTEM_ERROR = "系统内部未知错误。";
	
	public final static int SHNTEC_ERROR_CODE_LICENSE_INVALID = 5001;
	public final static String SHNTEC_ERROR_MESSAGE_LICENSE_INVALID = "系统License检查错误。";
	
	public final static int SHNTEC_ERROR_CODE_BAD_JSON = 5002;
	public final static String SHNTEC_ERROR_MESSAGE_BAD_JSON = "JSON消息格式错误。";
	
	public final static int SHNTEC_ERROR_CODE_NO_PERMISSION = 5003;
	public final static String SHNTEC_ERROR_MESSAGE_NO_PERMISSION = "没有操作权限。";
	
	public final static int SHNTEC_ERROR_CODE_NOT_FOUND = 5004;
	public final static String SHNTEC_ERROR_MESSAGE_NOT_FOUND = "没有找到请求的操作。";
	
	public final static int SHNTEC_ERROR_CODE_MISSING_PARAMETER = 5005;
	public final static String SHNTEC_ERROR_MESSAGE_MISSING_PARAMETER = "缺少所需的参数。";
	
	public final static int SHNTEC_ERROR_CODE_FILE_NOT_FOUND = 5006;
	public final static String SHNTEC_ERROR_MESSAGE_FILE_NOT_FOUND = "文件不存在。";
	
	public final static int SHNTEC_ERROR_CODE_INVALID_PARAMETER = 5007;
	public final static String SHNTEC_ERROR_MESSAGE_INVALID_PARAMETER = "无效的参数。";
	
	public final static int SHNTEC_ERROR_CODE_ACTION_FORBIDDEN = 5008;
	public final static String SHNTEC_ERROR_MESSAGE_ACTION_FORBIDDEN = "Action被禁用。";
	
	public final static int SHNTEC_ERROR_CODE_FILE_EXIST = 5009;
	public final static String SHNTEC_ERROR_MESSAGE_FILE_EXIST = "文件已存在。";
	
	private static HashMap<Integer, String> errorCodeMap;
	
	static  {
		
		errorCodeMap = new HashMap<Integer, String>();
	
		// Build in error code and related error message
		registerErrorCode(SHNTEC_ERROR_CODE_SUCCESS, SHNTEC_ERROR_MESSAGE_SUCCESS);
		registerErrorCode(SHNTEC_ERROR_CODE_SYSTEM_ERROR, SHNTEC_ERROR_MESSAGE_SYSTEM_ERROR);
		registerErrorCode(SHNTEC_ERROR_CODE_LICENSE_INVALID, SHNTEC_ERROR_MESSAGE_LICENSE_INVALID);
		registerErrorCode(SHNTEC_ERROR_CODE_BAD_JSON, SHNTEC_ERROR_MESSAGE_BAD_JSON);
		registerErrorCode(SHNTEC_ERROR_CODE_NO_PERMISSION, SHNTEC_ERROR_MESSAGE_NO_PERMISSION);
		registerErrorCode(SHNTEC_ERROR_CODE_NOT_FOUND, SHNTEC_ERROR_MESSAGE_NOT_FOUND);
		registerErrorCode(SHNTEC_ERROR_CODE_MISSING_PARAMETER, SHNTEC_ERROR_MESSAGE_MISSING_PARAMETER);
		registerErrorCode(SHNTEC_ERROR_CODE_FILE_NOT_FOUND, SHNTEC_ERROR_MESSAGE_FILE_NOT_FOUND);
		registerErrorCode(SHNTEC_ERROR_CODE_INVALID_PARAMETER, SHNTEC_ERROR_MESSAGE_INVALID_PARAMETER);
		registerErrorCode(SHNTEC_ERROR_CODE_ACTION_FORBIDDEN, SHNTEC_ERROR_MESSAGE_ACTION_FORBIDDEN);
		registerErrorCode(SHNTEC_ERROR_CODE_FILE_EXIST, SHNTEC_ERROR_MESSAGE_FILE_EXIST);
	}
	
	public static String getErrorMessage(int errorCode) {
		String errorMessage = null;
		try {
			errorMessage = errorCodeMap.get(errorCode).toString();
		}
		catch (Exception e) {
			ShntecLogger.logger.error("Error code: " + errorCode + " is not exist.");
			ShntecLogger.logger.error(e.getMessage());
		}
		
		return  errorMessage;
	}
	
	public static void registerErrorCode (int errorCode, String errorMessage) {
		if (errorCodeMap.containsKey(errorCode)) {
			ShntecLogger.logger.warn("Error code: " + errorCode + " has exist, ignore new error code register.");
			return;
		}
		
		errorCodeMap.put(errorCode, errorMessage);
	}
	
	public static HashMap<Integer, String> getErrorCodeMap() {

		return errorCodeMap;
		
	}
}
