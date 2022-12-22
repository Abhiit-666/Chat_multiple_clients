package com.example.server;

import java.net.ServerSocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

	private ServerSocket serverSocket;

	public ServerApplication(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void startServer() {
		while (!serverSocket.isClosed()) {
			Socket socket = serverSocket.accept();
			System.out.println("A client connected!");
			new Thread(new ServerThread(socket)).start();
		}
	}

	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		ServerSocket serversocket = new ServerApplication(9000);
		ServerApplication server = new ServerApplication(serversocket);
		server.startServer();

	}
}
