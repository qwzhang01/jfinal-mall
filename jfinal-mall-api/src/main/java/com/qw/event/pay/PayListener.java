package com.qw.event.pay;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.qw.conf.QuantityConf;
import com.qw.model.Order;
import com.qw.model.Store;
import com.qw.model.User;
import com.qw.service.common.ConfigService;
import com.qw.service.common.SmsTemplateService;
import com.qw.service.frontend.member.UserService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.member.PointService;
import net.dreamlu.event.core.EventListener;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;

/**
 * 常规支付回调成功后的逻辑
 * 1. 发送短信，微信推送
 * 2. 处理积分赠送逻辑
 * 3. 处理抢购抽奖逻辑
 */
public class PayListener {
    private Log log = Log.getLog(getClass());
    private Cache cache = null;

    public PayListener() {
        this.cache = Redis.use(QuantityConf.BUTLER_QUENE);
    }

    @EventListener({PayEvent.class})
    public void onApplicationEvent(PayEvent event) {
        Order order = event.getOrder();
        // 给商家发送短信，给客户发送微信推送
        msg(order);
        // 处理赠送积分逻辑
        handlePoint(order);
        // 上级级别重新计算
        handleUpLevel(order);
        // 处理拼团抢购订单
        handleLotteryOrder(order);
    }

    /**
     * 1. 给店家发短信，提示有人付款了
     * 2. 给用户推送微信消息
     */
    private void msg(Order order) {
        // 微信通知
        SmsTemplateService.me().payWx(order);
        Store store = Store.dao.findById(order.getStoreId());
        String servicePhone = store.getServicePhone();
        if (StrKit.notBlank(servicePhone)) {
            // 商家发短信
            SmsTemplateService.me().paidSms(servicePhone, order);
        }
    }

    /**
     * 1. 购买商品赠送积分，根据商品配置信息，赠送对应积分
     * 2. 首单赠送上级积分，积分值为配置信息
     * 3.
     *
     * @param order
     */
    private void handlePoint(Order order) {
        // 购买商品赠送积分
        OrderService.me().givePoint(order);
        // 首单赠送上级消费积分
        if (OrderService.me().isFirstPay(order)) {
            // 判断是否是首单，赠送上级积分
            User user = User.dao.findById(order.getUserId());
            Integer firstLeader = user.getFirstLeader();
            if (firstLeader != null && firstLeader != 0) {
                String firstOrderForParent = ConfigService.me().findValue("point_config", "first_order_for_parent");
                PointService.me().addEffective(firstLeader, new BigDecimal("0"),  new BigDecimal(firstOrderForParent), 6, order.getOrderId());
            }
        }
        // 处理赚取积分逻辑
        handleEarnPoint(order);
    }

    /**
     * 重新计算要求人的用户级别
     * @param order
     */
    private void handleUpLevel(Order order) {
        User user = User.dao.findById(order.getUserId());
        if(user.getFirstLeader() != null && user.getFirstLeader() != 0) {
            UserService.me().calLevel(user.getFirstLeader());
        }
    }

    /**
     * 1.首次购买：
     * A 普通推广 1.5倍 1%每天；
     * B 推广1-3人 1.5倍 2%每天；
     * C 推广4人以上 1.5倍  3%每天
     * D 推广总销售额，不含自己的，小于自己首单，按1.5倍出局，大于等于自己的，按2倍出局  TODO 这个逻辑没有实现
     * <p>
     * 2. 再次购买： 1.5倍，每天1%
     */
    private void handleEarnPoint(Order order) {
        // 查询订单对应的商品可以赚取积分的总和，如果可以赚取积分额度大于0，则添加投资记录
        BigDecimal investAmount = OrderService.me().findInvestAmount(order);
        if (investAmount != null && investAmount.compareTo(new BigDecimal("0")) > 0) {
            // 添加积分
            PointService.me().invest(investAmount, order);
            // 1.5倍变双倍的情况 TODO 再处理
        }
    }

    /**
     * 判断是否是抢购抽奖活动，如果是，发送消息处理对应逻辑
     */
    private void handleLotteryOrder(Order order) {
        if (order.getActivityType() != 3) {
            return;
        }
        Jedis jedis = cache.getJedis();
        try {
            jedis.publish(LotteryMqSubscribe.class.getName(), String.valueOf(order.getOrderId()));
        } finally {
            cache.close(jedis);
        }
    }
}