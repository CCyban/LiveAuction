package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SubscriptionHandler {

    SocketRequest _socketRequest;
    ObjectOutputStream _outputStream;
    int latestSocketResponseHash;

    public SubscriptionHandler(SocketRequest socketRequest, ObjectOutputStream outputStream) throws IOException {
        _socketRequest = socketRequest;
        _outputStream = outputStream;

        sendSocketResponse(getLatestSocketResponse());
    }

    public SocketResponse getLatestSocketResponse() {
        switch (_socketRequest.requestType) {
            case GetListOfAllAuctions:
                return new SocketResponse(AuctionRepository.getListOfAllAuctions());
            case GetAuctionDetailsById:
                return new SocketResponse("Some auction details");
            default:
                return null;
        }
    }

    private void sendSocketResponse(SocketResponse socketResponse) throws IOException {
        _outputStream.writeObject(socketResponse);
        latestSocketResponseHash = socketResponse.hashCode();
    }

    public void ensureClientHasLatestData() throws IOException {
        System.out.println("Client data hash: " + latestSocketResponseHash);
        SocketResponse latestSocketResponse = getLatestSocketResponse();
        System.out.println("Server data hash: " + latestSocketResponse.hashCode());
        if (latestSocketResponseHash != latestSocketResponse.hashCode()) {
            System.out.println("Difference detected. Sending latest..");
            sendSocketResponse(latestSocketResponse);
            latestSocketResponseHash = latestSocketResponse.hashCode();
        }
    }
}
