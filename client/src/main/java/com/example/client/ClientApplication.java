package com.example.client;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.net.Socket;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.core.net.server.Client;

@SpringBootApplication
public class ClientApplication {

	private Socket socket;
	private BufferedReader br;
	private BufferedWriter pw;
	private String username;

	public ClientApplication(Socket socket, String username) {
		try {
			this.socket = socket;
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;

		} catch (IOException e) {
			closeEverything(socket, br, pw);
		}
	}

	public void sendMessage() {
		try {
			pw.write(username);
			pw.newLine();
			pw.flush();
			while (socket.isConnected()) {
				String message = br.readLine();
				pw.write(username + ": " + message);
				pw.newLine();
				pw.flush();
			}
		} catch (IOException e) {
			closeEverything(socket, br, pw);
		}
	}

	public void listenForMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (socket.isConnected()) {
						String message = br.readLine();
						System.out.println(message);
					}
				} catch (IOException e) {
					closeEverything(socket, br, pw);
				}
			}
		}).start();
	}

	private void closeEverything(Socket socket2, BufferedReader br2, BufferedWriter pw2) {
		try {
			br2.close();
			pw2.close();
			socket2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your username:");
		String username = scanner.nextLine();

		Socket socket = new Socket("localhost", 8080);
		Client client = new Client(socket, username);
	}

}
