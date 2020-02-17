package com.qw.event.pay;

import com.qw.event.RedisConsumer;
import com.qw.model.*;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.prom.LotteryService;
import com.jfinal.log.Log;

import java.util.List;

/**
 * 一元抢购抽奖定时开奖 消息队列订阅端(消费方)
 */
public class LotteryMqSubscribe implements RedisConsumer {
    private Log log = Log.getLog(getClass());

    /**
     * 订阅 一元抢购抽奖人满开奖
     */
    @Override
    public void onMsg(String msg) {
        log.info("1元抢购抽奖活动消息队列" + msg);
        Order order = Order.dao.findById(msg);
        if (order == null) {
            throw new RuntimeException("拼团抽奖订单抽奖消息异常，订单数据不存在，传过来的ID = " + msg);
        }
        List<OrderGoods> orderGoods = OrderService.me().findOrderGood(order);
        OrderGoods orderGood = orderGoods.get(0);
        Integer activityId = orderGood.getActivityId();
        // 新增拼团抽奖订单
        LotteryOrder lotteryOrder = LotteryService.me().addByOrder(order);
        // 判断此时是否已人满
        LotteryGood lotteryGood = LotteryGood.dao.findById(activityId);
        Lottery lottery = Lottery.dao.findById(lotteryGood.getLotteryId());
        //定时开 不做处理，交给定时任务
        //定时开
        if (lottery.getType() == 1) {
            return;
        }
        //人满开
        boolean isMax = LotteryService.me().isMax(lotteryOrder.getActivityNum(), activityId);
        // 人不满 不做处理
        if (!isMax) {
            return;
        }
        List<LotteryOrder> list = LotteryService.me().findScreen(lotteryOrder);
        //人满，处理对应逻辑
        LotteryService.me().open(list, lotteryGood, lottery);
    }
}
