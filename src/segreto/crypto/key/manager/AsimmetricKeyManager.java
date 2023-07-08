package segreto.crypto.key.manager;

import segreto.crypto.Algorithm;
import java.security.Key;

/**
Un AsimmetricKeyManager può essere usato per gestire chiavi
pubbliche o private
Si usa un booleano per discriminarle
*/

public class AsimmetricKeyManager extends KeyManager {

	private boolean isPublic;

	public AsimmetricKeyManager(Algorithm cipherAlgorithm, Key secretKey, boolean mode) {
		super(cipherAlgorithm, secretKey);
		isPublic = mode;
	}

	public boolean isPublic() {
		return isPublic;
	}

}
