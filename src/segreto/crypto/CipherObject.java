package segreto.crypto;

import segreto.crypto.key.manager.KeyManager;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;

public abstract class CipherObject extends CryptoObject {

	private KeyManager keyManager;
	private Cipher cipher;

	public CipherObject (KeyManager keyManager, Algorithm algorithm) {
		this.keyManager = keyManager;
		setAlgorithm(algorithm);
	}

	public void setKeyManager(KeyManager newKeyManager) {
		keyManager = newKeyManager;
	}

	public abstract void setCipher(KeyManager keyManager, Algorithm algorithm);

	protected void setCipher(Cipher newCipher) {
		cipher = newCipher;
	}

	// impostando l'algoritmo bisogna anche modificare l'oggetto Cipher
	@Override
	public void setAlgorithm(Algorithm newAlgorithm) {
		super.setAlgorithm(newAlgorithm);
        	setCipher(keyManager, newAlgorithm);
	}

	// cifra/decifra
	public byte[] start(byte[] data) {
		byte[] returnData = null;
		try {
			returnData = cipher.doFinal(data);
		} catch (IllegalBlockSizeException|BadPaddingException e) {
			System.out.println(e.getMessage());
		} finally {
			return returnData;
		}
	}
}


