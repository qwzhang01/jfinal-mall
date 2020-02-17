package com.qw.service.frontend.prom;

import cn.qw.base.BaseService;
import com.qw.model.CouponList;
import com.qw.model.Order;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;
import java.util.Map;

/**
 * 优惠券
 */
public class CouponService extends BaseService {
    private static CouponService service;

    private CouponService() {
    }

    public static synchronized CouponService me() {
        if (service == null) {
            service = new CouponService();
        }
        return service;
    }

    /**
     * 获取用户可用优惠券数量
     */
    public Long availableCount(Integer userId) {
        String sql = "SELECT COUNT(*)"
                + " FROM butler_coupon_list l"
                + " LEFT JOIN butler_coupon c ON l.cid = c.id"
                + " LEFT JOIN butler_store s ON s.store_id = c.store_id"
                + " WHERE c.use_end_time > NOW() AND c.status = 1 AND l.deleted = 0 AND l.uid = ?";
        return Db.queryLong(sql, userId);
    }

    /**
     * 取消订单，退还优惠券
     */
    public void cancelOrder(Order order) {
        Map<String, Object> param = searchParam("order_id", order.getOrderId());
        param = searchParam(param, "uid", order.getUserId());
        List<CouponList> list = CouponList.dao.search(param);
        if (list != null && list.size() > 0) {
            list.stream().forEach(c -> {
                c.setUseTime(0);
                c.setStatus(0);
                c.setOrderId(0);
                c.saveOrUpdate(false);
            });
        }
    }
}