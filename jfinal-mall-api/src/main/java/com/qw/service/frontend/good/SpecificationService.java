package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecificationService extends BaseService {

    private static SpecificationService service;

    private SpecificationService() {
    }

    public static synchronized SpecificationService me() {
        if (service == null) {
            service = new SpecificationService();
        }
        return service;
    }

    private String findSku(String goodName, int storeId, int categoryId) {
        String sql = "SELECT replace(GROUP_CONCAT(s.`key`), '_', ',') FROM butler_good_sku s"
                + " LEFT JOIN butler_good g ON g.goods_id = s.goods_id"
                + " WHERE g.is_on_sale = 1 AND g.goods_state = 1 AND s.`key` <> ''";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(goodName)) {
            sql += " AND (g.goods_name LIKE ? OR g.keywords LIKE ?)";
            paras.add("%" + goodName + "%");
            paras.add("%" + goodName + "%");
        }
        if (storeId != 0) {
            sql += " AND g.store_id = ?";
            paras.add(storeId);
        }
        if (categoryId != 0) {
            sql += " AND (g.cat_id3 = ? OR g.cat_id2 = ? OR g.cat_id1 = ?)";
            paras.add(categoryId);
            paras.add(categoryId);
            paras.add(categoryId);
        }
        return Db.queryStr(sql, paras.toArray());
    }

    public List<Record> searchForm(String goodName, int storeId, int categoryId) {
        String skuId = findSku(goodName, storeId, categoryId);
        if (StrKit.isBlank(skuId)) {
            return new ArrayList<>();
        }
        skuId += "0";
        String sql = "SELECT v.id, v.item content, v.spec_id specificationId, s.name FROM butler_spec_item v LEFT JOIN butler_spec s ON s.id = v.spec_id WHERE v.id IN (" + skuId + ")";
        List<Record> list = Db.find(sql);
        Map<Integer, List<Record>> map = list.stream().collect(Collectors.groupingBy(s -> s.getInt("specificationId")));
        return map.keySet().stream().map(m -> {
            Record r = new Record();
            List<Record> specList = map.get(m);
            r.set("select", new ArrayList<>());
            r.set("name", specList.get(0).getStr("name"));
            r.set("value", specList.stream().map(v -> {
                v.remove("specificationId");
                v.remove("name");
                return v;
            }).collect(Collectors.toList()));
            return r;
        }).collect(Collectors.toList());
    }
}