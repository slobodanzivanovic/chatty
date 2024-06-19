package com.slobodan.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    @SuppressWarnings("unused")
    private final Socket socket;
    private final ChattyClient client;
    private BufferedReader reader;

    public ReadThread(Socket socket, ChattyClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                if (response != null) {
                    if (response.equals("@shutdown")) {
                        System.out.println("Server has shut down. Closing connection.");
                        break;
                    }
                    if (response.startsWith("Connected users: ")) {
                        String usersList = response.substring("Connected users: ".length());
                        System.out.println("Connected users: " + usersList.replaceAll("\\[|\\]", ""));
                    } else {
                        System.out.print(response);
                        if (client.getUserName() != null) {
                            System.out.print("\n[" + client.getUserName() + "]: " + "\n");
                        }
                    }
                }
            } catch (IOException exception) {
                System.out.println("Disconnected: " + exception.getMessage());
                break;
            }
        }
    }
}