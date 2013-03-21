/**
 * 
 */
package com.shntec.bp.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import com.shntec.bp.util.ShntecLogger;

/**
 * @author 1
 *
 */
public abstract class BaseSQLHelper {

	private ResultSet resultSet = null;
	
	protected Connection conn = null;
	
	private boolean isAutoCommit = true;
	
	public BaseSQLHelper() {
		setAutoCommit(true);
	}

	public BaseSQLHelper(boolean autoCommit) {
		setAutoCommit(autoCommit);
	}

	public abstract void close();
	
	// Execute select statement
	public ResultSet executeQuery(String sql, Object... args) {

		PreparedStatement statement;
		
		try {
			statement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		
			for (int i=0; i<args.length; ++i) {
				statement.setObject(i+1, args[i]);
			}
			
			resultSet = statement.executeQuery();
						
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}

		return resultSet;
	}
	
	//Execute update, insert and delete statement
	public int executeUpdate(String sql, Object... args) {
		
		int updatedCount = -1;
		
		PreparedStatement statement = null;

		try {
			statement = conn.prepareStatement(sql);

			for (int i=0; i<args.length; ++i) {
				statement.setObject(i+1, args[i]);
			}
			
			updatedCount = statement.executeUpdate();
			
			statement.close();
						
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
			return -1;
		}

		return updatedCount;
	}
	
	// Only for insert statement
	public int executeInsert(String tableName, HashMap <String, Object> columns) {

		int updatedCount = -1;

		LinkedList<Object> columnValueList = new LinkedList<Object>();

		String sqlStatement = "INSERT INTO ";
		String colomnString = " (";
		String valuesString = " VALUES (";
		
		PreparedStatement statement = null;

		try {
			int i = 0;			
			for (Object object: columns.keySet()) {
				String columnName = ((String)object);
				Object columnValue = columns.get(object);
				colomnString +=  columnName;
				valuesString += "?";
				columnValueList.addLast(columnValue);
				if (i++<columns.size()-1) {
					colomnString += ",";
					valuesString += ",";
				}
				else {
					colomnString += ") ";	
					valuesString += ") ";
				}
			}
			
			sqlStatement += tableName + colomnString + valuesString; 
			
			statement = conn.prepareStatement(sqlStatement);
			for (int j=0; j<columnValueList.size();++j){
				statement.setObject(j+1, columnValueList.get(j));
			}

			updatedCount = statement.executeUpdate();
			
			statement.close();
			
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
			return -1;
		}
		
		return updatedCount;
	}
	/*
	 * @return  Integer  ID of new inserted raw
	 */
	public Long executeInsertEx(String tableName, HashMap <String, Object> columns) {
		
		Long insertID = 0L;
		int updatedCount = -1;

		LinkedList<Object> columnValueList = new LinkedList<Object>();

		String sqlStatement = "INSERT INTO ";
		String colomnString = " (";
		String valuesString = " VALUES (";
		
		PreparedStatement statement = null;

		try {
			int i = 0;			
			for (Object object: columns.keySet()) {
				String columnName = ((String)object);
				Object columnValue = columns.get(object);
				colomnString +=  columnName;
				valuesString += "?";
				columnValueList.addLast(columnValue);
				if (i++<columns.size()-1) {
					colomnString += ",";
					valuesString += ",";
				}
				else {
					colomnString += ") ";	
					valuesString += ") ";
				}
			}
			
			sqlStatement += tableName + colomnString + valuesString; 
			
			statement = conn.prepareStatement(sqlStatement);
			for (int j=0; j<columnValueList.size();++j){
				statement.setObject(j+1, columnValueList.get(j));
			}

			updatedCount = statement.executeUpdate();
			
			statement.close();
			
			if (updatedCount < 0 ){
				return 0L;
			}
			
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
			return 0L;
		}

		// Get ID of insert new raw
		try {
			
			String querySqlStatement = "SELECT @@IDENTITY;";			
			PreparedStatement queryStatement = conn.prepareStatement(querySqlStatement);
			ResultSet queryResult = queryStatement.executeQuery();
			if (null != queryResult && queryResult.next()) {
				insertID = queryResult.getLong(1);
			}
			queryStatement.close();
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
		}
		
		return insertID;
	}

	public void setAutoCommit(boolean autoCommit) {
		if (conn != null) {
			try {
				if (conn.getAutoCommit() != autoCommit) {
					conn.setAutoCommit(autoCommit);
					this.isAutoCommit = autoCommit;
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}
	}
	
	public void enableTransaction() {
		if (isAutoCommit) {
			setAutoCommit(false);
		}
	}
	
	public void disableTransaction() {
		if (!isAutoCommit) {
			setAutoCommit(true);
		}
		
	}
	
	public void cancelTransaction() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
		}
	}
	
	public void commitTransaction() {
		try {
			conn.commit();
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
		}
	}
	
}
