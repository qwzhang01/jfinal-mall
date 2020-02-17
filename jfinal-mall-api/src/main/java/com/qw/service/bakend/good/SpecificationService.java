package com.qw.service.bakend.good;

import cn.qw.base.BaseService;
import com.qw.model.Spec;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

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

    public Page<Record> pageList(int pageNumber, int pageSize, String name, int categoryId) {
        String select = "SELECT s.id, s.name, s.categoryId, c.name_path categoryName";
        String from = " FROM butler_spec s";
        from += " LEFT JOIN butler_good_category c ON c.id = s.categoryId";
        from += " WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (categoryId != 0) {
            from += " AND s.categoryId = ?";
            paras.add(categoryId);
        }
        if (StrKit.notBlank(name)) {
            from += " AND s.name LIKE ?";
            paras.add("%" + name + "%");
        }
        from += " ORDER BY s.id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record spec : list) {
            findSpecItem(spec);
        }
        return page;
    }

    public List<Record> select(int categoryId) {
        String sql = "SELECT s.id, s.name FROM butler_spec s  WHERE s.categoryId = ? ORDER BY s.id DESC";
        List<Record> list = Db.find(sql, categoryId);
        for (Record spec : list) {
            findSpecItem(spec);
        }
        return list;
    }

    private Record findSpecItem(Record spec) {
        Integer id = spec.getInt("id");
        String sql = "SELECT item.id, item.item, item.spec_id specId, spec.name specName"
                + " FROM butler_spec_item item "
                + " LEFT JOIN butler_spec spec ON  spec.id = item.spec_id"
                + " WHERE item.spec_id = ?";
        List<Record> list = Db.find(sql, id);
        spec.set("item", list);
        return spec;
    }

    /**
     * 删除规格： 删除规格项、删除规格本身，其实也应该删除对应的商品SKU
     */
    public boolean delete(int specId) {
        // TODO 引用的商品处理
        String sql = "DELETE FROM butler_spec_item WHERE spec_id = ?";
        Db.update(sql, specId);
        return Spec.dao.deleteById(specId);
    }
}
