package cn.qw.kit;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;

/**
 * 支付宝支付-新版本
 */
public class AliPayKitNew {

    protected static final Log log = Log.getLog(AliPayKitNew.class);

    private static final AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
            PropKit.get("ali_pay_app_id"),
            PropKit.get("ali_pay_app_private_key"),
            "json",
            "UTF-8",
            PropKit.get("ali_pay_public_key_new"),
            "RSA2");

    /**
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，支付宝将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * 交易超过约定时间（签约时设置的可退款时间）的订单无法进行退款 支付宝退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。
     * 一笔退款失败后重新提交，要采用原来的退款单号。
     * 总退款金额不能超过用户实际支付金额
     *
     * @param bizContent
     * @return
     * @throws AlipayApiException
     */
    public static AlipayTradeRefundResponse refund(AlipayTradeRefundModel bizContent) throws AlipayApiException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizModel(bizContent);
        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        log.info("[发起退款支付宝返回参数]p = " + JsonKit.toJson(execute));
        return execute;
    }

    /**
     * 手机网页端发起支付
     *
     * @param bizContent
     * @return
     * @throws AlipayApiException
     */
    public static AlipayResponse mobileLaunch(AlipayTradeWapPayModel bizContent, String returnUrl, String notifyUrl) throws AlipayApiException {
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setReturnUrl(returnUrl);
        request.setNotifyUrl(notifyUrl);
        request.setBizModel(bizContent);
        AlipayTradeWapPayResponse alipayTradeWapPayResponse = alipayClient.pageExecute(request);
        return alipayTradeWapPayResponse;
    }

    /**
     * APP发起支付
     *
     * @param bizContent
     * @return
     * @throws AlipayApiException
     */
    public static AlipayResponse appLaunch(AlipayTradeWapPayModel bizContent, String returnUrl, String notifyUrl) throws AlipayApiException {
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setReturnUrl(returnUrl);
        request.setNotifyUrl(notifyUrl);
        request.setBizModel(bizContent);
        AlipayTradeAppPayResponse alipayTradeAppPayResponse = alipayClient.pageExecute(request);
        return alipayTradeAppPayResponse;
    }
}
