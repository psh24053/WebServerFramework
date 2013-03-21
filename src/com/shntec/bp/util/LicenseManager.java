package com.shntec.bp.util;


import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import com.shntec.license.jni.JniLicenseHandler;

public class LicenseManager {

	static final String LICENSE_PROPERTIES_FILE = "license.properties";

	static private LicenseManager instance = null;

	static private Properties licenseProperties = null;
		
	static private boolean isLicensed = false;
	
	static private Date expireDate = null; 
	
	static private HashMap<Integer, Integer> actionWhiteList = null;

	static public JniLicenseHandler jniLicenseHandler = null;
	
	String lastErrorMessage = null;

	static public synchronized LicenseManager getInstance() {
		if (null == instance) {
			instance = new LicenseManager();
		}
		
		return instance;
	}
	
	private LicenseManager() {
		initLicense();
		initWhiteList();
	}

	private void initLicense() {
		jniLicenseHandler = JniLicenseHandler.getInstance();
		try {
			jniLicenseHandler.loadLicenseFile();
			isLicensed = jniLicenseHandler.getLicenseStatus();
			expireDate = jniLicenseHandler.getExpireDate();
		} catch (Exception e) {
			ShntecLogger.logger.error("License management module initialization failed.");
			ShntecLogger.logger.error(e.getMessage());
		}
		
		if (isLicensed) {
			ShntecLogger.logger.debug("Current license status: " + jniLicenseHandler.getReasonMessage());
			ShntecLogger.logger.debug("Current license expired time: " + jniLicenseHandler.getExpireDate().toString());
		}
		else {
			ShntecLogger.logger.debug("Current license status: " + jniLicenseHandler.getReasonMessage());			
		}
	}
	
	private void initWhiteList() {
		actionWhiteList = new HashMap<Integer, Integer>();
	}
	
	public void addWhiteList (int actionCode) {
		actionWhiteList.put(actionCode, 1);
	}
	
	public String getLastErrorMessage() {
		return this.lastErrorMessage;
	}
	
	public boolean checkLicense() {
		return isLicensed;
	}
	
	public boolean checkWhiteList(Integer actionCode) {
		if (actionWhiteList.get(actionCode) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getMachineCode() {
		return jniLicenseHandler.getMachineCode();
	}
	
	/*
	 * @return
	 **/
	
	public boolean licenseRegister(String serialNumber){
		
		if (!jniLicenseHandler.verifySerialNumber(serialNumber)) {
			ShntecLogger.logger.error(jniLicenseHandler.getReasonMessage());
			this.lastErrorMessage = jniLicenseHandler.getReasonMessage();
			return false;
		}
		
		try {
			jniLicenseHandler.saveSerialNumber(serialNumber);
		} catch (Exception e) {
			ShntecLogger.logger.error(e.getMessage());
			this.lastErrorMessage = "Save license failed: " + e.getMessage();
			return false;
		}
		
		return true;
	}
	
}
