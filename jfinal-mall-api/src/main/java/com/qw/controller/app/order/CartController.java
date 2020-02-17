package com.qw.controller.app.order;

import cn.qw.base.RestController;
import com.qw.model.Cart;
import com.qw.model.Good;
import com.qw.model.GoodSku;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.order.CartService;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;
import java.util.Map;

/**
 * 购物车管理
 */
public class CartController extends RestController {

    /**
     * @title 购物车列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|购物车id|int|必填
     * @respParam session_id|唯一标识ID|char|必填
     * @respParam goodsId|商品ID|int|必填
     * @respParam goodsSn|商品货号|String|必填
     * @respParam goodsName|商品名称/标题|String|必填
     * @respParam originalImg|商品图片|String|必填
     * @respParam storeCount|商品库存|int|必填
     * @respParam marketPrice|市场价|int|必填
     * @respParam goodsPrice|本店价|int|必填
     * @respParam memberGoodsPrice|memberGoodsPrice|int|必填
     * @respParam supplyPrice|供应价格|int|必填
     * @respParam goodsNum|购买数量|int|必填
     * @respParam specKeyName|商品规格组合名称|String|必填
     * @respParam promType|0 普通订单,1 限时抢购, 2 团购 , 3 促销优惠|int|必填
     * @respParam promId|活动id|int|必填
     * @respParam selected|购物车选中状态|boolean|必填
     * @respParam storeId|商家店铺ID|int|必填
     * @respParam storeName|商家店铺名称|String|必填
     * @respParam doorServiceStatus|是否需要上门安装 0/1.无需上门服务;2.可选上门服务;3.必须上门服务|int|必填
     */
    public void cartList() {
        List<Map<String, Object>> cartList = CartService.me().cartList();
        renderJson(cartList);
    }

    /**
     * @param goodNum|商品数量|int|必填
     * @param skuId|规格商品ID|int|选填
     * @title 加入购物车
     */
    public void addToCart() {
        int skuId = getParaToInt("skuId", 0);
        if (skuId == 0) {
            renderParamNull("请选择要购买的商品");
            return;
        }
        int goodNum = getParaToInt("goodNum", 0);
        if (goodNum == 0) {
            renderParamNull("购买商品数量不能为0");
            return;
        }
        GoodSku sku = GoodSku.dao.findById(skuId);
        if (sku == null) {
            renderParamError("SKU不存在");
            return;
        }
        Good good = Good.dao.findById(sku.getGoodsId());
        if (good == null) {
            renderParamNull("商品不存在");
            return;
        }
        Long goodsSortNum = CartService.me().goodCount();

        Cart cart = CartService.me().findBySku(skuId);
        if (goodsSortNum >= 20 && cart == null) {
            renderParamNull("购物车最多只能放20种商品");
            return;
        }
        if (CartService.me().addToCart(cart, sku, good, goodNum)) {
            renderSuccess("加入购物车成功");
        } else {
            renderOperateError("加入购物车失败！");
        }
    }

    /**
     * @param cartId|购物车ID|int|必填
     * @param type|操作类型           1加 0减|int|必填
     * @title 修改购物车商品数量
     */
    public void updateCartNum() {
        int cartId = getParaToInt("cartId", 0);
        int type = getParaToInt("type", 0);
        if (CartService.me().updateCartNum(cartId, type)) {
            renderSuccess("修改成功");
        } else {
            renderOperateError("删除失败");
        }
    }

    /**
     * @param ids|购物车id(由,拼接)|String|必填
     * @title 删除购物车的商品
     */
    @Before(Tx.class)
    public void deleteCart() {
        List<Integer> idsArr = getList(Integer.class);
        for (Integer id : idsArr) {
            boolean deleteById = Cart.dao.deleteById(id);
            if (!deleteById) {
                renderOperateError("删除失败");
                return;
            }
        }
        renderSuccess("删除成功");
    }

    /**
     * @param ids|购物车id(由,拼接)|String|必填
     * @title 移入收藏夹
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before(Tx.class)
    public void moveToCollect() {
        List<Integer> idsArr = getList(Integer.class);
        for (Integer id : idsArr) {
            boolean operateResult = GoodService.me().cartToCollect(id);
            if (operateResult == false) {
                renderOperateError("操作失败");
                return;
            }
            Cart.dao.deleteById(id);
        }
        renderSuccess("加入收藏夹成功");
    }
}