package com.shntec.bp.action;

import java.util.Iterator;
import java.util.LinkedList;

import com.shntec.bp.common.JSONMessageConstant;
import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecActionHandler;
import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.json.JSONArray;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;

public class GetActionListAction extends ShntecBaseAction {

	public GetActionListAction() {
		super.actionCode = JSONMessageConstant.ACTION_CODE_GET_ACTION_LIST;
		super.actionName = JSONMessageConstant.ACTION_NAME_GET_ACTION_LIST;
		super.actionDescription = JSONMessageConstant.ACTION_DESCRIPTION_GET_ACTION_LIST;
		// No need authentication
		super.needAuthenticationCheck = false;
	}

	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		
		ShntecActionHandler handler = ShntecActionHandler.getInstance();

		LinkedList<ShntecBaseAction> actionList = handler.getActionList();
		
		JSONArray actionArray = new JSONArray();
		Iterator<ShntecBaseAction> iter = actionList.iterator();
		
		while (iter.hasNext()) {
			ShntecBaseAction actionObject = iter.next();
			JSONArray action = new JSONArray();
			action.put(actionObject.getActionCode());
			action.put(actionObject.getActionName());
			action.put(actionObject.getActionDescription());
			actionArray.put(action);
		}
		
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		JSONObject payload = new JSONObject();
		try {
			payload.put("ActionList", actionArray);
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
