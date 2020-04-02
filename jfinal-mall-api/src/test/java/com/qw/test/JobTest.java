package com.qw.test;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobTest {

    public static void main(String[] args) {
        // threadMethod();
        // timerTest();
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        long delay = 2;

        long interval = 1;

        // 从现在开始 2 秒钟之后启动，每隔 1 秒钟执行一次
        service.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Test: " + Calendar.getInstance().getTime());
                    }
                }, delay,
                interval, TimeUnit.SECONDS);
    }

    private static void timerTest() {
        Timer timer = new Timer();
        long delay = 2000;
        long interval = 1000;

        // 从现在开始 2 秒钟之后启动，每隔 1 秒钟执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Test: " + Calendar.getInstance().getTime());
            }
        }, delay, interval);
    }

    private static void threadMethod() {
        new Thread(() -> {
            while (true) {
                System.out.println("Test: " + Calendar.getInstance().getTime());

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }).start();
    }
}
