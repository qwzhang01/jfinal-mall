package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.ValidateKit;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.common.CaptchaService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;

/**
 * 验证码
 */
public class CaptchaController extends RestController {

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
