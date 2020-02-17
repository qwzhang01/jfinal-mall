package com.qw.service.bakend.member;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class RechargeService extends BaseService {
    private static RechargeService service;

    private RechargeService() {
    }

    public static synchronized RechargeService me() {
        if (service == null) {
            service = new RechargeService();
        }
        return service;
    }

    public Page<Record> pageList(String key, int pageNumber, int pageSize) {
        String select = "SELECT r.order_id chargeId, r.account amount, r.order_sn orderSn, r.pay_status payStatus, DATE_FORMAT(r.create_time, '%Y-%m-%d %H:%m') createTime, u.nickname";
        String from = " FROM butler_recharge r";
        from += " LEFT JOIN butler_user u ON u.user_id = r.user_id";
        from += " WHERE 1 = 1";
        // 充值状态0:待支付 1:充值成功 2:交易关闭
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from += " AND (u.nickname LIKE ? OR u.mobile LIKE ?)";
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from += " ORDER BY create_time DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        return page;
    }
}
