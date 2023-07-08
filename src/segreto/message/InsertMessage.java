package segreto.message;

import segreto.crypto.Hasher;
import segreto.crypto.key.CryptoKeyFactory;
import segreto.crypto.key.RandomKeyFactory;
import segreto.crypto.key.DerivatedKeyFactory;
import segreto.crypto.key.manager.KeyManager;
import segreto.crypto.key.manager.SimmetricKeyManager;
import segreto.crypto.Algorithm;
import segreto.crypto.Encrypter;
import java.security.Key;

import java.util.Base64;

public class InsertMessage extends RequestMessage {

	private byte[] data;

	public InsertMessage(String id, byte[] secret, KeyManager clientKey) {
		super(0, id);

		// crea chiave del segreto e ivSpec
		byte[] key, ivSpec;
		key = RandomKeyFactory.getByteKey(16);
		ivSpec = RandomKeyFactory.getByteKey(16);
		SimmetricKeyManager keyOfSecret = createKeyOfSecret(key, ivSpec);

		// cifra segreto con chiave del segreto
		byte[] encryptedSecret = encrypt(secret, keyOfSecret, Algorithm.AESCBC);

		// unisce secret+key+ivSpec in data
		data = new byte[encryptedSecret.length+32];
		System.arraycopy(encryptedSecret, 0, data, 0, encryptedSecret.length);
		System.arraycopy(key, 0, data, encryptedSecret.length, 16);
		System.arraycopy(ivSpec, 0, data, encryptedSecret.length+16, 16);
		// cifra i dati con la chiave personale
		data = encrypt(data, clientKey, Algorithm.AESCBC);
	}

	public byte[] getData() {
		return data;
	}

	private SimmetricKeyManager createKeyOfSecret(byte[] key, byte[] ivSpec) {
		Key keyOfSecret = CryptoKeyFactory.getKeyFromByte(key, Algorithm.AESCBC);
		return new SimmetricKeyManager(Algorithm.AESCBC, keyOfSecret, ivSpec);
	}

	private byte[] encrypt(byte[] data, KeyManager keyManager, Algorithm algorithm) {
		Encrypter encrypter = new Encrypter(keyManager, algorithm);
		return encrypter.start(data);
	}

	public String toString() {
		return Base64.getEncoder().encodeToString(data);
	}

}
