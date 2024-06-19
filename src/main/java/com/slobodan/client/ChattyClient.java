package com.slobodan.client;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.slobodan.server.Database;

public class ChattyClient {
    private final String hostname;
    private final int port;
    private String userName;
    private Socket socket;

    public ChattyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void registerOrLogin() {
        Console console = System.console();

        Database database = new Database("jdbc:postgresql://localhost:5432/chatty", "slobodan", "1234");

        while (true) {
            String choice = console.readLine("Enter 'register' to register or 'login' to login: ");
            if ("register".equalsIgnoreCase(choice)) {
                String username = console.readLine("Enter username: ");
                String password = console.readLine("Enter password: ");
                boolean success = database.registerUser(username, password);
                if (success) {
                    System.out.println("Registration successful. You can now log in.");
                    continue;
                } else {
                    System.out.println("Registration failed. Please try again.");
                }
            } else if ("login".equalsIgnoreCase(choice)) {
                String username = console.readLine("Enter username: ");
                String password = console.readLine("Enter password: ");
                boolean success = database.loginUser(username, password);
                if (success) {
                    System.out.println("Login successful. Connecting to server...");
                    this.userName = username;

                    try {
                        socket = new Socket(hostname, port);
                        System.out.println("Connected to " + hostname + ":" + port);

                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(username);

                        new ReadThread(socket, this).start();
                        new WriteThread(socket, this).start();

                    } catch (UnknownHostException exception) {
                        System.out.println(
                                "Wrong server host: " + hostname + " or port: " + port + "\n" + exception.getMessage());
                    } catch (IOException exception) {
                        System.out.println("I/O Error: " + exception.getMessage());
                    }
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter 'register' or 'login'.");
                }
            }
        }
    }

    public void execute() {
        registerOrLogin();

        if (userName == null) {
            return;
        }
    }

    String getUserName() {
        return this.userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        // if (args.length < 2) {
        // System.out.println("Usage: ChattyClient <hostname> <port>");
        // return;
        // }
        //
        // String hostname = args[0];
        // int port = Integer.parseInt(args[1]);

        ChattyClient client = new ChattyClient("localhost", 8989);
        client.execute();
    }
}