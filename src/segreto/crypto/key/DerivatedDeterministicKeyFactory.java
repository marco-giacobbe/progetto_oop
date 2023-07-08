package segreto.crypto.key;

import segreto.crypto.Hasher;
import segreto.crypto.Algorithm;
import java.security.MessageDigest;

/**
Classe usata per creare chiavi di cifratura derivandole da una stringa di caratteri
Salt generato a partire dalla stringa, la chiave sarà sempre la stessa
*/


public class DerivatedDeterministicKeyFactory extends DerivatedKeyFactory {

	public static byte[] getByteKey(int byteSize, String password) {
		byte [] hash = new Hasher(Algorithm.MD5).getHash(password.getBytes());
		return getByteKey(byteSize, password, hash);
	}
}
