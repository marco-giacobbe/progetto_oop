package segreto.util;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class SecretText extends Secret {

	private String text;

	public SecretText(String sText) {
		text = sText;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}


}
