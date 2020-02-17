package com.qw.controller.web.member;

import cn.qw.base.RestController;
import com.qw.service.bakend.member.PointService;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * 积分明细
 */
@RequiresAuthentication
public class PointController extends RestController {

    @RequiresPermissions(value = {"积分明细-查看"}, logical = Logical.OR)
    public void page() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");
        int type = getParaToInt("type", 0);
        int businessType = getParaToInt("business_type", 0);
        int isWithdraw = getParaToInt("is_withdraw", 0);
        Page<Record> pageList = PointService.me().effectivePage(type, businessType, isWithdraw, key, pageNumber, pageSize);
        renderJson(pageList);
    }
}