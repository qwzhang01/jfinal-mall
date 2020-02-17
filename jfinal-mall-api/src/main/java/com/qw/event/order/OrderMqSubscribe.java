package com.qw.event.order;

import com.qw.event.RedisConsumer;
import com.qw.model.Order;
import com.qw.service.common.SmsTemplateService;
import com.jfinal.log.Log;

/**
 * 下单后的消息队列
 * 1. 下单成功，发送微信公众号推送
 */
public class OrderMqSubscribe implements RedisConsumer {
    private Log log = Log.getLog(getClass());

    @Override
    public void onMsg(String msg) {
        Integer orderId = Integer.valueOf(msg);
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            log.error("订单生成异常，返回的订单ID：" + msg);
            return;
        }
        if (!SmsTemplateService.me().order(order)) {
            log.error("发送微信公众号推送失败");
        }
    }
}