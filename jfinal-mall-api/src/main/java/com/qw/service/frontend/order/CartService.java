package com.qw.service.frontend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import com.qw.model.*;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 购物车管理
 * @author qw
 */
public class CartService extends BaseService {
    private static CartService service;

    private CartService() {
    }

    public static synchronized CartService me() {
        if (service == null) {
            service = new CartService();
        }
        return service;
    }

    public List<Map<String, Object>> cartList() {
        StringBuilder sql = new StringBuilder("SELECT c.id id, c.goods_id goodsId, c.goods_sn goodsSn, c.goods_name goodsName");
        sql.append(",c.goods_num goodsNum, c.spec_key_name specKeyName");
        sql.append(",s.store_id storeId, s.store_name storeName");
        sql.append(",sku.item_id, sku.market_price marketPrice, sku.price goodsPrice, sku.supply_price supplyPrice, sku.store_count storeCount");
        sql.append(",g.goods_id, g.door_service_status doorServiceStatus, g.original_img originalImg, g.is_free_shipping isFreeShipping");
        sql.append(" FROM butler_cart c");
        sql.append(" LEFT JOIN  butler_good_sku sku ON sku.item_id = c.item_id");
        sql.append(" LEFT JOIN  butler_good g ON c.goods_id = g.goods_id");
        sql.append(" LEFT JOIN  butler_store s ON c.store_id = s.store_id");
        sql.append(" WHERE c.user_id = ?  ORDER BY c.add_time DESC");
        //对查询结果按商家店铺进行分类
        return groudByStoreName(Db.find(sql.toString(), AppIdKit.getUserId()));
    }

    /**
     * 查询结果按上商家店铺名分类
     */
    private List<Map<String, Object>> groudByStoreName(List<Record> cartList) {
        Map<String, List<Record>> cartMap = cartList.stream().map(c->{
            String originalImg = c.getStr("originalImg");
            if(StrKit.notBlank(originalImg)) {
                c.set("originalImg", PropKit.get("fileHost") + originalImg);
            }
            return c;
        }).collect(Collectors.groupingBy(c->c.getStr("storeName")));

        return cartMap.keySet().stream().map(key->{
            List<Record> recordList = cartMap.get(key);

            Map<String, Object> resMap = new HashMap<>();
            resMap.put("sendWay", Arrays.asList("包邮"));
            resMap.put("storeName", recordList.get(0).getStr("storeName"));
            resMap.put("storeId", recordList.get(0).getInt("storeId"));
            BigDecimal sumPrice = new BigDecimal("0");
            Integer sumNum = 0;
            for (Record record : recordList) {
                if(record.get("goods_id") != null && record.get("item_id") != null){
                    sumPrice = sumPrice.add(record.getBigDecimal("goodsPrice").multiply(new BigDecimal(String.valueOf(record.getInt("goodsNum")))));
                    sumNum += record.getInt("goodsNum");
                    record.set("status", 1);
                }else{
                    // 已经下架
                    record.set("status", 0);
                }
                record.remove("goods_id");
                record.remove("item_id");
            }
            resMap.put("sumPrice", sumPrice);
            resMap.put("sumNum", sumNum);
            resMap.put("list", recordList);
            return resMap;
        }).collect(Collectors.toList());
    }

    /**
     * 统计用户购物车中商品种类数目
     */
    public Long goodCount() {
        String sql = "SELECT COUNT(*) FROM butler_cart WHERE user_id = ?";
        return Db.queryLong(sql, AppIdKit.getUserId());
    }

    public Cart findBySku(int skuId) {
        String sql = "SELECT * FROM butler_cart WHERE item_id = ? AND user_id = ? LIMIT 1";
        return Cart.dao.findFirst(sql, skuId, AppIdKit.getUserId());
    }
    /**
     * 判断商品是否存在于购物车 且 将商品添加至购物车中
     */
    public boolean addToCart(Cart cart, GoodSku goodSku, Good good, Integer goodsNum) {
        if (cart != null) {
            // 已存在，增加数量即可
            cart.setGoodsNum(cart.getGoodsNum() + goodsNum);
            return cart.saveOrUpdate(false);
        }
        cart = new Cart();
        cart.setUserId(AppIdKit.getUserId());
        cart.setSessionId(AppIdKit.getUniqueId());
        cart.setGoodsId(goodSku.getGoodsId().intValue());
        cart.setGoodsSn(good.getGoodsSn());
        cart.setGoodsName(good.getGoodsName());
        cart.setGoodsNum(goodsNum);
        cart.setSpecKey(goodSku.getKey());
        cart.setSpecKeyName(goodSku.getKeyName());
        cart.setItemId(goodSku.getItemId().intValue());
        cart.setAddTime(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        cart.setStoreId(good.getStoreId());
        return cart.save(false);
    }

    public boolean updateCartNum(Integer cartId, Integer type) {
        Cart cart = Cart.dao.findById(cartId);
        if (type == 1) {
            cart.setGoodsNum(cart.getGoodsNum() + 1);
        }
        if (type == 0) {
            cart.setGoodsNum(cart.getGoodsNum() - 1);
        }
        return cart.update(false);
    }

    /**
     * 根据SKU删除
     */
    public boolean delBySku(Integer skuId) {
        String sql = "DELETE FROM butler_cart WHERE item_id = ? AND user_id = ?";
        return Db.update(sql, skuId, AppIdKit.getUserId()) > 0;
    }
}