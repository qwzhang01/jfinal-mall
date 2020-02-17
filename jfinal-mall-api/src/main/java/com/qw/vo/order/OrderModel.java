package com.qw.vo.order;

import com.qw.model.Order;
import com.qw.model.OrderGoods;

import java.io.Serializable;
import java.util.List;

/**
 * 一个完整的订单信息-对应数据库的信息
 */
public class OrderModel implements Serializable {
    private Order order;
    private List<OrderGoods> orderGoodList;

    public OrderModel() {
    }

    public OrderModel(Order order) {
        this.order = order;
    }

    public OrderModel(List<OrderGoods> orderGoodList) {
        this.orderGoodList = orderGoodList;
    }

    public OrderModel(Order order, List<OrderGoods> orderGoodList) {
        this.order = order;
        this.orderGoodList = orderGoodList;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderGoods> getOrderGoodList() {
        return orderGoodList;
    }

    public void setOrderGoodList(List<OrderGoods> orderGoodList) {
        this.orderGoodList = orderGoodList;
    }
}
