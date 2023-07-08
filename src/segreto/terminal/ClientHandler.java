package segreto.terminal;

import segreto.crypto.Algorithm;
import segreto.crypto.key.manager.*;
import segreto.crypto.key.CryptoKeyFactory;
import segreto.crypto.key.RandomKeyFactory;
import segreto.util.ObjectFactory;
import segreto.message.*;
import segreto.terminal.db.SpecDBManager;
import segreto.exception.*;
import java.security.Key;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

public class ClientHandler extends Host implements Runnable {

	private Semaphore semaphore;
	private boolean running;
	private SpecDBManager db;
	private String clientName;
	private ScheduledExecutorService exService;
	private ScheduledFuture<?> scheduledTask;
	private static final int KEEPALIVE_TIMEOUT = 10;

	public ClientHandler(Socket socket, Semaphore semaphore, String url, String username, String password,
				String driver, String database) {
		running = true;
		this.socket = socket;
		this.semaphore = semaphore;
		this.db = new SpecDBManager(url, username, password, driver, database, false);
		exService = Executors.newSingleThreadScheduledExecutor();
		initializeResources();
	}

	@Override
	public void run() {
		tryNewConnection();
		if (!running) {
			return;
		}
		sendSessionKey();
		auth();
		while (isRunning())
			getRequest();
	}

	// se running==false chiude la connessione
	private boolean isRunning() {
		if (!running) {
			closeConnection();
			return false;
		} else {
			return true;
		}
	}

	// verifica se il client si può connettere e manda un feedback
	private void tryNewConnection() {
		try {
			if (!semaphore.tryAcquire()) {
				sendAndThrow(new TooMuchConnectionException(), false);
			}
			sendAck(false);
		} catch (CSException e) {
			System.out.println(e.getMessage());
			running = false;
		}
	}

	private void closeConnection() {
		try {
			socket.close();
			stopTimer();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			semaphore.release();
		}
	}

	private void sendAck(boolean encrypterMode) {
		if (encrypterMode)
			sendEncryptedBytes(new AckMessage().serialize());
		else
			sendBytes(new AckMessage().serialize());
	}

	private void sendSessionKey() {
		// riceve chiave pubblica
		AsimmetricKeyManager pubKey = getPublicKey();

		// crea e invia la chiave di sessione
		sendEncryptedBytes(createSessionKey(), pubKey, Algorithm.RSAECB);

		// crea e invia un vettore di inizializzazione
		sendEncryptedBytes(createIv(), pubKey, Algorithm.RSAECB);
	}

	private AsimmetricKeyManager getPublicKey() {
		Key key = CryptoKeyFactory.getKeyFromByte(readBytes(), Algorithm.RSAECB, true);
		return new AsimmetricKeyManager(Algorithm.RSAECB, key, true);
	}

	private byte[] createSessionKey() {
		byte[] sKey = RandomKeyFactory.getByteKey(16);
		Key key = CryptoKeyFactory.getKeyFromByte(sKey, Algorithm.AESCBC);
		sessionKey = new SimmetricKeyManager(Algorithm.AESCBC, key);
		return sKey;
	}

	private byte[] createIv() {
		byte[] iv = CryptoKeyFactory.generateRandomByteString(16);
		sessionKey.setIv(iv);
		return iv;
	}

	private void auth() {
		startTimer(KEEPALIVE_TIMEOUT);
		byte[] rcvmsg = readDecryptedBytes();
		if (rcvmsg == null) {
			return;
		}
		AuthMessage msg = (AuthMessage)ObjectFactory.getObject(rcvmsg);
		if (!msg.isLogin()) {
			// l'utente vuole registrarsi
			createNewAccount(msg.getUsername(), msg.getPassword());
		} else {
			// l'utente vuole accedere
			login(msg.getUsername(), msg.getPassword());
		}
	}

	private void createNewAccount(String username, String password) {
		try {
			if (db.isUsernameAlreadyExists(username)==1) {
				sendAndThrow(new AccountAlreadyExistsException(), true);
			}
			if (!db.insertAccount(username, password)) {
				sendAndThrow(new ServerSideException(), true);
			}
			clientName = username;
			System.out.println("Registrazione avvenuta con successo");
			sendAck(true);
		} catch (CSException e) {
			running = false;
			System.out.println(e.getMessage());
		}
	}

	private void login (String username, String password) {
		try {
			if (db.isUsernameAlreadyExists(username)==0 ||
				!password.equals(db.getPassword(username))) {
				sendAndThrow(new BadSigninDataException(), true);
			}
			clientName = username;
			System.out.printf("%s ha effettuato l'accesso\n", clientName);
			sendAck(true);
		} catch (CSException e) {
			running = false;
			System.out.println(e.getMessage());
		}
	}

	private void getRequest() {
		startTimer(KEEPALIVE_TIMEOUT);
		// riceve una richiesta dal client e chiama l'opportuno metodo per la gestione
		byte[] rcvmsg = readDecryptedBytes();
		if (rcvmsg == null)
			return;
		Message msg = (Message)ObjectFactory.getObject(rcvmsg);
		if (msg.getType() == 0) {
			insertSecret(((InsertMessage)msg).getId(), ((InsertMessage)msg).getData());
		} else if (msg.getType() == 1) {
			makeQuery(((RequestMessage)msg).getId());
		} else if (msg.getType() == 2) {
			removeSecret(((RequestMessage)msg).getId());
		} else if (msg.getType() == 4) {
			System.out.printf("%s si è disconnesso\n", clientName);
			running = false;
		}
	}

	private void insertSecret(String id, byte[] data) {
		try {
			if (db.isSecretAlreadyExists(clientName, id) == 1) {
				sendAndThrow(new SecretAlreadyExistsException(), true);
			}
			if (!db.insertSecret(id, clientName, data)) {
				sendAndThrow(new ServerSideException(), true);
			}
			System.out.println("Nuovo segreto memorizzato");
			sendAck(true);
		} catch (CSException e) {
			System.out.println(e.getMessage());
		}
	}

	private void removeSecret(String id) {
		try {
			if (db.isSecretAlreadyExists(clientName, id) == 0) {
				sendAndThrow(new SecretNotFoundException(), true);
			}
			if (!db.deleteSecret(id, clientName)) {
				sendAndThrow(new ServerSideException(), true);
			}
			System.out.println("Segreto rimosso");
			sendAck(true);
		} catch (CSException e) {
			System.out.println(e.getMessage());
		}
	}

	private void makeQuery(String id) {
		try {
			if (db.isSecretAlreadyExists(clientName, id) == 0) {
				sendAndThrow(new SecretNotFoundException(), true);
			}
			byte[] data = db.getData(clientName, id);
			if (data == null) {
				sendAndThrow(new ServerSideException(), true);
			}
			sendEncryptedBytes(data);
			System.out.println("Segreto inviato");
		} catch (CSException e) {
			System.out.println(e.getMessage());
		}
	}

	//timer di keepalive
	private void startTimer(int seconds) {
		if (scheduledTask != null)
			scheduledTask.cancel(true);
		Runnable task = () -> {
			try {
				sendAndThrow(new TimeoutException(), true);
			} catch (CSException e) {
				System.out.println(e.getMessage());
				running = false;
				closeConnection();
			}
		};
		System.out.println("Timer partito");
		scheduledTask = exService.schedule(task, seconds, TimeUnit.SECONDS);
	}

	private void stopTimer() {
		scheduledTask.cancel(true);
		exService.shutdown();
	}

}
