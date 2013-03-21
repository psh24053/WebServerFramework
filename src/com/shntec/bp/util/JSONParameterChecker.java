package com.shntec.bp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.shntec.bp.exception.JSONInvalidParameterException;
import com.shntec.bp.exception.JSONMissingParameterException;
import com.shntec.bp.exception.ShntecException;
import com.shntec.bp.json.JSONArray;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;

public class JSONParameterChecker {

	JSONObject parameters = null;

	public JSONParameterChecker(JSONObject parameters) {

		this.parameters = parameters;

	}

	private boolean isParameterExist(String name, boolean required)
			throws JSONMissingParameterException {

		boolean isExist = true;

		// 检查JSON对象是否为空
		if (null == parameters) {
			isExist = false;
		} else {
			// 检查JSON对象是否包含指定名称的参数
			try {
				parameters.get(name);
			} catch (JSONException e) {
				ShntecLogger.logger.error(e.getMessage());
				isExist = false;
			}
		}

		// 参数不存在时做相应的异常处理
		if (!isExist) {
			if (required) {
				ShntecLogger.logger.error("Missing required parameter: \""
						+ name + "\"");
				// 仅当参数为必选时抛出异常
				JSONMissingParameterException ex = new JSONMissingParameterException(
						name);
				ex.setMoreInformation("缺少所需的参数: \"" + name + "\"");
				throw ex;
			} else {
				ShntecLogger.logger.info("Missing optional parameter: \""
						+ name + "\"");
			}
		}

		return isExist;
	}

	private Boolean getBooleanParameter(String name)
			throws JSONInvalidParameterException {

		Boolean ret = null;

		try {
			ret = parameters.getBoolean(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is Boolean.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为布尔类型(Boolean).");
			throw ex;
		}

		return ret;
	}

	private Boolean checkBooleanParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getBooleanParameter(name);
	}

	private Integer getIntegerParameter(String name)
			throws JSONInvalidParameterException {

		Integer ret = null;

		try {
			ret = parameters.getInt(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is Integer.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为整型(Integer).");
			throw ex;
		}

		return ret;
	}

	private Integer checkIntegerParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getIntegerParameter(name);
	}

	private Long getLongParameter(String name)
			throws JSONInvalidParameterException {

		Long ret = null;

		try {
			ret = parameters.getLong(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is Long.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name + "\"的类型错误，需要的参数类型为长整型(Long).");
			throw ex;
		}

		return ret;
	}

	private Long checkLongParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getLongParameter(name);
	}

	private String getStringParameter(String name)
			throws JSONInvalidParameterException {

		String ret = null;

		try {
			ret = parameters.getString(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is String.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为字符串类型(String).");
			throw ex;
		}

		return ret;
	}

	private String checkStringParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getStringParameter(name);
	}

	private JSONObject getObjectParameter(String name)
			throws JSONInvalidParameterException {
		
		JSONObject ret = null;

		try {
			ret = parameters.getJSONObject(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is JSONObject.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为JSON对象(JSONObject).");
			throw ex;
		}

		return ret;
	}

	private JSONObject checkObjectParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {
		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getObjectParameter(name);
	}

	private JSONArray getArrayParameter(String name)
			throws JSONInvalidParameterException {

		JSONArray ret = null;

		try {
			ret = parameters.getJSONArray(name);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is JSONArray.");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为JSON数组(JSONArray).");
			throw ex;
		}

		return ret;
		
	}
	
	private JSONArray checkArrayParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {
		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getArrayParameter(name);
	}
	
	// FIXME, set by default date format
	private static final String DATE_STRING_FROMAT = "yyyy-MM-dd";
	private static final String	DATETIME_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final SimpleDateFormat dateParser = new SimpleDateFormat(
			DATE_STRING_FROMAT);
	private static final SimpleDateFormat datetimeParser = new SimpleDateFormat(DATETIME_STRING_FORMAT);

	private Date getDateParameter(String name)
			throws JSONInvalidParameterException {
		Date ret = null;

		try {
			ret = dateParser.parse(parameters.getString(name));
		} catch (Exception e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is Date String in format of ["
					+ DATE_STRING_FROMAT + "].");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为日期时间字符串(Date String in format of ["
					+ DATE_STRING_FROMAT + "]).");
			throw ex;
		}

		return ret;
	}
	
	private Date getDatetimeParameter(String name)
			throws JSONInvalidParameterException {
		Date ret = null;

		try {
			ret = datetimeParser.parse(parameters.getString(name));
		} catch (Exception e) {
			ShntecLogger.logger.error("Invalid type of parameter: \"" + name
					+ "\", required type is Date String in format of ["
					+ DATETIME_STRING_FORMAT + "].");
			ShntecLogger.logger.error(e.getMessage());
			JSONInvalidParameterException ex = new JSONInvalidParameterException(
					name);
			ex.setMoreInformation("参数\"" + name
					+ "\"的类型错误，需要的参数类型为日期时间字符串(Date String in format of ["
					+ DATETIME_STRING_FORMAT + "]).");
			throw ex;
		}

		return ret;
	}

	private Date checkDateParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException {
		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getDateParameter(name);
	}
	
	private Date checkDatetimeParameter(String name, boolean required)
			throws JSONMissingParameterException, JSONInvalidParameterException
	{
		// 首先检查参数是否存在
		if (!isParameterExist(name, required)) {
			return null;
		}

		// 按照指定类型获取参数的值
		return getDatetimeParameter(name);
	}

	public Boolean checkRequiredBooleanParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkBooleanParameter(name, true);
	}

	public Boolean checkOptionalBooleanParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkBooleanParameter(name, false);
	}

	public Integer checkRequiredIntegerParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkIntegerParameter(name, true);
	}

	public Integer checkOptionalIntegerParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkIntegerParameter(name, false);
	}

	public Long checkRequiredLongParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkLongParameter(name, true);
	}

	public Long checkOptionalLongParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkLongParameter(name, false);
	}

	public String checkRequiredStringParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkStringParameter(name, true);
	}

	public String checkOptionalStringParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkStringParameter(name, false);
	}

	public JSONObject checkRequiredObjectParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkObjectParameter(name, true);
	}

	public JSONObject checkOptionalObjectParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkObjectParameter(name, false);
	}

	public JSONArray checkRequiredArrayParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkArrayParameter(name, true);
	}

	public JSONArray checkOptionalArrayParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkArrayParameter(name, false);
	}

	public Date checkRequiredDateParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkDateParameter(name, true);
	}

	public Date checkOptionalDateParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkDateParameter(name, false);
	}
	
	public Date checkRequiredDatetimeParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkDatetimeParameter(name, true);
	}
	
	public Date checkOptionalDatetimeParameter(String name)
			throws JSONMissingParameterException, JSONInvalidParameterException {

		return checkDatetimeParameter(name, false);
	}
	
	public static void main(String args[]) {

		JSONObject json = new JSONObject();

		try {
			json.put("test1", "123a");
			json.put("test2", "asdf");
			json.put("test3", "456");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//

		JSONParameterChecker checker = new JSONParameterChecker(json);

		try {
			checker.checkRequiredIntegerParameter("test1");
			checker.checkOptionalIntegerParameter("test1");
			checker.checkRequiredStringParameter("test2");
			checker.checkOptionalStringParameter("test2");
			checker.checkRequiredLongParameter("test3");
			checker.checkOptionalLongParameter("test3");
		} catch (ShntecException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMoreInformation());
		}

	}

}
