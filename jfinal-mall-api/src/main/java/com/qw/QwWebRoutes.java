package com.qw;

import com.jfinal.config.Routes;
import com.qw.controller.common.*;
import com.qw.controller.web.cms.ArticleController;
import com.qw.controller.web.sys.ParamController;
import com.qw.controller.web.sys.RoleController;
import com.qw.controller.web.sys.StaffController;
import com.qw.shiro.ShiroInterceptor;

/**
 * 后台管理系统路由
 *
 * @author qw
 */
public class QwWebRoutes extends Routes {

    @Override
    public void config() {
        addInterceptor(new ShiroInterceptor());

        add("/web/mail", SendMailController.class);
        add("/web/wechat", WechatController.class);

        add("/web/captcha", CaptchaController.class);
        add("/web/qr/code", QrCodeController.class);
        add("/web/file", FileController.class);

        add("/web/article", ArticleController.class);

        add("/web/staff", StaffController.class);
        add("/web/role", RoleController.class);
        add("/web/param", ParamController.class);
    }
}