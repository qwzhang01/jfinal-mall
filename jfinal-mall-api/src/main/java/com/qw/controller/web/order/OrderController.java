package com.qw.controller.web.order;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import cn.qw.render.PoiRender;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.conf.ButlerEmnu;
import com.qw.model.Order;
import com.qw.service.bakend.order.OrderService;
import com.qw.service.bakend.order.ReturnGoodsService;
import com.qw.service.frontend.order.PayService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单管理
 */
@RequiresAuthentication
@RequiresPermissions(value = {"订单信息", "订单发货", "退款管理", "退货管理", "拼团抽奖订单"}, logical = Logical.OR)
public class OrderController extends RestController {

    @RequiresPermissions(value = {"订单信息-查看", "订单发货-查看"}, logical = Logical.OR)
    public void pageList() {
        String masterOrderNo = getPara("mastOrderNo");
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        String storeName = getPara("storeName");
        String orderStatus = getPara("orderStatus");
        String payStatus = getPara("payStatus");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");
        String leaderName = getPara("leaderName");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Integer activityType = getParaToInt("activityType", 0);
        // 发货状态
        Integer shipStatus = getParaToInt("shipStatus");
        // 支付时间
        Date payDateRangeStart = getParaToDate("payDateRangeStart");
        Date payDateRangeEnd = getParaToDate("payDateRangeEnd");

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = OrderService.me().pageList(pageNumber, pageSize
                , storeName, orderNo, masterOrderNo, userMobile
                , orderStatus, payStatus, orderStart, orderEnd
                , leaderName, nickname, goodName
                , activityType, shipStatus, payDateRangeStart, payDateRangeEnd);

        renderJson(pageList);
    }

    @RequiresPermissions(value = {"订单信息-查看"}, logical = Logical.OR)
    public void download() {
        String masterOrderNo = getPara("mastOrderNo");
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        String storeName = getPara("storeName");
        String orderStatus = getPara("orderStatus");
        String payStatus = getPara("payStatus");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");
        String leaderName = getPara("leaderName");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Integer activityType = getParaToInt("activityType", 1);

        // 发货状态
        Integer shipStatus = getParaToInt("shipStatus");
        // 支付时间
        Date payDateRangeStart = getParaToDate("payDateRangeStart");
        Date payDateRangeEnd = getParaToDate("payDateRangeEnd");

        int pageNumber = 1;
        int pageSize = Integer.MAX_VALUE;
        Page<Record> pageList = OrderService.me().pageList(pageNumber, pageSize, storeName, orderNo, masterOrderNo, userMobile, orderStatus, payStatus, orderStart, orderEnd,
                leaderName, nickname, goodName, activityType, shipStatus, payDateRangeStart, payDateRangeEnd);
        List<Record> list = pageList.getList();
        for (Record r : list) {
            Integer status = r.getInt("orderStatus");
            r.remove("orderStatus");
            //订单状态.0待确认，1已确认，2已收货，3已取消，4已完成，5已作废
            if (0 == status) {
                r.set("orderStatus", "待确认");
            } else if (1 == status) {
                r.set("orderStatus", "已确认");
            } else if (2 == status) {
                r.set("orderStatus", "已收货");
            } else if (3 == status) {
                r.set("orderStatus", "已取消");
            } else if (4 == status) {
                r.set("orderStatus", "已完成");
            } else if (5 == status) {
                r.set("orderStatus", "已作废");
            } else {
                r.set("orderStatus", "");
            }
        }
        String[] headers = new String[]{"订单号", "支付中心流水号", "店铺", "下单时间", "付款完成时间", "商品名称", "订单总额",
                "支付方式", "订单状态", "买家", "收货人", "联系电话", "推广人", "平台抽成", "店铺收入", "平台推广奖励", "店铺推广奖励"};
        String[] columns = new String[]{"orderNo", "transactionId", "storeName", "orderTime", "payTime", "title", "orderAmount",
                "payName", "orderStatus", "nickname", "consignee", "mobile", "leaderName", "butlerAmount", "storeIncome", "promoteAmount", "storePromoteAmount"};

        render(PoiRender.me(list).fileName(encodingFileName(DateKit.dateToString(new Date(), "yyyy年MM月dd日") + "订单信息汇总表.xls")).headers(headers).columns(columns));
    }

