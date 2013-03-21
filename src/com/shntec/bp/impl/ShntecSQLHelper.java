package com.shntec.bp.impl;

import java.sql.BatchUpdateException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shntec.bp.common.BaseSQLHelper;
import com.shntec.bp.util.DatabaseManager;
import com.shntec.bp.util.ShntecLogger;

public class ShntecSQLHelper extends BaseSQLHelper {

	public ShntecSQLHelper() {
		super.conn = DatabaseManager.getInstance().getConnection();
	}

	public ShntecSQLHelper(boolean autoCommit) {
		super.conn = DatabaseManager.getInstance().getConnection();
		setAutoCommit(autoCommit);
	}

	@Override
	public void close() {
		if (null != conn)
		{
			DatabaseManager.getInstance().releaseConnection(conn);
			conn = null;
		}
	}

	/**
	 * Executes the SQL statement in this <code>PreparedStatement</code> object,
	 * which must be an SQL Data Manipulation Language (DML) statement, such as
	 * <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code>; or an
	 * SQL statement that returns nothing, such as a DDL statement.
	 * 
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 */
	public int updateClob(String sql, String clobString) {
		PreparedStatement statement;
		try {
			// Clob clobObject = conn.createClob();
			// clobObject.setString(1, clobString);
			statement = conn.prepareStatement(sql);
			statement.setString(1, clobString);
			return statement.executeUpdate();
		} catch (SQLException e) {
			ShntecLogger.logger.error(this, e);
			ShntecLogger.logger.error(e.getMessage());
			return -1;
		}
	}

	/**
	 * Submits a batch of commands to the database for execution and if all
	 * commands execute successfully, returns an array of update counts. The
	 * <code>int</code> elements of the array that is returned are ordered to
	 * correspond to the commands in the batch, which are ordered according to
	 * the order in which they were added to the batch. The elements in the
	 * array returned by the method <code>executeBatch</code> may be one of the
	 * following:
	 * <OL>
	 * <LI>A number greater than or equal to zero -- indicates that the command
	 * was processed successfully and is an update count giving the number of
	 * rows in the database that were affected by the command's execution
	 * <LI>A value of <code>SUCCESS_NO_INFO</code> -- indicates that the command
	 * was processed successfully but that the number of rows affected is
	 * unknown
	 * <P>
	 * If one of the commands in a batch update fails to execute properly, this
	 * method throws a <code>BatchUpdateException</code>, and a JDBC driver may
	 * or may not continue to process the remaining commands in the batch.
	 * However, the driver's behavior must be consistent with a particular DBMS,
	 * either always continuing to process commands or never continuing to
	 * process commands. If the driver continues processing after a failure, the
	 * array returned by the method
	 * <code>BatchUpdateException.getUpdateCounts</code> will contain as many
	 * elements as there are commands in the batch, and at least one of the
	 * elements will be the following:
	 * <P>
	 * <LI>A value of <code>EXECUTE_FAILED</code> -- indicates that the command
	 * failed to execute successfully and occurs only if a driver continues to
	 * process commands after a command fails
	 * </OL>
	 * <P>
	 * The possible implementations and return values have been modified in the
	 * Java 2 SDK, Standard Edition, version 1.3 to accommodate the option of
	 * continuing to proccess commands in a batch update after a
	 * <code>BatchUpdateException</code> obejct has been thrown.
	 * 
	 * @return an array of update counts containing one element for each command
	 *         in the batch. The elements of the array are ordered according to
	 *         the order in which commands were added to the batch.
	 * @exception SQLException
	 *                if a database access error occurs, this method is called
	 *                on a closed <code>Statement</code> or the driver does not
	 *                support batch statements. Throws
	 *                {@link BatchUpdateException} (a subclass of
	 *                <code>SQLException</code>) if one of the commands sent to
	 *                the database fails to execute properly or attempts to
	 *                return a result set.
	 * 
	 * 
	 * @see #addBatch
	 * @see DatabaseMetaData#supportsBatchUpdates
	 */
	public List<Integer> executeBulkUpdate(String sql,
			List<List<Object>> argsList) {
		List<Integer> list = new ArrayList<Integer>();
		PreparedStatement statement = null;

		try {

			statement = conn.prepareStatement(sql);
			int batchCount = 0;

			int batchNumber = argsList.size();
			for (int i = 0; i < batchNumber; i++) {
				List<Object> args = argsList.get(i);
				int argsNumberForEachSQL = args.size();
				for (int j = 0; j < argsNumberForEachSQL; j++) {
					statement.setObject(j + 1, args.get(j));
				}
				statement.addBatch();
				batchCount++;
				if (batchCount % 1000 == 0) {
					batchCount = 0;
					int[] tmpResultList = statement.executeBatch();
					for (int result : tmpResultList) {
						list.add(result);
					}
				}
			}

			int[] tmpResultList = statement.executeBatch();
			for (int result : tmpResultList) {
				list.add(result);
			}

			return list;

		} catch (Exception e) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ShntecLogger.logger.error(this, ex);
					ShntecLogger.logger.error(ex.getMessage());
				}
			}
			ShntecLogger.logger.error(this, e);
			ShntecLogger.logger.error(e.getMessage());
		}
		return null;

	}

}
