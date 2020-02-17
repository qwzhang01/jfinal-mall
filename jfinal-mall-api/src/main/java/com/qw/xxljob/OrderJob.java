package com.qw.xxljob;

import cn.qw.kit.DateKit;
import com.qw.model.Order;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.jfinal.log.Log;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;

import java.util.Calendar;
import java.util.List;

/**
 * 订单定时任务：0 30 01 * * ?
 * 1. 自动取消超过1天未支付的订单
 * 2. 自动删除超过30天的取消状态的订单
 * 3. 处理秒杀失效订单，redis中秒杀活动
 *
 * @author qw
 */
@JobHander
public class OrderJob extends IJobHandler {
    private final Log log = Log.getLog(getClass());

    @Override
    public ReturnT<String> execute(String... strings) throws Exception {
        log.error("定时任务OrderJob执行喽-----定时表达式 0 30 01 * * ?------执行时间：" + DateKit.today());
        handlerUnPay();
        handleCancel();
        handleFlash();
        return ReturnT.SUCCESS;
    }

    /**
     * 自动取消超过1天未支付的订单
     */
    private void handlerUnPay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        List<Order> orderList = OrderService.me().findUnPay(calendar.getTime(), 0);
        if (orderList != null && orderList.size() > 0) {
            orderList.stream().forEach(o -> OrderService.me().cancel(o));
        }
    }

    /**
     * 自动删除超过30天的取消状态的订单
     */
    private void handleCancel() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        List<Order> orderList = OrderService.me().findCancel(calendar.getTime());
        if (orderList != null && orderList.size() > 0) {
            orderList.forEach(o -> OrderService.me().delete(o));
        }
    }

    /**
     * 处理秒杀失效订单，redis中秒杀活动
     * 1. 删除过期超过1天的秒杀活动的商品
     */
    private void handleFlash() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        FlashSaleService.me().flushRedis(calendar.getTime());
    }
}