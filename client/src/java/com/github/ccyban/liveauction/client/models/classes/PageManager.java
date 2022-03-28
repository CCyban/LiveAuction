package com.github.ccyban.liveauction.client.models.classes;

import com.github.ccyban.liveauction.client.models.enumerations.Page;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.EnumMap;

public class PageManager {
    private static Stage stage;

    // Double braces creates an initialisation block from a created anonymous class
    private static EnumMap pageValues = new EnumMap<Page, PageValue>(Page.class) {{
        put(Page.Keys, new PageValue("/views/authentication/keys.fxml", "Exchange Keys"));
        put(Page.Login, new PageValue("/views/authentication/login.fxml", "Login Screen"));
        put(Page.AuctionList, new PageValue("/views/auction/list.fxml", "Auction List"));
        put(Page.AuctionPreview, new PageValue("/views/auction/preview.fxml", "Auction Preview"));
        put(Page.AuctionDetails, new PageValue("/views/auction/details.fxml", "Auction Details"));
    }};

    private final static class defaultResolution {
        public static int Height = 540;
        public static int Width = 900;
    };

    public static void initialise(Stage primaryStage, int defaultHeight, int defaultWidth) {
        stage = primaryStage;
        defaultResolution.Height = defaultHeight;
        defaultResolution.Width = defaultWidth;

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(final WindowEvent event) {
                AuctionConnection.cancelTimerTask();
            }
        });
    }

    public static void loadPage(final Page page) {
        loadPage(page, null);
    }

    public static void loadPage(final Page page, final AccountSession userData) {
        // Java EnumMaps are not generic so I have to cast each time I grab a value from it
        PageValue requestedPage = (PageValue) pageValues.get(page);

        stage.setTitle(requestedPage.getTitle());
        stage.setScene(new Scene(requestedPage.getRoot(), defaultResolution.Width, defaultResolution.Height));
        stage.setUserData(userData);
        stage.show();
    }
}
