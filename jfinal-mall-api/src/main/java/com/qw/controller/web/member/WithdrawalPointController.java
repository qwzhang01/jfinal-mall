package com.qw.controller.web.member;

import cn.qw.base.RestController;
import cn.qw.render.PoiRender;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.event.withdraw.PointWithdrawEvent;
import com.qw.interceptor.ResubmitInterceptor;
import com.qw.model.PointWithdraw;
import com.qw.model.User;
import com.qw.service.bakend.member.PointService;
import net.dreamlu.event.EventKit;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * 积分提现
 */
@RequiresAuthentication
@RequiresPermissions(value = {"打款", "审核", "成功记录", "拒绝记录"}, logical = Logical.OR)
public class WithdrawalPointController extends RestController {

    /**
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @param authStatus|1申请中              2审核通过3 审核未通过|int|非必填
     * @param payStatus|1.未打款              2打款中 3打款成功 4打款失败|int|非必填
     * @param userMobile|用户手机号|String|非必填
     * @param nickname|用户昵称|String|非必填
     * @param startDate|开始时间|String|非必填
     * @param endDate|结束时间|String|非必填
     * @title 获取提现列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|门店个id|int|必填
     * @respParam mobile|用户手机号|String|必填
     * @respParam amount|用户提现金额|decimal|必填
     * @respParam applyTime|申请提现时间|String|必填
     * @respParam bankUserName|持卡人姓名|String|必填
     * @respParam bankName|持卡人姓名|String|必填
     * @respParam bankCard|卡号|String|必填
     * @respParam authStatus|审批状态|int|必填
     * @respParam payStatus|打款状态|int|必填
     * @respParam payTime|打款时间|String|非必填
     * @respParam taxfee|手续费|String|必填
     * @respParam payCode|打款编号|String|非必填
     * @respParam errCode|打款失败编码|String|非必填
     * @respParam authTime|审核时间|String|非必填
     * @respParam refuseReason|不通过原因|String|非必填
     * @respParam authName |审核人|String|非必填
     */
    @RequiresPermissions(value = {"打款-查看", "审核-查看", "成功记录-查看", "拒绝记录-查看"}, logical = Logical.OR)
    public void page() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String withdrawSn = getPara("withdrawSn");
        int authStatus = getParaToInt("authStatus", 0);
        String userMobile = getPara("userMobile");
        String nickname = getPara("nickname");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        Page<Record> page = PointService.me().widthdrawlPage(pageNumber, pageSize, withdrawSn, authStatus, userMobile, nickname, startDate, endDate);
        renderJson(page);
    }
    @RequiresPermissions(value = {"打款-查看", "审核-查看", "成功记录-查看", "拒绝记录-查看"}, logical = Logical.OR)
    public void download() {
        int pageNumber = 1;
        int pageSize = Integer.MAX_VALUE;
        String withdrawSn = getPara("withdrawSn");
        int authStatus = getParaToInt("authStatus", 0);
        String userMobile = getPara("userMobile");
        String nickname = getPara("nickname");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        Page<Record> page = PointService.me().widthdrawlPage(pageNumber, pageSize, withdrawSn, authStatus, userMobile, nickname, startDate, endDate);
        List<Record> list = page.getList();

        String[] headers = new String[]{"申请人", "手机号码", "申请金额", "实际金额", "申请时间", "审核时间", "审核人", "开户行", "持卡人", "银行卡号"};
        String[] columns = new String[]{"nickname", "mobile", "amount", "actual_amount", "applyTime", "authTime", "authName", "bankName", "bankUserName", "bankCard"};
        render(PoiRender.me(list).fileName(encodingFileName("提现打款统计汇总表.xls")).headers(headers).columns(columns));
    }

    /**
     * @param id|提现记录id|int|必填
     * @param authStatus|1申请中2审核通过3     审核未通过|int|必填
     * @param reason|不通过时填写原因|String|必填
     * @title 审核
     */
    @Before({ResubmitInterceptor.class})
    @RequiresPermissions(value = {"审核-审核"}, logical = Logical.OR)
    public void author() {
        int id = getParaToInt("id", 1);
        int status = getParaToInt("authStatus", 10);
        if (status != 2 && status != 5) {
            renderParamError("参数异常，status只能是2或5");
            return;
        }
        String reason = getPara("reason");
        PointService.me().auth(id, status, reason);
        EventKit.post(new PointWithdrawEvent(id));
        renderSuccess("操作成功");
    }

    /**
     * @title 打款
     */
    @RequiresPermissions(value = {"打款-打款"}, logical = Logical.OR)
    public void pay() {
        int id = getParaToInt("id", 0);
        PointWithdraw withdrawal = PointWithdraw.dao.findById(id);
        if (withdrawal == null) {
            renderParamNull("ID不能为空");
            return;
        }
        // 提现状态（1待审核 2待打款 3已完成 5审核拒绝
        if (withdrawal.getWithdrawStatus() != 2) {
            renderParamError("审核不通过的提现申请不可以手动打款");
            return;
        }
        User user = User.dao.findById(withdrawal.getUserId());
        if (user == null) {
            renderParamError("打款失败，用户信息不存在");
            return;
        }
        String openId = user.getOpenId();
        if (StrKit.isBlank(openId)) {
            renderParamError("打款失败，用户没有做微信实名认证，无法使用微信打款");
            return;
        }
        boolean pay = PointService.me().pay(withdrawal, user);
        if (pay) {
            EventKit.post(new PointWithdrawEvent(id));
            renderSuccess("打款成功");
        } else {
            renderOperateError("打款失败");
        }
    }

    /**
     * @title 手动打款
     */
    @RequiresPermissions(value = {"打款-人工打款"}, logical = Logical.OR)
    public void payHand() {
        int id = getParaToInt("id", 0);
        PointWithdraw withdrawal = PointWithdraw.dao.findById(id);
        if (withdrawal == null) {
            renderParamNull("ID不能为空");
            return;
        }
        // 提现状态（1待审核 2待打款 3已完成 5审核拒绝
        if (withdrawal.getWithdrawStatus() != 2) {
            renderParamError("审核不通过的提现申请不可以手动打款");
            return;
        }
        boolean pay = PointService.me().pay(withdrawal);
        if (pay) {
            EventKit.post(new PointWithdrawEvent(id));
            renderSuccess("打款成功");
        } else {
            renderOperateError("打款失败");
        }
    }
}