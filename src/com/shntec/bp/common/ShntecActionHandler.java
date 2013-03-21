package com.shntec.bp.common;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.shntec.bp.util.ShntecConfigManager;
import com.shntec.bp.util.ShntecLogger;

public class ShntecActionHandler {

	private static ShntecActionHandler instance = null;
	
	// Hashmap: <Anction code, Class file>
	private HashMap<Integer, String> shntecActionList = null;
	
	public static synchronized ShntecActionHandler getInstance() {
		if ( null == instance) {
			instance = new ShntecActionHandler();
		}
		return instance;
	}

	private ShntecActionHandler() {
		shntecActionList = new HashMap<Integer, String>();
		loadActionList();
	}

	
	private ShntecBaseAction getActionInstance(String fullClassName) {

		ShntecBaseAction actionInstance = null;
		Class<?> actionClass = null;
		
		try {
			actionClass = Class.forName(fullClassName);
		} catch ( ClassNotFoundException e ) {
			ShntecLogger.logger.error("Can not find the implementation file of class: " + fullClassName);
			ShntecLogger.logger.error(e.getMessage());
			return actionInstance;
		}
		
		int newActionCode = JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION;
		String newActionName = null;
		String newActionDescription = null;
		
		try {
			actionInstance = (ShntecBaseAction) actionClass.newInstance();
			newActionCode = actionInstance.getActionCode();
			newActionName = actionInstance.getActionName();
			newActionDescription = actionInstance.getActionDescription();
			ShntecLogger.logger.debug("Instance action: " + fullClassName + 
					" action code: " + newActionCode + 
					", action name: " + newActionName + 
					", action description: " + newActionDescription);
		} catch (InstantiationException e) {
			ShntecLogger.logger.error("Action class： " + fullClassName + " initialization failed.");
			ShntecLogger.logger.error(e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			ShntecLogger.logger.error("Action calss： " + fullClassName + " illegal access error.");
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}
		catch (Exception e) {
			ShntecLogger.logger.error("Action calss： " + fullClassName + " unknown error.");
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}
		
		return actionInstance;
	}
	
	private int validateActionClass(String fullClassName) {
		
		ShntecBaseAction action = getActionInstance(fullClassName);
		int actionCode = JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION;
		
		// TODO: add more validation process
		
		if (null != action) {
			actionCode = action.getActionCode();
			if (JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION == actionCode) {
				ShntecLogger.logger.error("Action class: " + fullClassName + " validation failed.");
			}
		}
		

		return actionCode;
	}

	private boolean loadActionList() {

		ShntecLogger.logger.debug("Start to load base platformaction  package.");
		int i = 0;
		for (i=0; i<ShntecConfigManager.platformActionPackage.size(); ++i){
			loadActionPackage(ShntecConfigManager.platformActionPackage.get(i));
		}
		ShntecLogger.logger.debug("Load " + i + " base platformaction  package.");
		
		int j = 0;
		ShntecLogger.logger.debug("Start to load application platformaction  package.");
		for (j=0; j<ShntecConfigManager.applicationActionPackage.size(); ++j){
			loadActionPackage(ShntecConfigManager.applicationActionPackage.get(j));
		}
		
		ShntecLogger.logger.debug("Load " + j + " application platformaction  package.");

		return true;
	}
	
	
	public boolean loadActionPackage (String actionPackage) {
		
		ShntecLogger.logger.debug("Start to load action package: " + actionPackage);
		// First find the path of action package from the 
		URL packageUrl = ShntecActionHandler.class.getClassLoader().
				getResource(actionPackage.replace(".", "/"));
		if (packageUrl == null) {
			ShntecLogger.logger.error("Does not find package: " + actionPackage);
			return false;
		}
		
		String packagePath = packageUrl.getPath();
		// Iterate whole action package folder to find action class and load it
		File packageFolder = new File(packagePath);
		File actionClasses[] = packageFolder.listFiles();
		int counter = 0;
		for (int i=0; i<actionClasses.length; ++i) {
			if (actionClasses[i].getName().toLowerCase().endsWith("class")) {
				String className = actionClasses[i].getName().split(".class")[0];
				String fullClassName = actionPackage + "." + className;
				if (addAction(fullClassName)) {
					++counter;
				}
			}
		}
		
		ShntecLogger.logger.debug("Load " + counter + " actions from action package: " + actionPackage);
		
		return true;
	}

	public boolean addAction(String fullClassName) {
		
		// Check whether it is valid action class
		int actionCode = validateActionClass(fullClassName);
		
		// Check whether action code is duplicated
		if (shntecActionList.containsKey(actionCode)) {
			String existActionClass = shntecActionList.get(actionCode);
			ShntecLogger.logger.error("Action code already exists, actionCode=" 
					+ actionCode + ", existActionClass=" + existActionClass
					+ ", to be added action class:" + fullClassName);
			return false;
		}
		
		if (actionCode != JSONMessageConstant.ACTION_CODE_UNKNOWN_ACTION) {
			ShntecLogger.logger.debug("Add new action: " + fullClassName + " into action list.");
			shntecActionList.put(actionCode, fullClassName);
			return true;
		}

		ShntecLogger.logger.debug("Action: " + fullClassName + " is NOT added into action list.");

		return false;
	}
	
	public ShntecBaseAction getAction(int actionCode) {
		
		ShntecBaseAction newAction = null;

		if (shntecActionList.containsKey(actionCode)) {
			
			String fullClassName = shntecActionList.get(actionCode);
			newAction = getActionInstance(fullClassName);

			if ( null != newAction ) {
				ShntecLogger.logger.debug("Get action with action code: " + newAction.getActionCode() 
					+ ", action name: " + newAction.getActionName() 
					+ ", action description: " + newAction.getActionDescription());
			}
		}
		else {
			ShntecLogger.logger.error("Action with action code: " + actionCode + " is not found.");
		}
		
		return newAction;
	}
	
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
	
		ResponseMessageGenerator generator = null;
		
		ShntecBaseAction action = getAction(parser.getActionCode());
		
		if (null != action){
			ShntecLogger.logger.info("Start to execute: " + action.getActionName());
			generator = action.handleAction(parser);
		}
		else {
			ShntecLogger.logger.error("Dose not find action: " + parser.getActionCode());
			generator = new ResponseMessageGenerator().toError(parser, ShntecErrorCode.SHNTEC_ERROR_CODE_NOT_FOUND);
		}
		
		return generator;
	}
	
	public LinkedList<ShntecBaseAction> getActionList(){
		
		LinkedList<ShntecBaseAction> actionList = new LinkedList<ShntecBaseAction>();
		
		Iterator<Integer> iter = shntecActionList.keySet().iterator();
		
		while (iter.hasNext()) {
			int actionCode = iter.next();
			ShntecBaseAction action = getAction(actionCode);
			int i=0;
			for (;i<actionList.size(); ++i) {
				if (action.getActionCode() <= actionList.get(i).getActionCode()) {
					break;
				}
			}
			actionList.add(i, action);
		}

		return actionList;
	}
	
}
