package com.qw.service.bakend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReturnGoodsService extends BaseService {

    private static ReturnGoodsService service;

    private ReturnGoodsService() {
    }

    public static synchronized ReturnGoodsService me() {
        if (service == null) {
            service = new ReturnGoodsService();
        }
        return service;
    }

    public Page<Record> pageList(int pageNumber, int pageSize, String orderNo, String userMobile, String nickname, String goodName, Date orderStart, Date orderEnd, Integer status) {
        StringBuilder select = new StringBuilder("SELECT o.order_id orderId, o.order_sn orderNo, o.master_order_sn masterOrderNo, rg.goods_num goodNum, rg.refund_money refundMoney");
        select.append(", rg.reason, rg.`status`, rg.remark, FROM_UNIXTIME(rg.addtime, '%Y-%m-%d %H:%i') addTime");
        select.append(", FROM_UNIXTIME(rg.refund_time, '%Y-%m-%d %H:%i') refundTime, FROM_UNIXTIME(rg.checktime, '%Y-%m-%d %H:%i') checktime");
        select.append(", FROM_UNIXTIME(rg.receivetime, '%Y-%m-%d %H:%i') receivetime, FROM_UNIXTIME(rg.canceltime, '%Y-%m-%d %H:%i') canceltime");
        select.append(",(SELECT GROUP_CONCAT(og.goods_name ORDER BY og.rec_id ASC separator '\\r\\n') FROM butler_order_goods og where o.order_id=og.order_id GROUP BY og.order_id) title");
        // order_amount 订单金额 goods_price 商品金额
        select.append(",o.goods_price goodPrice, o.order_amount orderAmount");
        select.append(",IF(o.pay_time = 0, '', FROM_UNIXTIME(o.pay_time, '%Y-%m-%d %H:%i')) payTime, IF(o.add_time = 0, '', FROM_UNIXTIME(o.add_time, '%Y-%m-%d %H:%i')) orderTime");
        select.append(",u.mobile, u.nickname");
        select.append(",o.order_status orderStatus, o.pay_status payStatus, o.shipping_status shipStatus, o.pay_name payName, o.consignee, o.mobile, o.activity_type activityType");

        StringBuilder from = new StringBuilder(" FROM butler_return_goods rg");
        from.append(" LEFT JOIN butler_order_goods og ON og.rec_id = rg.rec_id");
        from.append(" LEFT JOIN butler_good g ON g.goods_id = og.goods_id");
        from.append(" LEFT JOIN butler_order o ON o.order_id = rg.order_id");
        from.append(" LEFT JOIN butler_user u ON u.user_id = o.user_id");
        from.append(" WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        if (!StrKit.isBlank(orderNo)) {
            from.append(" AND (o.order_sn LIKE ? OR o.master_order_sn LIKE ?)");
            paras.add("%" + orderNo + "%");
            paras.add("%" + orderNo + "%");
        }
        if (!StrKit.isBlank(userMobile)) {
            from.append(" AND u.mobile LIKE ?");
            paras.add("%" + userMobile + "%");
        }

        if (StrKit.notBlank(nickname)) {
            from.append(" AND u.nickname LIKE ?");
            paras.add("%" + nickname + "%");
        }
        if (StrKit.notBlank(goodName)) {
            from.append(" AND EXISTS (SELECT * FROM butler_order_goods WHERE butler_order_goods.order_id = o.order_id AND butler_order_goods.goods_name LIKE ?)");
            paras.add("%" + goodName + "%");
        }
        if (orderStart != null) {
            from.append(" AND o.add_time >= ?");
            paras.add(orderStart.getTime() / 1000);
        }
        if (orderEnd != null) {
            from.append(" AND o.add_time <= ?");
            paras.add(DateKit.lastSecondOfDay(orderEnd).getTime() / 1000);
        }
        if (status != null) {
            from.append(" AND rg.status = ?");
            paras.add(status);
        }

        from.append(" ORDER BY rg.id DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }
}