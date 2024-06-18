package com.slobodan.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class UserThread extends Thread {
    private final Socket socket;
    private final ChattyServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChattyServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try (
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream()) {
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "New user: " + userName + " joined the chatty server!";
            server.broadcast(serverMessage, this);

            String clientMessage;

            while ((clientMessage = reader.readLine()) != null && !clientMessage.equals("quit")) {
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
            }

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " left the chatty server!";
            server.broadcast(serverMessage, this);

        } catch (IOException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    void printUsers() {
        String message = server.hasUsers() ? "Connected users: " + server.getUserNames() : "No other users connected";
        sendMessage(message);
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}