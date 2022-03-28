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

public class ClientSubscriptionHandler {

    private SocketRequest _socketRequest;
    public Object _responsePayloadObject;
    private SecretKeySpec secretKey;

    public ClientSubscriptionHandler(SocketRequest socketRequest, Object responsePayloadObject) {
        _socketRequest = socketRequest;
        _responsePayloadObject = responsePayloadObject;
    }

    public void sendSocketRequest(ObjectOutputStream out, ObjectInputStream in) {
        try {
            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            SealedObject sealedSocketRequest = auctionConnection.encryptSocketRequest(_socketRequest);

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
                switch (_socketRequest.requestType) {
                    case GetListOfAllAuctions -> ((ObservableList<Auction>) _responsePayloadObject).setAll(FXCollections.observableArrayList((ArrayList<Auction>) (incomingResponse.responsePayload)));
                    case GetAuctionDetailsById -> {
                        System.out.println("Setting new auction details");
                        _responsePayloadObject = incomingResponse.responsePayload;
                    }
                }
            }
        }
        System.out.println("A Client Subscription Ended 🔚");
    }
}
