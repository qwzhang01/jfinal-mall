package com.qw.controller.web.cms;

import cn.qw.base.RestController;
import com.qw.model.HomeSet;
import com.qw.service.bakend.cms.HomeSetService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.stream.Collectors;

@RequiresAuthentication
@RequiresPermissions(value = {"APP首页", "APP启动页", "分类广告"}, logical = Logical.OR)
public class AppSetController extends RestController {

    public void page() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        int type = getParaToInt("type", 0);
        // 1banner2金刚区3资讯区4大广告5小广告6专场
        if (type != 1 && type != 2 && type != 3 && type != 4 && type != 5 && type != 6) {
            renderParamError("type数据错误");
            return;
        }
        Page<HomeSet> page = HomeSetService.me().page(pageNumber, pageSize, type);
        List<Record> list = page.getList().stream().map(s -> {
            Integer gotoId = s.getGotoId();
            Integer gotoType = s.getGotoType();
            Record record = s.toRecord();
            record.remove("gotoId");
            record.remove("goto_id");
            record.set("goto_id", HomeSetService.me().gotoId(gotoType, gotoId));
            return record;
        }).collect(Collectors.toList());
        Page<Record> recordPage = new Page<Record>(list, page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow());
        renderJson(recordPage);
    }

    // 保存表单
    public void save() {
        // 获取参数
        // 1banner2金刚区3资讯区4打广告5小广告6专场
        int type = getParaToInt("type", 0);
        // 1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀
        int gotoType = getParaToInt("gotoType", 0);
        int gotoId = getParaToInt("gotoId", 0);
        int parentId = getParaToInt("parentId", 0);
        String img = getPara("img");
        String title = getPara("title", "");
        String webUrl = getPara("webUrl", "");
        String bgColor = getPara("bgColor", "");
        // 校验参数
        if (type != 1 && type != 2 && type != 3 && type != 4 && type != 5 && type != 6) {
            renderParamError("type/position参数非法");
            return;
        }
        if (gotoType != 1 && gotoType != 2 && gotoType != 3 && gotoType != 4 && gotoType != 5 && gotoType != 6 && gotoType != 7 && gotoType != 8 && gotoType != 9 && gotoType != 10 && gotoType != 11 && gotoType != 12 && gotoType != 13) {
            renderParamError("gotoType 参数非法");
            return;
        }
        if (gotoType == 1 || gotoType == 2 || gotoType == 3 || gotoType == 5 || gotoType == 6) {
            if (gotoId == 0) {
                renderParamError("GotoId 不能为空");
                return;
            }
        }
        if (!StrKit.notBlank(img)) {
            renderParamError("图片地址不能为错误");
            return;
        }
        if (type == 2) {
            if (!StrKit.notBlank(title)) {
                renderParamError("title不能为空");
                return;
            }
        }
        if (gotoType == 4) {
            if (StrKit.isBlank(webUrl)) {
                renderParamError("webUrl不能为空");
                return;
            }
        }
        if (type == 1 || type == 5) {
            if (!StrKit.notBlank(bgColor)) {
                renderParamError("背景颜色不能为空");
                return;
            }
        }
        HomeSet homeSet = new HomeSet();
        homeSet.setPosition(type);
        homeSet.setGotoType(gotoType);
        homeSet.setGotoId(gotoId);
        homeSet.setImg(img);
        homeSet.setTitle(title);
        homeSet.setWebUrl(webUrl);
        homeSet.setBackgroundColor(bgColor);
        homeSet.setParentId(parentId);
        homeSet.save(false);
        renderSuccess("保存成功");
    }

    /**
     * 保存显示状态
     */
    public void showSave() {
        int id = getParaToInt("id", 0);
        int isShow = getParaToInt("isShow", 0);
        HomeSet homeSet = HomeSet.dao.findById(id);
        if (homeSet == null) {
            renderParamError("参数错误，设置不存在");
            return;
        }
        if (1 != isShow && 2 != isShow) {
            // 1显示2不显示
            renderParamError("isShow参数只能是1 或2");
            return;
        }
        homeSet.setIsShow(isShow);
        homeSet.update(false);
        renderSuccess("设置成功");
    }

    // 查询跳转页面
    public void findGoto() {
        int gotoType = getParaToInt("gotoType", 0);
        String keyword = getPara("keyword");
        if (gotoType != 1 && gotoType != 2 && gotoType != 3 && gotoType != 5 && gotoType != 6) {
            renderParamError("参数错误，type传值错误");
            return;
        }
        renderJson(HomeSetService.me().findGoto(gotoType, keyword));
    }

    @Before(Tx.class)
    public void delete() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamError("ID不能为空");
            return;
        }
        HomeSetService.me().deleteByParentId(id);
        HomeSetService.me().deleteById(id);
        renderSuccess("删除成功");
    }

    @Before(Tx.class)
    public void show() {
        int id = getParaToInt("id", 0);
        int isShow = getParaToInt("isShow", 1);
        HomeSet homeSet = HomeSet.dao.findById(id);
        if (homeSet == null) {
            renderParamError("ID不能为空");
            return;
        }
        if (isShow != 1 && isShow != 2) {
            renderParamError("isShow 错误");
            return;
        }
        homeSet.setIsShow(isShow);
        homeSet.save(false);
        renderSuccess("删除成功");
    }

    public void goodPage() {
        int parentId = getParaToInt("parentId", 1);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String goodName = getPara("goodName");
        Page<Record> page = HomeSetService.me().goodPage(pageNumber, pageSize, parentId, goodName);
        renderJson(page);
    }
}