package com.shntec.bp.common;

import com.shntec.bp.util.ShntecConfigManager;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.bp.util.LicenseManager;


public class NormalRequestDispatcher {

	// Indicate whether need to check license
	private boolean needCheckLicense = false;
	
	public void setNeedCheckLicense (boolean needCheckLicense) {
		this.needCheckLicense = needCheckLicense;
	}

	public boolean getNeedCheckLicense() {
		return needCheckLicense;
	}

	public NormalRequestDispatcher() {

	}
	
	public NormalRequestDispatcher(boolean needCheckLicense) {

		this.needCheckLicense = needCheckLicense;
	
	}

	public ResponseMessageGenerator dispatcherHandler(RequestMessageParser parser) {

		ShntecLogger.logger.debug("Enter NormalRequestDispatcher.dispatcherHandler() ...");
		
		ResponseMessageGenerator generator = null;
		
		// First check product registration
		if (needCheckLicense && !LicenseManager.getInstance().checkLicense()) {
			
			// Check white name list, some action need not license 
			if (!LicenseManager.getInstance().checkWhiteList(parser.getActionCode())) {
				ShntecLogger.logger.debug("License check failed: " + LicenseManager.getInstance().jniLicenseHandler.getReasonMessage());
			
				// Not licensed, return related error message
				generator = new ResponseMessageGenerator();
				return generator.toError(parser, ShntecErrorCode.SHNTEC_ERROR_CODE_LICENSE_INVALID);
			}
		}
		
		// Find action handler and execute action
		try {
			if ( ShntecConfigManager.enableVirtualAction ) {
				ShntecLogger.logger.debug("Handle action by virtual action handler.");
				// Handled by virtual action handler
				VirtualActionHandler virtualActionHandler =  VirtualActionHandler.getInstance();
				generator = virtualActionHandler.handleAction(parser);
			} else {
				ShntecLogger.logger.debug("Handle action by shntec action handler.");				
				// Handled by real action handler
				ShntecActionHandler shntecActionHandler = ShntecActionHandler.getInstance();
				generator = shntecActionHandler.handleAction(parser);
			}
		} catch (Exception e) {
			ShntecLogger.logger.error("Execute action: " + parser.getActionCode() + " failed.");
			ShntecLogger.logger.error(e.getMessage());
			ShntecLogger.logger.error(this, e);
			generator = null;
		}
		
		return generator;
	}
	
}
