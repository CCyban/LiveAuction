package com.github.ccyban.liveauction.client.models.classes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

public class PageValue {
    String _resource = null;
    String _title = null;

    public PageValue(String resource, String title) {
        _resource = resource;
        _title = title;
    }

    public Parent getRoot() {
        try {
            System.out.println("Loading: " + _resource + "...");
            return FXMLLoader.load(getClass().getResource(_resource));
        }
        catch (Exception e) {
            // TODO: Add proper error logging here
            System.out.println("Issue Loading: " + _resource);
            new Alert(Alert.AlertType.ERROR, "Failed to load the " + _title).show();
            return null;
        }
    }

    public String getTitle() {
        return _title;
    }
}
