package com.qw.controller.app.order;

import cn.qw.base.RestController;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.model.PromOrder;
import com.qw.service.frontend.prom.PromOrderService;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Record;

/**
 * 订单促销活动
 */
public class PromOrderController extends RestController {
    /**
     * @param type|活动类型 0满额打折,1满额优惠金额,2满额送积分,3满额送优惠券,4满额送商品|int|必填
     * @title 活动是否开启
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam activity|活动是否进行中 1:进行 0:不进行|int|必填
     * @respParam goodsId|商品ID|int|选填
     * @respParam money|满赠额度|float|选填
     */
    public void getPropOrder() {
        Integer type = getParaToInt("type");
        PromOrder promOrder = PromOrderService.me().getPropOrder(type);
        Record record = new Record();
        if (promOrder != null) {
            record.set("activity", 1);
            record.set("goodsId", promOrder.get("goodsId"));
            record.set("money", promOrder.get("money"));
            renderJson(record);
        } else {
            renderJson(record.set("activity", 0));
        }
    }

    /**
     * 判断是否存在满5000赠智能门锁的活动  CartController  getPropOrder 这个方法可以代替这个
     *
     * @title 判断是否存在满5000赠智能门锁的活动
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam data|存在true 不存在false|boolean|必填
     */
    @Deprecated
    @Clear(RestSecurityInterceptor.class)
    public void hasProm() {
        boolean prom = PromOrderService.me().has5000Prom();
        renderJson(prom);
    }
}