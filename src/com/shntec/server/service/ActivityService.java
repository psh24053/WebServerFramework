package com.shntec.server.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shntec.bp.impl.ShntecSQLHelper;
import com.shntec.bp.util.ShntecLogger;
import com.shntec.server.model.Activity;

public class ActivityService {

	/**
	 * 返回所有activity
	 * @return
	 */
	public List<Activity> SelectAll(int start, int count){
		
		ShntecSQLHelper helper = new ShntecSQLHelper();
		ResultSet rs = null;
		List<Activity> data = null;
		
		try {
			data = new ArrayList<Activity>();
			Object[] args = {start,count};
			rs = helper.executeQuery("SELECT * FROM mi_activity ORDER BY act_date DESC LIMIT ?,?",args);
			while(rs.next()){
				Activity item = new Activity();
				item.setAct_content(rs.getString("act_content"));
				item.setAct_date(rs.getString("act_date"));
				item.setAct_id(rs.getInt("act_id"));
				item.setAct_img(rs.getString("act_img"));
				item.setAct_location_des(rs.getString("act_location_des"));
				item.setAct_location_gps(rs.getString("act_location_gps"));
				item.setAct_name(rs.getString("act_name"));
				
				data.add(item);
				
			}
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage(), e);
		} finally{
			helper.close();
		}
		
		
		return data;
	}
	
	
}
