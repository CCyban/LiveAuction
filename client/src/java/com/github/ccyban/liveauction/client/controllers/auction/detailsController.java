package com.github.ccyban.liveauction.client.controllers.auction;

import com.github.ccyban.liveauction.client.models.classes.AuctionConnection;
import com.github.ccyban.liveauction.client.models.classes.ClientSubscriptionHandler;
import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.classes.AccountSession;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class detailsController implements Initializable {

    private AtomicReference<Auction> auction = new AtomicReference<>();

    @FXML
    private Button buttonBid;

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

    @FXML
    private Label labelBidWarning;

    @FXML
    private Button buttonAuctionFollow;

    @FXML
    private ListView listViewBiddingLog;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AccountSession accountSession = AccountSession.getAccountSession();
        auction.set(accountSession.getSelectedAuction());


        ClientSubscriptionHandler clientSubscriptionHandler = new ClientSubscriptionHandler(new SocketRequest(SocketRequestType.GetAuctionDetailsById, auction.get().getAuctionUUID(), null), auction, () -> onUINeedsUpdate());

        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
        auctionConnection.requestSocketData(clientSubscriptionHandler);

        // Auto client-side table updates (e.g. countdown)
        auctionConnection.setTimerTask((new TimerTask() {
            @Override
            public void run() {
                onUINeedsUpdate();
            }
        }));
    }

    @FXML
    private void onBackToList() {

        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
        auctionConnection.cancelTimerTask();
        auctionConnection.closeAllActiveSubscriptions();
        PageManager.loadPage(Page.AuctionList);
    }

    @FXML
    private void onBid() {
        if (auction != null) {
            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            auctionConnection.postBid(auction.get().getAuctionUUID(), new Bid(getNextBidAmount(), AccountSession.getAccountSession().accountSessionUUID));
        }
    }

    @FXML
    private void onFollow() {
        AuctionConnection.getAuctionConnection().followAuction(auction.get().getAuctionUUID());
    }

    public BigDecimal getNextBidAmount() {
        if (auction.get().getTopBid().amount.compareTo(new BigDecimal(0)) == 0) {
            return auction.get().getStartingBidPrice();
        }
        else {
            return auction.get().getTopBid().amount.add(auction.get().getIncrementalBidPace());
        }
    }

    private void onUINeedsUpdate() {
        Platform.runLater(() -> {
            UUID currentTopBidUserUUID = auction.get().getTopBid().userUUID;
            UUID accountSessionUUID = AccountSession.getAccountSession().accountSessionUUID;

            if (auction.get().getSecondsLeft() <= 0) {
                buttonBid.setDisable(true);
                labelAuctionHealth.setStyle("-fx-border-style: Dashed;" + "-fx-background-color: Tomato");
                labelAuctionHealth.setText("Status: Ended");
                labelHighestBid.setText("Final Bid: £" + auction.get().getTopBid().amount);

                if (currentTopBidUserUUID != null && currentTopBidUserUUID.equals(accountSessionUUID)) {
                    labelBidWarning.setText("You won the auction");
                }
                else {
                    labelBidWarning.setText("This auction has finished, you cannot bid anymore");
                }
            }
            else {
                labelHighestBid.setText("Current Bid: £" + auction.get().getTopBid().amount);

                if (currentTopBidUserUUID != null && currentTopBidUserUUID.equals(accountSessionUUID)) {
                    buttonBid.setDisable(true);
                    labelBidWarning.setText("You currently hold the top bid");
                }
                else {
                    buttonBid.setDisable(false);
                    labelBidWarning.setText("By choosing to bid, you will be bidding £" + getNextBidAmount());
                }
            }

            labelAuctionName.setText("Name: " + auction.get().getName());
            labelSeller.setText("Seller: " + auction.get().getSellerName());
            labelStartingBid.setText("Starting Bid: £" + auction.get().getStartingBidPrice());
            labelIncrementalPace.setText("Bidding Increments: £" + auction.get().getIncrementalBidPace());
            labelEndsIn.setText(auction.get().getTimeLeftStringProperty().get());

            buttonAuctionFollow.setText(auction.get().getUserFollowers().contains(accountSessionUUID) ? "★" : "☆");

            ArrayList<Bid> bids = auction.get().getBids();
            ArrayList<String> biddingLog = new ArrayList<>();
            biddingLog.add("Auction Started");
            for (Bid bid: bids) {
                if (bid.userUUID.equals(accountSessionUUID)) {
                    biddingLog.add(0, "You placed a bid worth £" + bid.amount);
                }
                else {
                    biddingLog.add(0, "Someone placed a bid worth £" + bid.amount);
                }
            }
            if (auction.get().getSecondsLeft() <= 0) {
                biddingLog.add(0, "Auction Finished");
            }
            listViewBiddingLog.setItems(FXCollections.observableList(biddingLog));
        });
    }
}
