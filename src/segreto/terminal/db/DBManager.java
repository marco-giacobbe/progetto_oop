package segreto.terminal.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
Interfaccia base per lavorare su un database
*/

public abstract class DBManager {

	private Statement statement;
	private Connection connector;

	public DBManager(String url, String username, String password, String driver) {
		try {
			Class.forName(driver);
			connector = DriverManager.getConnection(url, username, password);
		} catch (SQLException|ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		initializeDB();
	}

	public abstract void initializeDB();

	public void openStatement() throws SQLException {
		statement = connector.createStatement();
	}

	public void closeStatement() throws SQLException {
		statement.close();
	}

	public void closeConnector() throws SQLException {
		connector.close();
	}

	public void createDatabase(String databaseName) throws SQLException {
		statement.execute(String.format("CREATE DATABASE %s", databaseName));
	}

	public boolean databaseExists(String databaseName) throws SQLException {
		ResultSet rs = connector.getMetaData().getCatalogs();
		while (rs.next()) {
			if (rs.getString(1).equalsIgnoreCase(databaseName)) {
				return true;
			}
		}
		return false;
	}

	public void useDatabase(String databaseName) throws SQLException {
		statement.execute(String.format("USE %s", databaseName));
	}

	public void createTable(String tableName, String tableBody) throws SQLException {
		statement.execute(String.format("CREATE TABLE %s (%s);", tableName, tableBody));
	}

	public boolean tableExists(String tableName, String catalogName) throws SQLException {
		return connector.getMetaData().getTables(catalogName, null, tableName, null).next();
	}

	public void insertInto(String tableName, Object[] values) throws SQLException {
		String valuesString = "?";
		for (int i=1;i<values.length;i++) {
			valuesString = valuesString + ", ?";
		}
		String sql = String.format("INSERT INTO %s VALUES (%s)", tableName, valuesString);
		PreparedStatement stat = connector.prepareStatement(sql);
		for (int i=0;i<values.length;i++) {
			if (values[i] instanceof byte[]) {
				stat.setBytes(i+1, (byte[])values[i]);
			} else {
				stat.setString(i+1, (String)values[i]);
			}
		}
		stat.executeUpdate();
		stat.close();
	}

	public void delete(String tableName, String whereBody) throws SQLException {
		statement.execute(String.format("DELETE FROM %s WHERE %s", tableName, whereBody));
	}

	public boolean isIDAlreadyExists(ResultSet rs) throws SQLException {
		return rs.next();
	}

	public ResultSet selectByName(String tableName, String whereBody) throws SQLException {
		if (whereBody != null)
			return statement.executeQuery(String.format("SELECT * FROM %s WHERE %s", tableName, whereBody));
		else
			return statement.executeQuery(String.format("SELECT * FROM %s", tableName));
	}

	public Object getValueFromRS(ResultSet rs, int row, String column) throws SQLException{
		for (;row>0 && rs.next();row--);
		return rs.getObject(column);
	}

}


