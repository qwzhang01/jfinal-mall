package com.qw.controller.web.good;

import cn.qw.base.RestController;
import com.qw.model.GoodCategory;
import com.qw.model.Spec;
import com.qw.model.SpecItem;
import com.qw.service.bakend.good.SpecificationService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * 规格管理
 */
@RequiresAuthentication
@RequiresPermissions("商品规格")
public class SpecController extends RestController {

    /**
     * @param name|规格名称|String|必填
     * @title 商品规格列表
     * @respParam specList|商品规格集合|List<Spec>|必填
     * @respParam id|规格ID|int|必填
     * @respParam name|规格名称|String|必填
     * @respParam specItemListForOneSpec|商品规格对应的规格集合|List<SpecItem>|必填
     * @respParam id|规格值ID|int|必填
     * @respParam item|规格值名称|String|必填
     * @respParam spec_id|规格ID|int|必填
     */
    @RequiresPermissions("商品规格-查看")
    public void page() {
        String name = getPara("name");
        int categoryId = getParaToInt("categoryId", 0);

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> page = SpecificationService.me().pageList(pageNumber, pageSize, name, categoryId);
        renderJson(page);
    }

    public void select() {
        int categoryId = getParaToInt("categoryId", 0);
        if (categoryId == 0) {
            renderParamNull("参数不能为空");
            return;
        }
        List<Record> page = SpecificationService.me().select(categoryId);
        renderJson(page);
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("参数不能为空");
            return;
        }
        Spec spec = Spec.dao.findById(id);
        if (spec == null) {
            renderParamError("参数错误，规格不存在");
            return;
        }

        Record record = new Record();
        record.set("id", spec.getId());
        record.set("name", spec.getName());
        record.set("categoryId", spec.getCategoryId());
        GoodCategory cat = GoodCategory.dao.findById(spec.getCategoryId());
        String parentIdPath = cat.getParentIdPath();
        record.set("categoryIds", parentIdPath.split("_"));

        renderJson(record);
    }

    /**
     * @param id|规格ID|int|编辑必填
     * @param name|规格名称|String|必填
     * @param categoryId|分类ID|int|必填
     * @title 规格新增 & 规格编辑
     * @respBody {"status":"0","data":true, "msg":"保存成功"}
     */
    @RequiresPermissions(value = {"商品规格-新增", "商品规格-编辑"}, logical = Logical.OR)
    public void save() {
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        int categoryId = getParaToInt("categoryId");
        if (!StrKit.notBlank(name)) {
            renderParamNull("规格名字不能为空");
            return;
        }
        if (categoryId == 0) {
            renderParamNull("分类不能为空");
            return;
        }
        Spec spec = Spec.dao.findById(id);
        if (spec == null) {
            spec = new Spec();
        }
        spec.setName(name);
        spec.setCategoryId(categoryId);
        boolean saveOrUpdate = spec.saveOrUpdate(false);
        if (saveOrUpdate) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param id|规格ID|int|必填
     * @title 删除规格(单个)
     * @respPram {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions("商品规格-删除")
    @Before(Tx.class)
    public void delete() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("规格ID不能为空");
            return;
        }
        // TODO 有商品在用的时候，不让删除
        boolean delete = SpecificationService.me().delete(id);
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }

    /**
     * @param id|规格值ID|int|编辑必填
     * @param specId|规格ID|int|必填
     * @param item|规格值名称|String|必填
     * @title 规格值新增 & 规格值编辑
     * @respBody {"status":"0","data":true, "msg":"保存成功"}
     */
    @RequiresPermissions(value = {"商品规格-新增值", "商品规格-编辑值"}, logical = Logical.OR)
    public void saveItem() {
        Integer id = getParaToInt("id", 0);
        Integer specId = getParaToInt("specId", 0);
        String item = getPara("item");
        if (!StrKit.notBlank(item)) {
            renderParamNull("参数不能为空");
            return;
        }
        if (specId == 0) {
            renderParamNull("参数不能为空");
            return;
        }
        SpecItem specItem = new SpecItem();
        if (id != 0) {
            specItem.setId(id);
        }
        specItem.setItem(item);
        specItem.setSpecId(specId);
        boolean saveOrUpdate = specItem.saveOrUpdate(false);
        if (saveOrUpdate) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param id|规格值ID|int|必填
     * @title 删除规格值(单个)
     * @respPram {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions("商品规格-删除值")
    public void deleteItem() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("规格值ID不能为空");
            return;
        }
        boolean delete = SpecItem.dao.deleteById(id);
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }
}