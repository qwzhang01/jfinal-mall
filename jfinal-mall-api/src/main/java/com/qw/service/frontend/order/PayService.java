package com.qw.service.frontend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.AliPayKit;
import cn.qw.kit.AliPayKitNew;
import cn.qw.kit.DateKit;
import cn.qw.kit.JsonKit;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.qw.conf.ButlerEmnu;
import com.qw.event.pay.PayEvent;
import com.qw.helper.WxPayHelper;
import com.qw.model.Order;
import com.qw.model.Recharge;
import com.qw.model.ReturnGoods;
import com.qw.model.User;
import com.qw.service.frontend.member.BalanceService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import net.dreamlu.event.EventKit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.util.*;

/**
 * 支付service
 * @author qw
 */
public class PayService extends BaseService {
    private static PayService service;

    private PayService() {
    }

    public static synchronized PayService me() {
        if (service == null) {
            service = new PayService();
        }
        return service;
    }

    /**
     * 微信回调逻辑处理
     */
    public String wxNotify(Map<String, String> params) {

        Map<String, String> xml = new HashMap<>();
        xml.put("return_code", "FAIL");
        xml.put("return_msg", "ERROR");
        // 判断是否为支付 回调
        // SUCCESS/FAIL
        String return_code = params.get("return_code");
        String return_msg = params.get("return_msg");
        if (!"SUCCESS".equals(return_code)) {
            // log.error("微信支付回调参数：" + JsonKit.toJson(params));
            log.error("微信支付回调失败：" + return_msg);
            return PaymentKit.toXml(xml);
        }
        // 判断支付结果是否成功
        // SUCCESS/FAIL
        String result_code = params.get("result_code");
        if ("FAIL".equals(result_code)) {
            String err_code = params.get("err_code");
            String err_code_des = params.get("err_code_des");
            log.error("微信支付回调失败，失败编码：" + err_code + "；失败描述：" + err_code_des);
            return PaymentKit.toXml(xml);
        }
        // 校验参数是否正确
        if (!PaymentKit.verifyNotify(params, PropKit.get("wx_pay_key"))) {
            return PaymentKit.toXml(xml);
        }

        // 商家数据包，原样返回
        //String attach = params.get("attach");
        // 总金额，单位为分
        String total_fee = params.get("total_fee");
        // 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
        String tradeNo = params.get("out_trade_no");
        // 微信支付单号
        String transId = params.get("transaction_id");
        // 支付时间
        String timeEnd = params.get("time_end");
        // 交易类型 JSAPI、NATIVE、APP
        String trade_type = params.get("trade_type");

        BigDecimal payFee = new BigDecimal(total_fee).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        Date payTime = DateKit.stringtoDate(timeEnd, "yyyyMMddHHmmss");

        boolean result = callback(tradeNo, transId, payTime, payFee, ButlerEmnu.PayType.Wechat);
        if (!result) {
            log.error("transId:" + transId + "\n timeEnd:" + timeEnd + "\n trade_type:" + trade_type + "\n orderId:" + tradeNo);
            return PaymentKit.toXml(xml);
        }

        xml.put("return_code", "SUCCESS");
        xml.put("return_msg", "OK");
        return PaymentKit.toXml(xml);
    }

    /**
     * 支付宝回调逻辑
     *
     * @param params
     * @return
     */
    public String aLiNotify(Map<String, String> params) throws AlipayApiException {
        if (!AliPayKit.verify(params)) {
            return "failure";
        }

        // 付款状态
        String trade_status = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(trade_status)) {
            return "failure";
        }
        // 订单编号
        String outTradeNo = params.get("out_trade_no");
        // 支付宝交易号
        String transId = params.get("trade_no");
        //支付金额
        String totalFee = params.get("total_fee");
        // 交易付款时间
        String gmt_payment = params.get("gmt_payment");
        Date payTime = DateKit.stringtoDate(gmt_payment, "yyyy-MM-dd HH:mm:ss");

