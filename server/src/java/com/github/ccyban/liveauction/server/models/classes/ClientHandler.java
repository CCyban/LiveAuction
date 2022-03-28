package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.server.Main;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import org.mockito.Mockito;

import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private AuctionRepository auctionRepository;
    private Runnable onClientConnect;
    private Runnable onClientDisconnect;
    public SubscriptionHandler subscriptionHandler;
    private KeySecurity keySecurity;

    public ClientHandler(Socket socket, AuctionRepository _auctionRepository, Runnable _onClientConnect, Runnable _onClientDisconnect)
    {
        super("ClientHandlerThread");
        clientSocket = socket;
        onClientConnect = _onClientConnect;
        onClientDisconnect = _onClientDisconnect;
        auctionRepository = _auctionRepository;
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
            System.out.println("Client Connected");
            onClientConnect.run();

            // Socket communication encryption stage

            while (!keySecurity.hasSecret && (incomingRequest = (SocketRequest) inputStream.readObject()) != null) {
                switch (incomingRequest.requestType) {
                    case GetPublicKey -> {
                        outputStream.writeObject(new SocketResponse(keySecurity.getServerPublicKey()));
                        System.out.println("SENT PUBLIC KEY: ✔");
                    }
                    case PostSymmetricKey -> {
                        keySecurity.setEncryptedSecretKey((byte[]) incomingRequest.requestPayload);
                        System.out.println("GOT SECRET KEY: ✔");
                    }
                }
            }

            // Secure socket communication achieved, can discuss openly

            // Note: The loop condition hangs on inputStream.readObject()
            while ((incomingEncryptedRequest = (SealedObject) inputStream.readObject()) != null) {
                System.out.println("Server received: " + incomingEncryptedRequest);
                incomingRequest = (SocketRequest) keySecurity.desealObject(incomingEncryptedRequest);

                switch (incomingRequest.requestType) {
                    case PostAuctionBid -> auctionRepository.bidOnAuction(incomingRequest.targetUUID, (Bid) incomingRequest.requestPayload);
                    case GetListOfAllAuctions, GetAuctionDetailsById -> {
                        subscriptionHandler = new SubscriptionHandler(auctionRepository, keySecurity, incomingRequest, outputStream);
                    }
                    case CloseAllSubscriptions -> outputStream.writeObject(keySecurity.sealObject(new SocketResponse(null)));
                }
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
