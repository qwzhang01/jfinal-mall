package com.qw.service.bakend.prom;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import cn.qw.kit.HtmlKit;
import com.qw.conf.QuantityConf;
import com.qw.conf.module.FlashConf;
import com.qw.model.FlashInfo;
import com.qw.model.FlashSale;
import com.qw.model.Good;
import com.qw.model.GoodSku;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 秒杀活动/秒杀商品管理
 *
 * @author qw
 */
public class FlashSaleService extends BaseService {
    private static FlashSaleService service;
    private Cache cache;

    private FlashSaleService() {
        this.cache = Redis.use(QuantityConf.FLASH_SALE);
    }

    public static synchronized FlashSaleService me() {
        if (service == null) {
            service = new FlashSaleService();
        }
        return service;
    }

    /**
     * 秒杀活动列表
     */
    public Page<Record> pageList(int pageNumber, int pageSize, int type, String name) {
        String select = "SELECT id, name, DATE_FORMAT(beginTime, '%Y-%m-%d %H:%i') beginTime, DATE_FORMAT(endTime, '%Y-%m-%d %H:%i') endTime, buyLimit";
        String from = " FROM butler_flash_info WHERE";
        List<Object> paras = new ArrayList<>();
        // type 1即将开始 2秒杀中 3已经结束秒杀
        Date nowTime = new Date();
        if (1 == type) {
            from += " beginTime > ?";
            paras.add(nowTime);
        }
        if (2 == type) {
            from += " beginTime <= ? AND endTime >= ?";
            paras.add(nowTime);
            paras.add(nowTime);
        }
        if (3 == type) {
            from += " endTime < ?";
            paras.add(nowTime);
        }
        if (StrKit.notBlank(name)) {
            from += " AND name LIKE ?";
            paras.add("%" + name + "%");
        }
        if (1 == type) {
            from += " ORDER BY beginTime ASC";
        } else {
            from += " ORDER BY beginTime DESC";
        }
        return Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
    }

    /**
     * 秒杀商品列表
     */
    public Page<Record> goodList(int pageNumber, int pageSize, FlashInfo flash, String goodName) {
        String select = "SELECT\n" +
                "\tflash.id flashId,\n" +
                "\tgood.goods_name title,\n" +
                "\t sku.price goodPrice,\n" +
                "\tflash.goodId,\n" +
                "\tflash.imgPath, \n" +
                "\tflash.price,\n" +
                "\tflash.goodNum,\n" +
                "\tflash.buyNum,\n" +
                "\tflash.buyLimit,\n" +
                "\tflash.fakeBuyNum,\n" +
                "\tflash.skuId,\n" +
                "\tflash.offSaleStatus,\n" +
                "\tflash.orderNum,\n" +
                "\tflash.isFake,\n" +
                "\tflash.point,\n" +
                "\tflash.pointLimit,\n" +
                "\tsku.key_name,\n" +
                "\tflash.sorted";
        String from = " FROM butler_flash_sale flash\n"
                + " LEFT JOIN butler_good_sku sku ON flash.skuId = sku.item_id"
                + " LEFT JOIN butler_good good ON flash.goodId = good.goods_id"
                + " WHERE flash.flashId = ? AND sku.item_id > 0";

        List<Object> paras = new ArrayList<>();
        paras.add(flash.getId());
        if (StrKit.notBlank(goodName)) {
            from += " AND good.goods_name LIKE ?";
            paras.add("%" + goodName + "%");
        }
        from += " ORDER BY flash.sorted DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                r.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }
        return page;
    }

    /**
     * 复制一场活动的商品
     */
    public void addGood(FlashInfo info, int fromId) {
        String sql = "SELECT * FROM butler_flash_sale WHERE flashId = ?";
        List<FlashSale> saleList = FlashSale.dao.find(sql, fromId);
        saleList.stream().map(s -> {
            s.setFlashId(info.getId());
            s.remove("id");
            return s;
        }).forEach(g -> {
            g.save(false);
            g.setSorted(g.getId());
            g.update(false);
        });
    }

    /**
     * 为某个活动添加一个商品
     */
    public boolean addGood(FlashInfo flash, FlashSale sale) {
        sale.setFlashId(flash.getId());
        sale.setOffSaleStatus(1);
        return Db.tx(() -> {
            boolean saveOrUpdate = sale.saveOrUpdate(false);
            sale.setSorted(sale.getId());
            boolean update = sale.update(false);
            return saveOrUpdate && update;
        });
    }

    public boolean offSale(List<Integer> flashGoodIds) {
        List<String> collect = flashGoodIds.stream().map(f -> "?").collect(Collectors.toList());
        String join = StringUtils.join(collect, ",");
        String sql = "UPDATE butler_flash_sale SET offSaleStatus = 2 WHERE offSaleStatus = 1 AND id IN (" + join + ")";
        return Db.update(sql, flashGoodIds.toArray()) > 0;
    }

    public boolean sort(Integer flashGoodId1, Integer flashGoodId2) {
        FlashSale flashSale1 = FlashSale.dao.findById(flashGoodId1);
        FlashSale flashSale2 = FlashSale.dao.findById(flashGoodId2);
        int sort = flashSale1.getSorted();
        flashSale1.setSorted(flashSale2.getSorted());
        flashSale2.setSorted(sort);
        return Db.tx(() -> flashSale1.update(false) && flashSale2.update(false));
    }

