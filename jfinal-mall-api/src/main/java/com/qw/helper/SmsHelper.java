package com.qw.helper;

import cn.qw.kit.JsonKit;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.qw.event.message.SmsEvent;
import com.qw.service.common.ConfigService;
import com.jfinal.log.Log;
import net.dreamlu.event.EventKit;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.Map;

/**
 * // 接口类型：互亿无线触发短信接口，支持发送验证码短信、订单通知短信等。
 * // 账户注册：请通过该地址开通账户http://user.ihuyi.com/register.html
 * // 注意事项：
 * //（1）调试期间，请使用用系统默认的短信内容：您的验证码是：【变量】。请不要把验证码泄露给其他人。
 * //（2）请使用 APIID 及 APIKEY来调用接口，可在会员中心获取；
 * //（3）该代码仅供接入互亿无线短信接口参考使用，客户可根据实际需要自行编写；
 *
 * @author qw
 */
public class SmsHelper {
    private static final Log log = Log.getLog(SmsHelper.class);
    private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    public static boolean send(String phone, String templateCode, Map<String, String> templateParam) {

        String param = JsonKit.toJson(templateParam);

        CommonRequest request = new CommonRequest();
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse exec = exec(request);
            String data = exec.getData();
            JSONObject jsonObject = JSON.parseObject(data);
            String code = jsonObject.getString("Code");
            EventKit.post(new SmsEvent("OK".equals(code), code, phone, exec.getData(), param));
            return "OK".equals(code);
        } catch (ClientException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            EventKit.post(new SmsEvent(false, "", phone, e.getMessage(), param));
        }
        return false;
    }

    private static CommonResponse exec(CommonRequest request) throws ClientException {
        Map<String, String> aliyunConifg = ConfigService.me().findMap("aliyun_config");
        String aliyun_access_key_id = aliyunConifg.get("aliyun_access_key_id");
        String aliyun_access_key_secret = aliyunConifg.get("aliyun_access_key_secret");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyun_access_key_id, aliyun_access_key_secret);
        IAcsClient client = new DefaultAcsClient(profile);

        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("SignName", "白金管家");

        return client.getCommonResponse(request);
    }

    public static boolean send(String phone, String content) {
        Map<String, String> aliyunConifg = ConfigService.me().findMap("ihuyi_config");

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");

        NameValuePair[] data = {//提交短信
                //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("account", aliyunConifg.get("account")),
                //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                new NameValuePair("password", aliyunConifg.get("password")),
                new NameValuePair("mobile", phone),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try {
            client.executeMethod(method);

            String submitResult = method.getResponseBodyAsString();

            Document doc = DocumentHelper.parseText(submitResult);
            Element root = doc.getRootElement();
            String code = root.elementText("code");

            EventKit.post(new SmsEvent("2".equals(code), code, phone, submitResult, content));

            return "2".equals(code);
        } catch (HttpException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (DocumentException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        EventKit.post(new SmsEvent(false, "", phone, "发送短信异常，请查看异常日志", content));
        return false;
    }
}