package segreto.terminal.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;

/**
Estende l'interfaccia base secondo le specifiche dell'applicazione
*/

public class SpecDBManager extends DBManager {

	private String database;

	public SpecDBManager(String url, String username, String password,
				String driver, String database, boolean isTest) {
		super(url, username, password, driver);
		this.database = database;
		try {
			if (isTest) {
				closeConnector();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void initializeDB() {
		try {
			openStatement();
                        if(!databaseExists("Segreto")) {
                                createDatabase("Segreto");
                        }
                        useDatabase("Segreto");
                        if(!tableExists("Data", "Segreto")) {
                                createTable("Data", "id VARCHAR(300), username VARCHAR(255), data LONGBLOB, " +
						"PRIMARY KEY (id, username)");
                        }
                        if(!tableExists("Account", "Segreto")) {
                                createTable("Account", "username VARCHAR(255), password VARCHAR(300)");
                        }
		} catch (SQLException e) {
                        System.out.println(e.getMessage());
                }

	}

	public void closeDB() {
		try {
			closeStatement();
			closeConnector();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public int isUsernameAlreadyExists(String username) {
		try {
			boolean exists = isIDAlreadyExists(selectByName("Account", String.format("username='%s'", username)));
			return (exists) ? 1:0;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}

	public int isSecretAlreadyExists(String username, String id) {
		try {
			boolean exists =  isIDAlreadyExists(selectByName("Data",
				String.format("id='%s' AND username='%s'", id, username)));
			return (exists) ? 1:0;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}

	}

	public boolean insertAccount(String username, String password) {
		Object[] values = {username, password};
		try {
			insertInto("Account", values);
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean insertSecret(String id, String username, byte[] data) {
		Object[] values = {id, username, data};
		try {
			insertInto("Data", values);
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean deleteSecret(String id, String username) {
		try {
			delete("Data", String.format("id='%s' AND username='%s'",id,username));
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	public String getPassword(String username) {
		try {
			ResultSet rs = selectByName("Account", String.format("username='%s'", username));
			return (String)getValueFromRS(rs, 1, "password");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public byte[] getData(String username, String id) {
		try {
			ResultSet rs = selectByName("Data", String.format("id='%s' AND username='%s'",id, username));
			return (byte[])getValueFromRS(rs, 1, "data");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}
