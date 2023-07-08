package segreto.terminal;

import segreto.crypto.key.manager.SimmetricKeyManager;
import segreto.crypto.key.manager.KeyManager;
import segreto.crypto.Algorithm;
import segreto.crypto.Encrypter;
import segreto.crypto.Decrypter;
import segreto.exception.CSException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;

import java.util.Base64;

public abstract class Host {

	protected Socket socket;
	protected OutputStream out;
	protected InputStream in;
	protected byte[] buffer;
	protected SimmetricKeyManager sessionKey;
	private static final int BUFFER_SIZE = 1024;

	public void initializeResources() {
		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();
			buffer = new byte[BUFFER_SIZE];
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

        public void sendBytes(byte[] bytesToSend) {
                try {
                        out.write(bytesToSend);
                        out.flush();
                } catch (IOException e) {
                        System.out.println(e.getMessage());
                }
        }

	public void sendEncryptedBytes(byte[] bytesToSend) {
		sendEncryptedBytes(bytesToSend, sessionKey, Algorithm.AESCBC);
	}

	public void sendEncryptedBytes(byte[] bytesToSend, KeyManager km, Algorithm algorithm) {
		byte[] encryptedBytes = encrypt(bytesToSend, km, algorithm);
		sendBytes(encryptedBytes);
	}

        public byte[] readBytes() {
		byte[] readedByte = new byte[0];
                try {
			int byteReaded;
			do {
                        	byteReaded = in.read(buffer);
				byte[] tempReadedByte = new byte[readedByte.length];
				System.arraycopy(readedByte, 0, tempReadedByte, 0, readedByte.length);
				readedByte = new byte[readedByte.length + byteReaded];
				System.arraycopy(tempReadedByte, 0, readedByte, 0, tempReadedByte.length);
				System.arraycopy(buffer, 0, readedByte, tempReadedByte.length, byteReaded);
			} while (byteReaded == 1024);
			return readedByte;
                } catch (IOException e) {
                        System.out.println(e.getMessage());
                        return null;
                }

        }

	public byte[] readDecryptedBytes() {
		byte[] decrypted = decrypt(readBytes(), sessionKey, Algorithm.AESCBC);
		return decrypted;
		//return decrypt(readBytes(), sessionKey, Algorithm.AESCBC);
	}

	public byte[] readDecryptedBytes(KeyManager km, Algorithm algorithm) {
		return decrypt(readBytes(), km, algorithm);
	}

        public byte[] encrypt(byte[] data, KeyManager keyManager, Algorithm algorithm) {
                Encrypter encrypter = new Encrypter(keyManager, algorithm);
                return encrypter.start(data);
        }

	public byte[] encrypt(byte[] data) {
		Encrypter encrypter = new Encrypter(sessionKey, Algorithm.AESCBC);
		return encrypter.start(data);
	}

        public byte[] decrypt(byte[] data, KeyManager keyManager, Algorithm algorithm) {
                Decrypter decrypter = new Decrypter(keyManager, algorithm);
                return decrypter.start(data);
       	}

	public byte[] decrypt(byte[] data) {
		Decrypter decrypter = new Decrypter(sessionKey, Algorithm.AESCBC);
		return decrypter.start(data);
	}

	public void sendAndThrow(CSException e, boolean encrypterMode) throws CSException{
		if (encrypterMode)
			sendEncryptedBytes(e.serialize());
		else
			sendBytes(e.serialize());
		throw e;
	}

	//public void printbyte(byte[] b) {
	//	System.out.println(Base64.getEncoder().encodeToString(b)+"\n\n");
	//}

}

