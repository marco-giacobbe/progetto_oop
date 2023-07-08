package segreto.crypto.key.manager;

import segreto.crypto.Algorithm;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
Un SimmetricKeyManager conserva anche il vettore di inizializzazione
usato per la cifratura
*/

public class SimmetricKeyManager extends KeyManager{

	private IvParameterSpec iv;

	public SimmetricKeyManager(Algorithm cipherAlgorithm, Key secretKey, byte[] iv) {
		super(cipherAlgorithm, secretKey);
		this.iv = new IvParameterSpec(iv);
	}

	//usato quando si vuole inizializzare ivSpec in un secondo momento
	public SimmetricKeyManager(Algorithm cipherAlgorithm, Key secretKey) {
		super(cipherAlgorithm, secretKey);
	}

	public void setIv(byte[] iv) {
		this.iv = new IvParameterSpec(iv);
	}

	public IvParameterSpec getIvSpec() {
		return iv;
	}


}
