package com.qw.event.order;

import net.dreamlu.event.core.ApplicationEvent;

/**
 * 提交订单事件
 */
public class OrderEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public OrderEvent(Object source) {
        super(source);
    }
}