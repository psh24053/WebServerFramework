package com.shntec.bp.util;

import java.io.File;

import org.apache.log4j.*;

public class ShntecLogger {

	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	
	public static final String loggerName = "com.shntec.bp";
	
	public static Logger logger = Logger.getLogger(loggerName);
	
	static {
		String log4jProperties = ShntecConfigManager.getConfPath() + File.separator + LOG4J_PROPERTIES_FILE;
		PropertyConfigurator.configure(log4jProperties);
	}
	
	public ShntecLogger() {
		
	}
	
	public Logger logger() {
		return Logger.getLogger("com.shntec.bp");
	}
	
	// TODO: detailed log configuration
}
