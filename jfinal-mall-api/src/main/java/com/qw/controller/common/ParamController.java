package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.MathKit;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.qw.interceptor.SecurityInterceptor;
import com.qw.model.Region;
import com.qw.service.common.ConfigService;
import com.qw.service.common.RegionService;

import java.util.List;
import java.util.Map;

/**
 * 参数管理
 */
public class ParamController extends RestController {

    /**
     * @param parentId|父级ID|int|可为空
     * @title 获取省市区三级联动数据
     * @respParam id|id |int|必填
     * @respParam name|名称|int|必填
     * @respParam level|等级|int|必填
     * @respParam parent_id|父级ID|int|必填
     */
    @Clear(SecurityInterceptor.class)
    public void regin() {
        Integer parentId = getParaToInt("parentId", 0);
        List<Region> list = RegionService.me().findByParentId(parentId);
        if (list == null || list.size() <= 0) {
            renderParamError("参数错误，不存在对应下级数据");
            return;
        }
        renderJson(list);
    }

    /**
     * @param version|版本号|int|必填
     * @title 获取最新APP
     * @respParam new|（0无新版本1有新版本）|int|必填
     * @respParam url|新版本日志|String|非必填
     * @respParam log|更新日志|String|非必填
     * @respParam force|是否强制更新|boolean|必填
     */
    @Clear
    public void androidApk() {
        String version = getPara("version");
        if (StrKit.isBlank(version)) {
            renderParamError("app版本号无效");
            return;
        }
        Map<String, String> android = ConfigService.me().findMap("android");
        String app_version = android.get("app_version");
        String app_log = android.get("app_log");
        String app_path = android.get("app_path");

        Record result = new Record();
        result.set("new", 0);
        if (MathKit.compareVersion(version, app_version) < 0) {
            result.set("new", 1);
            result.set("url", PropKit.get("fileHost") + app_path);
            result.set("log", app_log);
            result.set("force", false);
        }
        renderJson(result);
    }
}