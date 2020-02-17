package com.qw.controller.app.member;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import com.qw.model.Recharge;
import com.qw.model.User;
import com.qw.service.common.CaptchaService;
import com.qw.service.frontend.member.BalanceService;
import com.qw.service.frontend.member.RechargeService;
import com.qw.service.frontend.member.UserService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 钱包管理
 */
public class BalanceController extends RestController {

    /**
     * @param userId|手机ID|int|必填
     * @title 钱包详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam availableAmount|可用金额|decimal|必填
     * @respParam frozenAmount|冻结金额|decimal|必填
     */
    public void detail() {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        Record detail = BalanceService.me().detail(userId);
        renderJson(detail);
    }

    /**
     * @param userId|手机ID|int|必填
     * @param year|年份|int|非必填
     * @param month|月份|int|非必填
     * @param amountType|1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8推广客户首单平台奖励|int|非必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 收支明细
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam inTotal|收入总金额|decimal|必填
     * @respParam outTotal|支出总金额|decimal|必填
     * @respParam page|分页列表 |Page<Object>|必填
     * @respParam amount|金额|decimal|必填
     * @respParam amountType|1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费|int|必填
     * @respParam inOutFlag|1收入 2支出|int|必填
     * @respParam frozenFlag|订单支付但是没有结束的佣金1冻结 2正常，3取消|int|必填
     * @respParam createTime|时间|datetime|必填
     */
    public void list() {
        Integer userId = AppIdKit.getUserId();
        int year = getParaToInt("year", 0);
        int month = getParaToInt("month", 0);
        int amountType = getParaToInt("amountType", 0);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        if (year == 0) {
            renderParamError("年不能为空");
            return;
        }
        if (month == 0) {
            renderParamError("月不能为空");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date start = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = calendar.getTime();

        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }
        Page<Record> pageList = BalanceService.me().pageList(user, amountType, start, end, pageNumber, pageSize);
        BigDecimal inTotal = BalanceService.me().total(user, amountType, start, end, 1);
        BigDecimal outTotal = BalanceService.me().total(user, amountType, start, end, 2);
        Record result = new Record();
        result.set("page", pageList);
        result.set("inTotal", inTotal);
        result.set("outTotal", outTotal);
        renderJson(result);
    }

    /**
     * @param userId|手机ID|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 充值记录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam amount|金额|decimal|必填
     * @respParam createTime|时间|datetime|必填
     * @respParam payStatus|0:待支付 1:充值成功 2:交易关闭|decimal|必填
     */
    public void chargeList() {
        Integer userId = AppIdKit.getUserId();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }
        Page<Record> pageList = RechargeService.me().pageList(user, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param userId|手机ID|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 提现记录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam amount|金额|decimal|必填
     * @respParam applyTime|时间|datetime|必填
     * @respParam payStatus|1.未打款 2打款中 3打款成功 4打款失败|decimal|必填
     * @respParam authStatus|1申请中 2审核通过3 审核未通过|decimal|必填
     */
    public void withdrawalList() {
        Integer userId = AppIdKit.getUserId();
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamError("用户不存在");
            return;
        }
        // TODO 提现逻辑和以前的不一样了
        // Page<Record> widthdrawlPage = WithdrawalService.me().widthdrawlPage(user, pageNumber, pageSize);
        // renderJson(widthdrawlPage);
    }

    /**
     * @param userId|手机ID|int|必填
     * @param name|开户人姓名|String|必填
     * @param bankName|开户行|String|必填
     * @param bankCard|银行卡号|String|必填
     * @param phoneCaptcha|短信验证按|String|必填
     * @param amount|提现金额|String|必填
     * @title 申请提现
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void apply() {
        // 获取参数
        Integer userId = AppIdKit.getUserId();
        String name = getPara("name");
        String bankName = getPara("bankName");
        String bankCard = getPara("bankCard");
        String phoneCaptcha = getPara("phoneCaptcha");
        String amountStr = getPara("amount");
        // 校验参数
        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamNull("用户ID无效");
            return;
        }
        if (!StrKit.notBlank(name, bankName, bankCard, phoneCaptcha, amountStr)) {
            renderParamNull("参数不能为空（）");
            return;
        }
        String mobile = user.getMobile();
        if (StrKit.isBlank(mobile)) {
            renderParamError("请先绑定手机号");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(phoneCaptcha, mobile)) {
            renderParamError("短信验证码错误");
            return;
        }
        BigDecimal amount = new BigDecimal(amountStr);
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            renderParamError("提现金额不能小于0");
            return;
        }
        // TODO 提现和以前的逻辑不一样了
        /*boolean applyWithdrawal = UserService.me().applyWithdrawal(user, name, bankName, bankCard, amount);
        if (applyWithdrawal) {
            renderSuccess("申请成功");
        } else {
            renderParamError("余额不足");
        }*/
    }

    /**
     * @param userId|手机ID|int|必填
     * @param amount|充值金额|decimal|必填
     * @title 充值
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam order_id|充值订单ID|String|必填
     * @respParam order_sn|充值订单编号|String|必填
     * @respParam user_id|用户ID|int|必填
     * @respParam account|充值金额|decimal|必填
     */
    public void charge() {
        Integer userId = AppIdKit.getUserId();
        String amountStr = getPara("amount");
        // 校验参数
        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamNull("用户ID无效");
            return;
        }
        BigDecimal amount = new BigDecimal(amountStr);
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            renderParamError("充值金额不能小于0");
            return;
        }
        Recharge charge = RechargeService.me().charge(user, amount);
        if (charge != null) {
            renderJson(charge);
        } else {
            renderOperateError("充值失败");
        }
    }
}