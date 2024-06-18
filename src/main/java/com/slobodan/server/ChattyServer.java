package com.slobodan.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChattyServer {
    private final int port;
    private final Set<String> users = ConcurrentHashMap.newKeySet();
    private final Set<UserThread> userThreads = ConcurrentHashMap.newKeySet();

    public ChattyServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Starting server on port " + port);
            System.out.println("Waiting for connections...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getRemoteSocketAddress());

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        users.add(userName);
    }

    void removeUser(String userName, UserThread aUser) {
        boolean removed = users.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " left");
        }
    }

    Set<String> getUserNames() {
        return this.users;
    }

    boolean hasUsers() {
        return !this.users.isEmpty();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ChattyServer <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        ChattyServer server = new ChattyServer(port);
        server.execute();
    }
}