package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.classes.SocketResponse;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class AuctionConnection {
    private static Timer tickTimer = new Timer();

    public static void setTimerTask(TimerTask timerTask) {
        tickTimer.schedule(timerTask, 0, 1000);
    }

    public static void cancelTimerTask() {
        tickTimer.cancel();
    }

    // This area is generally used to fetch current auction data from socket & feed it into the table each tick

    // -- Cut off area --

    private static AuctionConnection uniqueConnection;

    public static Socket clientSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    private static Thread clientSubscription;

    private AuctionConnection () {
        // Starting connection
        // TODO: Add/use authentication variables here
        try {
            clientSocket = new Socket("localhost", 9090);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static AuctionConnection getAuctionConnection() {
        if (uniqueConnection == null) {
            uniqueConnection = new AuctionConnection();
        }
        return uniqueConnection;
    }

    public void requestSocketData(ClientSubscriptionHandler clientSubscriptionHandler) {
        Thread clientSubscription = new Thread(new Runnable() {
            public void run() {
                clientSubscriptionHandler.sendSocketRequest(out, in);
            }
        });
        clientSubscription.start();
    }

}
