package com.qw.event.flash;

public class FlashPayEvent {
    private String orderDetailKey;

    public FlashPayEvent(String orderDetailKey) {

        this.orderDetailKey = orderDetailKey;
    }

    public String getOrderDetailKey() {
        return orderDetailKey;
    }

    public void setOrderDetailKey(String orderDetailKey) {
        this.orderDetailKey = orderDetailKey;
    }
}
