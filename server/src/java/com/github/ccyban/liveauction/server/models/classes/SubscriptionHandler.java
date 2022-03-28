package com.github.ccyban.liveauction.server.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.SealedObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubscriptionHandler {

    private SocketRequest socketRequest;
    private ObjectOutputStream outputStream;
    private AuctionRepository auctionRepository;
    private KeySecurity keySecurity;

    public SubscriptionHandler(AuctionRepository auctionRepository, KeySecurity keySecurity, SocketRequest socketRequest, ObjectOutputStream outputStream) throws IOException {
        this.auctionRepository = auctionRepository;
        this.keySecurity = keySecurity;
        this.socketRequest = socketRequest;
        this.outputStream = outputStream;

        sendSocketResponse(getLatestSocketResponse());
    }

    public SocketResponse getLatestSocketResponse() {
        switch (socketRequest.requestType) {
            case GetListOfAllAuctions:
                return new SocketResponse(auctionRepository.getListOfAllAuctions());
            case GetAuctionDetailsById:
//                System.out.println("uuid target is " + socketRequest.targetUUID);
//                System.out.println("result is " + auctionRepository.getAuctionByUUID(socketRequest.targetUUID));

//                if (latestResponsePayload != null) {
//                    System.out.println("Top bid from rep: " + auctionRepository.getAuctionByUUID(socketRequest.targetUUID).getTopBid());
//                    System.out.println("Top bid from copy: " + ((Auction) latestResponsePayload).getTopBid());
//                }

                return new SocketResponse(auctionRepository.getAuctionByUUID(socketRequest.targetUUID));
            default:
                return null;
        }
    }

    private void sendSocketResponse(SocketResponse socketResponse) throws IOException {
        if (socketResponse.responsePayload != null) {

            SealedObject sealedSocketResponse = keySecurity.sealObject(socketResponse);
            outputStream.writeObject(sealedSocketResponse);
        }
        else {
            System.out.println("Null Payload?");
        }
    }

    public void ensureClientHasLatestData() throws IOException {
        SocketResponse latestSocketResponse = getLatestSocketResponse();

        // Do not have to worry about performance of sending the same data twice because it would be recognised by the socket hashset
        System.out.println("Sending latest data 📩");
        sendSocketResponse(latestSocketResponse);
    }
}
