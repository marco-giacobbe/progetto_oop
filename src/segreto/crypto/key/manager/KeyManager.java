package segreto.crypto.key.manager;

import segreto.crypto.Algorithm;
import java.security.Key;

/**
Un KeyManager conserva la chiave di cifratura e l'algoritmo per
il quale viene usata
*/

public abstract class KeyManager {

	private Key key;
	private Algorithm cipherAlgorithm;

	public KeyManager(Algorithm cipherAlgorithm, Key secretKey) {
		this.cipherAlgorithm = cipherAlgorithm;
		key = secretKey;
	}

	public Key getSecretKey() {
		return key;
	}

	protected void setKey(Key newKey) {
		key = newKey;
	}

}
