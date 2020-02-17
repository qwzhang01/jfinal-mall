package com.qw.event.good;

import cn.qw.kit.JsonKit;
import com.qw.event.RedisConsumer;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.member.UserSignService;
import com.qw.service.frontend.member.PointService;
import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品浏览消息队列订阅端
 * 客户浏览商品的时候，做的事情
 * 1. 记录用户浏览记录
 */
public class GoodVisitMqSubscribe implements RedisConsumer {
    private Log log = Log.getLog(getClass());

    /**
     * 订阅商品浏览消息
     */
    @Override
    public void onMsg(String msg) {
        log.info("商品查看消息队列：" + msg);
        GoodVisitEvent event = JsonKit.toBean(msg, GoodVisitEvent.class);
        int shareUserId = event.getShareUserId();
        if (shareUserId != 0) {
            // 有邀请人，走签到送积分的逻辑,一天只能领取一次
            if (!PointService.me().hasCheckIn(shareUserId, new Date())) {
                UserSignService.me().checkIn(shareUserId);
            }
        }
        // 记录浏览记录-保存浏览记录
        boolean visit = GoodService.me().visit(event.getUserId(), event.getGoodId(), event.getShareUserId());
        if (!visit) {
            throw new RuntimeException("记录商品浏览记录失败");
        }
    }
}