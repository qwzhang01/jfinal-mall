package com.qw.controller.web.good;

import cn.qw.base.RestController;
import com.qw.model.Brand;
import com.qw.model.GoodCategory;
import com.qw.service.bakend.good.BrandService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * 品牌管理
 */
@RequiresAuthentication
@RequiresPermissions("品牌管理")
public class BrandController extends RestController {

    /**
     * @param status|状态                    (0正常 1审核中 2审核失败)|int|非必填
     * @param key|搜索关键字|string|非必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 品牌信息列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|品牌id|int|必填
     * @respParam name|品牌名称|String|必填
     * @respParam logo|品牌logo|String|必填
     * @respParam cat_name|品牌分类|String|必填
     * @respParam is_hot|是否推荐|int|必填
     * @respParam status|状态 |int|必填
     * @respParam sort|排序|int|必填
     */
    @RequiresPermissions("品牌管理-查看")
    public void pageList() {
        Integer status = getParaToInt("status");
        String key = getPara("key");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = BrandService.me().pageList(pageNumber, pageSize, status, key);
        renderJson(pageList);
    }

    public void select() {
        int categoryId = getParaToInt("categoryId", 0);
        List<Record> pageList = BrandService.me().select(categoryId);
        renderJson(pageList);
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        Brand brand = Brand.dao.findById(id);
        if (brand == null) {
            renderParamError("参数错误，品牌不存在");
            return;
        }
        Integer categoryId = brand.getCategoryId();
        GoodCategory category = GoodCategory.dao.findById(categoryId);
        Record record = brand.toRecord();
        record.set("categoryId", categoryId);
        if (category != null) {
            String parentIdPath = category.getParentIdPath();
            record.set("categoryIds", parentIdPath.split("_"));
            String logo = brand.getLogo();
            if (StrKit.notBlank(logo)) {
                record.set("logoShowPath", PropKit.get("fileHost") + logo);
            }
        }
        renderJson(record);
    }

    /**
     * @param id|id|int|非必填
     * @param categoryId|分类id|int|必填
     * @param name|品牌名称|String|必填
     * @param logo|品牌logo|String|必填
     * @param desc|品牌描述|String|必填
     * @title 新增品牌 & 编辑品牌
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions(value = {"品牌管理-新增", "品牌管理-编辑"}, logical = Logical.OR)
    public void save() {
        Long id = getParaToLong("id", 0L);
        int categoryId = getParaToInt("categoryId", 0);
        String name = getPara("name", "");
        String logo = getPara("logo", "");
        String desc = getPara("desc", "");
        if (categoryId == 0) {
            renderParamError("参数不能为空");
            return;
        }
        if (!StrKit.notBlank(name, logo, desc)) {
            renderParamError("参数不能为空");
            return;
        }

        Brand brand = Brand.dao.findById(id);
        if (brand == null) {
            brand = new Brand();
        }
        brand.setCategoryId(categoryId);
        brand.setName(name);
        brand.setLogo(logo);
        brand.setDesc(desc);

        boolean saveOrUpdate = brand.saveOrUpdate(false);
        if (saveOrUpdate) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param ids|品牌id(用逗号拼接)|String|必填
     * @title 删除/批量删除品牌
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions("品牌管理-删除")
    public void delete() {
        String idsStr = getPara("ids");
        if (idsStr == null) {
            renderParamNull("品牌ID不能为空");
            return;
        }
        String[] ids = idsStr.split(",");
        for (int i = 0; i < ids.length; i++) {
            Brand brand = Brand.dao.findById(ids[i]);
            if (brand == null) {
                renderParamError("品牌不存在");
                return;
            }
            boolean delete = Brand.dao.deleteById(ids[i]);
            if (delete) {
                renderSuccess("删除成功");
            } else {
                renderOperateError("删除失败");
            }
        }
    }
}
