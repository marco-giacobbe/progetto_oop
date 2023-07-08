package segreto.message;

import segreto.crypto.Hasher;
import segreto.crypto.Algorithm;

public class RequestMessage extends Message {

	private String id;

	public RequestMessage(int type, String id) {
		super(type);
		Hasher hasher = new Hasher(Algorithm.SHA256);
		this.id = hasher.getStringedHash(id.getBytes());
	}

	public String getId() {
		return id;
	}

}
