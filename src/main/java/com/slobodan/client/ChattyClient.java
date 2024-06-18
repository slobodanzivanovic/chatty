package com.slobodan.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChattyClient {
    private final String hostname;
    private final int port;
    private String userName;

    public ChattyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to " + hostname + ":" + port);

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException exception) {
            System.out.println("Wrong server host: " + hostname + " or port: " + port + "\n" + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("I/O Error: " + exception.getMessage());
        }
    }

    String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ChattyClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChattyClient client = new ChattyClient(hostname, port);
        client.execute();
    }
}