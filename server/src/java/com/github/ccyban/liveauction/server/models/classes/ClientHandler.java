package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Runnable onClientConnect;
    private Runnable onClientDisconnect;
    public SubscriptionHandler subscriptionHandler;

    public ClientHandler (Socket socket, Runnable _onClientConnect, Runnable _onClientDisconnect)
    {
        super("ClientHandlerThread");
        this.clientSocket = socket;
        onClientConnect = _onClientConnect;
        onClientDisconnect = _onClientDisconnect;
    }

    public void run()
    {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            SocketRequest incomingRequest;
            System.out.println("Client Connected");
            onClientConnect.run();

            // Note: The loop condition hangs on inputStream.readObject()
            while ((incomingRequest = (SocketRequest) inputStream.readObject()) != null) {
                System.out.println("Server received: " + incomingRequest);
                subscriptionHandler = new SubscriptionHandler(incomingRequest, outputStream);
            }
            clientSocket.close();
        }
        catch (IOException e) {
            System.out.println("Client disconnected");
            System.out.println(e.getMessage());
            subscriptionHandler = null;
            onClientDisconnect.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
