package com.shntec.bp.util;

import java.util.ArrayList;
import java.util.List;

import com.shntec.bp.common.JSONSerializable;
import com.shntec.bp.exception.JSONSerializableException;
import com.shntec.bp.json.JSONArray;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;

public class JSONSerializableUtil {

	public JSONSerializableUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static JSONArray toJSNOArray(List<?> array) {
		
		JSONArray jsonArray = new JSONArray();
		
		try {
			for (Object obj: array) {
				JSONObject json = ((JSONSerializable)(obj)).toJSON();
				jsonArray.put(json);
			} 
		}
		catch (JSONSerializableException e) {
			ShntecLogger.logger.error("Serialize List<?> to JSONArray failed.");
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}
		
		return jsonArray;
	}
	
	public static List<JSONSerializable> fromJSNOArray(
			JSONArray jsonArray, 
			Class<? extends JSONSerializable> objClass) {
		
		ArrayList<JSONSerializable> array = new ArrayList<JSONSerializable>();
		
		try {
			for ( int i=0; i<jsonArray.length(); ++i ) {
				JSONObject json = jsonArray.getJSONObject(i);
				JSONSerializable obj = (JSONSerializable)objClass.newInstance();
				obj.fromJSON(json);
				array.add(obj);
			}
		} catch (JSONException e) {
			ShntecLogger.logger.error("Handle JSONArray failed, class name:" 
					+ objClass.getName());
			ShntecLogger.logger.error(e.getMessage());
			return null;
		} catch (InstantiationException e) {
			ShntecLogger.logger.error("Instance class failed, class name:" 
					+ objClass.getName());
			ShntecLogger.logger.error(e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			ShntecLogger.logger.error("Access class failed, class name:" 
					+ objClass.getName());
			ShntecLogger.logger.error(e.getMessage());
			return null;
		} catch (JSONSerializableException e) {
			ShntecLogger.logger.error("Serialize to JSON failed, class name:" 
					+ objClass.getName());
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}
		
		return array;
	}
}
