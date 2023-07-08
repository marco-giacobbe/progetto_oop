package segreto.crypto.key;

import segreto.crypto.Algorithm;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
Classe usata per creare chiavi di cifratura derivandole da una stringa di caratteri
Salt generato casualmente, la chiave non sarà sempre la stessa a parità di stringa
*/

public class DerivatedKeyFactory extends CryptoKeyFactory {

	public static byte[] getByteKey(int byteSize, String password) {
		byte[] salt = generateRandomByteString(byteSize*8);
		return getByteKey(byteSize, password, salt);
	}

	protected static byte[] getByteKey(int byteSize, String password, byte[] salt) {
                KeySpec kSpec = new PBEKeySpec(password.toCharArray(), salt, 100000, byteSize*8);
                try {
                        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2withHmacSHA256");
                        return skf.generateSecret(kSpec).getEncoded();
                } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

}