        boolean result = callback(outTradeNo, transId, payTime, new BigDecimal(totalFee), ButlerEmnu.PayType.Ali);
        if (result) {
            return "success";
        }
        return "failure";
    }

    public boolean callback(String tradeNo, String transId, Date payTime, BigDecimal payFee, ButlerEmnu.PayType payType) {
        if (tradeNo.startsWith("RCA")) {
            // 充值
            return updateRechargeStatus(tradeNo, transId, payTime, payFee, payType);
        } else if (tradeNo.startsWith("F")) {
            // 秒杀支付
            return FlashSaleService.me().payCallback(tradeNo, transId, payFee, payType);
        } else {
            return updatePayStatus(tradeNo, transId, payTime, payFee, payType);
        }
    }

    private boolean updateRechargeStatus(String tradeNo, String transId, Date payTime, BigDecimal payFee, ButlerEmnu.PayType payType) {
        String sql = "SELECT * FROM butler_recharge WHERE order_sn = ? AND pay_status = 0 LIMIT 1";
        Recharge recharge = Recharge.dao.findFirst(sql, tradeNo);
        if (recharge == null) {
            return true;
        }
        if (!recharge.getAccount().equals(payFee)) {
            return false;
        }
        recharge.setPayStatus(ButlerEmnu.ChargePayStatus.Paied.getValue());
        recharge.setPayName(payType.getName());
        return Db.tx(() -> {
            boolean update = recharge.saveOrUpdate(false);
            // 加一条流水 添加一下总的金额
            boolean addBalanceDetail = BalanceService.me().addBalanceDetail(recharge);
            boolean available = BalanceService.me().addAvailable(recharge);
            return update && addBalanceDetail && available;
        });
    }

    private boolean updatePayStatus(String tradeNo, String transId, Date payTime, BigDecimal payFee, ButlerEmnu.PayType payType) {
        String sql = "SELECT SUM(order_amount) FROM butler_order WHERE master_order_sn = ? OR order_sn = ?";
        BigDecimal amount = Db.queryBigDecimal(sql, tradeNo, tradeNo);
        if (amount.compareTo(payFee) != 0) {
            return false;
        }
        return updatePayStatus(tradeNo, transId, payTime, payType);
    }

    private boolean updatePayStatus(String tradeNo, String transId, Date payTime, ButlerEmnu.PayType payType) {
        String sql = "SELECT * FROM butler_order WHERE master_order_sn = ?";
        List<Order> list = Order.dao.find(sql, tradeNo);
        if (list != null && list.size() > 0) {
            for (Order o : list) {
                if (!updatePayStatus(o.getOrderSn(), transId, payType)) {
                    log.error("支付回调，有逻辑出错，请手动修正数据， 订单编号：" + tradeNo);
                    return false;
                }
            }
            return true;
        }
        return updatePayStatus(tradeNo, transId, payType);
    }

    private boolean updatePayStatus(String tradeNo, String transId, ButlerEmnu.PayType payType) {
        String sql = "SELECT * FROM butler_order WHERE order_sn = ? AND pay_status IN(0, 2) LIMIT 1";
        Order order = Order.dao.findFirst(sql, tradeNo);
        if (order == null) {
            return false;
        }
        // 订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单
        // 订单支付状态改为已支付状态
        // 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转
        order.setPayTime(System.currentTimeMillis() / 1000);
        order.setPayStatus(1);
        order.setTransactionId(transId);
        order.setPayCode(String.valueOf(payType.getValue()));
        order.setPayName(payType.getName());
        boolean update = order.update();
        if (update) {
            // 支付成功后的各种计算：返利、返现、各种级别、阶段的账户返现
            EventKit.post(new PayEvent(order));
            return true;
        }
        return false;
    }

    /**
     * type 1APP支付 2公众号支付 3H5支付 4PC页面支付
     */
    public Element wxLanuch(String tradeNo, String totalFee, String payDesc, String ip, ButlerEmnu.DeviceEnum device, User user) throws DocumentException {
        String openId = "";
        if (ButlerEmnu.DeviceEnum.WEIXIN == device) {
            openId = user.getOpenId();
            if (StrKit.isBlank(openId)) {
                throw new RuntimeException("参数异常，openID为空");
            }
        }
        return wxLanuch(tradeNo, totalFee, payDesc, ip, device, openId);
    }

    private Element wxLanuch(String tradeNo, String totalFee, String payDesc, String ip, ButlerEmnu.DeviceEnum device, String openId) throws DocumentException {
        String appid = PropKit.get("wx_pay_app_id");
        String paternerKey = PropKit.get("wx_pay_key");
        String mch_id = PropKit.get("wx_pay_merchant_id");

        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("body", "白金管家" + payDesc);
        params.put("out_trade_no", tradeNo);
        params.put("total_fee", totalFee);
        params.put("spbill_create_ip", ip);
        if (ButlerEmnu.DeviceEnum.ANDROID == device || ButlerEmnu.DeviceEnum.IOS == device) {
            params.put("trade_type", PaymentApi.TradeType.APP.name());
        } else if (ButlerEmnu.DeviceEnum.WEIXIN == device) {
            params.put("openid", openId);
            params.put("appid", PropKit.get("wxa_appId"));
            params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        } else if (ButlerEmnu.DeviceEnum.WAP == device) {
            params.put("trade_type", PaymentApi.TradeType.MWEB.name());
        } else if (ButlerEmnu.DeviceEnum.PC == device) {
            params.put("trade_type", PaymentApi.TradeType.NATIVE.name());
        } else {
            throw new RuntimeException("微信发起支付支付方式不存在");
        }
        params.put("nonce_str", UUID.randomUUID().toString().replace("_", "").toLowerCase().substring(0, 30));
        // 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
        params.put("sign_type", "MD5");
        params.put("attach", "这个是附加信息，可以在回调中使用");
        // 符合ISO 4217标准的三位字母代码，默认人民币：CNY
        params.put("fee_type", "CNY");
        params.put("notify_url", PropKit.get("host") + "/tomcat/api/pay/wxNotify");

        params.put("sign", PaymentKit.createSign(params, paternerKey));

        String xmlResult = PaymentApi.pushOrder(params);// 调用统一下单接口

        Document doc = DocumentHelper.parseText(xmlResult);
        Element root = doc.getRootElement();
        return root;
    }

    /**
     * 微信退款回调
     */
    public String wxRefundNotify(Map<String, String> params) {
        Map<String, String> xml = new HashMap<>();
        xml.put("return_code", "FAIL");
        xml.put("return_msg", "ERROR");
        // 判断是否为支付 回调 SUCCESS/FAIL
        String return_code = params.get("return_code");
        String return_msg = params.get("return_msg");
        if (!"SUCCESS".equals(return_code)) {
            log.error("微信退款回调失败：" + return_msg);
            return PaymentKit.toXml(xml);
        }
        String reqInfo = params.get("req_info");
        String reqInfoXml = WxPayHelper.reqInfo(reqInfo);
        Map<String, String> reqInfoMap = PaymentKit.xmlToMap(reqInfoXml);
        String refundStatus = reqInfoMap.get("refund_status");
        if (!"SUCCESS".equals(refundStatus)) {
            return PaymentKit.toXml(xml);
        }
        //String transactionId = reqInfoMap.get("transaction_id");
        String outTradeNo = reqInfoMap.get("out_trade_no");
        String refundId = reqInfoMap.get("refund_id");
        //String outRefundNo = reqInfoMap.get("out_refund_no");
        String refundFeeStr = reqInfoMap.get("refund_fee");
        BigDecimal refundFee = new BigDecimal(refundFeeStr).divide(new BigDecimal("100"));
        String successTimeStr = reqInfoMap.get("success_time");
        Date successTime = DateKit.stringtoDate(successTimeStr, "yyyy-MM-dd HH:mm:ss");
        if (!refundCallback(outTradeNo, refundId, successTime, refundFee)) {
            log.error("退款回调失败=" + JsonKit.toJson(reqInfoMap));
            return PaymentKit.toXml(xml);
        }
        xml.put("return_code", "SUCCESS");
        xml.put("return_msg", "OK");
        return PaymentKit.toXml(xml);
    }

    /**
     * 微信退款回调、处理订单状态
     */
    private boolean refundCallback(String outTradeNo, String refundId, Date successTime, BigDecimal refundFee) {
        String sql = "SELECT * FROM butler_order WHERE master_order_sn = ? OR order_sn = ?";
        Order order = Order.dao.findFirst(sql, outTradeNo, outTradeNo);
        if (order == null) {
            log.error("退款失败，订单编号（" + outTradeNo + ")对应订单不存在");
            return false;
        }
        return refundCallback(order, refundId, successTime, refundFee);
    }

    /**
     * 退款回调处理的逻辑
     */
    private boolean refundCallback(Order order, String refundId, Date successTime, BigDecimal refundFee) {
        // 订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单
        // 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款
        order.setPayStatus(3);
        return updateReturnGoodStatus(order, refundId, successTime, refundFee);
    }

    /**
     * 处理退货记录状态
     */
    private boolean updateReturnGoodStatus(Order order, String refundId, Date successTime, BigDecimal refundFee) {
        String sql = "SELECT * FROM butler_return_goods WHERE order_id = ?";
        ReturnGoods returnGood = ReturnGoods.dao.findFirst(sql, order.getOrderId());
        if (returnGood == null) {
            return order.update(false);
        }
        // -2用户取消-1不同意0待审核1通过2已发货3待退款4换货完成5退款完成6申诉仲裁
        returnGood.setStatus(5);
        returnGood.setRefundMoney(refundFee);
        returnGood.setRefundType(1);
        returnGood.setRefundTime(Integer.valueOf(String.valueOf(successTime.getTime() / 1000)));
        return Db.tx(() -> order.update(false) && returnGood.update(false));
    }

    /**
     * 微信发起退款
     */
    public Map<String, String> wxRefundLanuch(Order order, BigDecimal refundamount, String reson) {
        if (refundamount == null || new BigDecimal("0").compareTo(refundamount) == 0) {
            refundamount = order.getOrderAmount();
        }
        refundamount.setScale(BigDecimal.ROUND_HALF_UP, 2);

        String appid = PropKit.get("wx_pay_app_id");
        String mch_id = PropKit.get("wx_pay_merchant_id");
        String paternerKey = PropKit.get("wx_pay_key");

        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        //微信回调通知地址,可以使用自己定义也可以使用回调到指定的位置，这里使用的是自定义可以在回调中添加一些功能 //本地测试用 http://cron.bjgjyun.com/api/pay/wxRefundNotify
        params.put("notify_url", PropKit.get("host") + "/tomcat/api/pay/wxRefundNotify");
        //商户订单号
        params.put("out_trade_no", order.getMasterOrderSn());
        //商户退款订单号
        params.put("out_refund_no", order.getMasterOrderSn());
        //订单总金额
        params.put("total_fee", String.valueOf(new BigDecimal("100").multiply(order.getOrderAmount()).intValue()));
        //退款金额
        params.put("refund_fee", String.valueOf(refundamount.multiply(new BigDecimal("100")).intValue()));
        if (StrKit.notBlank(reson)) {
            params.put("refund_desc", reson);
        }
        Map<String, String> refund = PaymentApi.refund(params, paternerKey, PathKit.getRootClassPath() + "/cert/apiclient_cert.p12");
        return refund;
    }

    /**
     * 支付宝退款
     */
    public boolean aLiRefund(Order order, BigDecimal refundamount, String reson) {
        if (refundamount == null || new BigDecimal("0").compareTo(refundamount) == 0) {
            refundamount = order.getOrderAmount();
        }
        refundamount.setScale(BigDecimal.ROUND_HALF_UP, 2);

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(order.getMasterOrderSn());
        model.setTradeNo(order.getTransactionId());
        model.setRefundAmount(String.valueOf(refundamount));
        if (StrKit.notBlank(reson)) {
            model.setRefundReason(reson);
        }
        try {
            AlipayTradeRefundResponse refund = AliPayKitNew.refund(model);
            log.info("支付宝退款发起返回参数 = " + JsonKit.toJson(refund));
            String code = refund.getCode();
            if (!"10000".equals(code)) {
                throw new RuntimeException("支付宝申请退款失败：" + refund.getMsg());
            }
            return refundCallback(order, "", new Date(), refundamount);
        } catch (AlipayApiException e) {
            log.error("退款失败订单：" + JsonKit.toJson(order));
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> buildAppResult(Element element) {
        Map<String, String> packageParams = new HashMap<>();
        packageParams.put("appid", element.elementText("appid"));
        packageParams.put("partnerid", element.elementText("mch_id"));
        packageParams.put("prepayid", element.elementText("prepay_id"));
        packageParams.put("package", "Sign=WXPay");
        packageParams.put("noncestr", element.elementText("nonce_str"));
        packageParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        String packageSign = PaymentKit.createSign(packageParams, PropKit.get("wx_pay_key"));
        packageParams.put("sign", packageSign);
        return packageParams;
    }

    public Map<String, String> buildJsResult(Element element) {
        Map<String, String> packageParams = new HashMap<>();
        packageParams.put("appId", element.elementText("appid"));
        packageParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        packageParams.put("nonceStr", element.elementText("nonce_str"));
        packageParams.put("package", "prepay_id=" + element.elementText("prepay_id"));
        packageParams.put("signType", "MD5");
        String packageSign = PaymentKit.createSign(packageParams, PropKit.get("wx_pay_key"));
        packageParams.put("paySign", packageSign);
        return packageParams;
    }
}