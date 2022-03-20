package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ClientSubscriptionHandler {

    SocketRequest _socketRequest;
    Object _responsePayloadObject;

    Boolean isListening = true;

    public ClientSubscriptionHandler(SocketRequest socketRequest, Object responsePayloadObject) {
        _socketRequest = socketRequest;
        _responsePayloadObject = responsePayloadObject;
    }

    public void sendSocketRequest(ObjectOutputStream out, ObjectInputStream in) {

        try {
            out.writeObject(_socketRequest);
            listenForSocketResponses(in);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void listenForSocketResponses(ObjectInputStream in) throws IOException, ClassNotFoundException {
        while(isListening) {
            SocketResponse incomingResponse = (SocketResponse) in.readObject();
            System.out.println("Client received: " + incomingResponse);

            switch (_socketRequest.requestType) {
                case GetListOfAllAuctions -> ((ObservableList<Auction>) _responsePayloadObject).setAll(FXCollections.observableArrayList((ArrayList<Auction>) incomingResponse.responsePayload));
                case GetAuctionDetailsById -> _responsePayloadObject = incomingResponse.responsePayload;
            }
        }
    }
}
