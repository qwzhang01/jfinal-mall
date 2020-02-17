package com.qw.service.bakend.good;

import cn.qw.base.BaseService;
import cn.qw.kit.ArrayKit;
import cn.qw.kit.DateKit;
import cn.qw.kit.HtmlKit;
import com.qw.model.Good;
import com.qw.model.GoodImages;
import com.qw.model.GoodSku;
import com.qw.vo.good.EsGood;
import com.qw.vo.good.GoodFormVo;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

    public Page<Record> pageList(int pageNumber, int pageSize, String goodName, String storeName, String brandName, String catName, Integer saleStatus) {
        String select = "SELECT g.goods_id goodId, g.sort, g.goods_name goodName, g.goods_sn goodSn, gc3.`name_path` namePath, g.is_new, g.is_hot"
                + ",g.video, g.goods_state goodState,g.is_on_sale saleStatus, g.original_img imgPath, s.store_name storeName"
                + ",g.is_recommend isRecommend, g.point_as_money, g.is_earn_point"
                + ",g.store_promote_rate storePromoteRate, brand.`name` brandName, FROM_UNIXTIME(g.on_time, '%Y-%m-%d %H:%i') onTime"
                + ",(SELECT COUNT(*) FROM butler_good_sku WHERE butler_good_sku.goods_id = g.goods_id) as skuCount";
        String from = " FROM butler_good g";
        from += " LEFT JOIN butler_store s ON s.store_id = g.store_id\n";
        from += " LEFT JOIN butler_brand brand ON brand.id = g.brand_id\n";
        from += " LEFT JOIN butler_good_category gc3 ON gc3.id = g.good_category_id";
        from += " WHERE 1 = 1";

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(goodName)) {
            from += " AND g.goods_name LIKE ?";
            paras.add("%" + goodName + "%");
        }
        if (StrKit.notBlank(storeName)) {
            from += " AND s.store_name LIKE ?";
            paras.add("%" + storeName + "%");
        }
        if (StrKit.notBlank(brandName)) {
            from += " AND brand.`name` LIKE ?";
            paras.add("%" + brandName + "%");
        }
        if (StrKit.notBlank(catName)) {
            from += " AND ( gc3.name_path LIKE ? )";
            paras.add("%" + catName + "%");
        }
        if (saleStatus != null) {
            from += " AND g.is_on_sale = ?";
            paras.add(saleStatus);
        }
        from += " ORDER BY g.sort DESC, g.goods_id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            List<GoodSku> spec = spec(r.getLong("goodId"));
            if (spec != null && spec.size() > 0) {
                GoodSku goodSku = spec.get(0);
                r.set("shopPrice", goodSku.getPrice());
                r.set("supplyPrice", goodSku.getSupplyPrice());
                r.set("marketPrice", goodSku.getMarketPrice());
            }
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                r.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }
        return page;
    }

    public List<Record> search(String goodName) {
        List<Record> list = new ArrayList<>();
        for (int i = 1; list.size() < 10; i++) {
            list.addAll(search(goodName, i, 10 - list.size()));
            if (i == 5) {
                break;
            }
        }
        if (list.size() == 0) {
            list.addAll(search(goodName, 10));
        }
        return list;
    }

    private List<Record> search(String goodName, int num) {
        String sql = "SELECT good.goods_id goodId, good.goods_name name FROM butler_good good"
                + " WHERE good.is_on_sale = 1 AND good.goods_name LIKE ? ORDER BY good.goods_name ASC LIMIT " + num;
        List<Record> list = Db.find(sql, goodName + "%");
        return list;
    }

    private List<Record> search(String goodName, int startPos, int num) {
        String sql = "SELECT * FROM("
                + "SELECT goods_id goodId, goods_name name, is_on_sale,"
                + "substring(trim(goods_name), "
                + startPos
                + ") AS subName FROM butler_good"
                + ") as good"
                + " WHERE good.is_on_sale = 1 AND good.subName LIKE ? ORDER BY good.subName ASC LIMIT " + (10 - num) + "," + num;
        List<Record> list = Db.find(sql, goodName + "%");
        return list;
    }

    public List<Record> searchCategory(String categoryName) {
        List<Record> list = new ArrayList<>();
        for (int i = 1; list.size() < 10; i++) {
            list.addAll(searchCategory(categoryName, i, 10 - list.size()));
            if (i == 5) {
                break;
            }
        }
        if (list.size() < 10) {
            list.addAll(searchCategory(categoryName, 10 - list.size()));
        }
        return list;
    }

    private List<Record> searchCategory(String categoryName, int num) {
        String sql = "SELECT good.id `key`, good.mobile_name `value` FROM butler_good_category good"
                + " WHERE  good.mobile_name LIKE ? ORDER BY good.mobile_name ASC LIMIT " + num;
        List<Record> list = Db.find(sql, categoryName + "%");
        return list;
    }

    private List<Record> searchCategory(String categoryName, int startPos, int num) {
        String sql = "SELECT * FROM("
                + "SELECT id `key`, mobile_name `value`,"
                + "substring(trim(mobile_name), "
                + startPos
                + ") AS subName FROM butler_good_category"
                + ") as good"
                + " WHERE  good.subName LIKE ? ORDER BY good.subName ASC LIMIT " + num;
        List<Record> list = Db.find(sql, categoryName + "%");
        return list;
    }

    /**
     * 获取商品画廊
     */
    public String[] gallery(Good good) {
        String sql = "SELECT image_url FROM butler_good_images WHERE goods_id = ?";
        List<String> list = Db.query(sql, good.getGoodsId());
        return list.toArray(new String[list.size()]);
    }

    /**
     * 根据价格升序 获取所有SKU
     *
     * @param goodId
     * @return
     */
    public List<GoodSku> spec(long goodId) {
        String sql = "SELECT * FROM butler_good_sku WHERE goods_id = ? ORDER BY price ASC";
        return GoodSku.dao.find(sql, goodId);
    }

    public boolean saveOrUpdate(GoodFormVo goodForm) {
        // 编辑的时候的额外逻辑
        if (goodForm.getGoodsId() != null && goodForm.getGoodsId() != 0) {
            // 删除原来的画廊
            delAblum(goodForm.getGoodsId());
            // 删除原来的SKU
            delSku(goodForm.getGoodsId());
        }
        // 保存商品信息
        Good good = new Good();
        good.setGoodsId(goodForm.getGoodsId());
        Integer[] catId = goodForm.getCatId();
        good.setCatId1(goodForm.getCatId()[0]);
        good.setGoodCategoryId(goodForm.getCatId()[0]);
        if (catId.length > 1) {
            good.setCatId2(goodForm.getCatId()[1]);
            good.setGoodCategoryId(goodForm.getCatId()[1]);
        } else {
            good.setCatId2(0);
        }
        if (catId.length > 2) {
            good.setCatId3(goodForm.getCatId()[2]);
            good.setGoodCategoryId(goodForm.getCatId()[2]);
        } else {
            good.setCatId3(0);
        }
        good.setGoodsSn(snGood());
        good.setGoodsName(goodForm.getGoodsName());
        good.setStoreId(goodForm.getStoreId());
        good.setBrandId(goodForm.getBrandId());
        good.setKeywords(goodForm.getKeywords());
        good.setGoodsRemark(goodForm.getGoodsRemark());
        good.setGoodsContent(Arrays.toString(goodForm.getDetailSaveImgList()));
        good.setOriginalImg(goodForm.getOriginalImg());
        good.setIsOnSale(goodForm.getIsOnSale());
        good.setIsFreeShipping(goodForm.getIsFreeShipping());
        good.setIsRecommend(goodForm.getIsRecommend());
        good.setIsHot(goodForm.getIsHot());
        good.setIsEarnPoint(goodForm.getIsEarnPoint());
        good.setPointAsMoney(goodForm.getPointAsMoney());
        good.setGiveIntegral(goodForm.getGiveIntegral());
        good.setSort(goodForm.getSort());
        good.saveOrUpdate(false);

        Long goodsId = good.getGoodsId();
        // 保存画廊
        List<GoodImages> goodImg = Arrays.stream(goodForm.getImgAlbum()).map(url -> {
            GoodImages img = new GoodImages();
            img.setImageUrl(url);
            img.setGoodsId(goodsId.intValue());
            return img;
        }).collect(Collectors.toList());
        Db.batchSave(goodImg, goodImg.size());
        // 保存SKU
        List<GoodSku> skuList = goodForm.getSkuData().stream().map(f -> {
            GoodSku sku = new GoodSku();
            sku.setGoodsId(goodsId.intValue());
            sku.setSku(snSku());
            sku.setSpecImg(f.getSaveImgPath());
            sku.setKey(f.getSkuComputerCode());
            sku.setKeyName(findSpecName(f.getSkuComputerCode()));
            sku.setPrice(f.getShopPrice());
            sku.setSupplyPrice(f.getSupplyPrice());
            sku.setMarketPrice(f.getMarketPrice());
            sku.setStoreCount(f.getStoreCount().intValue());
            sku.setDisAbled(f.getDisAbled());
            return sku;
        }).collect(Collectors.toList());
        Db.batchSave(skuList, skuList.size());
        return true;
    }

    private String findSpecName(String keys) {
        if (StrKit.isBlank(keys)) {
            return "";
        }
        keys = keys.replaceAll("_", ",");
        if (keys.startsWith(",")) {
            keys = keys.substring(1);
        }
        if (keys.endsWith(",")) {
            keys = keys.substring(0, keys.length() - 1);
        }
        String sql = "SELECT CONCAT(s.`name`, '：', i.item) FROM butler_spec_item i LEFT JOIN butler_spec s ON s.id = i.spec_id WHERE i.id IN (" + keys + ")";
        List<String> specName = Db.query(sql);
        if (specName == null || specName.size() <= 0) {
            return "";
        }
        return ArrayKit.join(specName.toArray(), "；");
    }

    private boolean delAblum(Long goodId) {
        String sql = "DELETE FROM butler_good_images WHERE goods_id = ?";
        int update = Db.update(sql, goodId);
        return update > 0;
    }

    private boolean delSku(Long goodsId) {
        String sql = "DELETE FROM butler_good_sku WHERE goods_id = ?";
        int update = Db.update(sql, goodsId);
        return update > 0;
    }

    private String snGood() {
        String sn = "SPU";
        sn = sn + DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
        String randomNumeric = RandomStringUtils.randomNumeric(5);
        return sn + randomNumeric;
    }

    private String snSku() {
        String sn = "SKU";
        sn = sn + DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
        String randomNumeric = RandomStringUtils.randomNumeric(5);
        return sn + randomNumeric;
    }

    public boolean delete(Long goodId) {
        delAblum(goodId);
        delSku(goodId);
        return Good.dao.deleteById(goodId);
    }

    /**
     * 获取商品的详情图片
     *
     * @param goodsContent
     * @return
     */
    public String[] getContent(String goodsContent) {
        List<String> img = HtmlKit.getImg(goodsContent);
        if (img != null && img.size() > 0) {
            return img.toArray(new String[img.size()]);
        }
        goodsContent = goodsContent.replaceAll("\\[", "");
        goodsContent = goodsContent.replaceAll("]", "");
        return Arrays.stream(goodsContent.split(",")).map(s -> s.trim()).toArray(String[]::new);
    }

    public void putOffSale(List<Integer> goodIdList) {
        String goodIdStr = ArrayKit.join(goodIdList.toArray(), ",");
        String sql = "UPDATE butler_good SET is_on_sale = 0 WHERE goods_id in (" + goodIdStr + ")";
        Db.update(sql);
    }

    public List<EsGood> findEsAll() {
        return null;
    }
}