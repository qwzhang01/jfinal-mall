package com.qw.controller.web.cms;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import com.jfinal.plugin.activerecord.Record;
import com.qw.service.frontend.member.UserService;
import com.qw.service.frontend.order.OrderService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 首页管理
 */
@RequiresAuthentication
public class HomeController extends RestController {

    public void userInfo() {
        Record result = new Record();
        // 总人数
        result.set("totalCount", UserService.me().count(null, null));
        // 月新增人数
        result.set("mouthCount", UserService.me().count(DateKit.firstSecondOfMonth(new Date()), new Date()));
        // 周新增人数
        result.set("weekCount", UserService.me().count(DateKit.firstSecondOfWeek(new Date()), new Date()));
        // 日新增人数
        result.set("dayCount", UserService.me().count(DateKit.firstSecondOfDay(new Date()), new Date()));
        renderJson(result);
    }

    public void orderInfo() {
        Record result = new Record();
        // 待支付
        result.set("unPayCount", OrderService.me().count(null, null, 1));
        // 待发货
        result.set("unShipCount", OrderService.me().count(null, null, 2));
        // 待收成
        result.set("unReceverCount", OrderService.me().count(null, null, 4));
        // 累计支付订单数
        result.set("payCount", OrderService.me().count(null, null, 3));
        // 当日支付订单数
        result.set("dayPayCount", OrderService.me().count(DateKit.firstSecondOfDay(new Date()), new Date(), 3));
        // 本月订单累计总数
        result.set("monthPayCount", OrderService.me().count(DateKit.firstSecondOfMonth(new Date()), new Date(), 3));

        // 累计支付金额
        result.set("totalPayAmount", OrderService.me().amount(null, null));
        // 当日支付金额
        result.set("dayPayAmount", OrderService.me().amount(DateKit.firstSecondOfDay(new Date()), new Date()));
        // 本月订单累计金额
        result.set("monthPayAmount", OrderService.me().amount(DateKit.firstSecondOfMonth(new Date()), new Date()));

        renderJson(result);
    }

    public void orderStatic() {
        Record result = new Record();
        String[] xAxis = new String[6];
        Long[] count = new Long[6];
        BigDecimal[] amount = new BigDecimal[6];

        Calendar calendar = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            Date time = calendar.getTime();
            String month = DateKit.dateToString(time, "MM") + "月";
            xAxis[i] = month;
            count[i] = OrderService.me().count(DateKit.firstSecondOfMonth(time), DateKit.lastSecondOfMonth(time), 3);
            amount[i] = OrderService.me().amount(DateKit.firstSecondOfMonth(time), DateKit.lastSecondOfMonth(time));
            calendar.set(Calendar.DAY_OF_MONTH, -1);
        }

        result.set("xAxis", xAxis);
        result.set("count", count);
        result.set("amount", amount);
        renderJson(result);
    }
}