    @RequiresPermissions(value = {"订单发货-查看"}, logical = Logical.OR)
    public void downloadShippping() {
        String masterOrderNo = getPara("mastOrderNo");
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        String storeName = getPara("storeName");
        String orderStatus = getPara("orderStatus");
        String payStatus = getPara("payStatus");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");
        String leaderName = getPara("leaderName");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Integer activityType = getParaToInt("activityType", 1);
        // 发货状态
        Integer shipStatusParam = getParaToInt("shipStatus");
        // 支付时间
        Date payDateRangeStart = getParaToDate("payDateRangeStart");
        Date payDateRangeEnd = getParaToDate("payDateRangeEnd");
        int pageNumber = 1;
        int pageSize = Integer.MAX_VALUE;
        Page<Record> pageList = OrderService.me().pageList(pageNumber, pageSize, storeName, orderNo, masterOrderNo, userMobile, orderStatus, payStatus, orderStart, orderEnd,
                leaderName, nickname, goodName, activityType, shipStatusParam, payDateRangeStart, payDateRangeEnd);
        List<Record> list = pageList.getList();
        for (Record r : list) {
            Integer shipStatus = r.getInt("shipStatus");
            r.remove("shipStatus");
            // 0未发货 1已发货 2已收货
            if (0 == shipStatus) {
                r.set("shipStatus", "未发货");
            } else if (1 == shipStatus) {
                r.set("shipStatus", "已发货");
            } else if (2 == shipStatus) {
                r.set("shipStatus", "已收货");
            } else {
                r.set("shipStatus", "");
            }
        }
        String[] headers = new String[]{"订单编号", "用户昵称", "用户手机", "商品信息", "收货地址", "订单金额", "发货状态", "支付时间"};
        String[] columns = new String[]{"orderNo", "nickname", "mobile", "title", "shippingAddress", "orderAmount", "shipStatus", "payTime"};
        render(PoiRender.me(list).fileName(encodingFileName(DateKit.dateToString(new Date(), "yyyy年MM月dd日") + "发货订单统计表.xls")).headers(headers).columns(columns));
    }

    /**
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @param userMobile|用户手机号|String|非必填
     * @param orderNo|订单号|orderNo|非必填
     * @param masterOrderNo|主订单号|String|非必填
     * @param storeName|店铺名称|String|非必填
     * @param startDate|开始时间|String|非必填
     * @param endDate|结束时间|String|非必填
     * @param promType|订单类型：0默认1抢购2团购3优惠4预售5虚拟6拼团|int|非必填
     * @param orderStatus|订单状态.0待确认，1已确认，2已收货，3已取消，4已完成，5已作废|int|非必填
     * @param payStatus|支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款|int|非必填
     * @title 获取订单列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam orderId|订单id|int|必填
     * @respParam orderNo|订单编号|String|必填
     * @respParam masterOrderNo||主订单编号|String|必填
     * @respParam title|订单商品|String|必填
     * @respParam orderAmount|订单总金额|decimal|必填
     * @respParam butlerAmount|平台佣金费用|decimal|必填
     * @respParam promoteAmount|平台奖励金额|decimal|必填
     * @respParam storePromoteAmount|店铺奖励金额|decimal|必填
     * @respParam storeIncome|店铺收入金额|decimal|必填
     * @respParam payTime|支付时间|String|必填
     * @respParam storeName|门店名称|String|必填
     * @respParam mobile|用户手机号|String|必填
     * @respParam nickname|用户昵称|String|非必填
     * @respParam orderStatus |订单状态.0待确认，1已确认，2已收货，3已取消，4已完成，5已作废|int|非必填
     * @respParam payStatus |支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款|int|非必填
     */
    @RequiresPermissions(value = {"拼团抽奖订单-查看"}, logical = Logical.OR)
    public void lotteryPageList() {
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        Integer orderStatus = getParaToInt("orderStatus");
        Integer payStatus = getParaToInt("payStatus");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Integer activityStatus = getParaToInt("activityStatus", 0);

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = OrderService.me().lotteryPageList(pageNumber, pageSize, orderNo, userMobile, orderStatus, payStatus, orderStart, orderEnd, nickname, goodName, activityStatus);

        renderJson(pageList);
    }

