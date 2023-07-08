package segreto.message;

import segreto.crypto.Hasher;
import segreto.crypto.Algorithm;

public class AuthMessage extends Message {

	private boolean isLogin;
	private String username;
	private String password;

	public AuthMessage(boolean isLogin, String username, String password) {
		super(3);
		Hasher hasher = new Hasher(Algorithm.SHA256);
		this.isLogin = isLogin;
		this.username = username;
		this.password = hasher.getStringedHash(password.getBytes());
	}

	public boolean isLogin() {
		return isLogin;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
