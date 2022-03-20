package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerHandler extends Thread {

    private final int portNumber = 9090;
    private final ServerSocket serverSocket;
    private ThreadPoolExecutor clientThreadPool = (ThreadPoolExecutor) newFixedThreadPool(128);
    private ArrayList<Socket> clientSockets = new ArrayList<>();
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Boolean isListening = true;
    private Consumer activeConcurrentConnectionsChangeConsumer;
    private Timer subscriptionTimer;

    public ServerHandler(ServerSocket _serverSocket, Consumer _activeConcurrentConnectionsChangeConsumer) {
        serverSocket = _serverSocket;
        activeConcurrentConnectionsChangeConsumer = _activeConcurrentConnectionsChangeConsumer;
    }

    @Override
    public void run() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount());

        subscriptionTimer = new Timer();
        subscriptionTimer.schedule(((new TimerTask() {
            @Override
            public void run() {
                onSubscriptionsCheckTick();
            }
        })), 0, 1000);

        try {
            while(isListening) {
                Socket newClientSocket = serverSocket.accept();
                clientSockets.add(newClientSocket); // For disconnecting? (todo: check if it even works/needed since it only stops new connections last time I tested)

                ClientHandler newClientHandler = new ClientHandler(newClientSocket, () -> onClientConnect(), () -> onClientDisconnect());
                clientHandlers.add(newClientHandler);
                clientThreadPool.execute(newClientHandler);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber
                    + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public void onClientConnect() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount());
    }

    public void onClientDisconnect() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount() - 1);
    }

    private void onSubscriptionsCheckTick() {
        for (ClientHandler clientHandler: clientHandlers) {
            try {
                if (clientHandler.subscriptionHandler != null) {
                    clientHandler.subscriptionHandler.ensureClientHasLatestData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        stopSubscriptionTimer();
        shutDownConnections();
        shutDownClientThreadPool();
    }

    private void stopSubscriptionTimer() {
        subscriptionTimer.cancel();
    }

    private void shutDownConnections() {
        for (Socket client: clientSockets) {
            try {
                client.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shutDownClientThreadPool() {
        clientThreadPool.shutdown();
    }
}
