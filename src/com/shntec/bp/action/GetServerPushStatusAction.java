/**
 * 
 */
package com.shntec.bp.action;

import java.util.Date;

import com.shntec.bp.common.JSONMessageConstant;
import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.impl.ServerPushManager;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.bp.util.SimpleDateParser;

/**
 * @author 1
 *
 */
public class GetServerPushStatusAction extends ShntecBaseAction {

	/**
	 * 
	 */
	public GetServerPushStatusAction() {
		super.actionCode = JSONMessageConstant.ACTION_CODE_GET_SERVER_PUSH_STATUS;
		super.actionName = JSONMessageConstant.ACTION_NAME_GET_SERVER_PUSH_STATUS;
		super.actionDescription = JSONMessageConstant.ACTION_DESCRIPTION_GET_SERVER_PUSH_STATUS;
		// No need authentication
		super.needAuthenticationCheck = false;
	}

	/* (non-Javadoc)
	 * @see com.shntec.bp.common.ShntecBaseAction#handleAction(com.shntec.bp.common.RequestMessageParser)
	 */
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		
		JSONObject parameter = parser.getParameterJsonObject();
		
		if (parameter == null) {
			return generator.toError(parser, 
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
		}
		
		// Start to retrieve required parameters from request
		
		// Optional, if no client ID set, return all client's server push status.
		String clientId = null;
		try {
			clientId = parameter.getString("cid");			
		} catch (JSONException e) {
			ShntecLogger.logger.info("Missing required parameter: \"cid\" (User ID)");
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER, 
					"缺少所需的参数: \"cid\" (Client ID)");
		}
		
		ServerPushManager manager = ServerPushManager.getInstance();
		JSONObject payload = new JSONObject();

		if (clientId != null) {
			int status = manager.getServerPushStatus(clientId);
			Date lastSuccessHeartBeat = manager.getLastSuccessHeartBeat(clientId);
			Date now = new Date();
			try {
				payload.put("sta", status);
				payload.put("lsh", SimpleDateParser.format(lastSuccessHeartBeat));
				payload.put("now", SimpleDateParser.format(now));
			} catch (JSONException e) {
				ShntecLogger.logger.error("Generate JSON response failed.");
				ShntecLogger.logger.error(e.getMessage());
				return generator.toError(parser, 
						ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, 
						"Generate JSON response error, reason: " + e.getMessage());
			}
		}
		else {
			//
		}
		
		return generator.toSuccess(parser, payload);
	}

}
