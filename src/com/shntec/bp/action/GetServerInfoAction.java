package com.shntec.bp.action;

import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;

import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.common.JSONMessageConstant;
import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.util.ShntecLogger;

public class GetServerInfoAction extends ShntecBaseAction {

	public GetServerInfoAction () {
		super.actionCode = JSONMessageConstant.ACTION_CODE_GET_SERVER_INFO;
		super.actionName = JSONMessageConstant.ACTION_NAME_GET_SERVER_INFO;
		super.actionDescription = JSONMessageConstant.ACTION_DESCRIPTION_GET_SERVER_INFO;
		// No need authentication
		super.needAuthenticationCheck = false;
	}

	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {

		ResponseMessageGenerator generator = new ResponseMessageGenerator();

		JSONObject payload = null;
		
		try {
			payload = new JSONObject();
			payload.put("ServerName", "Shntec Server");
		} catch (JSONException e) {
			ShntecLogger.logger.error("Generate JSON response failed.");
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, 
					"Generate JSON response error, reason: " + e.getMessage());
		}

		return generator.toSuccess(parser, payload);
	}

}
