package com.shntec.server.action;

import java.util.List;

import com.shntec.bp.common.RequestMessageParser;
import com.shntec.bp.common.ResponseMessageGenerator;
import com.shntec.bp.common.ShntecBaseAction;
import com.shntec.bp.common.ShntecErrorCode;
import com.shntec.bp.json.JSONArray;
import com.shntec.bp.json.JSONException;
import com.shntec.bp.json.JSONObject;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.server.model.Activity;
import com.shntec.server.service.ActivityService;

public class GetActivityListAction extends ShntecBaseAction {

	public GetActivityListAction(){
		this.actionCode = 5000;
		this.actionDescription = "";
		this.actionName = "GetActivityListAction";
	}
	
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		// TODO Auto-generated method stub
		ResponseMessageGenerator generator = new ResponseMessageGenerator();

		JSONObject parameter = parser.getParameterJsonObject();
		
		int start = -1;
		try {
			start = parameter.getInt("start");
		} catch (JSONException e) {
			ShntecLogger.logger
					.info("Missing required parameter: \"start\" (start)");
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser,
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER,
					"缺少所需的参数: \"start\" (start)");
		}
		
		int count = -1;
		try {
			count = parameter.getInt("count");
		} catch (JSONException e) {
			ShntecLogger.logger
					.info("Missing required parameter: \"count\" (count)");
			ShntecLogger.logger.error(e.getMessage());
			return generator.toError(parser,
					ShntecErrorCode.SHNTEC_ERROR_CODE_MISSING_PARAMETER,
					"缺少所需的参数: \"count\" (count)");
		}
		
		
		ActivityService service = new ActivityService();
		
		List<Activity> data = service.SelectAll(start, count);
		
		JSONObject payload = new JSONObject();
		
		JSONArray list = new JSONArray();
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				Activity item = data.get(i);
				JSONObject json = new JSONObject();
				json.put("id", item.getAct_id());
				json.put("content", item.getAct_content());
				json.put("date", item.getAct_date());
				json.put("img", item.getAct_img());
				json.put("location_des", item.getAct_location_des());
				json.put("location_gps", item.getAct_location_gps());
				json.put("name", item.getAct_name());
				
				list.put(json);
			}
			
			payload.put("list", list);
			payload.put("total", list.length());
			
		} catch (JSONException e) {
			ShntecLogger.logger.error(e.getMessage(), e);
			return generator.toError(parser,
					ShntecErrorCode.SHNTEC_ERROR_CODE_BAD_JSON,
					"构造JSON数据错误: "+e.getMessage());
		}
		
		
		return generator.toSuccess(parser, payload);
	}

}
