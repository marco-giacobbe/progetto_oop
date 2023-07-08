package segreto.crypto.key;

import segreto.crypto.Algorithm;

import java.security.SecureRandom;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
Classe astratta che implementa metodi ausiliari per la generazione delle
chiavi di cifratura
*/

public abstract class CryptoKeyFactory {

	// Restituisce una stringa di byte randomica
	public static byte[] generateRandomByteString(int byteSize) {
                SecureRandom sr = new SecureRandom();
		sr.setSeed(System.currentTimeMillis());
                byte[] byteString = new byte[byteSize];
                sr.nextBytes(byteString);
                return byteString;
	}

	// Restituisce una chiave simmetrica partendo dalla sua raprpesentazione in byte
	public static Key getKeyFromByte(byte[] byteKey, Algorithm algorithm) {
		return new SecretKeySpec(byteKey, algorithm.getValue());
	}

	// Restituisce una chiave asimmetrica partendo dalla sua rappresentazione in byte
	public static Key getKeyFromByte(byte[] byteKey, Algorithm algorithm, boolean isPublic) {
		try {
			KeyFactory kFactory = KeyFactory.getInstance(algorithm.getValue());
			if (isPublic) {
				return kFactory.generatePublic(new X509EncodedKeySpec(byteKey));
			} else {
				return kFactory.generatePrivate(new PKCS8EncodedKeySpec(byteKey));
			}
		} catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

}
