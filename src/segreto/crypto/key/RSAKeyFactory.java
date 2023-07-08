package segreto.crypto.key;

import segreto.crypto.Algorithm;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
Classe usata per generare chiavi di cifratura asimmetriche
*/

public class RSAKeyFactory extends CryptoKeyFactory{

	public static byte[][] getByteKey (int byteSize) {
		KeyPairGenerator kpg = null;
		Algorithm algorithm = Algorithm.RSAECB;
		try {
			kpg = KeyPairGenerator.getInstance(algorithm.getValue());
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
		SecureRandom sr = new SecureRandom();
		sr.setSeed(generateRandomByteString(byteSize));
		kpg.initialize(byteSize*8, sr);
		KeyPair keys = kpg.generateKeyPair();
		return new byte[][] {keys.getPrivate().getEncoded(),
				keys.getPublic().getEncoded()};
	}

}
