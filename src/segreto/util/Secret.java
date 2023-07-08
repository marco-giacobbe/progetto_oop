package segreto.util;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;


public abstract class Secret implements Serializable {

	public void show() {
		System.out.println(this);
	}

	public void toFile(String id) {
                try (PrintWriter pw = new PrintWriter(String.format("%s.secret",id))) {
                        pw.println(this);
                } catch (FileNotFoundException e) {
                        System.exit(1);
                }
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
