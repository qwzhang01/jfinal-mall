package com.qw.service.bakend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.helper.WxPayHelper;
import com.qw.model.Order;
import com.qw.model.PointSum;
import com.qw.model.PointWithdraw;
import com.qw.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分后台server
 *
 * @author qw
 */
public class PointService extends BaseService {

    private static PointService service;

    public static synchronized PointService me() {
        if (service == null) {
            service = new PointService();
        }
        return service;
    }

    /**
     * 有奖购买分页列表
     */
    public Page<Record> investPage(String key, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT ");
        select.append("iv.id,");
        select.append("iv.user_id,");
        select.append("iv.order_id,");
        select.append("iv.invest_amount,");
        select.append("iv.verification_amount,");
        select.append("iv.create_time,");
        select.append("u.nickname,");
        select.append("u.mobile,");
        select.append("o.order_amount,");
        select.append("o.order_sn,");
        select.append("f.amount,");
        select.append("f.remain_amount,");
        select.append("(SELECT GROUP_CONCAT(CONCAT('名称：', og.goods_name, '；数量：', og.goods_num) ORDER BY og.rec_id ASC separator '\\r\\n') FROM butler_order_goods og where o.order_id=og.order_id GROUP BY og.order_id) title");
        StringBuilder from = new StringBuilder(" FROM butler_point_freeze f");
        from.append(" LEFT JOIN butler_point_invest iv ON f.invest_id = iv.id");
        from.append(" LEFT JOIN butler_user u ON u.user_id = iv.user_id");
        from.append(" LEFT JOIN butler_order o ON o.order_id = iv.order_id ");
        from.append(" WHERE f.type = 1 ");

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from.append(" AND (u.mobile LIKE ? OR u.nickname LIKE ?)");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from.append(" ORDER BY iv.id DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }

    /**
     * 有效积分明细分页列表
     */
    public Page<Record> effectivePage(int pType, int pBusinessType, int isWithdraw, String key, int pageNumber, int pageSize) {

        StringBuilder select = new StringBuilder("SELECT ef.id, ef.user_id, ef.type, ef.is_withdraw, ef.business_type, ef.business_id, ef.amount, ef.create_time, u.nickname, u.mobile");
        StringBuilder from = new StringBuilder(" FROM butler_point_effective ef");
        from.append(" LEFT JOIN butler_user u ON u.user_id = ef.user_id");
        from.append(" WHERE 1 = 1 ");

        List<Object> paras = new ArrayList<>();
        if (pType != 0) {
            from.append(" AND ef.type = ? ");
            paras.add(pType);
        }
        if (pBusinessType != 0) {
            from.append(" AND ef.business_type = ? ");
            paras.add(pBusinessType);
        }
        if (isWithdraw != 0) {
            from.append(" AND ef.is_withdraw = ? ");
            paras.add(isWithdraw);
        }
        if (StrKit.notBlank(key)) {
            from.append(" AND (u.mobile LIKE ? OR u.nickname LIKE ?)");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from.append(" ORDER BY ef.id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            // 业务类型（1注册 2分享转发 3 取消订单 4提现 5消费 6下线首单奖励  8购买商品赠送 9提现审核拒绝退回 10线下录入）
            Integer businessType = r.getInt("business_type");
            Integer businessId = r.getInt("business_id");
            if (7 == businessType) {
                User user = User.dao.findById(businessId);
                r.set("businessName", "邀请用户：" + user.getNickname());
            }
            if (3 == businessType || 5 == businessType || 6 == businessType || 8 == businessType) {
                // 取消订单
                Order order = Order.dao.findById(businessId);
                if (order != null) {
                    r.set("businessName", "订单编号：" + order.getOrderSn());
                } else {
                    r.set("businessName", "未支付的秒杀订单");
                }
            }
            if (4 == businessType || 9 == businessType) {
                // 提现
                PointWithdraw withdraw = PointWithdraw.dao.findById(businessId);
                r.set("businessName", "提现编号：" + withdraw.getWithdrawSn());
            }
        }
        return page;
    }

    /**
     * 提现积分分页列表
     */
    public Page<Record> widthdrawlPage(int pageNumber, int pageSize, String withdrawSn, int withdrawStatus, String userMobile, String nickname, String startDate, String endDate) {
        StringBuilder select = new StringBuilder("SELECT u.mobile, u.nickname");
        select.append(",w.id, w.withdraw_sn, w.amount, w.actual_amount, w.create_time applyTime, w.`name` bankUserName, w.bankName bankName, w.bankCard  bankCard");
        select.append(",w.withdraw_status authStatus, w.pay_time payTime, w.auth_time authTime, w.remark refuseReason");
        select.append(",authUser.name authName");
        StringBuilder from = new StringBuilder(" FROM butler_point_withdraw w");
        from.append(" LEFT JOIN butler_user u ON w.user_id = u.user_id ");
        from.append(" LEFT JOIN oms_staff authUser ON w.auth_user_id = authUser.id ");
        from.append(" WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(withdrawSn)) {
            from.append(" AND w.withdraw_sn= ?");
            paras.add(withdrawSn);
        }
        if (withdrawStatus != 0) {
            from.append(" AND w.withdraw_status= ?");
            paras.add(withdrawStatus);
        }
        if (!StrKit.isBlank(userMobile)) {
            from.append(" AND u.mobile like ?");
            paras.add("%" + userMobile + "%");
        }
        if (!StrKit.isBlank(nickname)) {
            from.append(" AND u.nickname like ?");
            paras.add("%" + nickname + "%");
        }
        if (!StrKit.isBlank(startDate)) {
            from.append(" AND w.create_time >= ?");
            paras.add(startDate + " 00:00:00");

        }
        if (!StrKit.isBlank(endDate)) {
            from.append(" AND w.create_time < ?");
            paras.add(endDate + " 23:59:59");
        }
        from.append(" ORDER BY w.create_time DESC");
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
    }

    /**
     * 下线人工送积分
     */
    public boolean offLinePoint(User user, BigDecimal point, int isWithdrow) {
        // 计算签到领取积分的额度
        // 1可以2不可以 isWithdrow
        BigDecimal withdrawAmount = new BigDecimal("0");
        BigDecimal effectiveAmount = new BigDecimal("0");
        if (1 == isWithdrow) {
            withdrawAmount = point;
        } else {
            effectiveAmount = point;
        }
        boolean addEffective = com.qw.service.frontend.member.PointService.me().addEffective(user.getUserId(), withdrawAmount, effectiveAmount, 10, 0);
        return addEffective;
    }

    /**
     * 审核
     */
    public boolean auth(int id, int status, String reason) {
        return Db.tx(() -> {
            PointWithdraw pointWithdraw = PointWithdraw.dao.findById(id);
            pointWithdraw.setWithdrawStatus(status);
            pointWithdraw.setActualAmount(pointWithdraw.getAmount());
            if (StrKit.notBlank(reason)) {
                pointWithdraw.setRemark(reason);
            }
            pointWithdraw.setAuthTime(new Date());
            pointWithdraw.setAuthUserId(AppIdKit.getUserId());
            // 提现状态（1待审核 2待打款 3已完成 5审核拒绝
            if (status == 2) {
                return pointWithdraw.update();
            }
            //审核未通过，退回账户
            return pointWithdraw.update() && cancelWithdraw(pointWithdraw);
        });
    }

    /**
     * 打款
     */
    public boolean pay(PointWithdraw withdraw, User user) {
        BigDecimal actualAmount = withdraw.getActualAmount();
        Map<String, String> result = null;
        if (actualAmount.compareTo(new BigDecimal("200")) <= 0) {
            result = WxPayHelper.sendRedPack(withdraw, user.getOpenId());
            withdraw.setPayType(2);
        } else {
            result = WxPayHelper.sendBank(withdraw);
            withdraw.setPayType(4);
        }
        String result_code = result.get("result_code");
        if (!"SUCCESS".equals(result_code)) {
            log.error(JsonKit.toJson(result));
            return false;
        }
        // 微信红包单号
        String send_listid = result.get("send_listid");
        withdraw.setPayTime(new Date());
        withdraw.setPayUserId(AppIdKit.getUserId());
        withdraw.setPaySn(send_listid);
        withdraw.setWithdrawStatus(3);
        return withdraw.update();
    }

    /**
     * 手动打款
     */
    public boolean pay(PointWithdraw withdraw) {
        withdraw.setPayType(1);
        withdraw.setPayTime(new Date());
        withdraw.setPayUserId(AppIdKit.getUserId());
        withdraw.setWithdrawStatus(3);
        return withdraw.update();
    }

    /**
     * 取消提现申请逻辑
     */
    private boolean cancelWithdraw(PointWithdraw pointWithdraw) {
        // 重新计算汇总积分
        PointSum sum = com.qw.service.frontend.member.PointService.me().findByUserId(pointWithdraw.getUserId());
        sum.setWithdrawableRemain(sum.getWithdrawableRemain().add(pointWithdraw.getAmount()));
        sum.setUsableRemain(sum.getUsableRemain().add(pointWithdraw.getAmount()));
        sum.setWithdrawableTotal(sum.getWithdrawableTotal().subtract(pointWithdraw.getAmount()));
        // 添加有效积分明细
        return sum.update() && com.qw.service.frontend.member.PointService.me().addEffective(sum.getUserId(), 1, 9, 1, pointWithdraw.getId(), "", pointWithdraw.getAmount());
    }
}