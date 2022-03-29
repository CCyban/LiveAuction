package com.github.ccyban.liveauction.server.models.classes;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

class ServerLogTest {

    @BeforeEach
    void setUp() {
        ServerLog.getInstance().restartLog();
    }

    @Test
    void getInstance() {
        // Creates one and returns it
        ServerLog serverLog = ServerLog.getInstance();
        Assert.assertNotNull(serverLog);

        // Gets the stored instance
        serverLog = ServerLog.getInstance();
        Assert.assertNotNull(serverLog);
    }

    @Test
    void log() throws InterruptedException {
        // Init JFX toolkit to avoid exception on JFX-based unit test
        JFXPanel fxPanel = new JFXPanel();

        String logMessage = "Some Log Message";
        ServerLog.getInstance().log(logMessage);

        ObservableList<String> serverLog = ServerLog.getInstance().getLog();

        waitForRunLater();

        Assert.assertEquals(2, serverLog.stream().count());
        Assert.assertEquals(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | " + logMessage, serverLog.get(0));
    }

    @Test
    void clientLog() throws InterruptedException {
        // Init JFX toolkit to avoid exception on JFX-based unit test
        JFXPanel fxPanel = new JFXPanel();

        String clientLogMessage = "Some Log Message";
        int randomHashCode = hashCode();

        ServerLog.getInstance().clientLog(randomHashCode, clientLogMessage);

        ObservableList<String> serverLog = ServerLog.getInstance().getLog();

        waitForRunLater();

        Assert.assertEquals(2, serverLog.stream().count());
        Assert.assertEquals(
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | " + " Client:" + randomHashCode
                        + " | " +  clientLogMessage, serverLog.get(0));
    }

    @Test
    void getLog() {
        ObservableList<String> serverLog = ServerLog.getInstance().getLog();

        Assert.assertEquals(1, serverLog.stream().count());
        Assert.assertEquals(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " | ✍ Log Started", serverLog.get(0));
    }

    void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> semaphore.release());
        semaphore.acquire();
    }
}