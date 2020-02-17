package com.qw.controller.app.member;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.Base64Encoder;
import cn.qw.kit.CryptKit;
import cn.qw.kit.ValidateKit;
import com.qw.model.Store;
import com.qw.model.User;
import com.qw.service.common.CaptchaService;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.member.BalanceService;
import com.qw.service.frontend.member.UserService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.store.StoreService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;

/**
 * 用户管理
 */
public class UserController extends RestController {

    /**
     * @param userId|用户ID|int|必填
     * @title 获取用户详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam user_id|用户ID|Integer|必填
     * @respParam nickname|昵称|String|必填
     * @respParam sex|性别|String|必填
     * @respParam mobile|手机|String|必填
     * @respParam head_pic|头像|String|必填
     * @respParam coupon_count|优惠券数量|int|必填
     * @respParam collect_count|收藏数量|int|必填
     * @respParam focus_count|关注数量|int|必填
     * @respParam visit_count|访问数量|int|必填
     * @respParam return_count|退货数量|int|必填
     * @respParam waitPay|待付款数量|int|必填
     * @respParam waitSend|待发货数量|int|必填
     * @respParam waitReceive|待收货数量|int|必填
     * @respParam orderCount|订单总数量|int|必填
     * @respParam cart_goods_num|购物车数量|int|必填
     * @respParam levelName|用户级别|String|必填
     * @respParam province_name|省|String|必填
     * @respParam city_name|市|String|必填
     * @respParam district_name|区|String|必填
     * @respParam uncomment_count|待评价|int|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeStatus|(1未开店 2开店审核中 3开店成功 4审核失败)|int|必填
     * @respParam qr|二维码地址|String|必填
     */
    public void detail() {
        Integer userId = AppIdKit.getUserId();
        Record info = UserService.me().detail(userId);
        if (info == null) {
            renderParamError("参数错误，用户不存在");
            return;
        }
        long birthday = info.getLong("birthday");
        info.set("birthday", new Date(birthday * 1000L));
        info.set("storeStatus", StoreService.me().status(userId));
        info.set("storeId", 0);
        Store store = StoreService.me().userStore(userId);
        if (store != null) {
            info.set("storeId", store.getStoreId());
        }
        renderJson(info);
    }

    /**
     * @param userId|用户ID|int|必填
     * @title 获取用户简单信息
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam userId|用户ID|Integer|必填
     * @respParam mobile|手机|String|必填
     * @respParam usableRemain|剩余可用积分|decimal|必填
     */
    public void simpleDetail() {
        Record info = UserService.me().simpleDetail();
        if (info == null) {
            renderParamError("参数错误，用户不存在");
            return;
        }
        renderJson(info);
    }