    /**
     * 退款订单（未发货）
     */
    @RequiresPermissions(value = {"退款管理-查看"}, logical = Logical.OR)
    public void refundPageList() {
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        String storeName = getPara("storeName");
        String leaderName = getPara("leaderName");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = OrderService.me().refundPageList(pageNumber, pageSize, orderNo, userMobile, storeName, leaderName, nickname, goodName, orderStart, orderEnd);
        renderJson(pageList);
    }

    /**
     * 退款退货订单（已发货）
     */
    @RequiresPermissions(value = {"退货管理-查看"}, logical = Logical.OR)
    public void returnPageList() {
        String orderNo = getPara("orderNo");
        String userMobile = getPara("mobile");
        String nickname = getPara("nickname");
        String goodName = getPara("goodName");
        Date orderStart = getParaToDate("rangeDateStart");
        Date orderEnd = getParaToDate("rangeDateEnd");
        Integer status = getParaToInt("status");

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = ReturnGoodsService.me().pageList(pageNumber, pageSize, orderNo, userMobile, nickname, goodName, orderStart, orderEnd, status);
        renderJson(pageList);
    }

    /**
     * 订单发货
     */
    @RequiresPermissions(value = {"订单发货-发货"}, logical = Logical.OR)
    public void saveDeliver() {
        int orderId = getParaToInt("orderId", 0);
        // 快递公司ID
        int expressCompanyId = getParaToInt("expressCompanyId", 0);
        // 快递编号
        String shippingCode = getPara("shippingCode");
        // 物流费用
        BigDecimal shippingPrice = getParaToDecimal("shippingPrice");
        if (expressCompanyId == 0) {
            renderParamNull("快递公司不能为空");
            return;
        }
        if (StrKit.isBlank(shippingCode)) {
            renderParamNull("快递编号不能为空");
            return;
        }
        if (shippingPrice == null) {
            renderParamNull("运费不能为空");
            return;
        }
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamError("订单不存在");
            return;
        }
        if (order.getPayStatus() != 1) {
            renderParamError("未支付订单无法发货");
            return;
        }
        if (order.getShippingStatus() != 0) {
            renderParamError("不是未发货状态，不能发货");
            return;
        }
        order.setShippingStatus(1);
        order.setShippingCode(shippingCode);
        order.setShippingName(expressCompanyId + "");
        order.setShippingPrice(shippingPrice);
        order.setShippingTime(System.currentTimeMillis() / 1000);
        if (order.update()) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param orderId|订单ID|int|必填
     * @param refundamount|退款金额|Double|非必填
     * @param refundReason|退款原因|String|非必填
     * @title 财务同意退款
     * @respBody {"status":"0","data":true, "msg":"操作成功"}
     */
    @RequiresPermissions(value = {"退款管理-查看", "退货管理-查看"}, logical = Logical.OR)
    public void refund() {
        int orderId = getParaToInt("orderId", 0);
        BigDecimal refundamount = getParaToDecimal("refundamount");
        String refundReason = getPara("refundReason", "");
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("参数错误，订单不存在！");
            return;
        }
        if (order.getPayStatus() != 1 && (order.getOrderStatus() != 0 || order.getOrderStatus() != 4)) {
            renderParamError("参数错误，订单状态不是可以退款的状态");
            return;
        }
        // 支付宝退款
        if ("1".equals(order.getPayCode())) {
            boolean refund = PayService.me().aLiRefund(order, refundamount, refundReason);
            if (refund) {
                renderSuccess("退款成功");
                return;
            }
        }
        //微信 支付(金额单位:分)
        if ("2".equals(order.getPayCode())) {
            PayService.me().wxRefundLanuch(order, refundamount, refundReason);
            renderSuccess("退款发起成功");
            return;
        }
        renderParamError("当前订单不支持在线退款，请联系系统管理员操作...");
    }

    @RequiresPermissions(value = {"订单信息-线下支付"}, logical = Logical.OR)
    public void payOffLine() {
        int orderId = getParaToInt("orderId", 0);
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单ID错误");
            return;
        }
        String transId = "OF" + DateKit.dateToString(new Date(), "yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(5);
        boolean callback = PayService.me().callback(order.getOrderSn(), transId, new Date(), order.getOrderAmount(), ButlerEmnu.PayType.Offline);
        if (callback) {
            renderSuccess("线下支付成功");
        } else {
            renderOperateError("操作失败");
        }
    }
}