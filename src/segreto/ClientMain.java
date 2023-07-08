package segreto;

import segreto.util.*;
import segreto.terminal.Client;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.Console;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

import java.util.Base64;

public class ClientMain {

	private Scanner scanner;
	private Client client;

	public ClientMain() {
		scanner = new Scanner(System.in);
		client = new Client();
		client.startConnection();
		client.getSessionKey();
	}

	public static void main(String[] args) {
		ClientMain cm = new ClientMain();
		cm.run();

	}

	public void run() {
		login();
		while(menu()!=3);
	}

	private void login() {
		System.out.print("username: ");
		String username = scanner.nextLine();
		Console console = System.console();
		if (console == null) {
			System.exit(1);
		}
		String password = new String(console.readPassword("password: "));
		client.auth((selectChoice(1, "mode (0=signup, 1=login): ")!=0), username, password);
	}

	private void exit() {
		client.stopConnection();
	}

	private int menu() {
		System.out.println("\n0) Inserire un nuovo segreto");
                System.out.println("1) Recuperare un segreto");
                System.out.println("2) Eliminare un segreto");
		System.out.println("3) Exit");
		int choice = selectChoice(3, "Cosa vuoi fare? ");
		switch (choice) {
			case 0:
				newSecret();
				break;
			case 1:
				getSecret();
				break;
			case 2:
				removeSecret();
				break;
			case 3:
				exit();
				break;
			default:
				break;
		}
		return choice;
	}

	private void newSecret() {
		System.out.println("\nInserisci l'ID del segreto.");
		System.out.println("N.B l'ID servirà per recuperare il segreto");
		System.out.println("Perdere l'ID significa perdere il segreto per sempre!");
		System.out.print("id: ");
		String id = scanner.nextLine();
		System.out.println("\n0) Nuova password");
		System.out.println("1) Nuovo testo");
		System.out.println("2) Nuovo file");
		int choice = selectChoice(2, "Che tipo di segreto vuoi caricare? ");
		switch (choice) {
			case 0:
				newSecretPassword(id);
				break;
			case 1:
				newSecretText(id);
				break;
			case 2:
				newSecretFile(id);
				break;
			default:
				break;
		}
	}

	private void newSecretPassword(String id) {
		System.out.print("username da registrare: ");
		String username = scanner.nextLine();
		System.out.print("password da registrare: ");
		String password = scanner.nextLine();
		SecretPassword s = new SecretPassword(username, password);
		client.loadSecret(id, s.serialize());
	}

	private void newSecretText(String id) {
		System.out.println("inserisci il testo da registrare");
		System.out.println("\\n+\\end per terminare");
		String text = "";
		String tempText;
		while (true) {
			tempText = scanner.nextLine();
			if (tempText.equals("\\end"))
				break;
			text = text + tempText + "\n";
		}
		SecretText s = new SecretText(text);
		client.loadSecret(id, s.serialize());
	}

	private void newSecretFile(String id) {
		System.out.print("inserisci il path del file: ");
		String filepath = scanner.nextLine();
		try {
			if (!(new File(filepath).isFile()))
				throw new FileNotFoundException("file non trovato");
			client.loadSecret(id, new SecretFile(filepath).serialize());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private void getSecret() {
		System.out.print("\nID del segreto: ");
		String id = scanner.nextLine();
		Secret s = (Secret)client.querySecret(id);
		if (s==null)
			return;
		s.show();
		if (selectChoice(1, "1 per memorizzare il segreto, 0 altrimenti: ") == 1) {
			s.toFile(id);
		}
	}

	private void removeSecret() {
		System.out.print("\nID del segreto: ");
		String id = scanner.nextLine();
		client.removeSecret(id);
	}

	private int selectChoice(int maxValue, String inputHelp) {
                while (true) {
                        try{
                                scanner = new Scanner(System.in);
                                System.out.print(inputHelp);
                                int choice = scanner.nextInt();
                                scanner.nextLine(); //legge \n
                                if (choice<0 || choice>maxValue)
                                        throw new IllegalArgumentException("Input non valido");
                                return choice;
                        } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                        } catch (InputMismatchException e) {
                                System.out.println("Input non valido");
                        }
                }
	}

}
