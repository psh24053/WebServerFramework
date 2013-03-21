package com.shntec.bp.common;

import java.util.HashMap;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;

import com.shntec.bp.util.ShntecLogger;

/**
 * @author andy 2011-01-06 16:23
 * @version 1.0.2
 * DESC virtual handler module
 */

public class VirtualActionHandler {
	private static VirtualActionHandler instance = null;
	
	private static final String VIRTUAL_ACTION_CONF_FILE = "virtual_action.xml";
	
	private HashMap<Integer, String> shntecVirtualActionList = null;
	
	public static synchronized VirtualActionHandler getInstance() {
		if ( null == instance ) {
			instance = new VirtualActionHandler();
		}
		return instance;
	}
	
	private VirtualActionHandler() {
		shntecVirtualActionList = new HashMap<Integer, String>();
		loadVirtualActionList();
	}
	
	/**
	 * @param requestJSON
	 * @return responseJSON
	 * DESC 根据requestJSON中的code找到预设的responseJSON
	 */
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {

		JSONObject responseJSON = null;
		int actionCode = parser.getActionCode();
		String responseJSONString = getAction(actionCode);
		
		if ( null != responseJSONString ) {
			try {
				responseJSON = new JSONObject(responseJSONString);
			} catch (JSONException e) {
				ShntecLogger.logger.error("Virtual handler module generate action response error.");
				ShntecLogger.logger.error(e.getMessage());
				return null;
			}
		}
		else {
			ShntecLogger.logger.error(" Can not find action : " + actionCode + " from virual handler configuration file.");
			return null;
		}

		ResponseMessageGenerator generator = new ResponseMessageGenerator();

		generator.setResponseObject(responseJSON);
		
		return generator;
	}
	
	private boolean loadVirtualActionList() {
		Element root = getVirtualActionConfig().getRootElement();
		for( Iterator<?> i = root.elementIterator("action"); i.hasNext(); ) {
			Element action = (Element) i.next();
			int actionCode = Integer.parseInt(action.attributeValue("code"));
			String actionName = action.attributeValue("name");
			String actionStr = action.getText().trim();
			addAction(actionCode, actionStr);
		}

		return true;
	}
	
	/**
	 * @param actionCode
	 * @param newActionStr
	 * DESC 向hashmap中保存预设code和responseJSONString
	 */
	private boolean addAction(int actionCode, String newActionStr) {
		
		if ( null == shntecVirtualActionList ) {
			shntecVirtualActionList = new HashMap<Integer, String>();
		}
		
		String oldActionStr = shntecVirtualActionList.get(actionCode);
		if ( null != oldActionStr ) {
			return false;
		}
		
		shntecVirtualActionList.put(actionCode, newActionStr);
		
		return true;
	}
	
	/**
	 * @param actionCode
	 * DESC 根据code从hashmap中取得相应的responseJSONString
	 */
	private String getAction(int actionCode) {
		String actionStr = null;
		actionStr = (String) shntecVirtualActionList.get(actionCode);
		return actionStr;
	}
	
	/**
	 * DESC 获得虚处理模块配置
	 */
	private Document getVirtualActionConfig() {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(
					VirtualActionHandler.class.getClassLoader()
					.getResourceAsStream(VIRTUAL_ACTION_CONF_FILE));
		} catch (DocumentException e) {
			ShntecLogger.logger.error("Failed to read virtual handler configuration file: " + VIRTUAL_ACTION_CONF_FILE);
			e.printStackTrace();
		}
		
		return doc;
	}
}
