package com.qw;

import com.qw.controller.app.LoginController;
import com.qw.controller.app.StoreController;
import com.qw.controller.app.cms.ArticleController;
import com.qw.controller.app.cms.HomeController;
import com.qw.controller.app.good.FlashController;
import com.qw.controller.app.good.GoodController;
import com.qw.controller.app.good.LotteryController;
import com.qw.controller.app.member.*;
import com.qw.controller.app.order.CartController;
import com.qw.controller.app.order.OrderController;
import com.qw.controller.app.order.PayController;
import com.qw.controller.app.order.PromOrderController;
import com.qw.controller.common.*;
import com.jfinal.config.Routes;

/**
 * 客户端接口路由
 *
 * @author qw
 */
public class QwRestRoutes extends Routes {

    @Override
    public void config() {
        add("/api/home", HomeController.class);

        add("/api/login", LoginController.class);
        add("/api/user", UserController.class);
        add("/api/address", UserAddressController.class);
        add("/api/balance", BalanceController.class);
        add("/api/store", StoreController.class);

        add("/api/point", PointController.class);

        add("/api/good", GoodController.class);
        add("/api/prom/flash", FlashController.class);
        add("/api/prom/lottery", LotteryController.class);

        // 购物车路由
        add("/api/cart", CartController.class);
        // 订单路由
        add("/api/order", OrderController.class);
        // 促销订单
        add("/api/prom/order", PromOrderController.class);
        // 支付
        add("/api/pay", PayController.class);

        // 售后路由
        add("/api/door/server", DoorServerController.class);

        add("/api/article", ArticleController.class);

        add("/api/mail", SendMailController.class);
        add("/api/wechat", WechatController.class);
        add("/api/param", ParamController.class);

        add("/api/captcha", CaptchaController.class);
        add("/api/qr/code", QrCodeController.class);
        //文件上传
        add("/api/file", FileController.class);
    }
}