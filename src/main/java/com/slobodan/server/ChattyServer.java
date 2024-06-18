package com.slobodan.server;

import com.slobodan.client.ChattyClient;

public class ChattyServer {
    public static void main(String[] args) {
        ChattyClient client = new ChattyClient();
        System.out.println(client.message("Hello, World!"));
    }
}
