package com.qw.controller.web.sys;

import cn.qw.base.RestController;
import com.qw.service.common.SmsRecordService;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * 短信记录
 */
@RequiresAuthentication
@RequiresPermissions("短信记录")
public class SmsLogController extends RestController {
    @RequiresPermissions("短信记录-查看")
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String mobile = getPara("mobile");

        Page<Record> pageList = SmsRecordService.me().getSmsPage(mobile, null, pageNumber, pageSize);
        renderJson(pageList);
    }
}