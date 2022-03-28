package com.github.ccyban.liveauction.client.controllers.authentication;

import com.github.ccyban.liveauction.client.models.classes.AuctionConnection;
import com.github.ccyban.liveauction.client.models.classes.ClientSubscriptionHandler;
import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.Account;
import com.github.ccyban.liveauction.shared.models.classes.SocketRequest;
import com.github.ccyban.liveauction.shared.models.enumerations.SocketRequestType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    @FXML
    private TextField textFieldUsernameInput;

    @FXML
    private PasswordField passwordFieldPasswordInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void onEnter(ActionEvent actionEvent) {
        signIn(textFieldUsernameInput.getText(), passwordFieldPasswordInput.getText());
    }

    public void onClick(ActionEvent actionEvent) {
        signIn(textFieldUsernameInput.getText(), passwordFieldPasswordInput.getText());
    }

    public void signIn(final String usernameInput, final String passwordInput) {
        System.out.println("Inputs: USERNAME|" + usernameInput + "| PASSWORD|" + passwordInput + "|");

        Account loginAttempt = new Account(usernameInput, passwordInput);

        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();

        if (auctionConnection.signIn(loginAttempt)) {
            new Alert(Alert.AlertType.INFORMATION, "Successful Login").show();
            onSuccessfulSignIn();
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Invalid details. Please try again.").show();
        }
    }

    public void onSuccessfulSignIn() {
        PageManager.loadPage(Page.AuctionList);

    }
}
