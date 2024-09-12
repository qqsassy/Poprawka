package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private ConnectionThread connectionThread;

    public void start(String address, int port) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter your username: ");
            String username = reader.readLine();

            connectionThread = new ConnectionThread(address, port);
            connectionThread.start();
            connectionThread.login(username);

            String rawMessage;
            while ((rawMessage = reader.readLine()) != null) {
                Message message = new Message();
                if(rawMessage.equals("/online")) {
                    message = new Message(MessageType.Request, rawMessage);
                } else if(rawMessage.split(" ")[0].equals("/w")) {
                    message = new Message(MessageType.Whisper, rawMessage);
                } else {
                    message = new Message(MessageType.Broadcast, rawMessage);
                }
                connectionThread.send(message);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            stop();
        }
    }
    public void stop() {
        if (connectionThread != null && connectionThread.isAlive()) {
            try {
                connectionThread.client.close();
            } catch (IOException e) {
                System.err.println("Error closing connection thread: " + e.getMessage());
            }
        }
    }
}
