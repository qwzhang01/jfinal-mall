package com.qw;

import com.qw.controller.web.member.*;
import com.qw.controller.web.cms.AppSetController;
import com.qw.controller.web.cms.ArticleController;
import com.qw.controller.web.cms.HomeController;
import com.qw.controller.web.good.BrandController;
import com.qw.controller.web.good.GoodCategoryController;
import com.qw.controller.web.good.GoodController;
import com.qw.controller.web.good.SpecController;
import com.qw.controller.web.order.DoorServiceController;
import com.qw.controller.web.order.OrderController;
import com.qw.controller.web.order.ServiceWorkerControler;
import com.qw.controller.web.prom.CouponController;
import com.qw.controller.web.prom.FlashController;
import com.qw.controller.web.prom.LotteryController;
import com.qw.controller.web.store.StoreController;
import com.qw.controller.web.sys.*;
import com.qw.shiro.ShiroInterceptor;
import com.jfinal.config.Routes;

/**
 * 后台管理系统路由
 *
 * @author qw
 */
public class QwWebRoutes extends Routes {

    @Override
    public void config() {
        addInterceptor(new ShiroInterceptor());

        add("/web/member", MemberController.class);
        add("/web/invest", InvestController.class);
        add("/web/point", PointController.class);
        add("/web/point/withdrawal", WithdrawalPointController.class);

        add("/web/article", ArticleController.class);
        add("/web/home", HomeController.class);
        add("/web/app/set", AppSetController.class);

        add("/web/brand", BrandController.class);
        add("/web/category", GoodCategoryController.class);
        add("/web/good", GoodController.class);
        add("/web/spec", SpecController.class);

        add("/web/order", OrderController.class);
        add("/web/door/server", DoorServiceController.class);
        add("/web/service/worker", ServiceWorkerControler.class);

        add("/web/flash", FlashController.class);
        add("/web/lottery", LotteryController.class);
        add("/web/coupon", CouponController.class);

        add("/web/store", StoreController.class);

        add("/web/staff", StaffController.class);
        add("/web/role", RoleController.class);
        add("/web/param", ParamController.class);
        add("/web/sms/log", SmsLogController.class);
    }
}