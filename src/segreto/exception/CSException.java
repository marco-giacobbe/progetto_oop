package segreto.exception;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
Generica eccezione lato client/server
*/
public class CSException extends Exception implements Serializable {

	public CSException() {
		super("Eccezione client/server");
	}

	public CSException(String msg) {
		super(msg);
	}

	public byte[] serialize() {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(this);
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}
