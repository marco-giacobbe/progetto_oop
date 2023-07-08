package segreto.message;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public abstract class Message implements Serializable {

	// 0 per l'inserimento
	// 1 per la query
	// 2 per la rimozione
	// 3 per l'autenticazione
	// 4 per chiudere la connessione
	// 5 ack
	private int type;

	public Message(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
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
