package com.github.ccyban.liveauction.server.models.classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ServerHandler extends Thread {

    private final ServerSocket serverSocket;
    private final AuctionRepository auctionRepository;
    private final AccountRepository accountRepository;
    private ThreadPoolExecutor clientThreadPool = (ThreadPoolExecutor) newFixedThreadPool(128);
    private ArrayList<Socket> clientSockets = new ArrayList<>();
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Boolean isListening = true;
    private Consumer activeConcurrentConnectionsChangeConsumer;

    public ServerHandler(ServerSocket serverSocket, AuctionRepository auctionRepository, AccountRepository accountRepository, Consumer activeConcurrentConnectionsChangeConsumer) {
        this.serverSocket = serverSocket;
        this.auctionRepository = auctionRepository;
        this.accountRepository = accountRepository;
        this.activeConcurrentConnectionsChangeConsumer = activeConcurrentConnectionsChangeConsumer;
        ServerLog.getInstance().log("🔛 Server now Online");
    }

    @Override
    public void run() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount());

        try {
            while (isListening) {
                Socket newClientSocket = serverSocket.accept();
                clientSockets.add(newClientSocket);

                ClientHandler newClientHandler = new ClientHandler(newClientSocket, auctionRepository, accountRepository, () -> onClientConnect(), () -> onClientDisconnect(), () -> onSubscriptionDataUpdate());
                clientHandlers.add(newClientHandler);
                clientThreadPool.execute(newClientHandler);
            }
        } catch (IOException e) {
            activeConcurrentConnectionsChangeConsumer.accept(0);
            ServerLog.getInstance().log("🔚 Server is Offline");
        }
    }

    public void onClientConnect() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount());
    }

    public void onClientDisconnect() {
        activeConcurrentConnectionsChangeConsumer.accept(clientThreadPool.getActiveCount() - 1);
    }

    public void onSubscriptionDataUpdate() {
        ServerLog.getInstance().log("📩 Sending All Eligible Subscribed Clients Data Updates");
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.subscriptionHandler != null) {
                    clientHandler.subscriptionHandler.ensureClientHasLatestData();
                }
            } catch (IOException e) {
                ServerLog.getInstance().log("⚠ Exception caught when trying to send subscription data updates");
            }
        }
    }

    public void shutDown() {
        shutDownConnections();
        shutDownClientThreadPool();
    }

    private void shutDownConnections() {
        for (Socket client : clientSockets) {
            try {
                client.close();
            } catch (IOException e) {
                ServerLog.getInstance().log("⚠ Exception caught when trying to shut down connections");
            }
        }
    }

    private void shutDownClientThreadPool() {
        clientThreadPool.shutdown();
    }
}
