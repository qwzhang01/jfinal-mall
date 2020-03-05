package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.ValidateKit;
import cn.qw.rabbitmq.RabbitMQKit;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.bakend.cms.ArticleService;
import com.qw.service.common.CaptchaService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * 验证码
 */
public class CaptchaController extends RestController {

    @Clear(RestSecurityInterceptor.class)
    public void test() throws IOException, InterruptedException {
        Channel channel = RabbitMQKit.getChannel();
        channel.basicQos(4);
        channel.basicPublish("test-qw", "test", null, "Hello World".getBytes());
        // 如果消息发送失败了，可以在如下的回调中处理
        if(channel.waitForConfirms())  {
            System.out.println("发送成功");
        }
        RabbitMQKit.free(channel);
        renderText("发布成功");
    }

    /**
     * @param phone|手机号|String|必填
     * @title 获取短信验证码
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Clear(RestSecurityInterceptor.class)
    public void sms() {
        String phone = getPara("phone");
        if (StrKit.isBlank(phone)) {
            renderParamNull("手机号码不能为空");
            return;
        }
        if (!ValidateKit.validateMobile(phone)) {
            renderParamNull("不是有效的手机号码!");
            return;
        }
        if (CaptchaService.me().checkSmsCodeTimes(phone)) {
            CaptchaService.me().sendSmsCode(phone);
            renderSuccess("验证码已发送。");
        } else {
            renderOperateError("验证码已发送，60s内请勿多次获取。");
        }
    }
}
