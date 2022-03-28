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
            while(isListening) {
                Socket newClientSocket = serverSocket.accept();
                clientSockets.add(newClientSocket); // For disconnecting? (todo: check if it even works/needed since it only stops new connections last time I tested)

                ClientHandler newClientHandler = new ClientHandler(newClientSocket, auctionRepository, accountRepository, () -> onClientConnect(), () -> onClientDisconnect(), () -> onSubscriptionDataUpdate());
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

    public void onSubscriptionDataUpdate() {
        ServerLog.getInstance().log( "📩 Sending All Eligible Subscribed Clients Data Updates");
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
        shutDownConnections();
        shutDownClientThreadPool();
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
