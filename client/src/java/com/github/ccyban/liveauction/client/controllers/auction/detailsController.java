package com.github.ccyban.liveauction.client.controllers.auction;

import com.github.ccyban.liveauction.client.models.classes.AuctionConnection;
import com.github.ccyban.liveauction.client.models.classes.ClientSubscriptionHandler;
import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.classes.UserSession;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.UUID;

public class detailsController implements Initializable {

    private Auction auction;

    @FXML
    private Label labelHighestBid;

    @FXML
    private Label labelEndsIn;

    @FXML
    private Label labelAuctionHealth;

    @FXML
    private Label labelStartingBid;

    @FXML
    private Label labelSeller;

    @FXML
    private Label labelIncrementalPace;

    @FXML
    private Label labelAuctionName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create a socket connection specifically for getting the details of an auction
        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();

        UserSession userSession = UserSession.getUserSession();
        auction = userSession.getSelectedAuction();

        ClientSubscriptionHandler clientSubscriptionHandler = new ClientSubscriptionHandler(new SocketRequest(SocketRequestType.GetAuctionDetailsById, auction.getAuctionUUID(), null), auction);
        auctionConnection.requestSocketData(clientSubscriptionHandler);



        // Auto client-side table updates (e.g. countdown)
        auctionConnection.setTimerTask((new TimerTask() {
            @Override
            public void run() {
                onDetailsUpdateTick();
            }
        }));
    }

    @FXML
    private void onBackToList() {
        PageManager.loadPage(Page.AuctionList);
    }

    @FXML
    private void onBid() {
        if (auction != null) {
            BigDecimal newBidAmount = auction.getTopBid().add(auction.getIncrementalBidPace());

            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            auctionConnection.postBid(auction.getAuctionUUID(), new Bid(newBidAmount, UUID.randomUUID()));
        }
    }

    private void onDetailsUpdateTick() {
        if (auction != null) {
            Platform.runLater(() -> {
                labelHighestBid.setText("Current Bid: £" + auction.getTopBid());
                labelAuctionName.setText("Name: " + auction.getName());
                labelEndsIn.setText(auction.getTimeLeftStringProperty().get());
                labelAuctionHealth.setText("Status: " + (auction.getSecondsLeft() > 0 ? "Ongoing" : "Finished"));
                labelSeller.setText("Seller: " + auction.getSellerName());
                labelStartingBid.setText("Starting Bid: £" + auction.getStartingBidPrice());
                labelIncrementalPace.setText("Bidding Increments: £" + auction.getIncrementalBidPace());
            });
        }
        else {
            System.out.println("auction is null?");
        }
    }
}
