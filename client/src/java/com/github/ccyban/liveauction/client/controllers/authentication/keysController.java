package com.github.ccyban.liveauction.client.controllers.authentication;

import com.github.ccyban.liveauction.client.models.classes.AuctionConnection;
import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class keysController implements Initializable {

    @FXML
    Button buttonExchangeKeys;

    @FXML
    Button buttonProceedToLogin;

    @FXML
    Label labelKeyStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onExchangeKeys() {
        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();

        if (auctionConnection.connectionEstablished) {
            auctionConnection.exchangeSecureKeys();

            if (auctionConnection.hasSentSecret) {
                buttonExchangeKeys.setDisable(true);
                buttonProceedToLogin.setDisable(false);
                labelKeyStatus.setText("Key Status: Exchanged.\nNow employing AES-encrypted Socket Communication.");
            }
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Server is offline. Unable to exchange keys.").show();
        }
    }

    @FXML
    public void onProceedToLogin() {
        PageManager.loadPage(Page.Login);
    }
}
