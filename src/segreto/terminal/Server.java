package segreto.terminal;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import segreto.terminal.config.DBConfigHandler;
import segreto.terminal.config.ServerConfigHandler;
import segreto.terminal.config.XMLManager;
import segreto.terminal.db.SpecDBManager;
import java.util.concurrent.Semaphore;

public class Server{

	private ServerSocket sSocket;
	private Semaphore semaphore;
	private String driver;
	private String url;
	private String database;
	private String username;
	private String password;
	private int port;
	private int max_conn;
	private static final String DBCONFIG_FILENAME = "../../resources/dbconfig.xml";
	private static final String SERVERCONFIG_FILENAME = "../../resources/serverconfig.xml";

	public Server() {
		initializeDBProperties();
		initializeServer();
		try {
			sSocket = new ServerSocket(port);
			new SpecDBManager(url, username, password, driver, database, true);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		semaphore = new Semaphore(max_conn, true);
	}

	private void initializeDBProperties() {
                String[] data = new XMLManager(DBCONFIG_FILENAME, new DBConfigHandler()).parse();
                driver = data[0];
                url = String.format("jdbc:%s://%s:%s", data[1], data[2], data[3]);
                database = data[4];
                username = data[5];
                password = data[6];
	}

	private void initializeServer() {
		String[] data = new XMLManager(SERVERCONFIG_FILENAME, new ServerConfigHandler()).parse();
		port = Integer.parseInt(data[0]);
		max_conn = Integer.parseInt(data[1]);
	}

	public void run() {
		while (true) {
			System.out.println("Attendo una nuova connessione");
			try {
				new Thread(new ClientHandler(sSocket.accept(), semaphore, url, username, password,
						driver, database)).start();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void main (String[] args) {
		Server s = new Server();
		s.run();
	}
}
