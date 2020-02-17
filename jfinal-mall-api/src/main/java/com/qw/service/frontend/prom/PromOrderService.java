package com.qw.service.frontend.prom;

import cn.qw.base.BaseService;
import com.qw.model.PromGoods;
import com.qw.model.PromOrder;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;

public class PromOrderService extends BaseService {
    private static PromOrderService service;

    private PromOrderService() {
    }

    public static synchronized PromOrderService me() {
        if (service == null) {
            service = new PromOrderService();
        }
        return service;
    }

    public PromOrder getPropOrder(Integer type) {
        StringBuilder sql = new StringBuilder("SELECT goods_id goodsId, money");
        sql.append(" FROM butler_prom_order");
        sql.append(" WHERE type = ? AND `status` = 1");
        sql.append(" AND start_time <= ? AND end_time >= ?");

        Date nowDate = new Date();
        PromOrder promOrder = PromOrder.dao.findFirst(sql.toString(), type, nowDate, nowDate);
        return promOrder;
    }

    public boolean has5000Prom() {
        Date now = new Date();
        Long count = Db.queryLong("SELECT count(*) as count FROM butler_prom_order WHERE type = 4 AND `status` = 1 AND start_time <= ? AND end_time >= ?", now, now);
        return count > 0;
    }

    public Record cashProm() {
        // TODO 这里的活动写死了哦
        PromGoods promGoods = PromGoods.dao.findById(1000);
        if (promGoods == null) {
            return null;
        }
        return promGoods.toRecord();
    }
}
