package com.qw.service.bakend.good;

import cn.qw.base.BaseService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

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

    public Page<Record> pageList(int pageNumber, int pageSize, Integer status, String key) {
        String select = "SELECT b.*, c.name_path categoryName";
        String from = " FROM butler_brand b LEFT JOIN butler_good_category c ON b.category_id = c.id WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (status != null) {
            from += " AND b.status = ?";
            paras.add("status");
        }
        if (StrKit.notBlank(key)) {
            from += " AND b.name LIKE ?";
            paras.add("%" + key + "%");
        }
        from += " ORDER BY b.id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            String logo = r.getStr("logo");
            if (StrKit.notBlank(logo)) {
                r.set("logo", PropKit.get("fileHost") + logo);
            }
        }
        return page;
    }

    public List<Record> select(int categoryId) {
        String sql = "SELECT b.id, b.name FROM butler_brand b  WHERE 1 = 1 ORDER BY b.id DESC";
        // TODO 根据分类查找 以后要加上
        return Db.find(sql);
    }
}