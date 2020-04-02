package com.qw.service.common;


import cn.qw.base.BaseService;
import cn.qw.kit.HttpKit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

public class WxMpService extends BaseService {
    private static WxMpService service;

    private String appId;
    private String secret;

    private WxMpService() {
        this.appId = PropKit.get("wxa_appId");
        this.secret = PropKit.get("wxa_appSecret");
    }

    public static synchronized WxMpService me() {
        if (service == null) {
            service = new WxMpService();
        }
        return service;
    }

    public String getAccessToken() {


        String accessToken = CacheKit.get("com.qw.WxToken", "WechatMpAccessToken");
        if (StrKit.notBlank(accessToken)) {
            return accessToken;
        }
        String json = HttpKit.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret);
        JSONObject jsonObject = JSON.parseObject(json);
        if (!jsonObject.containsKey("access_token")) {
            throw new RuntimeException("微信获取access _token失败");
        }
        accessToken = jsonObject.getString("access_token");
        CacheKit.put("com.qw.WxToken", "WechatMpAccessToken", accessToken);
        return accessToken;
    }

    public String jsapiTicket(String accessToken) {
        String ticket = CacheKit.get("com.qw.WxToken", "WechatJsApiTicket");
        if (StrKit.notBlank(ticket)) {
            return ticket;
        }
        String json = HttpKit.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi");
        JSONObject jsonObject = JSON.parseObject(json);
        if (!"0".equals(jsonObject.getString("errcode"))) {
            throw new RuntimeException("获取jsapi_ticket失败");
        }
        ticket = jsonObject.getString("ticket");
        CacheKit.put("com.qw.WxToken", "WechatJsApiTicket",  ticket);
        return ticket;
    }
}
