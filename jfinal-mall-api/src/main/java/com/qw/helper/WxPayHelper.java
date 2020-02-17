package com.qw.helper;

import cn.qw.kit.Base64Util;
import cn.qw.kit.CryptKit;
import cn.qw.kit.JsonKit;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.RedPackApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.qw.model.PointWithdraw;
import com.qw.service.common.WxBankService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付工具
 */
public class WxPayHelper {
    private static final Log log = Log.getLog(WxPayHelper.class);

    private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";
    private static final SecretKeySpec key = new SecretKeySpec(CryptKit.md5(PropKit.get("wx_pay_key")).toLowerCase().getBytes(), "AES");

    private static final String PAY_BANK_URL = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

    /**
     * 金额转分
     */
    public static int bigDecimalToFee(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 解析微信退款中的req_info
     */
    public static String reqInfo(String base64Data) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING, "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64Util.decode(base64Data)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送红包
     */
    public static Map<String, String> sendRedPack(PointWithdraw withdraw, String openId) {
        Map<String, String> params = new HashMap<>();
        // 随机字符串，不长于32位
        params.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());

        // 微信支付分配的商户号
        params.put("mch_id", PropKit.get("wx_pay_merchant_id"));
        // 商户订单号（每个订单号必须唯一）组成
        params.put("mch_billno", withdraw.getWithdrawSn());
        // 微信分配的公众账号ID
        params.put("wxappid", PropKit.get("wxa_appId"));
        // 红包发送者名称
        params.put("send_name", "白金管家");

        // 接受红包的用户用户在wxappid下的openid
        params.put("re_openid", openId);
        // 付款金额，单位分
        params.put("total_amount", String.valueOf(bigDecimalToFee(withdraw.getActualAmount())));
        // 红包发放总人数
        params.put("total_num", "1");
        // 红包祝福语
        params.put("wishing", "分享商品送现金红包，感谢您的支持！");
        // 调用接口的机器Ip地址
        String hostAddress = "";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        params.put("client_ip", hostAddress);
        // 活动名称
        params.put("act_name", "分享商品送红包");
        // 备注信息
        params.put("remark", "提现申请ID：" + withdraw.getId());
        // 发放红包使用场景，红包金额大于200时必传
        params.put("scene_id", "PRODUCT_1");
        params.put("sign", PaymentKit.createSign(params, PropKit.get("wx_pay_key")));

        if (JFinal.me().getConstants().getDevMode()) {
            log.warn(PaymentKit.toXml(params));
        }

        String certPath = PathKit.getRootClassPath() + "/cert/apiclient_cert.p12";
        String xmlResult = RedPackApi.sendRedPack(params, certPath, PropKit.get("wx_pay_merchant_id"));
        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        return result;
    }

    public static Map<String, String> sendBank(PointWithdraw withdraw) {
        Map<String, String> params = new HashMap<>();
        // 微信支付分配的商户号
        params.put("mch_id", PropKit.get("wx_pay_merchant_id"));
        // 商户订单号，需保持唯一
        params.put("partner_trade_no", withdraw.getWithdrawSn());
        // 随机字符串，不长于32位
        params.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());

        params.put("enc_bank_no",CryptKit.wxRsaEncrypt(withdraw.getBankCard()));
        params.put("enc_true_name", CryptKit.wxRsaEncrypt(withdraw.getName()));
        params.put("bank_code", WxBankService.me().getId(withdraw.getBankName()));
        params.put("amount", String.valueOf(bigDecimalToFee(withdraw.getActualAmount())));

        // 备注信息
        params.put("desc", "提现申请ID：" + withdraw.getId());
        params.put("sign", PaymentKit.createSign(params, PropKit.get("wx_pay_key")));

        if (JFinal.me().getConstants().getDevMode()) {
            log.warn(PaymentKit.toXml(params));
        }

        String certPath = PathKit.getRootClassPath() + "/cert/apiclient_cert.p12";
        String xmlResult = HttpUtils.postSSL(PAY_BANK_URL, PaymentKit.toXml(params), certPath, PropKit.get("wx_pay_merchant_id"));
        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        return result;
    }

    /**
     * 获取RSA加密公钥
     */
    private static void getPublicKey(){
        String url = "https://fraud.mch.weixin.qq.com/risk/getpublickey";
        Map<String, String> params = new HashMap<>();
        // 微信支付分配的商户号
        params.put("mch_id", PropKit.get("wx_pay_merchant_id"));
        // 随机字符串，不长于32位
        params.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
        params.put("sign_type", "MD5");
        params.put("sign", PaymentKit.createSign(params, PropKit.get("wx_pay_key")));

        String certPath = PathKit.getRootClassPath() + "/cert/apiclient_cert.p12";
        String xmlResult = HttpUtils.postSSL(url, PaymentKit.toXml(params), certPath, PropKit.get("wx_pay_merchant_id"));
        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        log.error(JsonKit.toJson(result));
        log.error("------------------------------------------------------");
        log.error(result.get("pub_key"));
        log.error("------------------------------------------------------");
    }
}