package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.ArrayKit;
import cn.qw.kit.HtmlKit;
import com.qw.model.*;
import com.qw.service.frontend.member.UserService;
import com.qw.service.frontend.order.CartService;
import com.qw.service.frontend.store.StoreService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoodService extends BaseService {

    private static GoodService service;

    private GoodService() {
    }

    public static synchronized GoodService me() {
        if (service == null) {
            service = new GoodService();
        }
        return service;
    }

    /**
     * 获取凑单列表数据
     */
    public Page<Record> combineOrderGood(Integer beginAmount, Integer endAmount, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("select * ");

        StringBuilder from = new StringBuilder(" from (SELECT good.goods_id goodId, good.goods_name goodName, good.store_count storeCount");
        select.append(", good.sku, good.goods_sn goodSn, good.is_earn_point isEarnPoint, good.point_as_money pointAsMoney");
        select.append(", ( SELECT min( price ) FROM butler_good_sku WHERE butler_good_sku.goods_id = good.goods_id ) shopPrice");
        select.append(" FROM butler_good good");
        from.append(" WHERE good.is_on_sale = ?) AS g WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        paras.add(1);
        if (beginAmount > 0) {
            from.append(" AND g.shopPrice >= ?");
            paras.add(beginAmount);
        }
        if (endAmount > 0) {
            from.append(" AND g.shopPrice < ?");
            paras.add(endAmount);
        }
        from.append(" ORDER BY g.shopPrice ASC");

        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());

        // 要找商品的规格信息，规格的逻辑梳理一下
        List<Record> list = page.getList();
        for (Record r : list) {
            Integer goodId = r.getInt("goodId");
            // 获取商品规格
            r.set("spec", getSpecByGoodId(goodId));
            // 获取规格组合的价格、库存
            r.set("specConstitute", getSpecConstituteByGoodId(goodId));
            // 拼接图片地址
            String thumbPath = String.format("/public/upload/goods/thumb/%s/goods_thumb_%s_400_400.jpeg", String.valueOf(goodId), String.valueOf(goodId));
            r.set("thumbPath", PropKit.get("fileHost") + thumbPath);
        }
        return page;
    }

    /**
     * 猜你喜欢-用户的喜好和方便读的猜你喜欢
     * 1. 根据经纬度, 返回距离由近到远的商品
     * 2. 根据用户的浏览记录
     * 3. 根据客户的购买记录
     */
    public Page<Record> favouriteByUser(String longitude, String latitude, int pageNumber, int pageSize) {
        // 经纬度计算距离，对不对呢？TODO
        double lng = 0;
        double lat = 0;
        if (StrKit.notBlank(longitude, latitude)) {
            lng = Double.valueOf(longitude);
            lat = Double.valueOf(latitude);
        }

        List<Object> paras = new ArrayList<>();

        String select = "SELECT g.is_earn_point isEarnPoint, g.point_as_money pointAsMoney, g.goods_id, g.goods_name, g.is_virtual, g.cat_id3, g.original_img AS image, s.store_id";
        // 根据经纬度计算距离
        if (lng > 0 && lat > 0) {
            select += " ,ROUND("
                    + "        SQRT("
                    + "                POW(((" + lng + " - s.longitude) * 111),2)"
                    + "              + POW(((" + lat + " - s.latitude) * 111),2)"
                    + "        )"
                    + "       ,2"
                    + " ) AS distance";
        }
        String from = " FROM butler_good g"
                + " INNER JOIN butler_store s ON g.store_id = s.store_id"
                + " WHERE  s.store_state = 1 AND g.is_recommend = 1 AND g.goods_state = 1 AND g.is_on_sale = 1"
                + " ORDER BY g.sort DESC, g.goods_id DESC";
        // 原来可以人工设置排序,先取消
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record record : list) {
            record.set("shop_price", getSmallPrice(record.getLong("goods_id")));
            String image = record.getStr("image");
            if (StrKit.notBlank(image)) {
                record.set("image", PropKit.get("fileHost") + image);
            }
        }
        return page;
    }

    /**
     * 猜你喜欢-商品详情
     */
    public Page<Record> favouriteByGood(Good good, int pageNumber, int pageSize) {
        boolean isSameStore = isSameStore(good);

        String select = "SELECT g.goods_id goodId, g.goods_name goodName, g.original_img AS imagePath, g.is_earn_point isEarnPoint, g.point_as_money pointAsMoney";
        String from = " FROM butler_good g";
        from += " WHERE g.goods_state = 1 AND g.is_on_sale = 1";
        from += " AND g.cat_id1 = ? AND g.cat_id2 = ? AND g.cat_id3 = ?";

        List<Object> paras = new ArrayList<>();
        paras.add(good.getCatId1());
        paras.add(good.getCatId2());
        paras.add(good.getCatId3());
        if (isSameStore) {
            from += " AND g.store_id = ?";
            paras.add(good.getStoreId());
        }
        from += " ORDER BY g.goods_id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record record : list) {
            record.set("shopPrice", getSmallPrice(record.getLong("goodId")));
            String image = record.getStr("imagePath");
            if (StrKit.notBlank(image)) {
                record.set("imagePath", PropKit.get("fileHost") + image);
            }
        }
        return page;
    }

    // 商品搜索： 根据分类搜索、根据名字搜索
    // 排序： 综合，好评，价格，销量
    // 不同情况下，存在价格区间，品牌，规格
    public Page<Record> searchPage(int pageNumber, int pageSize,//
                                   int categoryId, String goodName,//
                                   int isEarnPoint, boolean pointAsMoney,//
                                   BigDecimal beginMoney, BigDecimal endMoney, String brand, String specification,//
                                   String sortColumn, String sortType) {//

        StringBuilder select = new StringBuilder("SELECT gspu.is_earn_point isEarnPoint, gspu.point_as_money pointAsMoney, gspu.goods_id goodId, gspu.goods_sn goodSpu");
        select.append(", gspu.goods_name goodName, gspu.sales_sum saleNum,gspu.original_img image");
        select.append(", (SELECT COUNT(*) FROM butler_comment WHERE butler_comment.goods_id = gspu.goods_id) AS goodCommentNum");
        select.append(", (SELECT SUM(goods_rank) / COUNT(*) / 5 FROM butler_comment WHERE butler_comment.goods_id = gspu.goods_id) AS goodCommentRate");
        select.append(",(SELECT price FROM  butler_good_sku where butler_good_sku.goods_id = gspu.goods_id ORDER BY price asc LIMIT 1) as shopPrice");

        StringBuilder from = new StringBuilder(" FROM butler_good gspu");
        from.append(" WHERE gspu.is_on_sale = 1 AND gspu.goods_state = 1");

        List<Object> paras = new ArrayList<>();
        if (isEarnPoint == 1) {
            from.append(" AND gspu.is_earn_point = 1");
        }
        if (pointAsMoney) {
            from.append(" AND gspu.point_as_money > 0");
        }
        if (categoryId > 0) {
            from.append(" AND (gspu.cat_id3 = ? OR gspu.cat_id2 = ? OR gspu.cat_id1 = ?)");
            paras.add(categoryId);
            paras.add(categoryId);
            paras.add(categoryId);
        }
        if (StrKit.notBlank(goodName)) {
            from.append("  AND (gspu.goods_name LIKE ? OR gspu.keywords LIKE ?)");
            paras.add("%" + goodName + "%");
            paras.add("%" + goodName + "%");
        }
        if (beginMoney != null) {
            from.append(" AND gspu.shop_price >= ?");
            paras.add(beginMoney);
        }
        if (endMoney != null) {
            from.append(" AND gspu.shop_price <= ?");
            paras.add(endMoney);
        }
        if (StrKit.notBlank(brand)) {
            from.append(" AND gspu.brand_id IN (" + brand + ")");
        }
        if (StrKit.notBlank(specification)) {
            String[] split = specification.split(",");
            if (split.length > 0) {
                from.append(" AND EXISTS (SELECT * FROM butler_good_sku WHERE gspu.goods_id = butler_good_sku.goods_id AND butler_good_sku.`key` LIKE ?");
                paras.add(split[0]);
            }
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    from.append(" AND butler_good_sku.`key` LIKE ?");
                    paras.add(split[i]);
                }
            }
            from.append(")");
        }

        from.append(" ORDER BY " + sortColumn + " " + sortType);

        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record v : list) {
            goodCommentRate(v);
            v.set("image", PropKit.get("fileHost") + v.getStr("image"));
        }
        return page;
    }

    /**
     * 获取店铺商品列表
     */
    public Page<Record> storeGoodPageList(Store store, int type, int categoryId, String sortColumn, String sortType, String goodName, BigDecimal startMoney, BigDecimal endMoney, String brand, String specification, int pageNumber, int pageSize) {
        // type 1 推荐 2 新品 3 促销 4 分类
        // sortColumn normal sale price
        // sortType ASC  DESC
        if (3 == type) {
            return storeGoodPageList(store, pageNumber, pageSize);
        }
        String select = "SELECT g.goods_id, g.goods_name, g.cat_id3, g.original_img AS image, g.is_earn_point isEarnPoint, g.point_as_money pointAsMoney";
        select += ",(SELECT price FROM  butler_good_sku where butler_good_sku.goods_id = g.goods_id ORDER BY price asc LIMIT 1) as shop_price";

        String from = " FROM butler_good g";
        from += " WHERE g.goods_state = 1 AND g.is_on_sale = 1 AND g.store_id = ?";

        List<Object> paras = new ArrayList<>();
        paras.add(store.getStoreId());
        if (type == 1) {
            from += " AND g.is_recommend = 1";
        }
        if (type == 2) {
            from += " AND g.is_new = 1";
        }
        if (type == 4) {
            from += " AND g.cat_id1 = ?";
            paras.add(categoryId);
        }
        if (StrKit.notBlank(goodName)) {
            from += " AND g.goods_name LIKE ?";
            paras.add("%" + goodName + "%");
        }
        if (startMoney != null) {
            from += (" AND g.shop_price >= ?");
            paras.add(startMoney);
        }
        if (endMoney != null) {
            from += (" AND g.shop_price <= ?");
            paras.add(endMoney);
        }
        if (StrKit.notBlank(brand)) {
            from += (" AND g.brand_id IN (" + brand + ")");
        }
        if (StrKit.notBlank(specification)) {
            String[] split = specification.split(",");
            if (split.length > 0) {
                from += (" AND EXISTS (SELECT * FROM butler_good_sku WHERE g.goods_id = butler_good_sku.goods_id AND butler_good_sku.`key` LIKE ?");
                paras.add(split[0]);
            }
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    from += (" AND butler_good_sku.`key` LIKE ?");
                    paras.add(split[i]);
                }
            }
            from += (")");
        }
        if ("normal".equals(sortColumn)) {
            from += " ORDER BY g.goods_id DESC";
        }
        if ("sale".equals(sortColumn)) {
            from += " ORDER BY g.sales_sum " + sortType;
        }
        if ("price".equals(sortColumn)) {
            from += " ORDER BY shop_price " + sortType;
        }
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record record : list) {
            String image = record.getStr("image");
            if (StrKit.notBlank(image)) {
                record.set("image", PropKit.get("fileHost") + image);
            }
        }
        return page;
    }

    /**
     * 获取店铺1元抢购抽奖活动商品
     */
    private Page<Record> storeGoodPageList(Store store, int pageNumber, int pageSize) {
        String select = "SELECT g.goods_id, g.goods_name, g.cat_id3, g.original_img AS image, lg.id lotteryId, lg.price lotteryPrice";
        String from = " FROM butler_lottery_good lg" +
                " LEFT JOIN butler_good g ON lg.good_id = g.goods_id" +
                " LEFT JOIN butler_lottery l ON l.id = lg.lottery_id" +
                " WHERE lg.`status` = 2 AND g.store_id = ?";
        from += " ORDER BY lg.id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, store.getStoreId());
        List<Record> list = page.getList();
        for (Record record : list) {
            record.set("shop_price", getSmallPrice(record.getLong("goods_id")));
            String image = record.getStr("image");
            if (StrKit.notBlank(image)) {
                record.set("image", PropKit.get("fileHost") + image);
            }
        }
        return page;
    }

    public List<Record> getSpecConstituteByGoodId(Integer goodId) {
        String sql = "SELECT item_id specConstituteId, `key` AS specItemIdConstitute, price specConstitutePrice, supply_price specConstituteSupplyPrice, store_count specConstituteStoreCount, spec_img specImg"
                + " FROM butler_good_sku WHERE goods_id = ? ORDER BY price ASC";
        List<Record> list = Db.find(sql, goodId);
        if (list == null || list.size() <= 0) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<Record> getSpecByGoodId(Integer goodId) {
        // 获取商品所有的规格ID  butler_spec_item  id，用_链接
        String keySql = "SELECT GROUP_CONCAT(`key` ORDER BY store_count desc SEPARATOR '_') FROM butler_good_sku WHERE goods_id = ?";
        String keys = Db.queryStr(keySql, goodId);
        if (StrKit.isBlank(keys)) {
            return new ArrayList<>();
        }
        keys = keys.replaceAll("_", ",");
        if (keys.startsWith(",")) {
            keys = keys.substring(1);
        }
        if (keys.endsWith(",")) {
            keys = keys.substring(0, keys.length() - 1);
        }
        // 查询商品的所有规格信息
        String filterSpecSql = "SELECT spec.id AS specId, spec.name specName,item.id itemId, item.item itemName"
                + " FROM butler_spec_item AS item"
                + " LEFT JOIN butler_spec AS spec ON spec.id = item.spec_id"
                + " WHERE item.id IN(" + keys + ")"
                + " ORDER BY spec.id ASC";
        List<Record> filterSpecList = Db.find(filterSpecSql);
        Map<String, List<Record>> specName = filterSpecList.stream().collect(Collectors.groupingBy(k -> k.getStr("specName")));

        return specName.keySet().stream().map(k -> {
            Record result = new Record();
            result.set("specName", k);
            result.set("specList", specName.get(k));
            return result;
        }).collect(Collectors.toList());
    }

    private boolean isSameStore(Good good) {
        String sql = "SELECT COUNT(*) FROM butler_good g WHERE g.goods_state = 1 AND g.is_on_sale = 1 AND g.store_id = ? AND g.cat_id1 = ? AND g.cat_id2 = ? AND g.cat_id3 = ?";
        return Db.queryLong(sql, good.getStoreId(), good.getCatId1(), good.getCatId2(), good.getCatId3()) > 10;
    }

    /**
     * 获取收藏商品列表
     */
    public Page<Record> collectPage(int pageNumber, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT gc.collect_id collectId, gc.goods_id goodId");
        sql.append(",g.goods_name name, ( SELECT min( price ) FROM butler_good_sku WHERE butler_good_sku.goods_id = g.goods_id ) shopPrice, g.original_img image");
        sql.append(", g.is_earn_point isEarnPoint, g.point_as_money pointAsMoney");
        sql.append(",(SELECT COUNT(*) FROM butler_good_collect WHERE butler_good_collect.goods_id = gc.goods_id) AS collectNum");
        StringBuilder from = new StringBuilder(" FROM butler_good_collect gc");
        from.append(" LEFT JOIN  butler_good g ON g.goods_id = gc.goods_id");
        from.append(" WHERE gc.user_id = ?");
        from.append(" ORDER BY gc.collect_id DESC");

        Page<Record> page = Db.paginate(pageNumber, pageSize, sql.toString(), from.toString(), AppIdKit.getUserId());
        List<Record> list = page.getList();
        for (Record o : list) {
            String image = o.getStr("image");
            if (StrKit.notBlank(image)) {
                o.set("image", PropKit.get("fileHost") + image);
            }
        }
        return page;
    }

    public List<Record> topSeller(int count) {
        String orderSql = " SELECT COUNT(*) orderCount, od.goods_id goodId"
                + " FROM butler_order_goods od"
                + " LEFT JOIN butler_order o ON od.order_id = o.order_id"
                + " LEFT JOIN butler_good g ON g.goods_id = od.goods_id"
                + " WHERE o.pay_status = 1 AND g.is_on_sale = 1"
                + " GROUP BY od.goods_id ORDER BY orderCount DESC LIMIT " + count;

        List<Record> orderCount = Db.find(orderSql);
        for (Record oc : orderCount) {
            Record good = getSimpleDetail(oc.getInt("goodId"));
            oc.setColumns(good);
            oc.set("promotDesc", "好评率100%");
        }
        return orderCount;
    }

    /**
     * 人气榜，收藏+浏览最多的商品排行
     *
     * @param count
     * @return
     */
    public List<Record> topHot(int count) {
        String orderSql = "SELECT goods_id goodsId, collect_sum collectCount, click_count clickCount, is_earn_point isEarnPoint, point_as_money pointAsMoney, collect_sum + click_count hot"
                + " FROM butler_good WHERE is_on_sale = 1 ORDER BY hot DESC LIMIT " + count;
        return Db.find(orderSql);
    }

    private Record getSimpleDetail(Integer goodId) {
        String sql = "SELECT goods_name name, original_img image FROM butler_good WHERE goods_id = ? LIMIT 1";
        Record detail = Db.findFirst(sql, goodId);
        String image = detail.getStr("image");
        if (StrKit.notBlank(image)) {
            detail.set("image", PropKit.get("fileHost") + image);
        }
        return detail;
    }

    public List<Record> topHot() {
        List<Record> result = new ArrayList<>();
        // 单品人气榜
        Record singleHot = topHot(1).get(0);
        Record simpleDetail = getSimpleDetail(singleHot.getInt("goodsId"));
        simpleDetail.set("rank_title", "人气榜");
        simpleDetail.set("promot_desc", "收藏点赞" + singleHot.get("hot") + "次");
        simpleDetail.set("jump_type", "4");
        simpleDetail.set("related_id", "1");
        simpleDetail.set("rank_desc", simpleDetail.getStr("name").replaceAll("白金管家", ""));
        result.add(simpleDetail);

        result.addAll(topSellerCategory(3));

        return result;
    }

    private List<Record> topSellerCategory(int count) {

        String orderSql =
                " SELECT COUNT(*) orderCount, CONCAT(left(c.mobile_name, 2), '榜') rank_title, c.image, 4 as jump_type, g.cat_id1 as related_id, '' AS rank_desc"
                        + " FROM butler_order_goods od"
                        + " LEFT JOIN butler_order o ON od.order_id = o.order_id"
                        + " LEFT JOIN butler_good g ON g.goods_id = od.goods_id"
                        + " LEFT JOIN butler_good_category c ON c.id = g.cat_id1"
                        + " WHERE o.pay_status = 1 AND c.is_show = 1 GROUP BY g.cat_id1 ORDER BY orderCount DESC LIMIT " + count;

        List<Record> orderCount = Db.find(orderSql);
        for (Record oc : orderCount) {
            oc.set("promot_desc", "月销" + oc.get("orderCount") + "件");
            String image = oc.getStr("image");
            if (StrKit.notBlank(image)) {
                oc.set("image", PropKit.get("fileHost") + image);
            }
        }

        return orderCount;
    }

    public Long collectCount(Integer userId) {
        return Db.queryLong("SELECT COUNT(*) FROM butler_good_collect WHERE user_id = ?", userId);
    }

    public Long visitCount(Integer userId) {
        return Db.queryLong("SELECT COUNT(*) FROM butler_good_visit WHERE user_id = ?", userId);
    }

    public Long returnCount(Integer userId) {
        return Db.queryLong("SELECT COUNT(*) FROM butler_return_goods WHERE user_id = ?", userId);
    }

    public List<Record> findByStore(Integer storeId, int count) {
        String sql = "SELECT goods_id goodId, ( SELECT min( price ) FROM butler_good_sku WHERE butler_good_sku.goods_id = butler_good.goods_id ) shopPrice"
                + ",original_img originalImg, is_earn_point isEarnPoint, point_as_money pointAsMoney"
                + " FROM butler_good"
                + " WHERE is_on_sale = 1 AND store_id = ? ORDER BY is_recommend DESC, sales_sum DESC LIMIT ?";
        List<Record> list = Db.find(sql, storeId, count);
        for (Record r : list) {
            String img = r.getStr("originalImg");
            if (StrKit.notBlank(img)) {
                r.set("originalImg", PropKit.get("fileHost") + img);
            }
        }
        return list;
    }

    /**
     * 新增访问记录
     */
    public boolean visit(Integer userId, Long goodId, int shareUserId) {
        GoodVisit visit = new GoodVisit();
        visit.setUserId(userId);
        visit.setGoodsId(goodId.intValue());
        visit.setShareUserId(shareUserId);
        return visit.save();
    }

    /**
     * 商品从购物车移到收藏夹
     */
    public boolean cartToCollect(Integer cartId) {
        Cart cartInfo = Cart.dao.findById(cartId);
        Good good = Good.dao.findById(cartInfo.getGoodsId());
        if (cartInfo != null && good != null) {
            GoodCollect goodCollect = new GoodCollect();
            goodCollect.setUserId(cartInfo.getUserId());
            goodCollect.setGoodsId(cartInfo.getGoodsId());
            goodCollect.setCatId3(good.getCatId3());
            goodCollect.setAddTime(System.currentTimeMillis() / 1000);
            return goodCollect.save(false);
        }
        return false;
    }

    public List<Record> recommend(Good good) {
        String sql = "SELECT is_earn_point isEarnPoint, point_as_money pointAsMoney, goods_id, goods_name, (SELECT min( price ) FROM butler_good_sku WHERE butler_good_sku.goods_id = butler_good.goods_id ) shop_price,is_virtual, original_img imgPath FROM butler_good WHERE goods_state = 1 and is_on_sale=1 and cat_id3 = ? LIMIT 3";
        List<Record> list = Db.find(sql, good.getCatId3());
        for (Record r : list) {
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                r.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }
        return list;
    }

    private List<String> gallery(long goodId) {
        String sql = "SELECT image_url FROM butler_good_images WHERE goods_id = ?";
        List<String> list = Db.query(sql, goodId);
        return list.stream().map(l -> PropKit.get("fileHost") + l.trim()).collect(Collectors.toList());
    }

    /**
     * 判断商品是否被用户收藏
     */
    public boolean isCollect(long goodId) {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            return false;
        }
        GoodCollect goodCollect = GoodCollect.dao.searchFirst(searchParam(searchParam("user_id", userId), "goods_id", goodId));
        if (goodCollect == null) {
            return false;
        }
        return true;
    }

    public boolean collect(Good good) {
        GoodCollect goodCollect = new GoodCollect();
        goodCollect.setUserId(AppIdKit.getUserId());
        goodCollect.setGoodsId(good.getGoodsId().intValue());
        goodCollect.setAddTime(System.currentTimeMillis() / 1000);

        good.setCollectSum(good.getCollectSum() + 1);

        return Db.tx(() -> good.update(false) && goodCollect.saveOrUpdate(false));
    }

    public boolean cancelCollect(Good good) {
        good.setCollectSum(good.getCollectSum() - 1);
        GoodCollect goodCollect = GoodCollect.dao.searchFirst(searchParam(searchParam("user_id", AppIdKit.getUserId()), "goods_id", good.getGoodsId()));

        return Db.tx(() -> good.update(false) && goodCollect.delete());
    }

    /**
     * 获取商品中最便宜的SKU
     *
     * @return
     */
    public BigDecimal getSmallPrice(Long goodId) {
        String sql = "SELECT price FROM butler_good_sku WHERE goods_id = ? ORDER BY price ASC LIMIT 1";
        return Db.queryBigDecimal(sql, goodId);
    }

    public GoodSku getGoodBySmallPrice(Long goodId) {
        String sql = "SELECT * FROM butler_good_sku WHERE goods_id = ? ORDER BY price ASC LIMIT 1";
        return GoodSku.dao.findFirst(sql, goodId);
    }

    public List<GoodSku> getSku(Long goodId) {
        String sql = "SELECT * FROM butler_good_sku WHERE goods_id = ? ORDER BY price ASC";
        List<GoodSku> goodSkus = GoodSku.dao.find(sql, goodId);
        return goodSkus;
    }

    /**
     * 获取首页专题下面的商品信息
     */
    public List<Record> findHome(List<HomeSet> homeSets) {
        Integer[] goodIds = homeSets.stream().map(h -> h.getGotoId()).toArray(Integer[]::new);
        String sql = "SELECT is_earn_point isEarnPoint, point_as_money pointAsMoney, goods_id goodId, goods_name title"
                + ",(SELECT min( price ) FROM butler_good_sku WHERE butler_good_sku.goods_id = butler_good.goods_id ) price"
                + ",CONCAT('" + PropKit.get("fileHost") + "', original_img) goodImg"
                + " FROM butler_good WHERE is_on_sale = 1 AND goods_id IN (" + ArrayKit.join(goodIds) + ")";
        return Db.find(sql);
    }

    private Record goodCommentRate(Record v) {
        Object goodCommentRate = v.get("goodCommentRate");
        if (goodCommentRate == null) {
            v.set("goodCommentRate", 0);
            return v;
        }
        v.set("goodCommentRate", new BigDecimal(String.valueOf(goodCommentRate)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
        return v;
    }

    /**
     * 获取商品主详情
     * 1. 基本信息
     * 2. 规格信息
     */
    public Record detail(long goodId) {
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            return null;
        }
        if (good.getIsOnSale() != 1) {
            return null;
        }
        // 整理商品基本信息
        Record result = detail(good);
        // 整理商品规格信息
        result.set("goods_spec_list", getSpecByGoodId(good.getGoodsId().intValue()));
        result.set("spec_goods_price", getSku(good.getGoodsId()));
        // 获取商品管理信息
        return detail(good, result);
    }

    /**
     * 整理商品基本信息
     */
    public Record detail(Good good) {
        Record result = new Record();
        String originalImg = good.getOriginalImg();
        if (StrKit.notBlank(good.getOriginalImg())) {
            good.setOriginalImg(PropKit.get("fileHost") + originalImg.trim());
        }
        String video = good.getVideo();
        if (StrKit.notBlank(video)) {
            good.setVideo(PropKit.get("fileHost") + video);
        }
        result.set("good", good);

        List<String> img = HtmlKit.getImg(good.getGoodsContent());
        if (img == null || img.size() <= 0) {
            // 不在富文本里面取，本身存的就是数组
            String goodsContent = good.getGoodsContent().replaceAll("\\[", "");
            goodsContent = goodsContent.replaceAll("]", "");
            img = Arrays.asList(goodsContent.split(","));
        }
        result.set("goodDetailImg", img.stream().map(m -> PropKit.get("fileHost") + m.trim()).toArray());

        return result;
    }

    /**
     * 商品详情附带信息
     */
    public Record detail(Good good, Record result) {
        // 11. 收藏状态
        result.set("is_collect", isCollect(good.getGoodsId()));
        // 1. 获取默认收货人地址
        result.set("consignee", UserService.me().defaultConsignee(AppIdKit.getUserId()));
        // 4. 获取推荐商品
        result.set("recommend_goods", recommend(good));
        // 5. 获取画廊
        result.set("gallery", gallery(good.getGoodsId()));
        // 6. 获取最近的两条评论
        result.set("comment", CommentService.me().findLatest(good));
        // 7. 获取某个商品的评论统计
        result.set("statistics", CommentService.me().statistic(good));
        // 8. 店铺信息
        result.set("store", StoreService.me().simpleDetail(good.getStoreId()));
        // 9. 获取当前用户购物商品收藏数量
        if (AppIdKit.getUserId() != 0) {
            result.set("cartNum", CartService.me().goodCount());
        } else {
            result.set("cartNum", 0);
        }
        return result;
    }
}