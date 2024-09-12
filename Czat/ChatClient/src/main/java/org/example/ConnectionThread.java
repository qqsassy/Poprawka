package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    Socket client;
    PrintWriter writer;

    public ConnectionThread(String address, int port) throws IOException {
        client = new Socket(address, port);
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
                System.out.println(message.content);
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public void send(Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper().writeValueAsString(message);
        writer.println(rawMessage);
    }

    public void login(String login) throws JsonProcessingException {
        Message message = new Message(MessageType.Login, login);
        send(message);
    }
}
