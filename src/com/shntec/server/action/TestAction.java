package com.shntec.server.action;

import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;

public class TestAction extends ShntecBaseAction {

	public TestAction(){
		this.actionCode = 3000;
		this.actionDescription = "";
		this.actionName = "TestAction";
	}
	
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		ResponseMessageGenerator generator = new ResponseMessageGenerator();

		JSONObject parameter = parser.getParameterJsonObject();
		
		String orderID = null;
		try {
			orderID = parameter.getString("OrderID");
		} catch (JSONException e) {
			ShntecLogger.logger
					.info("Missing required parameter: \"OrderID\" (Order ID)");
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser,
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER,
					"缺少所需的参数: \"OrderID\" (Order ID)");
		}
		if(!orderID.equals("abc")){
			return generator.toError(parser,
					ShntecErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "错误");
		}
		
		JSONObject payload = new JSONObject();
		
		return generator.toSuccess(parser, payload);
		
	}

}
