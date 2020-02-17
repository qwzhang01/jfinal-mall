package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrandService extends BaseService {
    private static BrandService service;

    private BrandService() {
    }

    public static synchronized BrandService me() {
        if (service == null) {
            service = new BrandService();
        }
        return service;
    }

    public List<Record> findByGood(String goodName) {
        String sql = "SELECT b.id, b.`name`"
                + " FROM butler_brand b WHERE EXISTS(SELECT * FROM butler_good WHERE butler_good.brand_id = b.id AND butler_good.is_on_sale = 1"
                + " AND butler_good.goods_state = 1 AND (butler_good.goods_name LIKE ? OR butler_good.keywords LIKE ?))";
        return Db.find(sql, "%" + goodName + "%", "%" + goodName + "%").stream().map(r -> {
            r.set("select", new ArrayList<>());
            return r;
        }).collect(Collectors.toList());
    }

    public List<Record> findByCategoryId(int categoryId) {
        String sql = "SELECT b.id, b.`name`"
                + " FROM butler_brand b WHERE b.cat_id3 = ?";
        return Db.find(sql, categoryId).stream().map(r -> {
            r.set("select", new ArrayList<>());
            return r;
        }).collect(Collectors.toList());
    }

    public List<Record> searchForm(String goodName, int storeId, int categoryId) {
        if (storeId != 0) {
            // 店铺有没有自己的品牌ID
            return new ArrayList<>();
        }
        if (categoryId != 0) {
            return findByCategoryId(categoryId);
        }
        if (StrKit.notBlank(goodName)) {
            return findByGood(goodName);
        }
        return new ArrayList<>();
    }

    public Page<Record> pageList(int pageNumber, int pageSize, Integer status, String key) {
        String select = "SELECT * ";
        String from = " FROM butler_brand WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notNull(status)) {
            from += " AND status = ?";
            paras.add(status);
        }
        if (StrKit.notBlank(key)) {
            from += " AND name LIKE ?";
            paras.add("%" + key + "%");
        }
        from += " ORDER BY id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        return page;
    }
}