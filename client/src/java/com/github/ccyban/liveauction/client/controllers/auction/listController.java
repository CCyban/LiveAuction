package com.github.ccyban.liveauction.client.controllers.auction;

import com.github.ccyban.liveauction.client.models.classes.*;
import com.github.ccyban.liveauction.client.models.enumerations.Filter;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.Auction;
import com.github.ccyban.liveauction.shared.models.classes.Bid;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class listController implements Initializable {

    private ObservableList<Auction> auctionObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Auction> tableViewAuctions;

    @FXML
    private VBox vBoxAuctionFilters;

    private Filter selectedFilter = Filter.All;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAuctionTable();
        addFilterOptions();
    }

    private void loadAuctionTable() {
        loadAuctionColumnsIntoTable();
        loadAuctionItemsIntoList();
    }

    private void loadAuctionColumnsIntoTable() {
        // Set the TableColumns up for the TableView
        TableColumn<Auction, String> nameCol = new TableColumn<Auction, String>("Auction Name");
        nameCol.setPrefWidth(150);
        nameCol.setCellValueFactory(new PropertyValueFactory<Auction, String>("getNameString"));

        TableColumn<Auction, String> topBidCol = new TableColumn<Auction, String>("Top Bid");
        topBidCol.setPrefWidth(125);
        topBidCol.setCellValueFactory(new PropertyValueFactory<Auction, String>("getTopBidString"));

        TableColumn<Auction, String> timeLeftCol = new TableColumn<Auction, String>("Time Left");
        timeLeftCol.setPrefWidth(150);
        timeLeftCol.setCellValueFactory(new PropertyValueFactory<Auction, String>("getTimeLeftString"));

        TableColumn<Auction, String> favouritedCol = new TableColumn<>("★");
        favouritedCol.setPrefWidth(100);
        favouritedCol.setCellValueFactory(new PropertyValueFactory<Auction, String>("getHasFavouritedString"));

        // Add the constructed columns to the TableView
        tableViewAuctions.getColumns().addAll(nameCol, topBidCol, timeLeftCol, favouritedCol);

        // Hook up the observable list with the TableView
        tableViewAuctions.setItems(auctionObservableList);
    }

    private void loadAuctionItemsIntoList() {
        // Create a socket connection specifically for getting a list of all auctions
        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();

        ClientSubscriptionHandler clientSubscriptionHandler = new ClientSubscriptionHandler(new SocketRequest(SocketRequestType.GetListOfAllAuctions, null, null), auctionObservableList);
        auctionConnection.requestSocketData(clientSubscriptionHandler);

        // Auto client-side table updates (e.g. countdown)
        auctionConnection.setTimerTask((new TimerTask() {
            @Override
            public void run() {
                onTableUpdateTick();
            }
        }));
    }

    public void activateFilter(String filterStringForm) {
        Filter activatedFilter = getFilter(filterStringForm);
        selectedFilter = activatedFilter;
        setItemsWithFilterPredicate(activatedFilter);
    }

    public Filter getFilter(String stringForm) {
        for (Filter filter : Filter.values()) {
            if (stringForm.equals(filter.toString())) {
                return  filter;
            }
        }
        return null;
    }

    private void addFilterOptions() {
        ToggleGroup toggleGroupFilters = new ToggleGroup();

        for (Filter filter : Filter.values()) {

            // Some simple radio button configurations
            RadioButton radioButtonFilter = new RadioButton(filter.toString());
            radioButtonFilter.setWrapText(true);
            radioButtonFilter.setToggleGroup(toggleGroupFilters);
            radioButtonFilter.setFont(new Font(18));

            // Setting the default filter to be selected by default
            if (filter.equals(selectedFilter)) {
                radioButtonFilter.setSelected(true);
            }

            // Hook up an event handler
            radioButtonFilter.setOnAction(actionEvent -> activateFilter(((RadioButton) actionEvent.getSource()).getText()));

            // Adding it into the UI auction filters VBox
            vBoxAuctionFilters.getChildren().add(radioButtonFilter);
        }
    }

    private void setItemsWithFilterPredicate(Filter filter) {
        switch (filter) {
            case Ongoing:
                tableViewAuctions.setItems(auctionObservableList.filtered(AuctionListPredicates.predicateOnlyOngoings));
                break;
            case Finished:
                tableViewAuctions.setItems(auctionObservableList.filtered(AuctionListPredicates.predicateOnlyFinished));
                break;
            case PlacedABid:
                tableViewAuctions.setItems(auctionObservableList.filtered(AuctionListPredicates.predicateOnlyHasBid));
                break;
            case Favourited:
                tableViewAuctions.setItems(auctionObservableList.filtered(AuctionListPredicates.predicateOnlyFavourites));
                break;
            default:
                tableViewAuctions.setItems(auctionObservableList);
                break;
        }
    }

    private void onTableUpdateTick() {
        // Re-filters in case any values have changed
        setItemsWithFilterPredicate(selectedFilter);

        // Refreshes table on tick
        tableViewAuctions.refresh();

        System.out.println("refreshed table data");
    }

    @FXML
    private void onOpenAuction() {
        // todo: do check if an auction is selected

        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();
        auctionConnection.cancelTimerTask();
        auctionConnection.closeAllActiveSubscriptions();

        Auction selectedAuction = tableViewAuctions.getSelectionModel().getSelectedItem();

        if (selectedAuction != null) {

            UserSession userSession = UserSession.getUserSession();
            userSession.setSelectedAuction(selectedAuction);

            PageManager.loadPage(Page.AuctionDetails, userSession);
        }
    }
}
