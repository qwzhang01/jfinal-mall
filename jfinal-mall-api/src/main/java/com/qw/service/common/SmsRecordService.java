package com.qw.service.common;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class SmsRecordService extends BaseService {
    private static SmsRecordService service;

    private SmsRecordService() {
    }

    public static synchronized SmsRecordService me() {
        if (service == null) {
            service = new SmsRecordService();
        }
        return service;
    }

    public Page<Record> getSmsPage(String phoneNumber, Integer status, int pageNumber, int pageSize) {
        String select = "SELECT * ";
        StringBuilder sqlExceptSelect = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sqlExceptSelect.append(" FROM oms_sms_log sms WHERE 1 = 1");
        if (StrKit.notBlank(phoneNumber)) {
            sqlExceptSelect.append(" AND sms.mobile like ? ");
            params.add("%" + phoneNumber + "%");
        }
        if (status != null) {
            sqlExceptSelect.append(" AND sms.`status` = ? ");
            params.add(status);
        }
        sqlExceptSelect.append(" ORDER BY sms.id DESC ");
        return Db.paginate(pageNumber, pageSize, select, sqlExceptSelect.toString(), params.toArray());
    }
}