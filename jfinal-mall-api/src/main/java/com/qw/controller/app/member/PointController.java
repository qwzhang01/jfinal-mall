package com.qw.controller.app.member;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.DateKit;
import com.qw.model.PointSum;
import com.qw.model.PointWithdraw;
import com.qw.model.User;
import com.qw.service.common.CaptchaService;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.member.UserSignService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.member.PointService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分控制器
 */
public class PointController extends RestController {
    /**
     * @title 获取积分汇总信息
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam mine_invest_total|我的投资汇总|decimal|必填
     * @respParam subline_invest_total|我的下线投资汇总|decimal|必填
     * @respParam mine_invest_remain|我的投资剩余积分|decimal|必填
     * @respParam subline_invest_remain|我的下线投资剩余积分|decimal|必填
     * @respParam freeze_remain|待领取的剩余积分（剩余冻结积分）|decimal|必填
     * @respParam usable_remain|剩余可用积分（包含可提现积分）|decimal|必填
     * @respParam withdrawable_remain|剩余可提现积分|decimal|必填
     * @respParam total_income|总收益|decimal|必填
     * @respParam withdrawable_total|总提现总额|decimal|必填
     */
    public void sum() {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        PointSum sum = PointService.me().findByUserId(userId);
        Record result = sum.toRecord();
        result.remove("id");
        result.remove("create_time");
        result.remove("created_by");
        result.remove("update_time");
        result.remove("update_by");
        // 总收益
        result.set("total_income", sum.getUsableRemain().add(sum.getWithdrawableTotal().add(sum.getConsumeTotal())));
        result.set("withdraw_limit", ConfigService.me().findValue("point_config", "withdraw_limit"));
        renderJson(result);
    }

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页容量|int|必填
     * @param verificationStatus|0全部 1已核销 2未核销|int|必填
     * @title 获取我的投资列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam order_sn|订单编号|String|必填
     * @respParam good_name|商品名称|String|必填
     * @respParam good_price|金额|String|必填
     */
    public void mineInvestPage() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        Page<Record> page = OrderService.me().investPage(userId, pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页容量|int|必填
     * @param verificationStatus|0全部 1已核销 2未核销|int|必填
     * @title 获取我的投资列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam order_sn|订单编号|String|必填
     * @respParam good_name|商品名称|String|必填
     * @respParam good_price|金额|String|必填
     * @respParam nickname|会员昵称|String|必填
     */
    public void subInvestPage() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> page = OrderService.me().subInvestPage(pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页容量|int|必填
     * @title 收益明细
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam business_type|1注册 2签到 3 取消订单 4提现 5消费|int|必填
     * @respParam amount|金额|double|必填
     * @respParam create_time|时间|String|必填
     */
    public void incomeDetail() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        Page<Record> page = PointService.me().incomeDetailPage(userId, pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页容量|int|必填
     * @title 提现记录
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam withdraw_sn|提现编码|String|必填
     * @respParam create_time|申请时间|String|必填
     * @respParam amount|提现金额|amount|必填
     * @respParam withdraw_status|提现状态（1待审核 2待打款 3已完成 5审核拒绝|int|必填
     */
    public void withdrawDetail() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        Page<PointWithdraw> page = PointService.me().withdrawDetail(userId, pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @title 判断今天是否已经签到
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam data|当天是否已签到|boolean|必填
     */
    public void hasCheckIn() {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
        }
        boolean hasCheckIn = UserSignService.me().hasCheckIn(userId, new Date());
        renderJson(hasCheckIn);
    }

    /**
     * @param amount|提现金额|int|必填
     * @param name|姓名|String|必填
     * @param bankName|开户行|String|必填
     * @param bankCard|银行卡号|String|必填
     * @param phoneCaptcha|手机验证码|String|必填
     * @title 提现申请
     */
    public void withdraw() {
        if(DateKit.isSunday(new Date())){
            renderParamError("周日不能申请提现，请周一再申请");
            return;
        }
        BigDecimal amount = getParaToDecimal("amount");
        String phoneCaptcha = getPara("phoneCaptcha");
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
            return;
        }
        if (!StrKit.notBlank(phoneCaptcha)) {
            renderParamNull("短信验证码不能为空");
            return;
        }
        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamNull("用户ID无效");
            return;
        }
        String mobile = user.getMobile();
        if (StrKit.isBlank(mobile)) {
            renderParamError("请先绑定手机号");
            return;
        }
        if (StrKit.isBlank(user.getRealname()) || StrKit.isBlank(user.getBankCard()) || StrKit.isBlank(user.getBankName())) {
            renderParamError("请先绑定好银行卡");
            return;
        }
        if (!CaptchaService.me().validateSmsCode(phoneCaptcha, mobile)) {
            renderParamError("短信验证码错误");
            return;
        }
        if (amount == null || amount.compareTo(new BigDecimal("0")) <= 0) {
            renderParamNull("提现金额不能为空");
            return;
        }
        // 提现申请的时候，把金额赚钱包，在钱包里面提现
        PointSum sum = PointService.me().findByUserId(userId);
        if (sum.getWithdrawableRemain().compareTo(new BigDecimal("0")) <= 0) {
            renderParamError("可提现积分为0，无法提现");
            return;
        }
        if (sum.getWithdrawableRemain().compareTo(amount) < 0) {
            renderParamError("提现金额大于可提现总额");
            return;
        }
        // 按照倍数提现
        String withdrawLimit = ConfigService.me().findValue("point_config", "withdraw_limit");
        BigDecimal withdrawConfig = new BigDecimal(withdrawLimit);
        if(amount.compareTo(withdrawConfig) < 0) {
            renderParamError("提现金额只能是" + withdrawLimit + "的倍数");
            return;
        }
        BigDecimal remainder = amount.divideAndRemainder(withdrawConfig)[1];
        if(remainder.compareTo(new BigDecimal("0")) != 0) {
            renderParamError("提现金额只能是" + withdrawLimit + "的倍数");
            return;
        }
        // 一天只能申请一次
        if(PointService.me().hasApply(new Date())){
            renderParamError("您今天已经申请过提现了，请明天再申请");
            return;
        }
        // 从积分转入钱包
        PointService.me().withdraw(sum, amount, user);
        renderSuccess("提现成功");
    }
}