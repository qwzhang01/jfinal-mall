package com.qw.service.bakend.order;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class DoorServiceService extends BaseService {
    private static DoorServiceService service;

    private DoorServiceService() {
    }

    public static synchronized DoorServiceService me() {
        if (service == null) {
            service = new DoorServiceService();
        }
        return service;
    }

    public Page<Record> pageList(int pageNumber, int pageSize, String i_name, String master_order_sn, String order_sn) {
        String select = "SELECT a.*,c.order_sn,c.master_order_sn,b.goods_name, d.realname, d.nickname, d.mobile as userMobile";

        String from = " FROM butler_door_server a";
        from += " LEFT JOIN butler_order_goods b ON a.order_id = b.order_id\n";
        from += " LEFT JOIN butler_order c ON a.order_id = c.order_id\n";
        from += " LEFT JOIN butler_user d ON c.user_id = d.user_id\n";
        from += " WHERE 1 = 1";

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(i_name)) {
            from += " AND a.i_name LIKE ?";
            paras.add("%" + i_name + "%");
        }
        if (StrKit.notBlank(master_order_sn)) {
            from += " AND c.master_order_sn LIKE ?";
            paras.add("%" + master_order_sn + "%");
        }
        if (StrKit.notBlank(order_sn)) {
            from += " AND c.order_sn LIKE ?";
            paras.add("%" + order_sn + "%");
        }
        from += " ORDER BY a.time DESC";
        return Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
    }
}
