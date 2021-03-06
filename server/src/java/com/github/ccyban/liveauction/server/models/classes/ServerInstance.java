package com.github.ccyban.liveauction.server.models.classes;

import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Consumer;

public class ServerInstance {
    private static ServerInstance uniqueInstance;

    private Boolean isOnline;
    private ServerSocket serverSocket;
    private ServerHandler server;
    private Thread serverThread;
    private AuctionRepository auctionRepository;
    private AccountRepository accountRepository;

    private ServerInstance() {
        isOnline = false;
    }

    public static ServerInstance getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ServerInstance();
        }

        return uniqueInstance;
    }

    public boolean startServer() {
        return changeServerStatus(true);
    }

    public boolean closeServer() {
        return changeServerStatus(false);
    }

    private synchronized boolean changeServerStatus(Boolean isOnline) {
        this.isOnline = isOnline;

        // Status Change Consumer
        if (statusChangeConsumer != null) {
            statusChangeConsumer.accept(this.isOnline);
        }

        // Perform the status change
        if (this.isOnline) {
            try {
                serverSocket = new ServerSocket(9090);

                // Mocking auction repository
                auctionRepository = Mockito.spy(AuctionRepository.class);
                MockHelper.mockAuctionRepository(auctionRepository);

                accountRepository = Mockito.spy(AccountRepository.class);
                MockHelper.mockAccountRepository(accountRepository);

                server = new ServerHandler(serverSocket, auctionRepository, accountRepository, activeConcurrentConnectionsChangeConsumer);
            }
            catch (IOException e) {
                ServerLog.getInstance().log("⚠ Exception caught trying to start server socket on port 9090");
                return false;
            }
            serverThread = new Thread(server);
            serverThread.start();
        }
        else {
            try {
                shutServerDown();
                serverSocket.close();
            }
            catch (IOException e) {
                ServerLog.getInstance().log("⚠ Exception caught trying to close server socket");
                return false;
            }
        }
        return true;
    }

    private static Consumer statusChangeConsumer;

    public void setStatusChangeConsumer(Consumer consumer) {
        statusChangeConsumer = consumer;
    }

    private static Consumer activeConcurrentConnectionsChangeConsumer;

    public void setActiveConcurrentConnectionsChangeConsumer(Consumer consumer) {
        activeConcurrentConnectionsChangeConsumer = consumer;
    }

    public void shutServerDown() {
        if (server != null) {
            server.shutDown();
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                ServerLog.getInstance().log("⚠ Exception caught trying to close server socket");
            }
        }
    }
}
