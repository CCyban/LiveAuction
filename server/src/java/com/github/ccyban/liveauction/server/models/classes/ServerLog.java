package com.github.ccyban.liveauction.server.models.classes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.ServerSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServerLog {
    private static ServerLog uniqueInstance;

    private ObservableList<String> serverLog;

    private ServerLog() {
        serverLog = FXCollections.observableArrayList(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | ✍ Log Started");
    }

    public static ServerLog getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ServerLog();
        }

        return uniqueInstance;
    }

    public void log(String messageToLog) {
        Platform.runLater(() -> {

            serverLog.add(0, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | " + messageToLog);
        });
    }

    public void clientLog(int clientSocketHashCode, String clientMessageToLog) {
        log("Client:" + clientSocketHashCode + " | " +  clientMessageToLog);
    }

    public ObservableList<String> getLog() {
        return serverLog;
    }
}
