package com.qw.event.pay;

import com.qw.model.Order;

import java.io.Serializable;

public class PayEvent implements Serializable {
    private static final long serialVersionUID = 5996561485210365132L;

    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public PayEvent(Order order) {
        this.order = order;
    }
}