package com.qw.service.bakend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.qw.service.common.RegionService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService extends BaseService {
    private static OrderService service;

    private OrderService() {
    }

    public static synchronized OrderService me() {
        if (service == null) {
            service = new OrderService();
        }
        return service;
    }

    public Page<Record> pageList(Integer pageNumber, Integer pageSize
            , String storeName, String orderNo, String masterOrderNo
            , String userMobile
            , String orderStatus, String payStatus
            , Date orderStart, Date orderEnd
            , String leaderName
            , String nickname
            , String goodName
            , Integer activityType
            , Integer shipStatus
            , Date payDateRangeStart, Date payDateRangeEnd) {

        StringBuilder select = new StringBuilder("SELECT o.order_id orderId, o.order_sn orderNo, o.master_order_sn masterOrderNo, o.transaction_id transactionId, o.user_note");
        select.append(",(SELECT GROUP_CONCAT(CONCAT('名称：', og.goods_name, '；数量：', og.goods_num) ORDER BY og.rec_id ASC separator '\\r\\n') FROM butler_order_goods og where o.order_id=og.order_id GROUP BY og.order_id) title");
        // order_amount 订单金额 goods_price 商品金额
        select.append(",o.total_amount goodPrice, o.order_amount orderAmount, o.point_as_money, FROM_UNIXTIME(o.shipping_time,'%Y-%m-%d %H:%i') shipping_time");
        select.append(",e.name as expressName, o.shipping_code");
        // 各种分佣
        select.append(",o.order_amount*odr.butler_commission_rate butlerAmount, o.order_amount*odr.promote_commission_rate promoteAmount, odr.store_promote storePromoteAmount");
        // 店铺收入
        select.append(",(o.order_amount*(1-odr.butler_commission_rate)-odr.store_promote) storeIncome");
        // 支付时间
        select.append(",IF(o.confirm_time = 0, '', FROM_UNIXTIME(o.confirm_time, '%Y-%m-%d %H:%i')) confirmTime, IF(o.pay_time = 0, '', FROM_UNIXTIME(o.pay_time, '%Y-%m-%d %H:%i')) payTime, IF(o.add_time = 0, '', FROM_UNIXTIME(o.add_time, '%Y-%m-%d %H:%i')) orderTime");
        select.append(",s.store_name storeName");
        select.append(",u.mobile, u.nickname, leader.nickname leaderName");
        select.append(",o.order_status orderStatus, o.pay_status payStatus, o.shipping_status shipStatus, o.pay_name payName, o.consignee, o.mobile, o.activity_type activityType");
        select.append(",o.province,o.city,o.district,o.twon,o.address,o.zipcode");
        select.append(",(SELECT COUNT(*) FROM butler_user_balance_detail WHERE frozen_flag = 1 AND in_out_flag = 1 AND amount_type IN (1, 2, 3, 5, 8) AND object_id = o.order_id) AS unFrozenValanceCount");

        StringBuilder from = new StringBuilder(" FROM butler_order o");
        from.append(" LEFT JOIN butler_order_div_rate odr ON o.order_id=odr.order_id");
        from.append(" LEFT JOIN butler_store s ON o.store_id=s.store_id");
        from.append(" LEFT JOIN butler_user u ON o.user_id=u.user_id");
        from.append(" LEFT JOIN oms_express_company e ON e.id=o.shipping_name");
        from.append(" LEFT JOIN butler_user leader ON u.first_leader = leader.user_id");
        from.append(" WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        if (shipStatus != null) {
            from.append(" AND o.shipping_status = ?");
            paras.add(shipStatus);
        }
        if (payDateRangeStart != null) {
            Date date = DateKit.firstSecondOfDay(payDateRangeStart);
            from.append(" AND o.pay_time >= ?");
            paras.add(date.getTime() / 1000);
        }
        if (payDateRangeEnd != null) {
            Date date = DateKit.lastSecondOfDay(payDateRangeEnd);
            from.append(" AND o.pay_time < ?");
            paras.add(date.getTime() / 1000);
        }
        if (activityType > 0) {
            from.append(" AND o.activity_type = ?");
            paras.add(activityType);
        }
        if (!StrKit.isBlank(storeName)) {
            from.append(" AND s.store_name LIKE ?");
            paras.add("%" + storeName + "%");
        }
        if (!StrKit.isBlank(orderNo)) {
            from.append(" AND o.order_sn LIKE ?");
            paras.add("%" + orderNo + "%");
        }
        if (!StrKit.isBlank(masterOrderNo)) {
            from.append(" AND o.master_order_sn LIKE ?");
            paras.add("%" + masterOrderNo + "%");
        }
        if (!StrKit.isBlank(userMobile)) {
            from.append(" AND u.mobile LIKE ?");
            paras.add("%" + userMobile + "%");
        }
        if (!StrKit.isBlank(orderStatus)) {
            from.append(" AND o.order_status = ?");
            paras.add(orderStatus);
        }
        if (!StrKit.isBlank(payStatus)) {
            from.append(" AND o.pay_status = ?");
            paras.add(payStatus);
        }
        if (orderStart != null) {
            from.append(" AND o.add_time >= ?");
            paras.add(orderStart.getTime() / 1000);
        }
        if (orderEnd != null) {
            from.append(" AND o.add_time <= ?");
            paras.add(DateKit.lastSecondOfDay(orderEnd).getTime() / 1000);
        }
        if (StrKit.notBlank(nickname)) {
            from.append(" AND u.nickname LIKE ?");
            paras.add("%" + nickname + "%");
        }
        if (StrKit.notBlank(leaderName)) {
            from.append(" AND leader.nickname LIKE ?");
            paras.add("%" + leaderName + "%");
        }
        if (StrKit.notBlank(goodName)) {
            from.append(" AND EXISTS (SELECT * FROM butler_order_goods WHERE butler_order_goods.order_id = o.order_id AND butler_order_goods.goods_name LIKE ?)");
            paras.add("%" + goodName + "%");
        }
        from.append(" ORDER BY o.order_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            // 构建收货信息
            String consignee = r.getStr("consignee");
            String mobile = r.getStr("mobile");
            String address = RegionService.me().orderAddress(r.getInt("province"), r.getInt("city"), r.getInt("district"), r.getInt("twon"));
            if (StrKit.notBlank(r.getStr("address"))) {
                address += r.getStr("address");
            }
            String shippingAddress = "";
            shippingAddress += "收货人：" + consignee + "；";
            shippingAddress += "电话：" + mobile + "；";
            shippingAddress += "地址：" + address;
            r.set("shippingAddress", shippingAddress);
        }
        return page;
    }

    public Page<Record> refundPageList(int pageNumber, int pageSize, String orderNo, String userMobile, String storeName, String leaderName, String nickname, String goodName, Date orderStart, Date orderEnd) {
        StringBuilder select = new StringBuilder("SELECT o.order_id orderId, o.order_sn orderNo, o.master_order_sn masterOrderNo, o.transaction_id transactionId");
        select.append(",(SELECT GROUP_CONCAT(og.goods_name ORDER BY og.rec_id ASC separator '\\r\\n') FROM butler_order_goods og where o.order_id=og.order_id GROUP BY og.order_id) title");
        // order_amount 订单金额 goods_price 商品金额
        select.append(",o.total_amount goodPrice, o.order_amount orderAmount");
        // 各种分佣
        // 支付时间
        select.append(",IF(o.pay_time = 0, '', FROM_UNIXTIME(o.pay_time, '%Y-%m-%d %H:%i')) payTime, IF(o.add_time = 0, '', FROM_UNIXTIME(o.add_time, '%Y-%m-%d %H:%i')) orderTime");
        select.append(",s.store_name storeName");
        select.append(",u.mobile, u.nickname, leader.nickname leaderName");
        select.append(",o.order_status orderStatus, o.pay_status payStatus, o.shipping_status shipStatus, o.pay_name payName, o.consignee, o.mobile, o.activity_type activityType");

        StringBuilder from = new StringBuilder(" FROM butler_order o");
        from.append(" LEFT JOIN butler_order_div_rate odr ON o.order_id=odr.order_id");
        from.append(" LEFT JOIN butler_store s ON o.store_id=s.store_id");
        from.append(" LEFT JOIN butler_user u ON o.user_id=u.user_id");
        from.append(" LEFT JOIN butler_user leader ON u.first_leader = leader.user_id");
        from.append(" WHERE o.order_status = 5");

        List<Object> paras = new ArrayList<>();
        if (!StrKit.isBlank(storeName)) {
            from.append(" AND s.store_name LIKE ?");
            paras.add("%" + storeName + "%");
        }
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
        if (StrKit.notBlank(leaderName)) {
            from.append(" AND leader.nickname LIKE ?");
            paras.add("%" + leaderName + "%");
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

        from.append(" ORDER BY o.order_id DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }

    /**
     * 获取拼团抽奖订单列表
     */
    public Page<Record> lotteryPageList(int pageNumber, int pageSize, String orderNo, String userMobile,//
                                        Integer orderStatus, Integer payStatus, Date orderStart, Date orderEnd,//
                                        String nickname, String goodName, Integer activityStatus) {//
        StringBuilder select = new StringBuilder("SELECT o.order_sn orderSn, o.master_order_sn masterOrderSn, lo.is_win isWin, lo.open_time openTime");
        select.append(",o.order_amount orderAmount, o.order_status orderStatus, o.pay_status payStatus, lg.title, l.start_time startTime, l.end_time endTime");
        select.append(",l.max_num maxNum, l.min_num minNum, u.nickname");

        StringBuilder from = new StringBuilder(" FROM butler_lottery_order lo");
        from.append(" LEFT JOIN butler_order o ON o.order_id = lo.order_id");
        from.append(" LEFT JOIN butler_lottery_good lg ON lg.id = lo.lottery_good_id");
        from.append(" LEFT JOIN butler_good g ON lg.good_id = g.goods_id");
        from.append(" LEFT JOIN butler_lottery l ON l.id = lg.lottery_id");
        from.append(" LEFT JOIN butler_user u ON u.user_id = lo.user_id");
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

        if (orderStart != null) {
            from.append(" AND o.add_time >= ?");
            paras.add(orderStart.getTime() / 1000);
        }
        if (orderEnd != null) {
            from.append(" AND o.add_time <= ?");
            paras.add(DateKit.lastSecondOfDay(orderEnd).getTime() / 1000);
        }
        if (orderStatus != null) {
            from.append(" AND o.order_status = ?");
            paras.add(orderStatus);
        }
        if (payStatus != null) {
            from.append(" AND o.pay_status = ?");
            paras.add(payStatus);
        }
        if (StrKit.notBlank(nickname)) {
            from.append(" AND u.nickname LIKE ?");
            paras.add("%" + nickname + "%");
        }
        if (StrKit.notBlank(goodName)) {
            from.append(" AND lg.title LIKE ?");
            paras.add("%" + goodName + "%");
        }
        from.append(" ORDER BY lo.id DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }
}
