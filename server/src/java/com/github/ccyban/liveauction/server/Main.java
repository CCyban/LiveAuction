package com.github.ccyban.liveauction.server;

import com.github.ccyban.liveauction.server.models.classes.ServerInstance;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/serverManagement.fxml"));
        primaryStage.setTitle("Server Management");
        primaryStage.setScene(new Scene(root, 600, 800));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent event) {
                // Shut server thread and client thread pool down
                ServerInstance serverInstance = ServerInstance.getInstance();
                serverInstance.shutServerDown();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
