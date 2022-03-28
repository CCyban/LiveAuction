package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.server.Main;
import com.github.ccyban.liveauction.shared.models.classes.Account;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import org.javatuples.Pair;
import org.mockito.Mockito;

import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private AuctionRepository auctionRepository;
    private AccountRepository accountRepository;
    private Runnable onClientConnect;
    private Runnable onClientDisconnect;
    private Runnable onSubscriptionDataUpdate;
    public SubscriptionHandler subscriptionHandler;
    private KeySecurity keySecurity;

    public ClientHandler(Socket socket, AuctionRepository auctionRepository, AccountRepository accountRepository, Runnable onClientConnect, Runnable onClientDisconnect, Runnable onSubscriptionDataUpdate)
    {
        super("ClientHandlerThread");
        clientSocket = socket;
        this.auctionRepository = auctionRepository;
        this.accountRepository = accountRepository;
        this.onClientConnect = onClientConnect;
        this.onClientDisconnect = onClientDisconnect;
        this.onSubscriptionDataUpdate = onSubscriptionDataUpdate;
        keySecurity = new KeySecurity();
    }

    public void run()
    {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            SealedObject incomingEncryptedRequest;
            SocketRequest incomingRequest;
            onClientConnect.run();
            ServerLog.getInstance().clientLog(clientSocket.hashCode(), "👋 Client Connected");

            // Socket communication encryption stage

            while (!keySecurity.hasSecret && (incomingRequest = (SocketRequest) inputStream.readObject()) != null) {
                switch (incomingRequest.requestType) {
                    case GetPublicKey -> {
                        outputStream.writeObject(new SocketResponse(keySecurity.getServerPublicKey()));
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(), "📢 Sent PUBLIC KEY");
                    }
                    case PostSymmetricKey -> {
                        keySecurity.setEncryptedSecretKey((byte[]) incomingRequest.requestPayload);
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(), "🔐 Received SECRET KEY");
                    }
                }
            }

            // Secure socket communication achieved, can discuss openly

            // Note: The loop condition hangs on inputStream.readObject()
            while ((incomingEncryptedRequest = (SealedObject) inputStream.readObject()) != null) {
                incomingRequest = (SocketRequest) keySecurity.desealObject(incomingEncryptedRequest);

                switch (incomingRequest.requestType) {
                    case PostAuctionBid -> {
                        auctionRepository.bidOnAuction(incomingRequest.targetUUID, (Bid) incomingRequest.requestPayload);
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(), "💰 New Bid Applied on Auction: " + incomingRequest.targetUUID);
                        onSubscriptionDataUpdate.run();
                    }
                    case GetListOfAllAuctions, GetAuctionDetailsById -> {
                        subscriptionHandler = new SubscriptionHandler(auctionRepository, keySecurity, incomingRequest, outputStream);
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(), "⏳ Assigned a Subscription Handler");
                    }
                    case CloseAllSubscriptions -> {
                        outputStream.writeObject(keySecurity.sealObject(new SocketResponse(null)));
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(), "⌛ Closed All Local Client Subscriptions");
                    }
                    case SignIn -> {
                        Account signInAttempt = (Account) incomingRequest.requestPayload;
                        UUID accountAttemptUUID = accountRepository.getAccountUUID(signInAttempt);

                        outputStream.writeObject(keySecurity.sealObject(new SocketResponse(accountAttemptUUID)));
                        ServerLog.getInstance().clientLog(clientSocket.hashCode(),
                                "🔑 Signed In Attempt as " + ((Account) incomingRequest.requestPayload).getUsername() + ": " +
                                        (accountAttemptUUID != null ? "Successful" : "Failed"));
                    }
                }
            }
            clientSocket.close();
        }
        catch (IOException e) {
            ServerLog.getInstance().clientLog(clientSocket.hashCode(), "👋 Client Disconnected");
            subscriptionHandler = null;
            onClientDisconnect.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
