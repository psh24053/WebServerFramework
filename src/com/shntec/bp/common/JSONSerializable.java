package com.shntec.bp.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import com.shntec.bp.exception.JSONSerializableException;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.bp.util.SimpleDateParser;

public abstract class JSONSerializable {

	private static final String JSON_NAME_PREFIX = "J_";
	private static final String JSON_SERIALIZABLE_IDENTITY_NAME = "JSONSerializableName";
	
	private String jsonSerializableName = null;
	
	public JSONSerializable() {
		jsonSerializableName = this.getClass().getName();
	}

	private String toJSONName(String fieldName) {
		return JSON_NAME_PREFIX + fieldName;
	}
	
	private String toFieldName(String jsonName) {
		if (jsonName.startsWith(JSON_NAME_PREFIX)) {
			return jsonName.split(JSON_NAME_PREFIX)[1];
		}
		else {
			return jsonName;
		}
	}
	
	public JSONObject toJSON() throws JSONSerializableException {

		JSONObject json = new JSONObject();
		
		Field fields[] = this.getClass().getDeclaredFields();
		
		for (Field field: fields) {
			String fieldName = field.getName();
			String jsonName = toJSONName(fieldName);
			field.setAccessible(true);
			Class<?> type = field.getType();
			try {
				// Handle primitive type
				if (type.isPrimitive()) {
					if (type.equals(Integer.TYPE)) {
						json.put(jsonName, field.getInt(this));
					}
					else if (type.equals(Boolean.TYPE)) {
						json.put(jsonName, field.getBoolean(this));
					}
					else if (type.equals(Long.TYPE)) {
						json.put(jsonName, field.getLong(this));
					}
					else if (type.equals(Float.TYPE)) {
						json.put(jsonName, 
								Float.toString(field.getFloat(this)));
					}
					else if (type.equals(Double.TYPE)) {
						json.put(jsonName, field.getDouble(this));
					}
					else {
						throw new JSONSerializableException(
							"Not supported primitive type: " + type.getName());
					}
				}
				// Handle non-primitive type (Object)
				else {
					Object obj = field.get(this);
					if (null == obj) {
						// Set null value of object
						json.put(jsonName, JSONObject.NULL);
					}
					else {
						if (type.equals(Integer.class)) {
							json.put(jsonName, (Integer)obj);
						}
						else if (type.equals(String.class)) {
							json.put(jsonName, (String)obj);
						}
						else if (type.equals(Date.class)) {
							json.put(jsonName, 
									SimpleDateParser.format((Date)obj));
						}
						else if (type.equals(BigDecimal.class)) {
							json.put(jsonName, 
									((BigDecimal)(obj)).toString());
						}
						else {
							throw new JSONSerializableException(
									"Not supported class type: " + type.getName());
						}
					}
				}
				
			}
			catch (JSONException e) {
				throw new JSONSerializableException(
						"Format field to JSON error: " + fieldName);
			} catch (IllegalArgumentException e) {
				throw new JSONSerializableException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new JSONSerializableException(e.getMessage());
			}
		}
		
		try {
			json.put(JSON_SERIALIZABLE_IDENTITY_NAME, jsonSerializableName);
		} catch (JSONException e) {
			throw new JSONSerializableException(
					"Format object to JSON error.");
		}

		return json;
	}
	
	public void fromJSON(JSONObject json) throws JSONSerializableException {

		// 检查输入的JSON对象是否适用于这个类
		try {
			String identity = json.getString(JSON_SERIALIZABLE_IDENTITY_NAME);
			ShntecLogger.logger.debug("JSON identity:" + this.jsonSerializableName);
			if (0 != identity.compareTo(this.jsonSerializableName)) {
				throw new JSONSerializableException(
						"Wrong JSON identity: " + identity);
			}
		} catch (JSONException e) {
			throw new JSONSerializableException(
					"Input JSONObject is invalid");
		}
		
		String jsonNames[] = JSONObject.getNames(json);
		
		for (String jsonName: jsonNames) {
			
			// Skipped identity item
			if (0 == jsonName.compareTo(JSON_SERIALIZABLE_IDENTITY_NAME)) {
				continue;
			}
			
			String fieldName = toFieldName(jsonName);
			
			try {
				Field field = this.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				Class<?> type = field.getType();
				// Handle primitive type
				if (type.isPrimitive()) {
					if (type.equals(Integer.TYPE)) {
						int value = json.getInt(jsonName);
						field.setInt(this, value);
					}
					else if (type.equals(Boolean.TYPE)) {
						boolean value = json.getBoolean(jsonName);
						field.setBoolean(this, value);
					}
					else if (type.equals(Long.TYPE)) {
						long value = json.getLong(jsonName);
						field.setLong(this, value);
					}
					else if (type.equals(Float.TYPE)) {
						float value = Float.parseFloat(
								json.getString(jsonName));
						field.setFloat(this, value);
					}
					else if (type.equals(Double.TYPE)) {
						double value = json.getDouble(jsonName);
						field.setDouble(this, value);
					}
					else {
						throw new JSONSerializableException(
							"Not supported primitive type: " + type.getName());
					}
				}
				// Handle non-primitive type (Object)
				else {
					if (json.isNull(jsonName)) {
						field.set(this, null);
					}
					else {
						if (type.equals(Integer.class)) {
							Integer value = json.getInt(jsonName);
							field.set(this, value);
						}
						else if (type.equals(String.class)) {
							String value = json.getString(jsonName);
							field.set(this, value);
						}
						else if (type.equals(Date.class)) {
							Date value = SimpleDateParser.parse(
									json.getString(jsonName));
							field.set(this, value);
						}
						else if (type.equals(BigDecimal.class)) {
							BigDecimal value = 
									new BigDecimal(json.getString(jsonName));
							field.set(this, value);
						}
						else {
							throw new JSONSerializableException(
									"Not supported class type: " + type.getName());
						}
					}
				}
			} catch (NoSuchFieldException e) {
				throw new JSONSerializableException(e.getMessage());
			} catch (IllegalArgumentException e) {
				throw new JSONSerializableException(e.getMessage());
			} catch (JSONException e) {
				throw new JSONSerializableException(
						"Parse JSON to field error: " + jsonName);
			} catch (IllegalAccessException e) {
				throw new JSONSerializableException(e.getMessage());
			} catch (ParseException e) {
				throw new JSONSerializableException(e.getMessage());
			}
		}
	}
}
