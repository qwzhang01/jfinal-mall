package com.qw.controller.web.sys;

import cn.qw.base.RestController;
import com.qw.model.ExpressCompany;
import com.qw.service.common.ConfigService;
import com.qw.service.common.ExpressCompanyService;
import com.qw.shiro.ShiroInterceptor;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数管理
 */
@RequiresAuthentication
@RequiresPermissions("参数查看")
public class ParamController extends RestController {
    @RequiresPermissions("参数查看-查看")
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");
        Page<Record> pageList = ConfigService.me().pageList(pageNumber, pageSize, key);
        renderJson(pageList);
    }

    @Clear(ShiroInterceptor.class)
    public void dict() {
        String type = getPara("type");
        List<Record> map = ConfigService.me().findList(type);
        renderJson(map);
    }


    @Clear(ShiroInterceptor.class)
    public void basic() {
        String type = getPara("key");
        String value = ConfigService.me().findBasic(type);
        renderJson(value);
    }

    @Clear(ShiroInterceptor.class)
    public void expressCompany() {
        List<ExpressCompany> list = ExpressCompanyService.me().available();
        if (list == null || list.size() <= 0) {
            renderParamError("参数错误，不存在对应下级数据");
            return;
        }
        renderJson(list.stream().map(s -> {
            Record r = new Record();
            r.set("id", s.getId());
            r.set("name", s.getName());
            return r;
        }).collect(Collectors.toList()));
    }
}