package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import com.qw.conf.ButlerEmnu;
import com.qw.model.*;
import com.qw.service.frontend.order.OrderService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.*;

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

    public Page<Record> pageList(User user, int amountType, Date start, Date end, int pageNumber, int pageSize) {
        String select = "SELECT amount, amount_type amountType, in_out_flag inOutFlag, frozen_flag frozenFlag, DATE_FORMAT(create_time, '%Y-%m-%d %H:%m') createTime, object_id";
        String from = " FROM butler_user_balance_detail WHERE user_id = ?";

        List<Object> paras = new ArrayList<>();
        paras.add(user.getUserId());
        if (amountType != 0) {
            from += " AND amount_type = ?";
            paras.add(amountType);
        }
        from += " AND create_time >= ? AND create_time <= ?";
        paras.add(start);
        paras.add(end);

        from += " ORDER BY create_time DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
//            Integer amountType = r.getInt("amountType");
//            Integer objectId = r.getInt("object_id");
            r.remove("object_id");
        }
        return page;
    }

    public BigDecimal total(User user, int amountType, Date start, Date end, int inOutFlag) {
        String select = "SELECT SUM(amount)";
        String from = " FROM butler_user_balance_detail WHERE user_id = ? AND in_out_flag = ?";

        List<Object> paras = new ArrayList<>();
        paras.add(user.getUserId());
        paras.add(inOutFlag);

        if (amountType != 0) {
            from += " AND amount_type = ?";
            paras.add(amountType);
        }
        from += " AND create_time >= ? AND create_time <= ?";
        paras.add(start);
        paras.add(end);

        return Db.queryBigDecimal(select + from, paras.toArray());
    }

    public Record detail(Integer userId) {
        UserBalance balance = getByUserId(userId);
        Record record = new Record();
        if (balance == null) {
            record.set("availableAmount", 0);
            record.set("frozenAmount", 0);
        } else {
            record.set("availableAmount", balance.getAvailableAmount());
            record.set("frozenAmount", balance.getFrozenAmount());
        }
        return record;
    }

    public BigDecimal achievementAmount(User user) {
        return achievementAmount(user.getUserId());
    }

    public BigDecimal achievementAmount(Integer userId) {
        UserBalance userBalance = getByUserId(userId);
        if (userBalance == null) {
            return new BigDecimal("0");
        }
        return userBalance.getAchievementAmount();
    }


    public boolean addBalanceDetail(Recharge charge) {
        UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
        userBalanceDetail.setUserId(charge.getUserId().intValue());
        userBalanceDetail.setAmount(charge.getAccount());
        userBalanceDetail.setAmountType(ButlerEmnu.BalanceAmountTypeEnum.Charge.getValue());
        userBalanceDetail.setObjectId(charge.getOrderId().intValue());
        userBalanceDetail.setInOutFlag(ButlerEmnu.BalanceInOutFlagEnum.In.getValue());
        userBalanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Normal.getValue());
        return userBalanceDetail.save(false);
    }

    public boolean subBalance(User user, BigDecimal amount) {
        return Db.update("UPDATE butler_user_balance SET available_amount = available_amount - ? WHERE user_id = ? AND available_amount >= ?"
                , amount, user.getUserId(), amount) > 0;
    }

    /**
     * 申请提现
     */
    public boolean applyWithdrawal(User user, BigDecimal amount) {
        // 可用金额减去提现的金额，提现中的金额加上提现金额
        String sql = "UPDATE butler_user_balance SET available_amount = (available_amount - " + amount + "), withdrawal_amount = (withdrawal_amount + " + amount + ") WHERE user_id = ? AND available_amount >= ?";
        return Db.update(sql, user.getUserId(), amount) > 0;
    }

    /**
     * 新增推广人首单立减
     */
    private boolean addBalanceDetai(Order order, Integer promoteUserId, BigDecimal reword) {
        UserBalanceDetail bulterPromoteAmountBalanceDetail = new UserBalanceDetail();
        bulterPromoteAmountBalanceDetail.setUserId(promoteUserId);
        bulterPromoteAmountBalanceDetail.setAmount(reword);
        bulterPromoteAmountBalanceDetail.setAmountType(ButlerEmnu.BalanceAmountTypeEnum.FirstProm.getValue());
        bulterPromoteAmountBalanceDetail.setObjectId(order.getOrderId());
        bulterPromoteAmountBalanceDetail.setInOutFlag(ButlerEmnu.BalanceInOutFlagEnum.In.getValue());
        bulterPromoteAmountBalanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Frozen.getValue());

        addFrozenBalance(promoteUserId, reword);

        return bulterPromoteAmountBalanceDetail.save(false);
    }

    /**
     * 新增店铺利润
     */
    private boolean addBalanceDetai(Order order, BigDecimal amount) {
        Store store = Store.dao.findById(order.getStoreId());

        addFrozenBalance(store.getUserId(), amount);

        UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
        userBalanceDetail.setUserId(store.getUserId());
        userBalanceDetail.setAmount(amount);
        userBalanceDetail.setAmountType(ButlerEmnu.BalanceAmountTypeEnum.SaleIncome.getValue());
        userBalanceDetail.setObjectId(order.getOrderId());
        userBalanceDetail.setInOutFlag(ButlerEmnu.BalanceInOutFlagEnum.In.getValue());
        userBalanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Frozen.getValue());

        return userBalanceDetail.save(false);
    }


    private boolean addFrozenBalance(Integer userId, BigDecimal frozenAmount) {
        UserBalance userBalance = getByUserId(userId);
        userBalance.setFrozenAmount(userBalance.getFrozenAmount().add(frozenAmount));
        return userBalance.saveOrUpdate(false);
    }

    public boolean cancelPay(Order order) {
        return Db.tx(() ->
                cancelStoreSaleFrozen(order)
                        && cancelButlerPromoteFrozen(order)
                        && cancelStorePromoteFrozen(order)
                        && cancelFirstPromoteReward(order)
        );
    }

    private boolean cancelStorePromoteFrozen(Order order) {
        UserBalanceDetail balanceDetail = getUserBalanceDetail(order.getOrderId(), ButlerEmnu.BalanceAmountTypeEnum.SaleIncome);
        if (balanceDetail == null) {
            return false;
        }

        subFrozenBalance(balanceDetail.getUserId(), balanceDetail.getAmount());

        balanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Cancel.getValue());
        return balanceDetail.update(false);
    }

    private boolean cancelButlerPromoteFrozen(Order order) {
        UserBalanceDetail balanceDetail = getUserBalanceDetail(order.getOrderId(), ButlerEmnu.BalanceAmountTypeEnum.ButerReward);
        if (balanceDetail == null) {
            return true;
        }

        subFrozenBalance(balanceDetail.getUserId(), balanceDetail.getAmount());

        balanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Cancel.getValue());
        return balanceDetail.update(false);
    }

    private boolean cancelFirstPromoteReward(Order order) {
        UserBalanceDetail balanceDetail = getUserBalanceDetail(order.getOrderId(), ButlerEmnu.BalanceAmountTypeEnum.FirstProm);
        if (balanceDetail == null) {
            return true;
        }

        subFrozenBalance(balanceDetail.getUserId(), balanceDetail.getAmount());

        balanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Cancel.getValue());
        return balanceDetail.update(false);
    }

    private boolean cancelStoreSaleFrozen(Order order) {
        UserBalanceDetail balanceDetail = getUserBalanceDetail(order.getOrderId(), ButlerEmnu.BalanceAmountTypeEnum.ShopReward);
        if (balanceDetail == null) {
            return true;
        }

        subFrozenBalance(balanceDetail.getUserId(), balanceDetail.getAmount());

        balanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Cancel.getValue());
        return balanceDetail.update(false);
    }

    private boolean subFrozenBalance(Integer userId, BigDecimal amount) {
        UserBalance userBalance = getByUserId(userId);
        userBalance.setFrozenAmount(userBalance.getFrozenAmount().subtract(amount));
        return userBalance.update(false);
    }

    private UserBalanceDetail getUserBalanceDetail(Integer orderId, ButlerEmnu.BalanceAmountTypeEnum balanceAmountType) {
        Map<String, Object> search = new HashMap<>();
        search.put("object_id", orderId);
        search.put("amount_type", balanceAmountType.getValue());
        search.put("in_out_flag", ButlerEmnu.BalanceInOutFlagEnum.In.getValue());
        search.put("frozen_flag", ButlerEmnu.BalanceFrozenFlagEnum.Frozen.getValue());
        return UserBalanceDetail.dao.searchFirst(search);
    }

    private UserBalance getByUserId(Integer userId) {
        Map<String, Object> search = new HashMap<>();
        search.put("user_id", userId);
        UserBalance userBalance = UserBalance.dao.searchFirst(search);
        if (userBalance == null) {
            userBalance = new UserBalance();
            userBalance.setAchievementAmount(new BigDecimal(0));
            userBalance.setUserId(userId);
            userBalance.setAvailableAmount(new BigDecimal(0));
            userBalance.setFrozenAmount(new BigDecimal(0));
            userBalance.setTotalAmount(new BigDecimal(0));
            userBalance.setWithdrawalAmount(new BigDecimal(0));
            userBalance.setPlatinumBit(new BigDecimal(0));
            userBalance.setAchievementAmount(new BigDecimal(0));
        }
        return userBalance;
    }

    /**
     * 根据订单获取所有的分润数据
     */
    private List<UserBalanceDetail> findByOrder(Order order) {
        // frozen_flag 订单支付但是没有结束的佣金1冻结 2正常，3取消
        // in_out_flag 1收入 2支出
        // amount_type 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8 推广首单奖励
        // object_id 关联对应业务的ID（1-orderId2-orderId3-orderId4-充值id 5 orderId 6提现id7-orderId8-orderId）
        String sql = "SELECT * FROM butler_user_balance_detail WHERE frozen_flag = 1 AND in_out_flag = 1 AND amount_type IN (1, 2, 3, 5, 8) AND object_id = ?";
        return UserBalanceDetail.dao.find(sql, order.getOrderId());
    }

    /**
     * 获取推广人员的未解冻，但可以解冻的收入明细
     *
     * @param time
     * @return
     */
    public List<UserBalanceDetail> userFindCalFrozen(Long time) {
        String sql = "SELECT d.* FROM butler_user_balance_detail d"
                + " LEFT JOIN butler_order o ON o.order_id = d.object_id"
                // amount_type 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8 推广首单奖励
                // in_out_flag 1收入 2支出
                + " WHERE d.amount_type IN (1, 2, 8) AND d.in_out_flag = 1"
                // 1冻结 2正常，3取消
                // 订单状态.0待确认，1已确认，2已收货，3已取消，4已完成，5已作废
                // 支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款
                // confirm_time 收货确认时间
                + " AND d.frozen_flag = 1 AND o.confirm_time <= ?"
                + " AND o.pay_status = 1 AND o.order_status = 4"
                + " ORDER BY d.create_time DESC";

        return UserBalanceDetail.dao.find(sql, time);
    }

    /**
     * 获取店铺未解冻但是可以解冻的钱
     */
    public List<UserBalanceDetail> storeFindCalFrozen(Long time) {
        // 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费
        String sql = "SELECT d.* FROM butler_user_balance_detail d"
                + " LEFT JOIN butler_order o ON o.order_id = d.object_id"
                + " WHERE d.amount_type = 5 AND d.in_out_flag = 1"
                + " AND d.frozen_flag = 1 AND o.confirm_time <= ?"
                + " AND o.pay_status = 1 AND o.order_status = 4"
                + " ORDER BY d.create_time DESC";

        return UserBalanceDetail.dao.find(sql, time);
    }

    /**
     * 1. 解冻商家销售收入
     * 2. 解冻推广平台分润
     * 3. 解冻推广店铺分润
     * 4. 钱包（减去冻结金额、添加可用金额、添加总金额、添加销售业绩）
     */
    public boolean unFrozen(UserBalanceDetail balanceDetail) {
        balanceDetail.setFrozenFlag(ButlerEmnu.BalanceFrozenFlagEnum.Normal.getValue());
        boolean update = balanceDetail.update(false);

        UserBalance userBalance = getByUserId(balanceDetail.getUserId());
        userBalance.setFrozenAmount(userBalance.getFrozenAmount().subtract(balanceDetail.getAmount()));
        userBalance.setAvailableAmount(userBalance.getAvailableAmount().add(balanceDetail.getAmount()));
        userBalance.setTotalAmount(userBalance.getTotalAmount().add(balanceDetail.getAmount()));

        return update && userBalance.update(false);
    }

    /**
     * 添加销售业绩
     */
    public boolean addAchievement(UserBalanceDetail balanceDetail) {
        Integer orderId = balanceDetail.getObjectId();
        Order order = Order.dao.findById(orderId);
        UserBalance userBalance = getByUserId(balanceDetail.getUserId());
        userBalance.setAchievementAmount(userBalance.getAchievementAmount().add(order.getTotalAmount()));
        return userBalance.saveOrUpdate(false);
    }

    /**
     * 添加充值金额
     */
    public boolean addAvailable(Recharge charge) {
        UserBalance userBalance = getByUserId(charge.getUserId().intValue());
        userBalance.setAvailableAmount(userBalance.getAvailableAmount().add(charge.getAccount()));
        return userBalance.saveOrUpdate(false);
    }

    /**
     * 订单推广奖励总和
     */
    public BigDecimal orderAchiveTotal(Integer orderId) {
        String sql = "SELECT SUM(amount) FROM butler_user_balance_detail WHERE in_out_flag = 1 AND amount_type IN (1, 2) AND frozen_flag = 2 AND object_id = ?";
        return Db.queryBigDecimal(sql, orderId);
    }
}