/**
 * 
 */
package com.shntec.bp.action;

import com.shntec.bp.common.JSONMessageConstant;
import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.impl.ServerPushManager;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;

/**
 * @author 1
 *
 */
public class TestServerPushAction extends ShntecBaseAction {

	/**
	 * 
	 */
	public TestServerPushAction() {
		super.actionCode = JSONMessageConstant.ACTION_CODE_TEST_SERVER_PUSH;
		super.actionName = JSONMessageConstant.ACTION_NAME_TEST_SERVER_PUSH;
		super.actionDescription = JSONMessageConstant.ACTION_DESCRIPTION_TEST_SERVER_PUSH;
		// No need authentication
		super.needAuthenticationCheck = true;
	}

	/* (non-Javadoc)
	 * @see com.shntec.bp.common.ShntecBaseAction#handleAction(com.shntec.bp.common.RequestMessageParser)
	 */
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {

		ResponseMessageGenerator generator = new ResponseMessageGenerator();

		JSONObject parameter = parser.getParameterJsonObject();
		
		if (parameter == null) {
			generator.toError(parser, ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER);
		}
		
		String message = null;
		try {
			message = parameter.getString("msg");
			ShntecLogger.logger.error("requset msg +++++++++++++" + message);
		} catch (JSONException e) {
			ShntecLogger.logger.error("Missing required parameter: \"msg\"" );
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER, 
					"缺少所需的参数: \"msg\"");
		}

		ServerPushManager.getInstance().broadcastContent(message);

		return generator.toSuccess(parser, null);
	}
	
}
