package segreto.util;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class SecretPassword extends Secret {

	private String username;
	private String password;

	public SecretPassword(String newUsername, String newPassword) {
		username = newUsername;
		password = newPassword;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return String.format("username: %s\npassword: %s", username, password);
	}

}
