package com.github.ccyban.liveauction.client.controllers.authentication;

import com.github.ccyban.liveauction.client.models.classes.AuctionConnection;
import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import com.github.ccyban.liveauction.shared.models.classes.Account;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
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
        Account loginAttempt = new Account(usernameInput, passwordInput);

        AuctionConnection auctionConnection = AuctionConnection.getAuctionConnection();

        Boolean isLoginSuccessful = auctionConnection.signIn(loginAttempt);
        if (isLoginSuccessful == null) {
            Platform.runLater(() -> {
                AuctionConnection.getAuctionConnection().lostConnection();
            });
        }
        else if (isLoginSuccessful) {
            onSuccessfulSignIn();
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Invalid details. Please try again.").show();
        }
    }

    public void onSuccessfulSignIn() {
        new Alert(Alert.AlertType.INFORMATION, "Successful Login").showAndWait();
        PageManager.loadPage(Page.AuctionList);
    }
}
