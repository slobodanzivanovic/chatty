package com.slobodan.client;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.slobodan.db.Database;

public class ChattyClient {
    private final String hostname;
    private final int port;
    private String userName;
    private Socket socket;

    public ChattyClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    private void displayLogo() {
        System.out.println();
        System.out.println("   _____ _           _   _             ________   ");
        System.out.println("  / ____| |         | | | |           /  ____  \\  ");
        System.out.println(" | |    | |__   __ _| |_| |_ _   _   /  / ___|  \\ ");
        System.out.println(" | |    | '_ \\ / _` | __| __| | | | |  | |       |");
        System.out.println(" | |____| | | | (_| | |_| |_| |_| | |  | |___    |");
        System.out.println("  \\_____|_| |_|\\__,_|\\__|\\__|\\__, |  \\  \\____|  / ");
        System.out.println("                              __/ |   \\________/  ");
        System.out.println("                             |___/                ");
        System.out.println();
        System.out.println();
    }

    public void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void registerOrLogin() {
        displayLogo();

        Console console = System.console();

        Database database = new Database("jdbc:postgresql://localhost:5432/chatty", "slobodan", "1234");

        while (true) {
            String choice = console.readLine("Enter 'register' to register or 'login' to login: ");
            if ("register".equalsIgnoreCase(choice)) {
                String username = console.readLine("Enter username: ");
                char[] passwordArray = console.readPassword("Enter password: ");
                String password = new String(passwordArray);
                boolean success = database.registerUser(username, password);
                if (success) {
                    System.out.println("Registration successful. You can now log in.");
                    continue;
                } else {
                    System.out.println("Registration failed. Please try again.");
                }
            } else if ("login".equalsIgnoreCase(choice)) {
                String username = console.readLine("Enter username: ");
                char[] passwordArray = console.readPassword("Enter password: ");
                String password = new String(passwordArray);
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

            clearConsole();
            displayLogo();
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