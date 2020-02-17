package com.qw.event;

public interface RedisConsumer {

    void onMsg(String msg);

    default void onSubscribe(int subscribedChannels) {

    }

    default void onUnsubscribe(int subscribedChannels) {

    }

    default void onPMsg(String pattern, String message) {

    }

    default void onPUnsubscribe(String pattern, int subscribedChannels) {
    }

    default void onPSubscribe(String pattern, int subscribedChannels) {
    }

    default void onPong(String pattern) {
    }
}