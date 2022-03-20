package com.github.ccyban.liveauction.server.controllers;

import com.github.ccyban.liveauction.server.models.classes.ServerInstance;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class serverManagementController implements Initializable {

    @FXML
    private ListView listViewServerLog;

    @FXML
    private Label labelServerStatus;

    @FXML
    private Label labelActiveConcurrentConnections;

    @FXML
    private Button buttonBringOnline;

    @FXML
    private Button buttonBringOffline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> ob = FXCollections.observableArrayList("Test", "Test2", "Test3", "Test", "Test2", "Test3", "Test", "Test2", "Test3", "Test", "Test2", "Test3", "Test", "Test2", "Test3", "Test", "Test2", "Test3", "Test", "Test2", "Test3");
        listViewServerLog.setItems(ob);

        ServerInstance serverInstance = ServerInstance.getInstance();

        // Creating UI update hooks to be used
        Consumer<Boolean> updateStatusLabelConsumer = isOnline -> labelServerStatus.setText("Server Status: " + (isOnline ? "Online" : "Offline"));
        serverInstance.setStatusChangeConsumer(updateStatusLabelConsumer);

        Consumer<Number> updateActiveConcurrentConnectionsConsumer = activeConcurrentConnectionsAmount -> Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelActiveConcurrentConnections.setText("Active Concurrent Connections: " + activeConcurrentConnectionsAmount);
            }
        });
        serverInstance.setActiveConcurrentConnectionsChangeConsumer(updateActiveConcurrentConnectionsConsumer);
    }

    public void onBringOnline() {
        ServerInstance serverInstance = ServerInstance.getInstance();
        if(serverInstance.startServer()) {
            flipServerStatusButtonDisables();
        }
    }

    public void onBringOffline() {
        ServerInstance serverInstance = ServerInstance.getInstance();
        if (serverInstance.closeServer()) {
            flipServerStatusButtonDisables();
        }
    }

    public void flipServerStatusButtonDisables() {
        buttonBringOnline.setDisable(!buttonBringOnline.isDisable());
        buttonBringOffline.setDisable(!buttonBringOffline.isDisable());
    }
}
