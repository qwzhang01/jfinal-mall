package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.qw.conf.ButlerEmnu;
import com.qw.model.Recharge;
import com.qw.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private String genSn() {
        String sn;
        String prefix = "RCA";
        while (true) {
            String timeStamp = DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
            int random = RandomUtils.nextInt(1000, 9999);
            sn = timeStamp + random;
            Long count = Db.queryLong("SELECT COUNT(*) FROM butler_recharge WHERE order_sn = ?", prefix + sn);
            if (count <= 0) {
                break;
            }
        }
        return prefix + sn;
    }

    public Recharge charge(User user, BigDecimal amount) {
        Recharge charge = new Recharge();
        charge.setOrderSn(genSn());
        charge.setUserId(new Long(user.getUserId()));
        charge.setNickname(user.getNickname());
        charge.setAccount(amount);
        charge.setPayStatus(ButlerEmnu.ChargePayStatus.WaitPay.getValue());
        if (charge.saveOrUpdate(false)) {
            return charge;
        }
        return null;
    }

    public Recharge charge(User user, BigDecimal amount, String orderSn) {
        Recharge recharge = null;
        if (StrKit.notBlank(orderSn)) {
            Map<String, Object> search = new HashMap<>();
            search.put("order_sn", orderSn);
            recharge = Recharge.dao.searchFirst(search);
        }
        if (recharge == null) {
            recharge = new Recharge();
            recharge.setUserId(new Long(user.getUserId()));
            recharge.setNickname(user.getNickname());
            recharge.setOrderSn(genSn());
            recharge.setPayName("微信APP支付");
            recharge.setCtime(new Long(System.currentTimeMillis() / 1000).intValue());
        }
        recharge.setAccount(amount);
        recharge.saveOrUpdate(false);
        return recharge;
    }

    public Page<Record> pageList(User user, int pageNumber, int pageSize) {
        String select = "SELECT account amount, pay_status payStatus, DATE_FORMAT(create_time, '%Y-%m-%d %H:%m') createTime";
        String from = " FROM butler_recharge WHERE user_id = ?";
        from += " ORDER BY create_time DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, user.getUserId());
        return page;
    }
}
