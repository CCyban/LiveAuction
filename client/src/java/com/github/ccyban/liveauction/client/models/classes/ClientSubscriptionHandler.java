package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientSubscriptionHandler {

    private SocketRequest socketRequest;
    public Object responsePayloadObject;
    private SecretKeySpec secretKey;
    private Runnable onUINeedsUpdate;

    public ClientSubscriptionHandler(SocketRequest socketRequest, Object responsePayloadObject, Runnable onUINeedsUpdate) {
        this.socketRequest = socketRequest;
        this.responsePayloadObject = responsePayloadObject;
        this.onUINeedsUpdate = onUINeedsUpdate;
    }

    public void sendSocketRequest(ObjectOutputStream out, ObjectInputStream in) {
        try {
            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            SealedObject sealedSocketRequest = auctionConnection.encryptSocketRequest(socketRequest);

            out.writeObject(sealedSocketRequest);
            listenForSocketResponses(in);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void listenForSocketResponses(ObjectInputStream in) throws IOException, ClassNotFoundException {
        while(true) {
            SealedObject incomingEncryptedResponse = (SealedObject) in.readObject();

            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            SocketResponse incomingResponse = auctionConnection.decryptSocketResponse(incomingEncryptedResponse);

            System.out.println("Client received: " + incomingResponse);

            // Check to see if it's a cancelled subscription
            if (incomingResponse.responsePayload == null) {
                break;
            }
            else {
                switch (socketRequest.requestType) {
                    case GetListOfAllAuctions -> ((ObservableList<Auction>) responsePayloadObject).setAll(FXCollections.observableArrayList((ArrayList<Auction>) (incomingResponse.responsePayload)));
                    case GetAuctionDetailsById -> {
                        System.out.println("Setting new auction details");
                        System.out.println("before auction top bid:" + ((AtomicReference<Auction>) responsePayloadObject).get().getTopBid());
                        ((AtomicReference<Auction>) responsePayloadObject).set((Auction) incomingResponse.responsePayload);
                        System.out.println("after auction top bid:" + ((AtomicReference<Auction>) responsePayloadObject).get().getTopBid());
                    }
                }
                onUINeedsUpdate.run();
            }
        }
        System.out.println("A Client Subscription Ended 🔚");
    }
}
