package com.qw.service.bakend.store;

import cn.qw.base.BaseService;
import com.qw.model.Store;
import com.qw.service.common.RegionService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class StoreService extends BaseService {
    private static StoreService service;

    private StoreService() {
    }

    public static synchronized StoreService me() {
        if (service == null) {
            service = new StoreService();
        }
        return service;
    }

    public Long focusCount(Integer userId) {
        return Db.queryLong("SELECT COUNT(*) FROM butler_store_collect WHERE user_id = ?", userId);
    }

    public Page<Record> pageList(String key, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT " +
                "s.store_id storeId" +
                ",s.store_name storeName" +
                ",s.store_address storeAddress" +
                ",s.user_name userName" +
                ",gc.`name` goodCatName,slc.`level`" +
                ",sls.level_name levelName" +
                // ",slc.commission_rate commissionRate," +
                ",s.butler_rate commissionRate," +
                "(SELECT SUM(ifnull(o.total_amount,0)) FROM butler_order o WHERE o.store_id=s.store_id AND o.order_status=4) saleAmount,\n" +
                "(SELECT SUM(ifnull(o.total_amount,0)*ifnull(odr.butler_commission_rate,0)) FROM butler_order o LEFT JOIN butler_order_div_rate odr ON o.order_id=odr.order_id WHERE o.store_id=s.store_id AND o.order_status=4) commissionAmount\n");

        StringBuilder from = new StringBuilder("FROM butler_store s\n" +
                "LEFT JOIN butler_good_category gc ON s.main_category_id=gc.id\n" +
                "LEFT JOIN butler_store_level sl ON s.store_id=sl.store_id\n" +
                "LEFT JOIN butler_store_level_category slc ON sl.id=slc.id\n" +
                "LEFT JOIN butler_store_level_set sls on slc.`level`=sls.`level` ");
        List<Object> paras = new ArrayList<Object>();
        if (!StrKit.isBlank(key)) {
            from.append(" WHERE s.store_name LIKE ? OR s.user_name LIKE ?");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from.append(" ORDER BY s.store_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        return page;
    }

    /**
     * 获取店铺审核分页列表
     */
    public Page<Record> pageAuth(Integer storeStatus, String storeName, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT s.store_id storeId, s.store_name storeName");
        select.append(",s.store_state storeState, FROM_UNIXTIME(s.store_time, '%Y-%m-%d %H:%i') storeTime, u.nickname, s.store_logo storeLogo, s.store_banner");
        select.append(",s.store_recommend recommendStatus");

        StringBuilder from = new StringBuilder(" FROM butler_store s");
        from.append(" LEFT JOIN butler_user u ON u.user_id = s.user_id");
        from.append(" WHERE 1=1");

        List<Object> paras = new ArrayList<>();
        if (storeStatus != null) {
            from.append(" AND s.store_state = ?");
            paras.add(storeStatus);
        }
        if (!StrKit.isBlank(storeName)) {
            from.append(" AND s.store_name LIKE ?");
            paras.add("%" + storeName + "%");
        }
        from.append(" ORDER BY s.store_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            r.set("storeLogo", PropKit.get("fileHost") + r.getStr("storeLogo"));
            r.set("store_banner", PropKit.get("fileHost") + r.getStr("store_banner"));
        }
        return page;
    }

    public String getStoreAddress(Integer storeId) {
        Store store = Store.dao.findById(storeId);
        Integer district = store.getDistrict();
        Integer city = store.getCityId();
        Integer province = store.getProvinceId();

        int regonId = 0;
        if (province != null && province != 0) {
            regonId = province;
        }
        if (city != null && city != 0) {
            regonId = city;
        }
        if (district != null && district != 0) {
            regonId = district;
        }

        String regionName = RegionService.me().getName(regonId);
        String address = store.getStoreAddress();
        if (StrKit.isBlank(address)) {
            address = "";
        }
        return regionName + address;
    }

    public List<Record> search(String title) {
        List<Record> list = new ArrayList<>();
        for (int i = 1; list.size() < 10; i++) {
            list.addAll(search(title, i, 10 - list.size()));
            if (i == 5) {
                break;
            }
        }
        if (list.size() < 10) {
            list.addAll(search(title, 10 - list.size()));
        }
        return list;
    }

    private List<Record> search(String title, int num) {
        String sql = "SELECT article.store_id `key`, article.store_name `value` FROM butler_store article"
                + " WHERE article.store_name LIKE ? ORDER BY article.store_name ASC LIMIT " + num;
        List<Record> list = Db.find(sql, title + "%");
        return list;
    }

    private List<Record> search(String goodName, int startPos, int num) {
        String sql = "SELECT * FROM("
                + "SELECT store_id `key`, store_name `value`,"
                + "substring(trim(store_name), "
                + startPos
                + ") AS subName FROM butler_store"
                + ") as good"
                + " WHERE good.subName LIKE ? ORDER BY good.subName ASC LIMIT " + num;
        List<Record> list = Db.find(sql, goodName + "%");
        return list;
    }

    public List<Record> select() {
        String sql = "SELECT store_id id, store_name name FROM butler_store ORDER BY store_id DESC";
        return Db.find(sql);
    }
}