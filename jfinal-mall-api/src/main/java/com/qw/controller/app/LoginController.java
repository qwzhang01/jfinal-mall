package com.qw.controller.app;

import cn.qw.base.RestController;
import cn.qw.kit.CryptKit;
import cn.qw.kit.DeviceKit;
import cn.qw.kit.ValidateKit;
import com.qw.conf.ButlerEmnu;
import com.qw.conf.QuantityConf;
import com.qw.event.user.SignUpEvent;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.model.User;
import com.qw.service.common.CaptchaService;
import com.qw.service.frontend.member.UserService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;
import net.dreamlu.event.EventKit;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录管理
 */
public class LoginController extends RestController {

    /**
     * @param mobile|手机号|String|必填
     * @param password|用户密码|String|必填
     * @param smsCode|短信验证码|String|必填
     * @param firstLeaderMobile|邀请人|String|必填
     * @title 用户注册
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam accessToken|登录token|String|必填
     * @respParam userId|用户id|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void signUp() {
        String mobile = getPara("mobile");
        String password = getPara("password");
        String smsCode = getPara("smsCode");
        String firstLeaderMobile = getPara("firstLeaderMobile");
        if (StrKit.isBlank(firstLeaderMobile)) {
            renderParamNull("请输入邀请人手机号码");
            return;
        }
        User firstUser = UserService.me().findByMobile(firstLeaderMobile);
        if (firstUser == null) {
            renderParamError("邀请人手机号码错误，邀请人不存在");
            return;
        }
        int firstLeader = firstUser.getUserId();
        if (!StrKit.notBlank(mobile, password, smsCode)) {
            renderParamNull("用户名、密码、短信验证码都不能为空");
            return;
        }
        if (password.length() < 6) {
            renderParamError("密码请设置至少6位");
            return;
        }
        User user = UserService.me().getByUserName(mobile);
        if (user != null) {
            renderParamError("该手机已经注册，请直接登录使用");
            return;
        }
        if (!ValidateKit.validateMobile(mobile)) {
            renderParamNull("不是有效的手机号码!");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(smsCode, mobile)) {
            renderParamNull("短信验证码错误，请重新输入!");
            return;
        }
        User users = UserService.me().signUp(mobile, password, firstLeader);
        EventKit.post(new SignUpEvent(users.getUserId()));
        renderSuccess("注册成功");
    }

    /**
     * @param mobile|手机号|String|必填
     * @param smsCode|短信验证码|String|必填
     * @param password|用户密码|String|必填
     * @param confirmPassword|确认密码|String|必填
     * @title 手机短信重置密码
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Clear(RestSecurityInterceptor.class)
    public void reSetPw() {
        String mobile = getPara("mobile");
        String smsCode = getPara("smsCode");
        String password = getPara("password");
        String confirmPassword = getPara("confirmPassword");
        if (!StrKit.notBlank(mobile, smsCode, password, confirmPassword)) {
            renderParamNull("用户名、密码、验证码都不能为空");
            return;
        }
        User user = UserService.me().getByUserName(mobile);
        if (user == null) {
            renderParamError("手机号码对应用户不存在");
            return;
        }
        if (!ValidateKit.validateMobile(mobile)) {
            renderParamNull("不是有效的手机号码!");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(smsCode, mobile)) {
            renderParamNull("短信验证码错误，请重新输入!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            renderParamError("两次输入密码不一致");
            return;
        }
        user.setPassword(CryptKit.butlerMd5(password));
        boolean update = user.update(false);
        if (update) {
            renderSuccess("重置成功");
        } else {
            renderOperateError("重置失败");
        }
    }

    /**
     * @param username|手机号或邮箱|String|必填
     * @param password|用户密码|String|必填
     * @param openId|openId|String|非必填
     * @title 密码登录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam accessToken|登录token|String|必填
     * @respParam userId|用户id|int|必填
     * @respParam openId|openId|String|非必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void signInPw() {
        String username = getPara("username");
        String password = getPara("password");
        String openId = getPara("openId");
        String wxAuthCode = getPara("wxAuthCode");
        if (!StrKit.notBlank(username, password)) {
            renderParamNull("用户名、密码都不能为空");
            return;
        }
        User user = UserService.me().getByUserName(username);
        if (user == null) {
            renderParamError("手机或邮箱不存在");
            return;
        }
        if (!CryptKit.butlerMd5(password).equals(user.getPassword())) {
            renderParamError("密码错误");
            return;
        }
        if (user.getIsLock()) {
            renderParamError("账号异常已被锁定");
            return;
        }
        com.jfinal.weixin.sdk.api.ApiResult userInfo = null;
        if (StrKit.notBlank(wxAuthCode)) {
            userInfo = SnsApi.getUserInfo(wxAuthCode, openId);
            if (!userInfo.isSucceed()) {
                userInfo = null;
            }
        }
        // 处理登录逻辑，保存token
        Map<String, Object> login = UserService.me().login(user, openId, userInfo);
        if (login == null) {
            renderParamError("您绑定的的账号已绑定其他微信，无法重复绑定");
            return;
        }
        renderJson(login);
    }

    /**
     * @param code|code|String|必填
     * @title 微信公众号登录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam accessToken|登录token|String|必填
     * @respParam userId|用户id|int|必填
     * @respParam openId|openId|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void signInWechatMp() {
        ButlerEmnu.DeviceEnum what = DeviceKit.what(getRequest());
        if (!what.equals(ButlerEmnu.DeviceEnum.WEIXIN)) {
            renderParamError("不在微信浏览器中，无法登陆");
            return;
        }
        String code = getPara("code");
        if (!StrKit.notBlank(code)) {
            renderParamNull("code不能为空");
            return;
        }
        SnsAccessToken accessToken = SnsAccessTokenApi.getSnsAccessToken(PropKit.get("wxa_appId"), PropKit.get("wxa_appSecret"), code);
        String wxAccessToken = accessToken.getAccessToken();
        String openId = accessToken.getOpenid();
        if (StrKit.isBlank(openId)) {
            renderOperateError("获取OpenID失败");
            return;
        }
        User user = UserService.me().getByOpenId(openId);
        if (user == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("openId", openId);
            map.put("accessToken", "");
            map.put("wxAccessToken", wxAccessToken);
            map.put("userId", "");
            renderJson(map);
            return;
        }
        if (user.getIsLock()) {
            renderParamError("账号异常已被锁定");
            return;
        }
        com.jfinal.weixin.sdk.api.ApiResult userInfo = SnsApi.getUserInfo(wxAccessToken, openId);
        if (!userInfo.isSucceed()) {
            userInfo = null;
            if (!userInfo.isSucceed()) {
                userInfo = null;
            }
        }
        // 处理登录逻辑，保存token
        Map<String, Object> login = UserService.me().login(user, openId, userInfo);
        if (login == null) {
            renderParamError("您绑定的的账号已绑定其他微信，无法重复绑定");
            return;
        }
        login.put("wxAccessToken", wxAccessToken);
        renderJson(login);
    }

    /**
     * @param mobile|手机号|String|必填
     * @param smsCode|短信验证码|String|必填
     * @param openId|openId|String|非必填
     * @title 手机短信登录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam accessToken|登录token|String|必填
     * @respParam userId|用户id|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void signInSmsCode() {
        String mobile = getPara("mobile");
        String smsCode = getPara("smsCode");
        String openId = getPara("openId");
        String wxAuthCode = getPara("wxAuthCode");
        if (!StrKit.notBlank(mobile, smsCode)) {
            renderParamNull("手机号、短信验证码不能为空");
            return;
        }
        User user = UserService.me().getByUserName(mobile);
        if (user == null) {
            renderParamError("手机号码对应用户不存在");
            return;
        }
        if (!ValidateKit.validateMobile(mobile)) {
            renderParamNull("不是有效的手机号码!");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(smsCode, mobile)) {
            renderParamNull("短信验证码错误，请重新输入!");
            return;
        }
        com.jfinal.weixin.sdk.api.ApiResult userInfo = null;
        if (StrKit.notBlank(wxAuthCode)) {
            userInfo = SnsApi.getUserInfo(wxAuthCode, openId);
            if (!userInfo.isSucceed()) {
                userInfo = null;
            }
        }
        // 处理登录逻辑，保存token
        Map<String, Object> login = UserService.me().login(user, openId, userInfo);
        if (login == null) {
            renderParamError("您绑定的的账号已绑定其他微信，无法重复绑定");
            return;
        }
        renderJson(login);
    }

    /**
     * @param token|token|String|必填
     * @title 退出登录
     */
    public void logout() {
        String token = getPara("token");
        Cache use = Redis.use(QuantityConf.TOKEN_REDIS);
        String userKey = CryptKit.cymd5(CryptKit.cyCry(token));
        use.del(userKey);
        renderSuccess("退出成功");
    }
}