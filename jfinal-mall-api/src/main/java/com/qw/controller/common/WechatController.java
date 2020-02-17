package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.DeviceKit;
import cn.qw.kit.WxMpSHA1Kit;
import com.qw.conf.ButlerEmnu;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.common.WxMpService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * 微信SDK接口
 */
public class WechatController extends RestController {

    /**
     * @title 校验服务器
     */
    @Clear(RestSecurityInterceptor.class)
    public void index() {

        String signature = getPara("signature");
        String timestamp = getPara("timestamp");
        String nonce = getPara("nonce");
        String echostr = getPara("echostr");

        List<String> paras = new ArrayList<>();
        paras.add(timestamp);
        paras.add(nonce);
        paras.add(PropKit.get("wxa_token"));
        Collections.sort(paras);
        String nSignature = DigestUtils.shaHex(paras.get(0) + paras.get(1) + paras.get(2));

        if (nSignature.equals(signature)) {
            renderText(echostr);
        } else {
            renderText("fail");
        }
    }

    /**
     * 获取微信js-sdkconfig信息 GET
     *
     * @param url|请求路径|String|必填
     * @title 获取微信js-sdkconfig信息
     * @respParam signature|签名|String|必填
     * @respParam appId|微信APPID|String|必填
     * @respParam nonceStr|微信随机字符串|String|必填
     * @respParam timestamp|时间戳|String|必填
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Clear(RestSecurityInterceptor.class)
    public void jsConfig() {
        ButlerEmnu.DeviceEnum what = DeviceKit.what(getRequest());
        if (!what.equals(ButlerEmnu.DeviceEnum.WEIXIN)) {
            renderParamError("不在微信浏览器中，无法获取");
            return;
        }

        String url = getPara("url");
        if (StrKit.isBlank(url)) {
            renderParamNull("参数url不能为空。");
            return;
        }

        String accessToken = WxMpService.me().getAccessToken();
        if (StrKit.isBlank(accessToken)) {
            renderParamError("微信获取token失败");
            return;
        }
        String jsapiTicket = WxMpService.me().jsapiTicket(accessToken);
        if (StrKit.isBlank(jsapiTicket)) {
            renderParamError("微信获取jsapiTicket失败");
            return;
        }

        String noncestr = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        String str = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", jsapiTicket, noncestr, timestamp, url);
        String signature = WxMpSHA1Kit.encode(str);

        Map<String, String> result = new HashMap<>();
        result.put("appId", PropKit.get("wxa_appId"));
        result.put("timestamp", timestamp);
        result.put("nonceStr", noncestr);
        result.put("signature", signature);

        renderJson(result);
    }
}