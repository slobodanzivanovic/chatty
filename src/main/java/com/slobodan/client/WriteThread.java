package com.slobodan.client;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread {
    private final Socket socket;
    private final ChattyClient client;
    private PrintWriter writer;

    public WriteThread(Socket socket, ChattyClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException exception) {
            System.out.println("Error writing to socket: " + exception.getMessage());
        }
    }

    public void run() {
        Console console = System.console();

        String userName = console.readLine("\nPlease enter your username: ");
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            text = console.readLine("[" + userName + "]: ");
            writer.println(text);
        } while (!text.equals("quit"));

        try {
            socket.close();
        } catch (IOException exception) {
            System.out.println("Error closing socket: " + exception.getMessage());
        }
    }
}