    /**
     * 秒杀数据结构 一共四层数据结构
     * 1. 一个sorted set 存放时间
     * 2. 每个时间对应一个具体的详情
     * 3. 一个时间对应一个sorted set 存放秒杀商品简介
     * 4. 每个秒杀商品简介对应一个具体的想起
     */
    public void synchronization(FlashInfo flash) {
        // 同步秒杀时间
        synchronizationTime(flash);
        // 同步秒杀商品/同步商品详情
        synchronizationGood(flash);
    }

    private void synchronizationTime(FlashInfo flash) {
        // @respParam flashId|活动ID|int|必填
        // @respParam font|显示|String|必填
        // @respParam start_time|开始时间戳|long|必填
        // @respParam end_time|结束时间戳|long|必填
        // @respParam number|1进行中 2未开始|int|必填
        // 同步秒杀时间段
        Record r = new Record();
        Date beginTime = flash.getDate("beginTime");
        Date endTime = flash.getDate("endTime");
        r.set("font", DateKit.dateToString(beginTime, "MM-dd HH:mm"));
        r.set("flashId", flash.getId());
        r.set("start_time", beginTime.getTime() / 1000);
        r.set("end_time", endTime.getTime() / 1000);
        // 存储排序
        cache.zrem(FlashConf.FLASH_TIME_SORTED_KEY, flash.getId());
        cache.zadd(FlashConf.FLASH_TIME_SORTED_KEY, beginTime.getTime() / 1000, flash.getId());
        // 存储时间详情，设置过期时间，如果秒杀结束，删除对应的数据，缓存数据先清后存
        cache.del(FlashConf.FLASH_TIME_KEY_PREFIX + flash.getId());
        cache.setex(FlashConf.FLASH_TIME_KEY_PREFIX + flash.getId(), Integer.parseInt(String.valueOf((endTime.getTime() - System.currentTimeMillis()) / 1000)), r);
    }

    private void synchronizationGood(FlashInfo flash) {
        // 所有缓存都先清除后保存
        Set zrange = cache.zrange(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flash.getId(), 0, -1);
        // 删除保存秒杀商品ID的
        cache.del(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flash.getId());
        // 删除秒杀商品详情
        if(zrange != null && zrange.size() > 0) {
            zrange.stream().forEach(flashGoodId->{
                cache.del(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
            });
        }
        // 重新保存缓存
        List<Record> list = flashList(flash);
        list.stream().forEach(r -> {
            Integer sorted = r.getInt("sorted");
            Integer flashGoodId = r.getInt("flashGoodId");
            // 同步秒杀商品简单详情
            cache.zadd(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flash.getId(), sorted, flashGoodId);
            synchronizationGoodDetail(flash, flashGoodId);
        });
    }

    /**
     * 获取秒杀商品ID和排序
     */
    private List<Record> flashList(FlashInfo info) {
        String select = "SELECT f.id flashGoodId, f.sorted";
        String from = " FROM butler_flash_sale f";
        from += " LEFT JOIN butler_good_sku sku ON f.skuId = sku.item_id";
        from += " WHERE f.flashId = ? AND f.offSaleStatus = 1 AND sku.item_id > 0";
        return Db.find(select + from, info.getId());
    }

    /**
     * 同步商品详情
     */
    private void synchronizationGoodDetail(FlashInfo info, Integer flashGoodId) {
        // 拼接完整商品信息，与常规商品信息格式一致
        // 秒杀商品信息
        FlashSale flashGood = FlashSale.dao.findById(flashGoodId);
        flashGood.setImgPath(PropKit.get("fileHost") + flashGood.getImgPath());

        // 商品SKU
        GoodSku sku = GoodSku.dao.findById(flashGood.getSkuId());

        // 具体的商品信息
        Good good = Good.dao.findById(flashGood.getGoodId());
        String originalImg = good.getOriginalImg();
        if (StrKit.notBlank(originalImg)) {
            good.setOriginalImg(PropKit.get("fileHost") + originalImg.trim());
        }
        String video = good.getVideo();
        if (StrKit.notBlank(video)) {
            good.setVideo(PropKit.get("fileHost") + video);
        }
        // 秒杀活动信息
        Record activity = new Record();
        activity.set("flashGoodId", flashGood.getId());
        activity.set("flashPrice", flashGood.getPrice());
        activity.set("activityBuyLimit", info.getBuyLimit());
        activity.set("startTime", info.getBeginTime());
        activity.set("endTime", info.getEndTime());
        activity.set("specId", flashGood.getSkuId());
        activity.set("point", flashGood.getPoint());
        activity.set("pointLimit", flashGood.getPointLimit());

        Record result = new Record();
        result.set("activity", activity);
        result.set("good", good);
        result.set("flashGood", flashGood);
        List<String> img = HtmlKit.getImg(good.getGoodsContent());
        if (img == null || img.size() <= 0) {
            String goodsContent = good.getGoodsContent().replaceAll("\\[", "");
            goodsContent = goodsContent.replaceAll("]", "");
            img = Arrays.asList(goodsContent.split(","));
        }
        result.set("goodDetailImg", img.stream().map(m -> PropKit.get("fileHost") + m.trim()).toArray());
        // 规格信息
        result.set("goods_spec_list", new ArrayList<>());
        result.set("spec_goods_price", new GoodSku[]{sku});
        cache.setex(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGood.getId(), Integer.parseInt(String.valueOf((info.getEndTime().getTime() - System.currentTimeMillis()) / 1000)), result);
    }
}