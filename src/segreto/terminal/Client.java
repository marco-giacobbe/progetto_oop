package segreto.terminal;

import segreto.util.SecretText;
import segreto.crypto.key.manager.KeyManager;
import segreto.crypto.key.manager.AsimmetricKeyManager;
import segreto.crypto.key.manager.SimmetricKeyManager;
import segreto.crypto.key.RSAKeyFactory;
import segreto.crypto.key.DerivatedDeterministicKeyFactory;
import segreto.crypto.key.CryptoKeyFactory;
import segreto.crypto.Algorithm;
import segreto.crypto.Encrypter;
import segreto.crypto.Decrypter;
import segreto.crypto.Hasher;
import segreto.util.ObjectFactory;
import segreto.message.*;
import segreto.exception.*;
import segreto.terminal.config.XMLManager;
import segreto.terminal.config.ClientConfigHandler;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.security.Key;

public class Client extends Host {

	private KeyManager clientKey;
	private int port;
	private String ip;
	private static final String CLIENTCONFIG_FILENAME = "../clientconfig.xml";

	public Client() {
		String[] data = new XMLManager(CLIENTCONFIG_FILENAME, new ClientConfigHandler()).parse();
		port = Integer.parseInt(data[0]);
		ip = data[1];
	}

	public void startConnection() {
		try {
			socket = new Socket(ip, port);
			initializeResources();
			if (checkFeedback(false)==-1)
				System.exit(1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public void stopConnection() {
		sendEncryptedBytes(new StopConnectionMessage().serialize());
	}

	public void getSessionKey() {
		/* crea coppia di chiavi e invia chiave privata
		queste chiavi serviranno solo per lo scambio della
		chiave di sessione */
		byte[][] byteKeys = RSAKeyFactory.getByteKey(256);
		Key prKey = CryptoKeyFactory.getKeyFromByte(byteKeys[0], Algorithm.RSAECB, false);
		KeyManager prKeyManager = new AsimmetricKeyManager(Algorithm.RSAECB, prKey, false);
		sendBytes(byteKeys[1]);

		// riceve la chiave di sessione e un vettore di inizializzazione
		byte[] sKey = readDecryptedBytes(prKeyManager, Algorithm.RSAECB);
		byte[] iv = readDecryptedBytes(prKeyManager, Algorithm.RSAECB);

		// crea il keymanager di sessione
		Key key = CryptoKeyFactory.getKeyFromByte(sKey, Algorithm.AESCBC);
		sessionKey = new SimmetricKeyManager(Algorithm.RSAECB, key, iv);
	}

	public void auth(boolean isLogin, String username, String password) {
		sendEncryptedBytes(new AuthMessage(isLogin, username, password).serialize());
		if (checkFeedback(true)==-1) {
			//se è partita un'eccezione
			System.exit(1);
		}
		clientKey = createClientKey(password);
		System.out.println("Accesso effettuato");
	}

	private int checkFeedback(boolean decrypterMode) {
		return checkFeedback(readBytes(), decrypterMode);
	}


	/*
	ritorna
	 -1 in caso di eccezione
	 0  in caso di ack
	 1  altrimenti
	*/
	private int checkFeedback(byte[] bytesToCheck, boolean decrypterMode) {
         	try {
			Object obj;
			if (decrypterMode) {
				obj = ObjectFactory.getObject(decrypt(bytesToCheck));
			} else {
				obj = ObjectFactory.getObject(bytesToCheck);
			}
			if (obj instanceof CSException)
				throw (CSException)obj;
			if (obj instanceof AckMessage)
				return 0;
			return 1;
                } catch (TimeoutException e) {
			System.out.println(e.getMessage()+". Riprova");
			System.exit(1);
			return -1;
		} catch (CSException e) {
                        System.out.println(e.getMessage());
                        return -1;
                }
	}

	private SimmetricKeyManager createClientKey(String password) {
		// il KeyManager del client presenta chiave e vettore di spec derivanti dalla password
		byte[] key = DerivatedDeterministicKeyFactory.getByteKey(16, password);
		Hasher hasher = new Hasher(Algorithm.MD5);
		byte[] iv = hasher.getHash(password.getBytes());
		Key clientKey = CryptoKeyFactory.getKeyFromByte(key, Algorithm.AESCBC);
		return new SimmetricKeyManager(Algorithm.AESCBC, clientKey, iv);
	}

	//crea un messaggio di inserimento e verifica l'esito dell'operazione
	public void loadSecret(String id, byte[] secret) {
		sendEncryptedBytes(new InsertMessage(id, secret, clientKey).serialize());
		if (checkFeedback(true)==0)
			System.out.println("Segreto caricato");
	}

	public Object querySecret(String id) {
		// crea e manda il messaggio di query
		sendEncryptedBytes(new RequestMessage(1, id).serialize());

		// decifra i dati ed effettua il parsing ottenendo l'oggetto
		byte[] secret = readBytes();
		if (checkFeedback(secret, true)==1)
			return ObjectFactory.secretParsing(decrypt(secret), clientKey);
		return null;

	}

	// crea un messaggio di rimozione e verifica l'esito dell'operazione
	public void removeSecret(String id) {
		sendEncryptedBytes(new RequestMessage(2,id).serialize());
                if (checkFeedback(true)==0) {
			System.out.println("Segreto rimosso");
		}
	}

}
