package cn.qw.plugin.event;

import java.util.TimerTask;

public class EventKit {
    public static void post(EventObject eventObject) {
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                eventObject.run();
            }
        });
    }
}
