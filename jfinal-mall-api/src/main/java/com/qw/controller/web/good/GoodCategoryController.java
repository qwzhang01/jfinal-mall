package com.qw.controller.web.good;

import cn.qw.base.RestController;
import cn.qw.kit.ArrayKit;
import com.qw.model.GoodCategory;
import com.qw.service.frontend.good.CategoryService;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Arrays;
import java.util.List;

/**
 * 商品分类管理
 */
@RequiresAuthentication
@RequiresPermissions("商品分类")
public class GoodCategoryController extends RestController {

    /**
     * @title 商品分类列表
     */
    @RequiresPermissions("商品分类-查看")
    public void tree() {
        List<Record> list = CategoryService.me().all(null);
        renderJson(list);
    }

    @RequiresPermissions("商品分类-查看")
    public void parentTree() {
        List<Record> list = CategoryService.me().parentTree();
        renderJson(list);
    }

    public void detail() {
        int id = getParaToInt("id", 0);
        GoodCategory detail = GoodCategory.dao.findById(id);
        if (detail == null) {
            renderParamError("参数错误，id为空");
            return;
        }
        Record record = new Record();
        record.set("id", detail.getId());
        record.set("mobileName", detail.getMobileName());
        record.set("parentId", detail.getParentId());
        Integer[] pId = Arrays.stream(detail.getParentIdPath().split("_"))
                .filter(i -> StrKit.notBlank(i))
                .filter(i -> !"0".equals(i))
                .map(i -> Integer.valueOf(i)).toArray(Integer[]::new);
        record.set("categoryIds", ArrayKit.subArray(pId, 0, pId.length - 2));
        record.set("image", detail.getImage());
        record.set("imageShowPath", PropKit.get("fileHost") + detail.getImage());
        record.set("icon", detail.getIcon());
        record.set("iconShowPath", PropKit.get("fileHost") + detail.getIcon());
        record.set("isShow", detail.getIsShow());
        record.set("isHot", detail.getIsHot());
        renderJson(record);
    }

    /**
     * @param id|分类ID|int|必填
     * @param name|分类名称|String|必填
     * @param mobile_name|品牌网址|String|必填
     * @param parent_id|一级分类id|int|必填
     * @param is_show|二级分类id|int|必填
     * @param image|品牌logo|String|必填
     * @param is_hot|排序|int|必填
     * @title 新增分类 & 编辑分类
     * @respBody {"status":"0","data":true, "msg":"保存成功"}
     */
    @RequiresPermissions(value = {"商品分类-新增", "商品分类-编辑"}, logical = Logical.OR)
    @Before(Tx.class)
    public void save() {
        long id = getParaToLong("id", 0L);
        String mobileName = getPara("mobileName", "");
        long parentId = getParaToLong("parentId", 0L);
        long isShow = getParaToLong("isShow", 0L);    //是否显示
        String image = getPara("image", "");
        String icon = getPara("icon", "");
        int isHot = getParaToInt("isHot", 0);        //是否推荐为热门分类

        if (!StrKit.notBlank(icon, mobileName)) {
            renderParamError("参数不能为空");
            return;
        }
        boolean save = CategoryService.me().save(id, mobileName, parentId, isHot, isShow, image, icon);
        if (save) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param id|分类id|int|必填
     * @title 删除分类(单个)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @RequiresPermissions("商品分类-删除")
    public void delete() {
        Integer id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("分类ID不能为空");
            return;
        }
        GoodCategory goodCategory = GoodCategory.dao.findById(id);
        if (goodCategory == null) {
            renderParamError("分类不存在");
            return;
        }
        boolean isParentId = CategoryService.me().isParentId(id);
        if (isParentId) {
            renderOperateError("删除失败，父类分类不可删除");
            return;
        }
        boolean hasLinkGood = CategoryService.me().hasLinkGood(id);
        if (hasLinkGood) {
            renderOperateError("删除失败，部分商品已使用该分类");
            return;
        }
        boolean delete = goodCategory.delete();
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }
}