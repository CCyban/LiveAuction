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
import java.util.concurrent.atomic.AtomicReference;

public class detailsController implements Initializable {

    private AtomicReference<Auction> auction = new AtomicReference<>();

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
        auction.set(userSession.getSelectedAuction());

        ClientSubscriptionHandler clientSubscriptionHandler = new ClientSubscriptionHandler(new SocketRequest(SocketRequestType.GetAuctionDetailsById, auction.get().getAuctionUUID(), null), auction, () -> onUINeedsUpdate());
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
        PageManager.loadPage(Page.AuctionList);
    }

    @FXML
    private void onBid() {
        if (auction != null) {
            BigDecimal newBidAmount = auction.get().getTopBid().add(auction.get().getIncrementalBidPace());

            AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
            auctionConnection.postBid(auction.get().getAuctionUUID(), new Bid(newBidAmount, UUID.randomUUID()));
        }
    }

    private void onUINeedsUpdate() {
        Platform.runLater(() -> {
            labelHighestBid.setText("Current Bid: £" + auction.get().getTopBid());
            labelAuctionName.setText("Name: " + auction.get().getName());
            labelEndsIn.setText(auction.get().getTimeLeftStringProperty().get());
            labelAuctionHealth.setText("Status: " + (auction.get().getSecondsLeft() > 0 ? "Ongoing" : "Finished"));
            labelSeller.setText("Seller: " + auction.get().getSellerName());
            labelStartingBid.setText("Starting Bid: £" + auction.get().getStartingBidPrice());
            labelIncrementalPace.setText("Bidding Increments: £" + auction.get().getIncrementalBidPace());
        });
    }
}
