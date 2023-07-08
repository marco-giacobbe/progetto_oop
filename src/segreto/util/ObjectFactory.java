package segreto.util;

import segreto.crypto.Algorithm;
import segreto.crypto.Decrypter;
import segreto.crypto.key.manager.KeyManager;
import segreto.crypto.key.manager.SimmetricKeyManager;
import segreto.crypto.key.CryptoKeyFactory;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.Key;

import java.util.Base64;
import java.util.Arrays;

/**
Classe che restituisce Oggetti a partire da byte
*/

public class ObjectFactory {

	// ritorna un oggetto a partire dalla sua serializzazione
	public static Object getObject(byte[] bytesObject) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytesObject);
			ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		} catch (IOException|ClassNotFoundException e) {
			return null;
		}
	}

	//Ritorna l'oggetto a partire dai dati memorizzati dal server
	public static Object secretParsing(byte[] bytesObject,
					   KeyManager clientKey) {
		Decrypter clientDecrypter = new Decrypter(clientKey, Algorithm.AESCBC);
		bytesObject = clientDecrypter.start(bytesObject);
		/* inizializza gli array contenenti il segreto
		la chiave e il vettore di inizializzazione*/
		byte[] segreto = new byte[bytesObject.length-32];
                byte[] key = new byte[16];
                byte[] iv = new byte[16];
                System.arraycopy(bytesObject, 0, segreto, 0, bytesObject.length-32);
                System.arraycopy(bytesObject, bytesObject.length-32, key, 0, 16);
                System.arraycopy(bytesObject, bytesObject.length-16, iv, 0, 16);

		// inizializza il keymanager tramite la chiave e il vettore
                Key keyOfSecret = CryptoKeyFactory.getKeyFromByte(key, Algorithm.AESCBC);
                SimmetricKeyManager skm = new SimmetricKeyManager(Algorithm.AESCBC, keyOfSecret, iv);

		//decifra il segreto
                Decrypter decrypter = new Decrypter(skm, Algorithm.AESCBC);
                return (Secret) ObjectFactory.getObject(decrypter.start(segreto));
	}

}