    /**
     * @param bank_name|开户行|String|必填
     * @param bank_card|银行卡好|String|必填
     * @param realname|银行卡好|String|必填
     * @title 绑定银行卡
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void bindBank() {
        String realname = getPara("realname");
        String bank_name = getPara("bank_name");
        String bank_card = getPara("bank_card");
        if (StrKit.isBlank(bank_name)) {
            renderParamError("开户行支行不能为空");
            return;
        }
        if (StrKit.isBlank(bank_card)) {
            renderParamError("银行卡号不能为空");
            return;
        }
        User user = User.dao.findById(AppIdKit.getUserId());
        if (user == null) {
            renderParamError("参数错误，用户不存在");
            return;
        }
        user.setRealname(realname);
        user.setBankName(bank_name);
        user.setBankCard(bank_card);
        boolean update = user.update();
        if (update) {
            renderSuccess("绑定成功");
        } else {
            renderOperateError("绑定失败");
        }
    }

    /**
     * @param userId|用户ID|int|必填
     * @param headImg|头像路径|String|必填
     * @param nickname|昵称|String|必填
     * @param sex|0                    保密 1 男 2 女|int|必填
     * @param birthday|出生日期|Date|必填
     * @param districtId|所在区的ID|int|必填
     * @title 更新用户信息
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void edit() {
        Integer userId = getParaToInt("userId", 0);
        if (userId == 0) {
            renderParamNull("参数错误，用户不存在");
            return;
        }
        String headImg = getPara("headImg");
        String nickname = getPara("nickname");
        Integer sex = getParaToInt("sex", 0);
        Date birthday = getParaToDate("birthday");
        Integer districtId = getParaToInt("districtId");
        boolean edit = UserService.me().edit(userId, headImg, nickname, sex, birthday, districtId);
        if (edit) {
            renderSuccess("更新成功");
        } else {
            renderOperateError("更新失败");
        }
    }

    /**
     * @param userId|用户ID|int|必填
     * @param mobile|修改后的手机号码|String|必填
     * @param oldSmsCode|原手机验证按|String|必填
     * @param newSmsCode|新手机验证码|String|必填
     * @title 修改手机号码
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void changeMobile() {
        int userId = getParaToInt("userId", 0);
        String oldSmsCode = getPara("oldSmsCode");
        String mobile = getPara("mobile");
        String newSmsCode = getPara("newSmsCode");
        if (userId == 0) {
            renderParamNull("参数错误，用户不存在");
            return;
        }
        if (!StrKit.notBlank(oldSmsCode, mobile, newSmsCode)) {
            renderParamError("原短信验证码、新手机号、新短信验证按都不能为空");
            return;
        }
        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }
        User validUser = UserService.me().getByUserName(mobile);
        if (validUser != null) {
            renderParamError("该手机已经注册，请直接登录使用");
            return;
        }
        if (!ValidateKit.validateMobile(mobile)) {
            renderParamNull("不是有效的手机号码!");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(oldSmsCode, user.getMobile())) {
            renderParamNull("原手机短信验证码错误，请重新输入!");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(newSmsCode, mobile)) {
            renderParamNull("新手机短信验证码错误，请重新输入!");
            return;
        }
        user.setMobile(mobile);
        user.update(false);
        renderSuccess("修改成功");
    }

    /**
     * @param smsCode|短信验证码|String|必填
     * @param oldPassword|旧密码|String|必填
     * @param newPassword|新密码|String|必填
     * @title 修改密码
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void changePwd() {
        String smsCode = getPara("smsCode");
        String oldPassword = getPara("oldPassword");
        String newPassword = getPara("newPassword");
        if (!StrKit.notBlank(smsCode, oldPassword, newPassword)) {
            renderParamError("原密码、新密码、短信验证码都不能为空");
            return;
        }
        User user = User.dao.findById(AppIdKit.getUserId());
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }

        if (!CaptchaService.me().validateSmsCode(smsCode, user.getMobile())) {
            renderParamNull("手机短信验证码错误，请重新输入!");
            return;
        }
        if (!CryptKit.butlerMd5(oldPassword).equals(user.getPassword())) {
            renderParamError("旧密码错误");
            return;
        }
        user.setPassword(CryptKit.butlerMd5(newPassword));
        user.saveOrUpdate(false);
        renderSuccess("修改成功");
    }

    /**
     * @title 下线汇总
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam data|下线人数汇总|long|必填
     */
    public void sublineSum() {
        Integer userId = AppIdKit.getUserId();
        if (userId == null || userId == 0) {
            renderParamError("用户ID不能为空");
            return;
        }
        Long count = UserService.me().inviteCount(userId);
        renderJson(count);
    }

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页容量|int|必填
     * @title 下线分页列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam user_id|用户ID|int|必填
     * @respParam nickname|用户昵称|long|必填
     * @respParam resgister_time|注册时间|String|必填
     * @respParam order_count|订单总数|long|必填
     * @respParam invest_total|投资总额|double|必填
     */
    public void sublinePage() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Integer userId = AppIdKit.getUserId();
        if (userId == null || userId == 0) {
            renderParamError("用户ID不能为空");
            return;
        }
        Page<Record> page = UserService.me().sublinePage(userId, pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @param userId|用户ID|int|必填
     * @title 用户邀请汇总(已废弃)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam inviteCount|要求注册人数|int|必填
     * @respParam orderTotalCount|要求人下单总数|String|必填
     * @respParam achievementAmount|返利总金额|decinal|必填
     * @respParam phone|手机|String|必填
     * @respParam token|token|String|必填
     * @respParam qr|二维码地址|String|必填
     */
    @Deprecated
    public void inviteSum() {
        Integer userId = getParaToInt("userId", 0);
        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }

        Record record = new Record();
        record.set("inviteCount", UserService.me().inviteCount(user.getUserId()));
        record.set("orderTotalCount", OrderService.me().inviteOrderTotalCount(user));
        record.set("achievementAmount", BalanceService.me().achievementAmount(user));
        record.set("phone", user.getMobile());
        record.set("token", AppIdKit.getToken());

        String url = ConfigService.me().findBasic("register_url");
        url += url + user.getMobile();
        url = Base64Encoder.encode(url);
        record.set("qr", PropKit.get("host") + "/tomcat/api/qr/code?title=" + user.getNickname() + "&width=200&height=200&url=" + url);

        renderJson(record);
    }

    /**
     * @param userId|用户ID|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 获取我的邀请人列表(已废弃)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam mobile|手机|String|必填
     * @respParam nickname|昵称|String|必填
     * @respParam regTime|注册时间|String|必填
     * @respParam userId|用户id|int|必填
     * @respParam headImg|用户头像|String|必填
     */
    @Deprecated
    public void inviteUserList() {
        Integer userId = getParaToInt("userId", 0);
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = UserService.me().inviteUserPageList(userId, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param userId|用户ID|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 获取我的邀请人下单列表(已废弃)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam commissionPrice|返佣金额|double|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam orderAmount|支付金额|double|必填
     * @respParam payTime|支付时间|String|必填
     */
    @Deprecated
    public void inviteOrderList() {
        Integer userId = getParaToInt("userId", 0);
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = OrderService.me().inviteUserPageList(userId, pageNumber, pageSize);
        renderJson(pageList);
    }
}