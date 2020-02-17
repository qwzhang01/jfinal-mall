package com.qw.controller.web.member;

import cn.qw.base.RestController;
import cn.qw.render.PoiRender;
import com.qw.model.User;
import com.qw.service.bakend.member.PointService;
import com.qw.service.bakend.member.UserService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;

/**
 * 用户管理
 */
@RequiresAuthentication
public class MemberController extends RestController {

    /**
     * @param key|搜索关键字|string|非必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 后台获取用户列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam userId|用户id|int|必填
     * @respParam mobile|用户手机号|String|必填
     * @respParam nickName|用户昵称|String|非必填
     * @respParam realName|用户真实姓名|String|非必填
     * @respParam levelName|用户级别|String|非必填
     * @respParam commissionRate|用户等级收益比例|double|非必填
     * @respParam availableAmount|可用金额|double|必填
     * @respParam frozenAmount|未到账金额（冻结金额）|double|必填
     * @respParam withdrawalAmount|提现中的金额|double|必填point
     * @respParam achievementAmount|提现中的金额|double|必填
     */
    @RequiresPermissions(value = {"会员信息-查看"}, logical = Logical.OR)
    public void page() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");
        String firstLeader = getPara("firstLeader");

        Page<Record> pageList = UserService.me().pageList(key, firstLeader, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param key|搜索关键字|string|非必填
     * @title 导出-人员Excel表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions(value = {"会员信息-查看"}, logical = Logical.OR)
    public void exportExcel() {
        // 搜索条件
        String key = getPara("key");
        String firstLeader = getPara("firstLeader");
        int pageNumber = 1;
        int pageSize = Integer.MAX_VALUE;
        Page<Record> pageList = UserService.me().pageList(key, firstLeader, pageNumber, pageSize);

        String[] headers = new String[]{"用户昵称", "手机号码", "真实姓名", "注册时间", "可用积分", "提现积分"};
        String[] columns = new String[]{"nickName", "mobile", "realname", "regTime", "usable_remain", "withdrawable_remain"};

        render(PoiRender.me(pageList.getList()).fileName(encodingFileName("会员信息汇总表.xls")).headers(headers).columns(columns));
    }

    @RequiresPermissions(value = {"会员信息-线下送积分"}, logical = Logical.OR)
    @Before({Tx.class})
    public void savePoint() {
        int userId = getParaToInt("userId", 0);
        BigDecimal point = getParaToDecimal("point");
        int isWithdrow = getParaToInt("isWithdrow", 0);

        User user = User.dao.findById(userId);
        if (user == null) {
            renderParamNull("用户不存在");
            return;
        }
        if (point == null) {
            renderParamNull("积分不能为空");
            return;
        }
        if(point.compareTo(new BigDecimal("0")) <= 0) {
            renderParamNull("积分不能为空");
            return;
        }
        if (isWithdrow != 1 && isWithdrow != 2) {
            renderParamNull("是否可以提现参数错误");
            return;
        }
        if (PointService.me().offLinePoint(user, point, isWithdrow)) {
            renderSuccess("保存成功");
            return;
        }
        renderOperateError("保存失败");
    }
}