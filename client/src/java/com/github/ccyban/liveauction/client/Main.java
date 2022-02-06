package com.github.ccyban.liveauction.client;

import com.github.ccyban.liveauction.client.models.classes.PageManager;
import com.github.ccyban.liveauction.client.models.enumerations.Page;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.github.ccyban.liveauction.client.models.classes.PageManager.loadPage;

public final class Main extends Application {
    @Override
    public void start(final Stage primaryStage) {
        // Initialising my own ApplicationManager to manage the application lifecycle
        PageManager.initialise(primaryStage, 480, 800);

        // Load the initial page of the application using the ApplicationManager
        loadPage(Page.Login);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
