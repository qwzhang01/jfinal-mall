package com.qw.service.bakend.member;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class BalanceService extends BaseService {
    private static BalanceService service;

    private BalanceService() {
    }

    public static synchronized BalanceService me() {
        if (service == null) {
            service = new BalanceService();
        }
        return service;
    }

    public Page<Record> pageList(String key, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT u.nickname, u.mobile, bd.*");
        StringBuilder from = new StringBuilder(" FROM butler_user_balance_detail bd LEFT JOIN butler_user u ON u .user_id = bd.user_id WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from.append(" AND (u.nickname LIKE ? OR u.mobile LIKE ?)");
            paras.add("" + key + "");
            paras.add("" + key + "");
        }
        from.append(" ORDER BY bd.id DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }
}