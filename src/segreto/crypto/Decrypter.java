package segreto.crypto;

import segreto.crypto.key.manager.*;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;


public class Decrypter extends CipherObject{

	public Decrypter (KeyManager keyManager, Algorithm algorithm) {
		super(keyManager, algorithm);
	}

	@Override
	public void setCipher(KeyManager keyManager, Algorithm algorithm) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(algorithm.toString());
                        if (algorithm.getValue() != "RSA")
                                cipher.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey(), ((SimmetricKeyManager)keyManager).getIvSpec());
                        else
                                cipher.init(Cipher.DECRYPT_MODE, keyManager.getSecretKey());
		} catch (NoSuchAlgorithmException|NoSuchPaddingException
                        |InvalidKeyException|InvalidAlgorithmParameterException e) {
			System.out.println(e.getMessage());
                }

		super.setCipher(cipher);
	}
}
