package segreto.util;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Base64;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;

import java.util.Base64;

public class SecretFile extends Secret {

	private byte[] bytesFile;

	public SecretFile(String filename) {
		try {
			bytesFile = Files.readAllBytes(new File(filename).toPath());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		//System.out.println(Base64.getEncoder().encodeToString(bytesFile));
	}

	public byte[] getBytes() {
		return bytesFile;
	}

	@Override
	public void toFile(String id) {
		//System.out.println(Base64.getEncoder().encodeToString(bytesFile));
		//try (ObjectOutputStream oos = new ObjectOutputStream(
		//	new FileOutputStream(id))) {
		try (FileOutputStream fos = new FileOutputStream(id)) {
			fos.write(bytesFile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "segreto non stringabile";
	}

}
