package com.example.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable {
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter pw;
    private String clientUsername;
    public static ArrayList<ServerThread> clients = new ArrayList<ServerThread>();

    public ServerThread(Socket socket) {
        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = br.readLine();
            clients.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has joined the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = br.readLine();
                broadcastMessage(clientUsername + ": " + message);
            } catch (IOException e) {
                closeEverything(socket, br, pw);
                break;
            }
        }
    }

    private void closeEverything(Socket socket2, BufferedReader br2, BufferedWriter pw2) {
        try {
            br2.close();
            pw2.close();
            socket2.close();
            clients.remove(this);
            broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ServerThread client : clients) {
            try {
                if (client != this) {
                    client.pw.write(message);
                    client.pw.newLine();
                    client.pw.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, br, pw);
            }
        }
    }

}
