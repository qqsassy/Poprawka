package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket socket;
    private List<ClientThread> clients = new CopyOnWriteArrayList<>();

    public Server(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public void listen() throws IOException {
        while (true) {
            Socket client = socket.accept();
            ClientThread thread = new ClientThread(client, this);
            clients.add(thread);
            thread.start();
        }
    }

    public synchronized void broadcast(Message message) throws JsonProcessingException {
        for (ClientThread client : clients) {
            client.send(message);
        }
    }

    public void removeClient(ClientThread client) {
        clients.remove(client);
    }

    public synchronized void online(ClientThread client) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        for(ClientThread c : clients) {
            builder.append(c.username).append("\n");
        }
        client.send(new Message(MessageType.Request, builder.toString()));
    }

    public synchronized void whisper(Message message, String destUsername) throws JsonProcessingException {
        for(ClientThread c : clients) {
            if(c.username.equals(destUsername)) {
                c.send(message);
            }
        }
    }
}
