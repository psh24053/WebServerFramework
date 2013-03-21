package com.shntec.bp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;

public final class ShntecConfigManager {

	static private final String DEFAULT_DATABASE_URL="jdbc:mysql://127.0.0.1/GingoCardsDb?useUnicode=true&characterEncoding=UTF-8";
	static private final String DEFAULT_DATABASE_USERNAME="GingoCardsDba";
	static private final String DEFAULT_DATABASE_PASSWORD="GingoCardsPwd";
	static private final String	KEYNAME_POSBATCH_CHECK = "IsCheckPosBatchNumber";

	static public final String SHNTEC_CONF_FOLDER = "conf";
	static public final String SHNTEC_PROPERTIES_FILE = "shntec.properties";
	
	
	static private ShntecConfigManager instance = null;
	static private Properties shntecProperties = null;
	
	// Base platform properties
	static public boolean enableVirtualAction = false;
	// Base platform action package
	static public LinkedList<String> platformActionPackage = null;;
	// Application action package
	static public LinkedList<String> applicationActionPackage = null;
	static public String databaseUrl = DEFAULT_DATABASE_URL;
	static public String databaseUsername = DEFAULT_DATABASE_USERNAME;
	static public String databasePassword = DEFAULT_DATABASE_PASSWORD;
	static public boolean isCheckPosBatchNumber = false;
	
	static public boolean enableAuthenticationCheck = false;
	
	static public String crmRequestURL = null;
	
	static public int SYSTEM_TYPE_UNKNOWN = 0;
	static public int SYSTEM_TYPE_LINUX = 1;
	static public int SYSTEM_TYPE_WINDOWS = 2;
	
	private int systemType = SYSTEM_TYPE_UNKNOWN;
	
	static public synchronized ShntecConfigManager getInstance() {

		if (null == instance ) {
			instance = new ShntecConfigManager();
		}
		
		return instance;
	}
	
	private ShntecConfigManager() {
		init();
		loadConfig();
	}

	private void init() {
	
		platformActionPackage = new LinkedList<String>();
		applicationActionPackage = new LinkedList<String>();
		
	}
	
	// Read configuration from default properties file.
	private void loadConfig() {
		
		// Find properties file from class path
		String shntecPropertiesFilePath = getConfPath() + File.separator + SHNTEC_PROPERTIES_FILE;
		
		ShntecLogger.logger.debug("Start to read golbal properties file: " + shntecPropertiesFilePath);
		
		try {
			FileInputStream shntecPropertiesFileStream = new FileInputStream(shntecPropertiesFilePath);
			shntecProperties = new Properties();
			shntecProperties.load(shntecPropertiesFileStream);
			shntecPropertiesFileStream.close();
		} catch (IOException e) {
			ShntecLogger.logger.error("Read global properties file: " + shntecPropertiesFilePath + " failed!");
			ShntecLogger.logger.error(e.getMessage());
			return;
		}
		
		// EnableVirtualAction 默认值为 false 。
		if (shntecProperties.getProperty("EnableVirtualAction", "false").equals("true")) {
			enableVirtualAction = true;
		}
		else {
			enableVirtualAction = false;
		}
		
		// Base platform action package
		String platformActionPackageArray[] = shntecProperties.getProperty("PlatformActionPackage", "com.shntec.bp.action").split(":");
		for (int i=0; i<platformActionPackageArray.length; ++i) {
			String actionPackage = platformActionPackageArray[i].trim();
			if (!actionPackage.isEmpty()) {
				platformActionPackage.push(actionPackage);
			}
		}
		
		// Application action package
		String applicationActionPackageArray[] = shntecProperties.getProperty("ApplicationActionPackage", "").split(":");
		for (int i=0; i<applicationActionPackageArray.length; ++i) {
			String actionPackage = applicationActionPackageArray[i].trim();
			if (!actionPackage.isEmpty()) {
				applicationActionPackage.push(actionPackage);
			}
		}
		databaseUrl = shntecProperties.getProperty("db.url", DEFAULT_DATABASE_URL);
		databaseUsername = shntecProperties.getProperty("db.username", DEFAULT_DATABASE_USERNAME);
		databasePassword = shntecProperties.getProperty("db.password", DEFAULT_DATABASE_PASSWORD);
		
		if ( shntecProperties.getProperty("EnableAuthenticationCheck", "false").equals("true")){
			enableAuthenticationCheck = true;
		}
		if ( shntecProperties.getProperty(KEYNAME_POSBATCH_CHECK, "false").equals("true")){
			isCheckPosBatchNumber = true;
		}

		crmRequestURL = shntecProperties.getProperty("CardRemoteManager.RequestURL", "http://127.0.0.1:8080/GingoCoreServer/CardFEP");
		
		ShntecLogger.logger.debug("Read golbal properties file: " + shntecPropertiesFilePath + " finished");
	}
	
	// Return path looks like "/var/www/webapp/appname/conf"
	public static String getConfPath () {

		String confPath = null;

		URL propertiesURL = ShntecConfigManager.class.getClassLoader().getResource(SHNTEC_PROPERTIES_FILE);
		
		if (propertiesURL != null) {
			String propertiesPath = propertiesURL.getPath();
			confPath = propertiesPath.substring(0, propertiesPath.lastIndexOf("/"));
		}
		else {
			confPath = CommonUtil.getWebappRoot() + File.separator + "WEB-INF" + 
					File.separator + SHNTEC_CONF_FOLDER;
		}
	
		return confPath;
	} 
	
	public int getSystemType () {
		
		return systemType;
		
	}
	
}
