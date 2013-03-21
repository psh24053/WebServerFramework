package com.shntec.bp.common;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import com.shntec.bp.util.ShntecLogger;

public abstract class ShntecBaseDAO {

	protected String tableName = null;

	protected BaseSQLHelper helper = null;
	
	protected String primaryKeyName = null;
	
	protected Object primaryKeyValue = null;

	// Creator is responsible for database connection release
	public ShntecBaseDAO(BaseSQLHelper helper) {
		this.helper = helper;
	}
	
	public String getTableName () {
		return tableName;
	}

	public void setSQLHelper(BaseSQLHelper helper) {
		if (this.helper != null) {
			helper.close();
		}
		this.helper = helper;
	}

	public BaseSQLHelper getSQLHelper() {
		return helper;
	}

	public void freeSQLHelper() {
		if (helper != null) {
			helper.close();
			helper = null;
		}
	}
	
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public void setPrimaryKeyVaue(Object primaryKeyValue) {
		this.primaryKeyValue = primaryKeyValue;
	}
	
	public Object getPrimaryKeyValue() {
		return primaryKeyValue;
	}

	protected boolean executeLoadTableSQL(String sql) {
		
		ResultSet rs = helper.executeQuery(sql);

		try {
			if (rs != null && rs.next()) {
				if (!loadResultSet(rs)) {
					return false;
				}
			}
			else {
				ShntecLogger.logger.error("Import table \"" + tableName + 
						"\" from database failed, SQL statement: " + sql);
				return false;
			}
		} catch (SQLException e) {
			ShntecLogger.logger.error("Handle result set failed.");
			ShntecLogger.logger.error(e.getMessage());
			return false;
		}
		finally {
			try {
				if (null != rs) {
					rs.getStatement().close();
					rs.close();
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}

		return true;		
	}
	
	protected abstract boolean loadResultSet(ResultSet rs);
	
	public boolean fromResultSet(ResultSet rs) {
		
		return loadResultSet(rs);
		
	}
	
	// Notice, only support Long, Integer and String as primary key
	public boolean fromDatabase(Object primaryKeyValue) {
		
		this.primaryKeyValue = primaryKeyValue;
		
		String sql = generatePrimaryKeySelectSql();
		
		if (!executeLoadTableSQL(sql)){
			ShntecLogger.logger.error("Import table \"" + tableName + 
				"\" from database failed, key name =" + primaryKeyName + 
				", key value = " + primaryKeyValue);
			return false;
		}

		return true;
	}

	abstract protected HashMap <String, Object> generateColumnList();
	
	public boolean toDatabase() {
		HashMap <String, Object> columnList = generateColumnList();
		int insertedCount = helper.executeInsert(tableName, columnList);
		if (insertedCount <= 0) {
			ShntecLogger.logger.error("Save data to database failed, table name=" + tableName);
			return false;
		}

		return true;	
	}
	
	public Long toDatabaseEx(){
		
		HashMap <String, Object> columnList = generateColumnList();
		
		Long insertId = helper.executeInsertEx(tableName, columnList);
		
		if (insertId <= 0){
			ShntecLogger.logger.error("Save data to database failed, table name=" + tableName);
			return -1L;
		}
		
		return insertId;
		
	}

	public boolean deleteFromDatabase() {
		
		if ( primaryKeyValue == null) {
			return false;
		}

		String sql = generatePrimaryKeyDeleteSql();
		int deletedCount = helper.executeUpdate(sql);
		if (deletedCount <= 0) {
			ShntecLogger.logger.error("Delete table \"" + tableName + 
					"\" from database failed, key name =" + primaryKeyName + 
					", key value = " + primaryKeyValue);

			return false;				
		}
		
		return true;
	}
	
	abstract protected void updateResultSet(ResultSet rs) throws SQLException;
	
	public boolean updateToDatabase() {

		String sql = generatePrimaryKeySelectSql();
		ResultSet rs = helper.executeQuery(sql);
		
		try {
			if (rs != null && rs.next()) {
				updateResultSet(rs);
				// Update to database
				rs.updateRow();
			}
			else {
				ShntecLogger.logger.error("Update table \"" + tableName + 
						"\" to database failed, key name =" + primaryKeyName + 
						", key value = " + primaryKeyValue);
				return false;
			}
		} catch (SQLException e) {
			ShntecLogger.logger.error("Handle result set failed.");
			ShntecLogger.logger.error(e.getMessage());
			return false;
		}
		finally {
			try {
				if (null != rs) {
					rs.getStatement().close();
					rs.close();
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}

		return true;
	}
	
	protected boolean checkLongExist(String entryName, Long entryValue) {
		
		String sql = "SELECT COUNT(" + entryName + ") FROM " + tableName + 
				" WHERE " + entryName + "=" + entryValue + ";";
		
		return checkExistByCount(sql);
	}
	
	protected boolean checkStringExist(String entryName, String entryValue) {
		
		String sql = "SELECT COUNT(" + entryName + ") FROM " + tableName + 
				" WHERE " + entryName + "='" + entryValue + "';";
		
		return checkExistByCount(sql);	
	}
	
	protected boolean checkStringExistIgnoreCase(String entryName, String entryValue) {

		boolean isExist = false;
		
		String sql = "SELECT " + entryName + " FROM " + tableName + 
				" WHERE " + entryName + "='" + entryValue + "';";
		
		ResultSet rs = helper.executeQuery(sql);
		
		try {
			if (rs != null && rs.next()) {
				String entry = rs.getString(entryName);
				if (0 == entry.compareToIgnoreCase(entryValue)){
					isExist = true;
				}
			}
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
		}
		finally {
			try {
				if (null != rs) {
					rs.getStatement().close();
					rs.close();
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}

		return isExist;
	}
	
	private String generatePrimaryKeySelectSql() {
		
		String sql = "SELECT * FROM " + tableName + 
				" WHERE " + primaryKeyName + "=";
		
		if ( primaryKeyValue instanceof Integer ) {
			sql += (Integer) primaryKeyValue;
		}
		else if ( primaryKeyValue instanceof Long ) {
			sql += (Long) primaryKeyValue;
		}
		else if (primaryKeyValue instanceof String) {
			sql += "'" + (String) primaryKeyValue +"'";
		}
		else {
			return null;
		}
		
		sql += ";";
		
		return sql;
	}

	private String generatePrimaryKeyDeleteSql() {
		
		String sql = "DELETE FROM " + tableName + 
				" WHERE " + primaryKeyName + "=";
		
		if ( primaryKeyValue instanceof Integer ) {
			sql += (Integer) primaryKeyValue;
		}
		else if ( primaryKeyValue instanceof Long ) {
			sql += (Long) primaryKeyValue;
		}
		else if (primaryKeyValue instanceof String) {
			sql += "'" + (String) primaryKeyValue +"'";
		}
		else {
			return null;
		}
		
		sql += ";";
		
		return sql;
	}
	
	
	private boolean checkExistByCount(String sql) {
		
		boolean isExist = false;
		
		ResultSet rs = helper.executeQuery(sql);
		
		try {
			if (rs != null && rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					isExist = true;
				}
			}
		} catch (SQLException e) {
			ShntecLogger.logger.error(e.getMessage());
			return isExist;
		}
		finally {
			try {
				if (null != rs) {
					rs.getStatement().close();
					rs.close();
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}
		
		return isExist;
	}
	
	protected void updateAllowNullColumns(ResultSet rs, String key , Object value){
		
		if(value == null){
			try {
				rs.updateNull(key);	
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}
		}else{
			
			Class<?> classType = value.getClass();
			
			try {
				if (classType.isInstance(new String())){
					if(rs.getString(key) != null){
						if (0!= rs.getString(key).compareTo((String)value)) {
							rs.updateString(key, (String)value);
						}
					}else{
						rs.updateString(key, (String)value);
					}
				}else if (classType.isInstance(new Long(0L))){
					if (value != (Long)rs.getLong(key)) {
						rs.updateLong(key, (Long)value);
					}
				}else if (classType.isInstance(new Integer(0))){
					if (value != (Integer)rs.getInt(key)) {
						rs.updateInt(key, (Integer)value);
					}
				}else if (classType.isInstance(new Date())){
					if (value != rs.getDate(key)) {
//						rs.updateDate(key, (java.sql.Date)value);
						rs.updateTimestamp(key, new Timestamp(((Date)value).getTime()));
					}
				}
				else if (classType.isInstance(new BigDecimal(0))) {
					if (value != rs.getBigDecimal(key)) {
						rs.updateBigDecimal(key, (BigDecimal)value);
					}
				}
				else if (classType.isInstance(new Boolean(false))) {
					if ((Boolean) value != rs.getBoolean(key)) {
						rs.updateBoolean(key, (Boolean) value);
					}
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}

		}
	}
	
	protected void updateNotAllowNullColumns(ResultSet rs, String key , Object value){
		
		if(value != null){
			Class<?> classType = value.getClass();
			
			try {
				if (classType.isInstance(new String())){
					if (0!= rs.getString(key).compareTo((String)value)) {
						rs.updateString(key, (String)value);
					}
				}else if (classType.isInstance(new Long(0L))){
					if (value != (Long)rs.getLong(key)) {
						rs.updateLong(key, (Long)value);
					}
				}else if (classType.isInstance(new Integer(0))){
					if (value != (Integer)rs.getInt(key)) {
						rs.updateInt(key, (Integer)value);
					}
				}else if (classType.isInstance(new java.sql.Date(1L))){
					if (value != rs.getDate(key)) {
//						rs.updateDate(key, (java.sql.Date)value);
						rs.updateTimestamp(key, new Timestamp(((Date)value).getTime()));
					}
				}
				else if (classType.isInstance(new BigDecimal(0))) {
					if (value != rs.getBigDecimal(key)) {
						rs.updateBigDecimal(key, (BigDecimal)value);
					}
				}
				else if (classType.isInstance(new Boolean(false))) {
					if ((Boolean) value != rs.getBoolean(key)) {
						rs.updateBoolean(key, (Boolean) value);
					}
				}
			} catch (SQLException e) {
				ShntecLogger.logger.error(e.getMessage());
			}

		}
	}

	
}
