package segreto.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hasher extends CryptoObject {

	public Hasher(Algorithm algorithm) {
		setAlgorithm(algorithm);
	}

	public byte[] getHash (byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance(getAlgorithm().getValue());
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public String getStringedHash(byte[] data) {
		return Base64.getEncoder().encodeToString(getHash(data));
	}

}
