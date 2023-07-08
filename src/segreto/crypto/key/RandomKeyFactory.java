package segreto.crypto.key;

/**
Classe utilizzata per creare chiavi di cifratura randomiche
*/

public class RandomKeyFactory extends CryptoKeyFactory {

	public static byte[] getByteKey(int byteSize) {
		return generateRandomByteString(byteSize);
	}

}
