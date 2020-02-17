package com.qw.controller.web.member;

import cn.qw.base.RestController;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.service.bakend.member.PointService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * 有奖购买
 */
@RequiresAuthentication
public class InvestController extends RestController {

    @RequiresPermissions(value = {"有奖购买-查看"}, logical = Logical.OR)
    public void page() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");
        Page<Record> pageList = PointService.me().investPage(key, pageNumber, pageSize);
        renderJson(pageList);
    }
}