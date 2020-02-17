package com.qw.event.flash;

import com.qw.conf.QuantityConf;
import com.qw.event.RedisConsumer;
import com.qw.event.order.OrderEvent;
import com.qw.model.FlashSale;
import com.qw.model.Order;
import com.qw.model.OrderGoods;
import com.qw.service.common.SmsTemplateService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.member.PointService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.qw.vo.order.OrderModel;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.event.EventKit;

import java.math.BigDecimal;

/**
 * 秒杀支付成功后的队列逻辑
 * 1. 订单属于正常订单，保存订单，扣减积分
 * 2. 订单属于异常订单，退款
 *
 * @author qw
 */
public class FlashPayMqSubscribe implements RedisConsumer {
    private Log log = Log.getLog(getClass());

    @Override
    public void onMsg(String msg) {
        log.info("秒杀支付消息队列：" + msg);
        Cache cache = Redis.use(QuantityConf.FLASH_SALE);
        OrderModel orderModel = cache.get(msg);
        handleFlash(orderModel);
    }

    private boolean handleFlash(OrderModel orderModel) {
        return Db.tx(() -> {
            // 保存订单
            Order order = orderModel.getOrder();
            order.save();
            OrderGoods orderGood = orderModel.getOrderGoodList().get(0);
            orderGood.setOrderId(order.getOrderId());
            orderGood.save();
            if (order.getOrderStatus() == 0) {
                handleSuccess(order, orderGood);
            } else if (order.getOrderStatus() == 3) {
                // 支付回调时，订单已经过期
                handleVoidable(orderModel);
            }
            return true;
        });
    }

    private void handleSuccess(Order order, OrderGoods orderGood) {
        // 扣减积分
        // PointService.me().consume(order);
        // 修改库存
        Integer activityId = orderGood.getActivityId();
        FlashSale flashSale = FlashSale.dao.findById(activityId);
        flashSale.setOrderNum(flashSale.getOrderNum() + 1);
        flashSale.setBuyNum(flashSale.getBuyNum() + 1);
        flashSale.update(false);
        // 发送微信公众号消息
        EventKit.post(new OrderEvent(order.getOrderId()));
    }

    /**
     * 处理过期订单逻辑
     */
    private void handleVoidable(OrderModel orderModel) {
        Order order = orderModel.getOrder();
        OrderGoods orderGood = orderModel.getOrderGoodList().get(0);
        // 如果还有库存，将过期订单还原回去
        if (FlashSaleService.me().payCallback(order.getUserId(), orderGood.getActivityId(), order.getMasterOrderSn())) {
            order.setOrderStatus(0);
            order.update();
            handleSuccess(order, orderGood);
            // 已经取消的订单，但是发现还有多余的库存，所以当做秒杀成功的订单，因为已经取消了，所以再把积分画出去
            PointService.me().consume(order);
        } else {
            // 如果没有库存，把钱原路退回去
            order.setOrderStatus(5);
            order.update();
            // 发起退款
            if (order.getOrderAmount().compareTo(new BigDecimal("0")) > 0) {
                OrderService.me().refundLanuch(order.getOrderId(), "秒杀库存已经卖完退款");
            }
            // 发短信通知，钱会原路退回去
            SmsTemplateService.me().voidablePay(order);
        }
    }
}