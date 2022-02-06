package com.github.ccyban.liveauction.client.controllers.authentication;

import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
        System.out.println("Inputs: USERNAME|" + usernameInput + "| PASSWORD|" + passwordInput + "|");

        // TODO: Auth logic here
        onSuccessfulSignIn();
    }

    public void onSuccessfulSignIn() {
        PageManager.loadPage(Page.AuctionList);
    }
}
