package com.qw.service.frontend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.DateKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.conf.ButlerEmnu;
import com.qw.event.order.OrderEvent;
import com.qw.model.*;
import com.qw.service.common.RegionService;
import com.qw.service.frontend.member.BalanceService;
import com.qw.service.frontend.member.UserAddressService;
import com.qw.service.frontend.member.PointService;
import com.qw.service.frontend.prom.CouponService;
import com.qw.vo.order.OrderDetailVo;
import com.qw.vo.order.OrderModel;
import net.dreamlu.event.EventKit;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 订单server
 *
 * @author qw
 */
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

    /**
     * 获取我的投资的分页列表
     *
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public Page<Record> investPage(Integer userId, int pageNumber, int pageSize) {
        String select = "SELECT o.order_id, o.order_sn, u.nickname";
        String from = " FROM butler_point_invest i";
        from += " LEFT JOIN butler_user u ON u.user_id = i.user_id";
        from += " LEFT JOIN butler_order o ON o.order_id = i.order_id";
        from += " WHERE i.user_id = ? ORDER BY i.create_time DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, userId);
        List<Record> list = page.getList();
        for (Record r : list) {
            r = getGoodDetail(r);
        }
        return page;
    }

    public Page<Record> subInvestPage(int pageNumber, int pageSize) {
        Integer userId = AppIdKit.getUserId();

        String select = "SELECT o.order_id, o.order_sn, u.nickname";
        String from = " FROM butler_point_invest i";
        from += " LEFT JOIN butler_user u ON u.user_id = i.user_id";
        from += " LEFT JOIN butler_order o ON o.order_id = i.order_id";
        from += " WHERE u.first_leader = ? ORDER BY i.create_time DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, userId);
        List<Record> list = page.getList();
        for (Record r : list) {
            getGoodDetail(r);
        }
        return page;
    }

    private Record getGoodDetail(Record invest) {
        List<Record> oDetil = findInvestOrderGood(invest.getLong("order_id"));
        String goodName = "";
        BigDecimal price = new BigDecimal("0");
        for (Record od : oDetil) {
            goodName += "；";
            goodName += od.getStr("goods_name").split(" ")[0];
            price = price.add(od.getBigDecimal("final_price"));
        }
        invest.set("good_name", goodName.substring(1));
        invest.set("good_price", price);
        return invest;
    }

    /**
     * 查询投资订单详情
     */
    private List<Record> findInvestOrderGood(Long orderId) {
        String sql = "SELECT od.goods_name, od.final_price FROM butler_order_goods od LEFT JOIN butler_good g ON g.goods_id = od.goods_id  WHERE g.is_earn_point = 1 AND od.order_id = ?";
        return Db.find(sql, orderId);
    }

    /**
     * 1元抢购抽奖、秒杀退款
     */
    public void refundLanuch(Integer orderId, String reason) {
        Order order = Order.dao.findById(orderId);
        if (order.getPayCode().equals("1")) {
            //支付宝 支付(金额单位:元)
            //支付宝退款处理
            PayService.me().aLiRefund(order, null, reason);
        }
        if (order.getPayCode().equals("2")) {
            //微信 支付(金额单位:分)
            //微信退款处理
            PayService.me().wxRefundLanuch(order, null, reason);
        }
        // 返回分润
        BalanceService.me().cancelPay(order);
    }

    /**
     * 已付款的，发起退款申请
     */
    public boolean refund(Order order) {
        User user = User.dao.findById(order.getUserId());
        order.setUserNote("用户退款订单");
        order.setConsignee(user.getNickname());
        order.setMobile(user.getMobile());
        order.setOrderStatus(5);

        return Db.tx(() -> {
            // 取消订单
            order.saveOrUpdate();
            // 生成退款退货记录
            ReturnGoodsService.me().add(order);
            return true;
        });
    }

    /**
     * 客户端我的页面进入的订单列表<br />
     *
     * <p>订单状态逻辑：</p>
     * 1. 正常流程 下单 支付 发货 收货 完成<br />
     * 2. 未支付的取消流程 下单 取消<br />
     * 3. 已支付未发货的退款流程 下单 支付  退款中 同意退款 退款完成<br />
     * 4. 已支付已发货的退款流程 下单 支付 发货 收货 退款中 同意退款 寄回 收货 退款完成<br />
     * 对应的数据库逻辑字段
     * order_status  订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单-有点类似于订单类型，标识订单处于哪个流程中
     * pay_status 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转
     * shipping_status 发货状态0未发货 1已发货 2已收货 -发货流程中订单流转状态
     * return_status 退货状态 -2用户取消-1不同意0待审核1通过2已发货3待退款4换货完成5退款完成6申诉仲裁 这个流程在另一张表里面 -退款流程中的状态流转
     */
    public Page<Record> pageList(Integer userId, int type, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT o.master_order_sn masterOrderSn, o.order_sn orderSn, o.order_id orderId, o.pay_status payStatus, o.is_comment");
        select.append(",o.order_status orderStatus, o.shipping_status shippingStatus, o.order_id orderId, o.order_amount orderAmount, o.shipping_price shippingPrice");
        select.append(",o.point_as_money pointAsMoney");
        select.append(",s.store_id storeId, s.store_name storeName");
        StringBuilder from = new StringBuilder(" FROM butler_order o");
        from.append(" LEFT JOIN  butler_store s ON s.store_id = o.store_id");
        from.append(" WHERE o.user_id = ?");

        List<Object> paras = new ArrayList<>();
        paras.add(userId);
        if (type != 0) {
            // 待付款
            if (type == 1) {
                from.append(" AND o.order_status = 0 AND o.pay_status = 0");
            }
            // 待发货
            if (type == 2) {
                from.append(" AND o.order_status = 0 AND o.pay_status = 1 AND o.shipping_status = 0");
            }
            // 待收货
            if (type == 3) {
                from.append(" AND o.order_status = 0 AND o.pay_status = 1 AND o.shipping_status = 1");
            }
            // 待评价
            if (type == 4) {
                from.append(" AND o.order_status = 0 AND o.pay_status = 1 AND o.shipping_status = 2 AND o.is_comment = 0");
            }
            // 退换货
            if (type == 5) {
                from.append(" AND o.order_status = 5");
            }
        }

        from.append(" ORDER BY o.order_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record o : list) {
            orderGood(o);
            calStatus(o);
        }
        return page;
    }

    public Record detail(Integer orderId) {
        StringBuilder sql = new StringBuilder("SELECT");
        sql.append(" o.pay_status payStatus, o.order_status orderStatus, o.shipping_status shippingStatus, o.is_comment");
        sql.append(",o.order_sn orderSn, o.order_id orderId, o.order_amount orderAmount, o.shipping_price shippingPrice");
        sql.append(",FROM_UNIXTIME(o.add_time,'%Y-%m-%d %H:%m:%s') addTime, o.pay_name payName, o.shipping_name shippingName");
        sql.append(",o.point_as_money pointAsMoney, o.activity_type");
        sql.append(",o.country, o.province, o.city, o.district, o.twon, o.address, o.mobile");
        sql.append(",s.store_id storeId, s.store_name storeName");
        sql.append(",u.nickname");
        sql.append(" FROM butler_order o");
        sql.append(" LEFT JOIN  butler_store s ON s.store_id = o.store_id");
        sql.append(" LEFT JOIN  butler_user u ON u.user_id = o.user_id");
        sql.append(" WHERE o.order_id = ? LIMIT 1");
        Record order = Db.findFirst(sql.toString(), orderId);
        if (order == null) {
            return null;
        }

        // 获取orderGoods
        order = orderGood(order);
        // 计算订单状态
        order = calStatus(order);
        // 发票
        order = calInviceType(order);
        // 收货地址
        order = calAddress(order);
        return order;
    }

    private Record calAddress(Record order) {
        String address = RegionService.me().orderAddress(order.getInt("province"), order.getInt("city"),
                order.getInt("district"), order.getInt("twon"));
        order.set("address", address + order.getStr("address"));
        order.remove("country");
        order.remove("province");
        order.remove("city");
        order.remove("district");
        order.remove("twon");

        return order;
    }

    private Record calInviceType(Record order) {
        Object invoiceType = order.get("invoiceType");
        order.remove("invoiceType");
        if (invoiceType != null) {
            if (invoiceType.equals(ButlerEmnu.InvoiceTypeEnum.Normal.getValue())) {
                order.set("invoiceType", ButlerEmnu.InvoiceTypeEnum.Normal.getName());
            }
            if (invoiceType.equals(ButlerEmnu.InvoiceTypeEnum.Electronic.getValue())) {
                order.set("invoiceType", ButlerEmnu.InvoiceTypeEnum.Normal.getName());
            }
            if (invoiceType.equals(ButlerEmnu.InvoiceTypeEnum.ValueAdded.getValue())) {
                order.set("invoiceType", ButlerEmnu.InvoiceTypeEnum.Normal.getName());
            }
        }
        return order;
    }

    private Record calStatus(Record order) {
        // 订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单-有点类似于订单类型，标识订单处于哪个流程中
        Integer orderStatus = order.getInt("orderStatus");
        // 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转
        Integer payStatus = order.getInt("payStatus");
        // 发货状态0未发货 1已发货 2已收货 -发货流程中订单流转状态
        Integer shippingStatus = order.getInt("shippingStatus");
        // 是否评价（0：未评价；1：已评价）
        Integer is_comment = order.getInt("is_comment");

        order.remove("payStatus");
        order.remove("orderStatus");
        order.remove("shippingStatus");
        order.remove("is_comment");
        // 转化后的订单
        // 1待付款 2待发货 3待收货 4 待评价 5退款中 6已退款 7已关闭 8已完成
        // 1待付款（取消订单、去支付） 2待发货（申请退款、提醒发货） 3待收货（申请退款、查看物流、确认收货） 4 待评价（申请退款、去评价） 5退款中 6已退款 7已关闭 8已完成
        if (orderStatus == 0) {
            if (payStatus == 0) {
                order.set("status", 1);
            } else if (payStatus == 1) {
                if (shippingStatus == 0) {
                    order.set("status", 2);
                } else if (shippingStatus == 1) {
                    order.set("status", 3);
                } else if (shippingStatus == 2) {
                    if (is_comment == 0) {
                        order.set("status", 4);
                    } else {
                        // 评价完的订单显示已完成
                        order.set("status", 8);
                    }
                }
            }
        } else if (orderStatus == 3) {
            order.set("status", 7);
        } else if (orderStatus == 4) {
            order.set("status", 8);
        } else if (orderStatus == 5) {
            // 目前没有退款流程，以后再处理这里的逻辑 ，这里分为退款中 退款完成
            order.set("status", 5);
        } else {
            order.set("status", 0);
        }
        return order;
    }

    private Record orderGood(Record order) {
        Integer orderId = order.getInt("orderId");
        String sql = "SELECT og.goods_name goodName, og.spec_key_name specName, og.goods_num goodNum, og.final_price finalPrice"//
                + ", og.goods_id goodId, og.goods_price goodPrice, g.original_img imgPath"//
                + " FROM butler_order_goods og"//
                + " LEFT JOIN butler_good g ON g.goods_id = og.goods_id"//
                + " WHERE og.order_id = ? ORDER BY og.rec_id DESC";//
        List<Record> list = Db.find(sql, orderId);

        for (Record r : list) {
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                r.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }

        long goodNum = list.stream().map(o -> o.getInt("goodNum")).collect(Collectors.summarizingInt(n -> n.intValue()))
                .getSum();
        order.set("goodTotalNum", goodNum);
        order.set("orderGood", list);
        return order;
    }

    public Page<Record> inviteUserPageList(Integer userId, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder(
                "SELECT o.user_id userId, od.goods_id goodId, od.order_id orderId, od.goods_num goodNum, g.goods_name goodName,");
        select.append("od.spec_key specKey, FROM_UNIXTIME(o.pay_time, '%m-%d') payTime, o.order_amount orderAmount");
        StringBuilder from = new StringBuilder(" FROM butler_order o");
        from.append(" LEFT JOIN butler_order_goods od ON od.order_id = o.order_id");
        from.append(" LEFT JOIN butler_good g ON g.goods_id = od.goods_id");
        from.append(" LEFT JOIN butler_user u ON u.user_id = o.user_id");
        from.append(" WHERE o.pay_status = 1 AND u.first_leader = ?");

        List<Object> paras = new ArrayList<>();
        paras.add(userId);
        from.append(" ORDER BY o.pay_time DESC");

        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        // , (od.goods_num * gc.commission_price) commissionPrice
        List<Record> list = page.getList();
        for (Record r : list) {
            Integer goodId = r.getInt("goodId");
            String specKey = r.getStr("specKey");
            Integer orderId = r.getInt("orderId");
            Map<String, Object> search = new HashMap<>();
            search.put("goods_id", goodId);
            search.put("order_id", orderId);
            search.put("agent_role_id", 5);
            if (StrKit.notBlank(specKey) && !"0".equals(specKey)) {
                search.put("spec_key", specKey);
            }
            // 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费
            // 1收入 2支出
            BigDecimal commission = BalanceService.me().orderAchiveTotal(r.getInt("orderId"));
            if (commission != null) {
                r.set("commissionPrice", commission);
            } else {
                r.set("commissionPrice", 0);
            }
        }
        return page;
    }

    /**
     * 生成订单编号
     *
     * @param activityType 1 普通订单 2 秒杀订单
     * @return
     */
    private String genSn(int activityType) {
        String prefix = "O";
        if (2 == activityType) {
            prefix = "F";
        }
        String sn;
        while (true) {
            String timeStamp = DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
            int random = (int) ((Math.random() * 9 + 1) * 1000);
            sn = timeStamp + random;
            Long count = Db.queryLong("SELECT COUNT(*) FROM butler_order WHERE order_sn = ? OR master_order_sn = ?", prefix + sn, prefix + sn);
            if (count <= 0) {
                break;
            }
        }
        return prefix + sn;
    }

    public List<Order> findByOrderSn(String orderSn) {
        String sql = "SELECT * FROM butler_order WHERE order_sn = ? OR master_order_sn = ?";
        return Order.dao.find(sql, orderSn, orderSn);
    }

    /**
     * 待付款订单数量
     */
    public Long waitPayCount(Integer userId) {
        // 正常流程中的未支付订单
        String sql = "SELECT COUNT(*) FROM butler_order o WHERE order_status = 0 AND pay_status = 0 AND user_id = ?";
        return Db.queryLong(sql, userId);
    }

    /**
     * 待发货订单数量
     */
    public Long waitSendCount(Integer userId) {
        // 正常流程中未发货的订单
        String sql = "SELECT COUNT(*) FROM butler_order o WHERE order_status = 0 AND pay_status = 1 AND shipping_status = 0  AND user_id = ?";
        return Db.queryLong(sql, userId);
    }

    /**
     * 待收货订单数量
     */
    public Long waitReciveCount(Integer userId) {
        String sql = "SELECT COUNT(*) FROM butler_order o WHERE order_status = 0 AND pay_status = 1 AND shipping_status = 1 AND user_id = ?";
        return Db.queryLong(sql, userId);
    }

    /**
     * 未评论评论订单数量
     */
    public Long unCommentCount(Integer userId) {
        String sql = "SELECT COUNT(*) FROM butler_order o WHERE order_status = 0 AND pay_status = 1 AND shipping_status = 2 AND o.is_comment = 0 AND user_id = ?";
        return Db.queryLong(sql, userId);
    }

    public Long cartCount(Integer userId) {
        String sql = "SELECT COUNT(*) FROM butler_cart WHERE user_id = ?";
        return Db.queryLong(sql, userId);
    }

    public Long inviteOrderTotalCount(User user) {
        String sql = "SELECT COUNT(*) as countResult FROM butler_order o LEFT JOIN butler_user u ON u.user_id = o.user_id WHERE o.pay_status = 1 AND u.first_leader = ?";
        return Db.queryLong(sql, user.getUserId());
    }

    /**
     * 获取指定用户超过指定时间没有支付的订单
     */
    public List<Order> findUnPay(Date time, Integer userId) {
        String sql = "SELECT * FROM butler_order WHERE pay_status = 0 AND order_status = 0 AND create_time <= ?";
        List<Object> paras = new ArrayList<>();
        paras.add(time);
        if (userId != null && userId != 0) {
            sql += " AND user_id = ?";
            paras.add(userId);
        }
        return Order.dao.find(sql, paras.toArray());
    }

    /**
     * 未付款订单，申请取消订单
     */
    public boolean cancel(Order order) {
        return Db.tx(() -> {
            // 退还优惠券
            CouponService.me().cancelOrder(order);

            // 修改订单状态
            order.setOrderStatus(3);
            order.setCancelTime(new Date());
            order.update();
            return PointService.me().cancelOrder(order);
        });
    }

    /**
     * 获取超过指定时间已经取消的订单
     */
    public List<Order> findCancel(Date time) {
        String sql = "SELECT * FROM butler_order WHERE pay_status = 0 AND order_status = 3 AND cancel_time <= ?";
        return Order.dao.find(sql, time);
    }

    /**
     * 删除订单
     */
    public boolean delete(Order order) {
        return Db.tx(() -> {
            List<OrderGoods> orderGoodsList = OrderGoods.dao.find("SELECT * FROM butler_order_goods WHERE order_id = ?", order.getOrderId());
            List<OrderDivRate> orderDivRateList = OrderDivRate.dao.find("SELECT * FROM butler_order_div_rate WHERE order_id = ?", order.getOrderId());
            List<LotteryOrder> lotteryOrderList = LotteryOrder.dao.find("SELECT * FROM butler_lottery_order WHERE order_id = ?", order.getOrderId());
            orderGoodsList.forEach(o -> o.delete());
            orderDivRateList.forEach(o -> o.delete());
            lotteryOrderList.forEach(o -> o.delete());
            return order.delete();
        });
    }


    /**
     * 根据订单，获取订单详情列表
     */
    public List<OrderGoods> findOrderGood(Order order) {
        String sql = "SELECT * FROM  butler_order_goods WHERE order_id = ?";
        return OrderGoods.dao.find(sql, order.getOrderId());
    }

    /**
     * 重复多余的方法
     */
    @Deprecated
    public List<Record> findGood(Order order) {
        List<OrderGoods> list = findOrderGood(order);
        return list.stream().map(og -> {
            Record r = new Record();
            r.set("goodName", og.getGoodsName());
            r.set("goodId", og.getGoodsId());
            r.set("finalPrice", og.getFinalPrice());
            r.set("goodImg", PropKit.get("fileHost") + Good.dao.findById(og.getGoodsId()).getOriginalImg());
            return r;
        }).collect(Collectors.toList());
    }

    public List<ReturnGoods> findReturn(Order order) {
        // -2用户取消-1不同意0待审核1通过2已发货3待退款4换货完成5退款完成6申诉仲裁
        String sql = "SELECT * FROM butler_return_goods WHERE order_id = ? AND user_id = ? AND status <> 5";
        return ReturnGoods.dao.find(sql, order.getOrderId(), order.getUserId());
    }

    /**
     * 评论订单
     */
    public boolean comment(Order order, List<Comment> goodComment, BigDecimal sellerScore, BigDecimal logisticsScore) {
        Map<String, Object> param = searchParam("order_id", order.getOrderId());
        param = searchParam(param, "user_id", order.getUserId());
        param = searchParam(param, "deleted", 0);
        OrderComment comment = OrderComment.dao.searchFirst(param);
        if (comment == null) {
            comment = new OrderComment();
        }
        comment.setSellerScore(sellerScore);
        comment.setLogisticsScore(logisticsScore);
        comment.setOrderId(order.getOrderId());
        comment.setUserId(order.getUserId());
        comment.setStoreId(order.getStoreId());
        comment.setCommemtTime(System.currentTimeMillis() / 1000);

        OrderComment finalComment = comment;
        return Db.tx(() -> {
            finalComment.saveOrUpdate(false);
            order.setIsComment(1);
            order.update(false);
            Db.batchSave(goodComment, goodComment.size());
            return true;
        });
    }

    /**
     * 确认收货
     */
    public boolean confirm(Order order) {
        order.setShippingStatus(2);
        order.setConfirmTime(new Integer(String.valueOf(System.currentTimeMillis() / 1000)));
        return order.update(false);
    }

    /**
     * 判断是否是支付的第一单
     */
    public boolean isFirstPay(Order order) {
        String sql = "SELECT order_id FROM butler_order WHERE user_id = ? AND order_status = 0 AND pay_status = 1 ORDER BY pay_time ASC LIMIT 1";
        return order.getOrderId().equals(Db.queryInt(sql, order.getUserId()));
    }

    /**
     * 退换货/退款订单 查看详情
     */
    public Record returnGoodsDetail(Integer id) {
        StringBuilder sql = new StringBuilder("SELECT rg.id id, rg.order_id orderId, rg.order_sn orderSn, rg.goods_id goodsId");
        sql.append(",rg.goods_num goodsNum, rg.type type, rg.reason reason, rg.`status`+0 status");
        sql.append(",rg.remark remark, rg.spec_key specKey, rg.refund_money refundMoney, rg.refund_type+0 refundType");
        sql.append(",FROM_UNIXTIME(rg.refund_time,'%Y-%m-%d %H:%m:%s') refundTime, FROM_UNIXTIME(rg.addtime,'%Y-%m-%d %H:%m:%s') addtime");
        sql.append(",FROM_UNIXTIME(rg.checktime,'%Y-%m-%d %H:%m:%s') checktime, FROM_UNIXTIME(rg.receivetime,'%Y-%m-%d %H:%m:%s') receivetime");
        sql.append(" FROM butler_return_goods rg");
        sql.append(" WHERE rg.id = ? LIMIT 1");
        return Db.findFirst(sql.toString(), id);
    }

    /**
     * 获取订单可以获取积分总额
     */
    public BigDecimal findInvestAmount(Order order) {
        String sql = "SELECT  SUM(od.final_price) FROM butler_order_goods od"
                + " LEFT JOIN butler_good g ON od.goods_id = g.goods_id"
                + " WHERE g.is_earn_point = 1 AND od.order_id = ?";
        return Db.queryBigDecimal(sql, order.getOrderId());
    }

    /**
     * 购买成功后赠送积分
     *
     * @param order
     * @return
     */
    public boolean givePoint(Order order) {
        return Db.tx(() -> {
            List<OrderGoods> orderGood = findOrderGood(order);
            IntSummaryStatistics summaryStatistics = orderGood.stream().map(og -> {
                Good good = Good.dao.findById(og.getGoodsId());
                og.setGiveIntegral(og.getGoodsNum() * good.getGiveIntegral());
                og.update();
                return og.getGiveIntegral();
            }).collect(Collectors.summarizingInt(i -> i));
            long sum = summaryStatistics.getSum();
            return PointService.me().addEffective(order.getUserId(), new BigDecimal("0"), new BigDecimal(String.valueOf(sum)), 8, order.getOrderId());
        });
    }

    /**
     * 保存订单
     * isCart 1是2否
     */
    public Record save(List<OrderModel> orderModelList, Integer isCart) {
        String mastOrderSn = genSn(1);
        AtomicReference<BigDecimal> realPayPrice = new AtomicReference<>(new BigDecimal("0"));
        AtomicReference<BigDecimal> pointAsMoney = new AtomicReference<>(new BigDecimal("0"));
        orderModelList.stream().forEach(o -> {
            Order order = o.getOrder();
            order.setMasterOrderSn(mastOrderSn);
            order.save();
            o.getOrderGoodList().forEach(og -> {
                og.setOrderId(order.getOrderId());
                og.save();
                if (1 == isCart) {
                    // 删除购物车对应记录
                    Integer skuId = og.getSkuId();
                    CartService.me().delBySku(skuId);
                }
            });
            // 消费积分
            PointService.me().consume(order);

            realPayPrice.set(realPayPrice.get().add(order.getOrderAmount()));
            pointAsMoney.set(pointAsMoney.get().add(order.getPointAsMoney()));
            // 发送微信公众号消息
            EventKit.post(new OrderEvent(order.getOrderId()));
        });
        return new Record() {{
            set("masterOrderSn", mastOrderSn);
            set("realPayPrice", realPayPrice.get());
            set("pointAsMoney", pointAsMoney.get());
        }};
    }

    /**
     * 提交订单-普通订单，生成保存起来的model
     * 1 是 2 否
     */
    public OrderModel genOrderModel(OrderModel orderModel, UserAddress userAddress, String userNote, BigDecimal usableRemain) {
        Order order = initByAddress(userAddress, 1);
        order.setAddTime(System.currentTimeMillis() / 1000);
        order.setUserNote(userNote);
        List<OrderGoods> orderGoodList = orderModel.getOrderGoodList().stream().map(og -> {
            Integer goodsNum = og.getGoodsNum();
            Integer skuId = og.getSkuId();
            GoodSku sku = GoodSku.dao.findById(skuId);
            Good good = Good.dao.findById(sku.getGoodsId());
            return initBySku(order, good, sku, goodsNum);
        }).collect(Collectors.toList());

        BigDecimal[] finalUsableRemain = {usableRemain};
        orderGoodList.stream().forEach(og -> {
            // 应付金额
            order.setTotalAmount(order.getTotalAmount().add(og.getFinalPrice()));

            if (og.getPointAsMoney().compareTo(new BigDecimal("0")) > 0) {
                if (finalUsableRemain[0].compareTo(og.getPointAsMoney()) < 0) {
                    // 剩余的积分小于需要抵扣的钱
                    // 剩余的积分大于需要抵扣的钱
                    og.setPointAsMoney(finalUsableRemain[0]);
                }
            }
            finalUsableRemain[0] = finalUsableRemain[0].subtract(og.getPointAsMoney());
            og.setFinalPrice(og.getFinalPrice().subtract(og.getPointAsMoney()));
            // 实付金额
            order.setOrderAmount(order.getOrderAmount().add(og.getFinalPrice()));
            // 积分抵扣金额
            order.setPointAsMoney(order.getPointAsMoney().add(og.getPointAsMoney()));
        });

        return new OrderModel(order, orderGoodList);
    }

    /**
     * 提交订单-秒杀，生成保存起来的model
     */
    public OrderModel genOrderModel(Good good, GoodSku sku, FlashSale flashGood, int addressId, String userNote) {
        Order order = initByAddress(addressId, 2);
        order.setAddTime(System.currentTimeMillis() / 1000);
        order.setUserNote(userNote);

        OrderGoods orderGood = initBySku(order, good, sku, 1);
        orderGood.setFinalPrice(flashGood.getPrice());
        orderGood.setPointAsMoney(flashGood.getPoint());
        orderGood.setActivityType(2);
        orderGood.setActivityId(flashGood.getId());
        order.setActivityType(2);

        order.setOrderAmount(orderGood.getFinalPrice());
        order.setTotalAmount(orderGood.getGoodsPrice());
        order.setPointAsMoney(orderGood.getPointAsMoney());

        return new OrderModel(order, Arrays.asList(orderGood));
    }

    /**
     * 根据商品SKU，初始化OrderGoods实体，还没有保存数据库，如果存在秒杀信息，初始化的时候计算一下秒杀逻辑
     */
    private OrderGoods initBySku(Order order, Good good, GoodSku sku, int num) {
        OrderGoods orderGood = new OrderGoods();
        orderGood.setOrderSn(order.getOrderSn());
        orderGood.setGoodsId(good.getGoodsId());
        orderGood.setSkuId(sku.getItemId().intValue());
        orderGood.setGoodsName(good.getGoodsName());
        orderGood.setGoodsSn(good.getGoodsSn());
        orderGood.setGoodsNum(num);
        orderGood.setSpecKey(sku.getKey());
        orderGood.setSpecKeyName(sku.getKeyName());
        orderGood.setGoodsPrice(sku.getPrice());
        orderGood.setSupplyPrice(sku.getSupplyPrice());
        orderGood.setCostPrice(sku.getMarketPrice());
        // 只有最终的这个，跟数量相乘，其他的都只记录，不计算
        // 但是 理论上不是这样的 应该乘以购买个数的
        orderGood.setFinalPrice(sku.getPrice().multiply(new BigDecimal(String.valueOf(num))));
        orderGood.setPointAsMoney(good.getPointAsMoney().multiply(new BigDecimal(String.valueOf(num))));
        orderGood.setGiveIntegral(good.getGiveIntegral() * num);
        orderGood.setActivityType(1);

        order.setStoreId(good.getStoreId());

        return orderGood;
    }

    /**
     * 根据用户信息，收货地址，初始化订单实体，还没有保存数据库
     */
    private Order initByAddress(int addressId, int activityType) {
        UserAddress userAddres = UserAddress.dao.findById(addressId);
        return initByAddress(userAddres, activityType);
    }

    private Order initByAddress(UserAddress userAddres, int activityType) {
        Order order = new Order();
        order.setOrderSn(genSn(activityType));
        order.setUserId(AppIdKit.getUserId());
        order.setMasterOrderSn(genSn(activityType));
        order.setOrderStatus(0);
        order.setPayStatus(0);
        order.setShippingStatus(0);
        order.setIsComment(0);
        order.setConsignee(userAddres.getConsignee());
        order.setCountry(userAddres.getCountry());
        order.setProvince(userAddres.getProvince());
        order.setCity(userAddres.getCity());
        order.setDistrict(userAddres.getDistrict());
        order.setAddress(userAddres.getAddress());
        order.setZipcode(userAddres.getZipcode());
        order.setMobile(userAddres.getMobile());
        order.setEmail(userAddres.getEmail());

        // 初始化后有可能被改的
        order.setOrderAmount(new BigDecimal("0"));
        order.setTotalAmount(new BigDecimal("0"));
        order.setPointAsMoney(new BigDecimal("0"));
        order.setActivityType(1);

        return order;
    }

    /**
     * 从购物车过来 - 提交订单钱，生成给客户做二次确认的预订单页面
     */
    public OrderDetailVo genOrderVo(Map<Integer, List<Cart>> cartMap) {
        OrderDetailVo detail = new OrderDetailVo();
        detail.setTotalNum(0);
        detail.setTotalPrice(new BigDecimal("0"));
        detail.setTotalPointAsMoney(new BigDecimal("0"));

        detail.setOrderStoreList(cartMap.keySet().stream().map(storeId -> {
            Store store = Store.dao.findById(storeId);
            OrderDetailVo.OrderStore orderStore = detail.new OrderStore();
            orderStore.setStoreId(storeId);
            orderStore.setStoreName(store.getStoreName());
            orderStore.setServicePhone(store.getServicePhone());
            orderStore.setSumPrice(new BigDecimal("0"));
            orderStore.setSumNum(0);
            orderStore.setOrderGood(cartMap.get(storeId).stream().map(og -> {
                Good good = Good.dao.findById(og.getGoodsId());

                OrderDetailVo.OrderGood orderGood = detail.new OrderGood();
                GoodSku sku = GoodSku.dao.findById(og.getItemId());
                orderGood.setGoodId(sku.getGoodsId().longValue());
                orderGood.setSkuId(sku.getItemId().intValue());
                orderGood.setGoodName(og.getGoodsName());
                orderGood.setGoodNum(og.getGoodsNum());
                orderGood.setGoodPrice(sku.getPrice());
                orderGood.setImgPath(PropKit.get("fileHost") + good.getOriginalImg());
                orderGood.setSpecName(sku.getKeyName());

                // 计算金额汇总
                orderStore.setSumNum(orderStore.getSumNum() + orderGood.getGoodNum());
                orderStore.setSumPrice(orderStore.getSumPrice().add(orderGood.getGoodPrice().multiply(new BigDecimal(String.valueOf(orderGood.getGoodNum())))));
                // 计算积分
                detail.setTotalPointAsMoney(detail.getTotalPointAsMoney().add(good.getPointAsMoney().multiply(new BigDecimal(String.valueOf(orderGood.getGoodNum())))));
                return orderGood;
            }).collect(Collectors.toList()));
            // 计算金额汇总
            detail.setTotalNum(detail.getTotalNum() + orderStore.getSumNum());
            detail.setTotalPrice(detail.getTotalPrice().add(orderStore.getSumPrice()));
            // express
            OrderDetailVo.Express express = detail.new Express();
            express.setId(0);
            express.setName("包邮");
            express.setPrice(new BigDecimal("0"));
            orderStore.setExpressList(Arrays.asList(express));
            return orderStore;
        }).collect(Collectors.toList()));

        return comConsignee(detail);
    }

    /**
     * 从商品详情页面过来 - 提交订单钱，生成给客户做二次确认的预订单页面
     */
    public OrderDetailVo genOrderVo(Good good, GoodSku sku, int buyNum, FlashSale flashGood, LotteryGood lotteryGood) {
        Store store = Store.dao.findById(good.getStoreId());
        // 开始逐步构建
        // OrderDetailVo
        OrderDetailVo detail = new OrderDetailVo();
        detail.setTotalNum(buyNum);
        detail.setTotalPrice(sku.getPrice());
        detail.setTotalPointAsMoney(good.getPointAsMoney());
        if (flashGood != null) {
            detail.setTotalPrice(flashGood.getPrice());
            detail.setTotalPointAsMoney(flashGood.getPoint());
        }
        if (lotteryGood != null) {
            detail.setTotalPrice(lotteryGood.getPrice());
            detail.setTotalPointAsMoney(new BigDecimal("0"));
        }
        // OrderStore
        OrderDetailVo.OrderStore orderStore = detail.new OrderStore();
        orderStore.setStoreId(good.getStoreId());
        orderStore.setStoreName(store.getStoreName());
        orderStore.setSumNum(buyNum);
        orderStore.setSumPrice(sku.getPrice());
        if (flashGood != null) {
            orderStore.setSumPrice(flashGood.getPrice());
        }
        if (lotteryGood != null) {
            orderStore.setSumPrice(lotteryGood.getPrice());
        }
        orderStore.setServicePhone(store.getServicePhone());
        // Express
        OrderDetailVo.Express express = detail.new Express();
        express.setId(0);
        express.setName("包邮");
        express.setPrice(new BigDecimal("0"));
        orderStore.setExpressList(Arrays.asList(express));
        // OrderGood
        OrderDetailVo.OrderGood orderGood = detail.new OrderGood();
        orderGood.setGoodId(good.getGoodsId());
        orderGood.setGoodName(good.getGoodsName());
        orderGood.setGoodNum(1);
        orderGood.setGoodPrice(sku.getPrice());
        if (flashGood != null) {
            orderGood.setGoodPrice(flashGood.getPrice());
        }
        if (lotteryGood != null) {
            orderGood.setGoodPrice(lotteryGood.getPrice());
        }
        if (good.getOriginalImg().startsWith("http")) {
            orderGood.setImgPath(good.getOriginalImg());
        } else {
            orderGood.setImgPath(PropKit.get("fileHost") + good.getOriginalImg());
        }
        orderGood.setSkuId(sku.getItemId().intValue());
        orderGood.setSpecName(sku.getKeyName());
        orderStore.setOrderGood(Arrays.asList(orderGood));
        // 添加进去
        detail.setOrderStoreList(Arrays.asList(orderStore));

        return comConsignee(detail);
    }

    private OrderDetailVo comConsignee(OrderDetailVo detail) {
        // Consignee
        OrderDetailVo.Consignee consignee = detail.new Consignee();
        UserAddress userAddress = UserAddressService.me().getDefault();
        if (userAddress != null) {
            consignee.setAddressId(userAddress.getAddressId());
            consignee.setName(userAddress.getConsignee());
            consignee.setMobile(userAddress.getMobile());
            consignee.setAddress(RegionService.me().orderAddress(userAddress.getProvince(), userAddress.getCity(), userAddress.getDistrict(), userAddress.getTwon()) + userAddress.getAddress());
        }
        detail.setConsignee(consignee);
        return detail;
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @param type  1 未支付 2未发货 3已支付 4待收货
     * @return
     */
    public Long count(Date begin, Date end, int type) {
        // 订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单-有点类似于订单类型，标识订单处于哪个流程中
        // 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转
        // 发货状态0未发货 1已发货 2已收货 -发货流程中订单流转状态
        String sql = "SELECT COUNT(*) FROM butler_order WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (type == 1) {
            // 未支付
            sql += " AND order_status = 0 AND pay_status = 0";
        } else if (type == 2) {
            // 未发货
            sql += " AND order_status = 0 AND pay_status = 1 AND shipping_status = 0";
        } else if (type == 3) {
            // 已支付
            sql += " AND order_status = 0 AND pay_status = 1";
        } else if (type == 4) {
            sql += " AND order_status = 0 AND pay_status = 1 AND shipping_status = 1";
        }
        if (type != 3) {
            if (begin != null) {
                sql += " AND create_time >= ?";
                paras.add(begin);
            }
            if (end != null) {
                sql += " AND create_time < ?";
                paras.add(end);
            }
        } else {
            if (begin != null) {
                sql += " AND pay_time >= ?";
                paras.add(begin.getTime() / 1000);
            }
            if (end != null) {
                sql += " AND pay_time < ?";
                paras.add(end.getTime() / 1000);
            }
        }
        return Db.queryLong(sql, paras.toArray());
    }

    public BigDecimal amount(Date begin, Date end) {
        String sql = "SELECT SUM(order_amount) FROM butler_order WHERE order_status = 0 AND pay_status = 1";
        List<Long> paras = new ArrayList<>();
        if (begin != null) {
            sql += " AND pay_time >= ?";
            paras.add(begin.getTime() / 1000);
        }
        if (end != null) {
            sql += " AND pay_time < ?";
            paras.add(end.getTime() / 1000);
        }
        return Db.queryBigDecimal(sql, paras.toArray());
    }
}