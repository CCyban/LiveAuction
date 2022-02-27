package com.github.ccyban.liveauction.client.models.classes;

import java.util.Timer;
import java.util.TimerTask;

public class AuctionConnection {
    private static Timer tickTimer = new Timer();

    public static void setTimerTask(TimerTask timerTask) {
        tickTimer.schedule(timerTask, 0, 1000);
    }

    public static void cancelTimerTask() {
        tickTimer.cancel();
    }



    // This area is generally used to fetch current auction data from socket & feed it into the table each tick
}
