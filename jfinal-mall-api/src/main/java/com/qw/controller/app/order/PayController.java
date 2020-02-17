package com.qw.controller.app.order;

import cn.qw.base.RestController;
import cn.qw.entity.ApiResult;
import cn.qw.kit.*;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.qw.conf.ButlerEmnu;
import com.qw.helper.WxPayHelper;
import com.qw.model.Order;
import com.qw.model.Recharge;
import com.qw.model.User;
import com.qw.service.frontend.member.RechargeService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.order.PayService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付&退款管理
 */
public class PayController extends RestController {

    /**
     * @param amount|充值金额|BigDecimal|非必填
     * @param order_sn|订单编号(主)|String|非必填
     * @title 微信发起支付（商品支付、钱包充值）(APP/H5/公众号/PC)
     */
    public void wxAppLanuch() {
        // 获取参数
        String orderSn = getPara("order_sn");
        String total = getPara("amount");
        // 校验参数
        User user = User.dao.findById(AppIdKit.getUserId());
        if (user == null) {
            renderParamNull("用户ID对用数据不存在");
            return;
        }

        int totalFee = 0;
        String payDesc = "";
        if (StrKit.isBlank(orderSn) || orderSn.startsWith("RCA")) {
            // 充值
            if (StrKit.isBlank(total)) {
                renderParamError("充值金额不能为" + total);
                return;
            }
            Recharge charge = RechargeService.me().charge(user, new BigDecimal(total), orderSn);
            totalFee = WxPayHelper.bigDecimalToFee(charge.getAccount());
            orderSn = charge.getOrderSn();
            payDesc = user.getNickname() + "钱包充值" + total + "元";
        } else {
            payDesc = "订单编号:" + orderSn + "支付";
            if (orderSn.startsWith("F")) {
                Order order = FlashSaleService.me().findByOrderSn(orderSn);
                if (order == null) {
                    renderParamError("订单编号对应数据不存在");
                    return;
                }
                if (0 != order.getOrderStatus()) {
                    renderParamError("秒杀订单已过期，无法发起支付");
                    return;
                }
                totalFee = WxPayHelper.bigDecimalToFee(order.getOrderAmount());
            } else {
                List<Order> orders = OrderService.me().findByOrderSn(orderSn);
                if (orders == null || orders.size() <= 0) {
                    renderParamError("订单编号对应数据不存在");
                    return;
                }
                totalFee = WxPayHelper.bigDecimalToFee(orders.stream().map(o -> o.getOrderAmount()).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        }
        String ip = IpKit.getIpAddr(getRequest());
        log.info("发起支付客户端IP地址： " + ip);
        if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        try {
            ButlerEmnu.DeviceEnum what = DeviceKit.what(getRequest());
            Element element = PayService.me().wxLanuch(orderSn, String.valueOf(totalFee), payDesc, ip, what, user);
            String return_code = element.elementText("return_code");
            if ("FAIL".equals(return_code)) {
                // 此字段是通信标识，非交易标识。通信失败
                String return_msg = element.elementText("return_msg");
                renderOperateError(return_msg);
                return;
            }
            String result_code = element.elementText("result_code");
            if ("FAIL".equals(result_code)) {
                // 交易失败
                String err_code = element.elementText("err_code");
                String err_code_des = element.elementText("err_code_des");
                String return_msg = err_code + ":" + err_code_des;
                renderOperateError(return_msg);
                return;
            }
            // APP支付
            if (what.equals(ButlerEmnu.DeviceEnum.ANDROID) || what.equals(ButlerEmnu.DeviceEnum.IOS)) {
                Map<String, String> packageParams = PayService.me().buildAppResult(element);
                renderJson(new ApiResult(packageParams, 0, "发起支付成功"));
                return;
            }
            //微信公众号内支付
            if (what.equals(ButlerEmnu.DeviceEnum.WEIXIN)) {
                Map<String, String> packageParams = PayService.me().buildJsResult(element);
                renderJson(new ApiResult(packageParams, 0, "发起支付成功"));
                return;
            }
            // 手机端h5支付
            if (what.equals(ButlerEmnu.DeviceEnum.WAP)) {
                String mwebUrl = element.elementText("mweb_url");
                renderJson(new ApiResult(mwebUrl, 0, "发起支付成功"));
                return;
            }
            // PC 端支付，生成二维码链接
            String codeUrl = element.elementText("code_url");
            renderJson(new ApiResult(codeUrl, 0, "发起支付成功"));
        } catch (DocumentException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            renderOperateError(e.getMessage());
        }
    }

    /**
     * @param amount|充值金额|BigDecimal|非必填
     * @param order_sn|订单编号(主)|String|非必填
     * @title 支付宝发起支付
     */
    public void aliAppLanuch() throws AlipayApiException, UnsupportedEncodingException {
        // 获取参数
        String orderSn = getPara("order_sn");
        String total = getPara("amount");
        String jumpUrl = getPara("jump_url");
        // 校验参数
        User user = User.dao.findById(AppIdKit.getUserId());
        if (user == null) {
            renderParamNull("用户ID对用数据不存在");
            return;
        }

        BigDecimal totalFee = null;
        String payDesc = "";
        if (StrKit.isBlank(orderSn) || orderSn.startsWith("RCA")) {
            // 充值
            if (StrKit.isBlank(total)) {
                renderParamError("充值金额不能为" + total);
                return;
            }
            Recharge charge = RechargeService.me().charge(user, new BigDecimal(total), orderSn);
            totalFee = charge.getAccount();
            orderSn = charge.getOrderSn();
            payDesc = user.getNickname() + "钱包充值" + total + "元";
        } else {
            payDesc = "订单编号:" + orderSn + "支付";
            if (orderSn.startsWith("F")) {
                // 秒杀订单
                Order order = FlashSaleService.me().findByOrderSn(orderSn);
                if (order == null) {
                    renderParamError("订单编号对应数据不存在");
                    return;
                }
                if (0 != order.getOrderStatus()) {
                    renderParamError("秒杀订单已过期，无法发起支付");
                    return;
                }
                totalFee = order.getOrderAmount();
            } else {
                List<Order> orders = OrderService.me().findByOrderSn(orderSn);
                if (orders == null || orders.size() <= 0) {
                    renderParamError("订单编号对应数据不存在");
                    return;
                }
                totalFee = orders.stream().map(o -> o.getOrderAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }
        // 获取设备信息
        ButlerEmnu.DeviceEnum what = DeviceKit.what(getRequest());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("partner", PropKit.get("alipay_partner"));
        parameters.put("_input_charset", "utf-8");
        parameters.put("notify_url", PropKit.get("host") + "/tomcat/api/pay/aliNotify");
        parameters.put("seller_id", PropKit.get("alipay_account"));
        parameters.put("out_trade_no", orderSn);
        parameters.put("total_fee", totalFee.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        parameters.put("subject", "白金管家" + payDesc);
        parameters.put("body", "白金管家" + payDesc);
        parameters.put("payment_type", "1");

        if (what.equals(ButlerEmnu.DeviceEnum.IOS) || what.equals(ButlerEmnu.DeviceEnum.ANDROID)) {
            // 移动端调用老版本的支付
            parameters.put("seller_id", PropKit.get("alipay_account"));
            parameters.put("service", "mobile.securitypay.pay");
        } else if (what.equals(ButlerEmnu.DeviceEnum.WAP)) {
            parameters.put("service", "alipay.wap.create.direct.pay.by.user");
            parameters.put("seller_id", PropKit.get("alipay_partner"));
            parameters.put("show_url", jumpUrl);
            parameters.put("return_url", jumpUrl);
            parameters.put("app_pay", "Y");
        } else {
            parameters.put("service", "create_direct_pay_by_user");
            parameters.put("seller_id", PropKit.get("alipay_partner"));
            parameters.put("return_url", jumpUrl);
            parameters.put("show_url", jumpUrl);
        }
        String sign = AlipaySignature.rsaSign(parameters, PropKit.get("alipay_private_key"), "UTF-8");
        parameters.put("sign_type", "RSA");
        String orderEncoded = AliPayKit.packageSign(parameters, true);

        orderEncoded = orderEncoded + "&sign=" + URLEncoder.encode(sign, "UTF-8");
        if (what.equals(ButlerEmnu.DeviceEnum.IOS) || what.equals(ButlerEmnu.DeviceEnum.ANDROID)) {
            renderJson(new ApiResult(orderEncoded, 0, "支付宝发起支付成功"));
        } else {
            orderEncoded = "https://mapi.alipay.com/gateway.do?" + orderEncoded;
            renderJson(new ApiResult(orderEncoded, 0, "支付宝发起支付成功"));
        }
    }

    /**
     * @title 微信支付回调
     */
    @Clear
    public void wxNotify() {
        String xmlMsg = HttpKit.readData(getRequest());
        Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
        renderText(PayService.me().wxNotify(params));
    }

    /**
     * @title 微信退款-回调
     */
    @Clear
    public void wxRefundNotify() {
        String xmlMsg = HttpKit.readData(getRequest());
        Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
        renderText(PayService.me().wxRefundNotify(params));
    }

    /**
     * @title 支付宝支付回调
     */
    @Clear
    public void aliNotify() throws AlipayApiException {
        Map<String, String> param = AliPayKit.notify(getRequest());
        renderText(PayService.me().aLiNotify(param));
    }

    @Clear
    public void ttt() {
        String tradeNo = getPara("tradeNo");
        BigDecimal payFee = getParaToDecimal("payFee");
        renderSuccess(String.valueOf(PayService.me().callback(tradeNo, DateKit.dateToString(new Date(), "yyyyMMddHHmmss"), new Date(), payFee, ButlerEmnu.PayType.Ali)));
    }
}