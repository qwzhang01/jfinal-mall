package com.qw.controller.web.store;

import cn.qw.base.RestController;
import com.qw.model.Store;
import com.qw.service.bakend.store.StoreService;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺管理
 */
@RequiresAuthentication
@RequiresPermissions(value = {"开店审核", "店铺信息"}, logical = Logical.OR)
public class StoreController extends RestController {

    /**
     * @param key                          |搜索关键字(门店名称\门店所属用户名称)|String|非必填|
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 获取门店列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam storeId|门店id|int|必填
     * @respParam storeName |门店名称|string|必填
     * @respParam storeAddress |门店地址|string|必填
     * @respParam userName  |门店所属人|string|必填
     * @respParam goodCatName |门店主营分类|string|必填
     * @respParam levelName  |门店主营分类级别|string|必填
     * @respParam commissionRate  |平台抽成比例|decimal|必填
     * @respParam saleAmount   |门店销售业绩|decimal|必填
     * @respParam commissionAmount |总计上交平台佣金费用|decimal| 必填
     */
    @RequiresPermissions(value = {"店铺信息-查看"}, logical = Logical.OR)
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");
        Page<Record> pageList = StoreService.me().pageList(key, pageNumber, pageSize);
        renderJson(pageList);
    }

    @RequiresPermissions(value = {"开店审核-查看"}, logical = Logical.OR)
    public void pageAuth() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String storeName = getPara("storeName");
        // 店铺状态，0关闭，1开启，2审核中
        Integer storeStatus = getParaToInt("storeStatus");
        Page<Record> pageList = StoreService.me().pageAuth(storeStatus, storeName, pageNumber, pageSize);
        renderJson(pageList);
    }

    @RequiresPermissions(value = {"开店审核-查看"}, logical = Logical.OR)
    public void auth() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        if (store.getStoreState() != 2) {
            renderParamError("不是待审核状态的店铺，无法审核");
            return;
        }
        store.setStoreState(1);
        store.saveOrUpdate(false);
        renderSuccess("审核成功");
    }

    @RequiresPermissions(value = {"开店审核-查看"}, logical = Logical.OR)
    public void close() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        if (store.getStoreState() != 1) {
            renderParamError("不是开启中的店铺，无法关闭");
            return;
        }
        store.setStoreState(0);
        store.saveOrUpdate(false);
        renderSuccess("关闭成功");
    }

    /**
     * 设置店铺佣金比例
     */
    @RequiresPermissions(value = {"店铺信息-查看"}, logical = Logical.OR)
    public void setButlerRate() {
        int storeId = getParaToInt("storeId", 0);
        BigDecimal butlerRate = getParaToDecimal("butlerRate");
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamNull("店铺ID不能为空");
            return;
        }
        if (butlerRate == null) {
            renderParamNull("平台分佣不能为空");
            return;
        }
        store.setButlerRate(butlerRate);
        boolean update = store.update(false);
        if (update) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * 获取下拉筛选
     */
    public void select() {
        List<Record> select = StoreService.me().select();
        renderJson(select);
    }
}