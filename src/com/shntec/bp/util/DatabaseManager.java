package com.shntec.bp.util;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseManager {
	
	private static DatabaseManager instance = null;
	
	private ComboPooledDataSource ds = null;

	private String databaseUrl = null;
	
	private String databaseUsername = null;
	
	private String databasePassword = null;
	
	private String driverClassName = "com.mysql.jdbc.Driver";
	
	//private LinkedList<Connection> connPool = null;
	
	private int initConnCount = 2;
	
	private int maxConnCount = 10;
	
	private int maxIdleTime = 60;
	
	private Integer currentConnectionCount = 0;
	
	static public synchronized DatabaseManager getInstance () {
		if (null == instance) {
			instance = new DatabaseManager();
		}
		return instance;
	}
	
	private DatabaseManager() {
		
		ShntecConfigManager configManager = ShntecConfigManager.getInstance();

		databaseUrl = configManager.databaseUrl;
		databaseUsername = configManager.databaseUsername;
		databasePassword = configManager.databasePassword;
		
		ds = new ComboPooledDataSource();
		try {
			ds.setDriverClass(driverClassName);
		} catch (PropertyVetoException e) {
			ShntecLogger.logger.error("Initialize database driver failed.");
			ShntecLogger.logger.error(e.getMessage());
		}
		
		ds.setJdbcUrl(databaseUrl);
		ds.setUser(databaseUsername);
		ds.setPassword(databasePassword);
		ds.setMaxPoolSize(maxConnCount);
		ds.setInitialPoolSize(initConnCount);
		ds.setMaxIdleTime(maxIdleTime);
		
	}
	
	public String getConfigInfo() {
		
		String text = "";
		
		text += "Database URL: " + databaseUrl + "\r\n";
		text += "Database Username: " + databaseUsername + "\r\n";
		text += "Database Password: " + databasePassword + "\r\n";
		
		return text;
	}
	
	public Connection getConnection() {
		
		Connection conn = null;
		
		try {
			//conn = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
			conn = ds.getConnection();
			synchronized (currentConnectionCount) {
				currentConnectionCount++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public synchronized void releaseConnection(Connection conn) {
		
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
				synchronized (currentConnectionCount) {
					currentConnectionCount--;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeDS() {
		if(ds != null) {
			ds.close();
			ds = null;
		}
		
	}
	
	public synchronized void resetDS() {
		if (null != instance) {
			closeDS();
			synchronized (currentConnectionCount) {
				currentConnectionCount = 0;
			}			
			instance = null;
		}
	}
	
	public int getCurrentConnectionCount() {
		
		int currentCount = 0;
		
		synchronized (currentConnectionCount) {
			
			currentCount = currentConnectionCount;
		
		}
		
		return currentCount;
	}
}
