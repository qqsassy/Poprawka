package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket client;
    private Server server;
    private PrintWriter writer;
    String username;

    public ClientThread(Socket socket, Server server) {
        this.client = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (InputStream input = client.getInputStream();
             OutputStream output = client.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            writer = new PrintWriter(output, true);

            String rawMessage;

            while ((rawMessage = reader.readLine()) != null) {
                Message message = new ObjectMapper().readValue(rawMessage, Message.class);

                switch (message.type) {
                    case Login -> {
                        username = message.content;
                        server.broadcast(new Message(MessageType.Broadcast, username + " has joined the chat"));
                    }
                    case Broadcast -> {
                        String formattedMessage = "[" + username + "] " + message.content;
                        server.broadcast(new Message(MessageType.Broadcast, formattedMessage));
                    }
                    case Logout -> {
                        server.broadcast(new Message(MessageType.Broadcast, username + " has left the chat"));
                        server.removeClient(this);
                        client.close();
                        return; // Exit the thread
                    }
                    case Request -> server.online(this);
                    case Whisper -> {
                        String destUsername = rawMessage.split(" ")[1];
                        String actualMessage = rawMessage.split(" ", 3)[2];
                        String formattedMessage = "[" + username + "] " + actualMessage;
                        server.whisper(new Message(MessageType.Whisper, formattedMessage), destUsername);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            if (username != null) {
                try {
                    server.broadcast(new Message(MessageType.Broadcast, username + " has left the chat"));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper().writeValueAsString(message);
        writer.println(rawMessage);
    }
}
