package com.qw.service.frontend.order;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.qw.model.DoorServer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;
import java.util.stream.Collectors;

public class DoorServerService extends BaseService {
    private static DoorServerService service;

    private DoorServerService() {
    }

    public static synchronized DoorServerService me() {
        if (service == null) {
            service = new DoorServerService();
        }
        return service;
    }

    public Record getDetail(Integer orderId, Integer goodId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT");
        sql.append(" ds.i_id id,");
        sql.append(" DATE_FORMAT(ds.i_start_time, '%Y-%m-%d %H:%m') appointStartime,");
        sql.append(" DATE_FORMAT(ds.i_end_time,'%Y-%m-%d %H:%m') appointEndTime,");
        sql.append(" ds.i_name workerName,");
        sql.append(" ds.i_phone workerPhone,");
        sql.append(" ds.i_active serverStatus,");
        sql.append(" DATE_FORMAT(ds.appoint_time, '%Y-%m-%d %H:%m') appointTime,");
        sql.append(" DATE_FORMAT(ds.done_time, '%Y-%m-%d %H:%m') doneTime");
        sql.append(" FROM");
        sql.append(" butler_door_server ds");
        sql.append(" WHERE");
        sql.append(" ds.order_id = ?");
        sql.append(" AND ds.goods_id = ?");
        sql.append(" LIMIT 1");
        return Db.findFirst(sql.toString(), orderId, goodId);
    }

    public boolean cancel(Integer id, Integer orderId, Integer goodId) {
        boolean tx = Db.tx(() -> {
            Db.update("UPDATE butler_door_server SET i_active = 3 WHERE i_id = ?", id);
            Db.update("UPDATE butler_order_goods SET door_service_status = 1 WHERE order_id = ? AND goods_id = ? AND door_service_status = 3", orderId, goodId);
            return true;
        });
        return tx;
    }

    public DoorServer getByOrderId(Integer orderId, Integer goodId) {
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("goods_id", goodId);
        return DoorServer.dao.searchFirst(map);
    }

    public List<Map<String, Object>> getAppointDay() {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        List<Map<String, Object>> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i < 8; i++) {
            calendar.add(Calendar.DATE, i);
            int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            Map<String, Object> r = new HashMap<>();
            Date date = calendar.getTime();
            r.put("showContent", DateKit.dateToString(date, "MM-dd") + " " + weeks[weekIndex]);
            result.add(r);
        }
        return result;
    }

    public List<Map<String, Object>> getAppointTime(String date) {
        Integer month = Integer.valueOf(date.substring(0, 2));
        Integer day = Integer.valueOf(date.substring(3, 5));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);

        return Arrays.asList(new String[]{"08:00-10:00", "10:00-12:00", "14:00-16:00", "16:00-18:00"}).stream().map(m -> {
            Map<String, Object> time = new HashMap<>();
            time.put("showContent", m);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(m.substring(0, 2)));
            Date beginTime = calendar.getTime();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(m.substring(6, 8)));
            Date endTime = calendar.getTime();
            time.put("appointTimeBegin", DateKit.dateToString(beginTime, "yyyy-MM-dd HH:mm"));
            time.put("appointTimeEnd", DateKit.dateToString(endTime, "yyyy-MM-dd HH:mm"));
            return time;
        }).collect(Collectors.toList());
    }
}