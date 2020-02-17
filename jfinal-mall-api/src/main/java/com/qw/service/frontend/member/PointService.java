package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.DateKit;
import cn.qw.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.model.*;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.order.OrderService;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 积分前端相关逻辑server
 * @author qw
 */
public class PointService extends BaseService {
    private static PointService service;

    private PointService() {
    }

    public static synchronized PointService me() {
        if (service == null) {
            service = new PointService();
        }
        return service;
    }

    /**
     * 收益明细
     */
    public Page<Record> incomeDetailPage(Integer userId, int pageNumber, int pageSize) {
        String select = "SELECT id, type, business_type, amount, DATE_FORMAT(create_time, '%Y-%m-%d %H:%m') create_time, is_withdraw";
        String from = " FROM butler_point_effective WHERE user_id = ? ORDER BY id DESC";
        return Db.paginate(pageNumber, pageSize, select, from, userId);
    }

    /**
     * 提现记录
     */
    public Page<PointWithdraw> withdrawDetail(Integer userId, int pageNumber, int pageSize) {
        String select = "SELECT *";
        String from = " FROM butler_point_withdraw WHERE user_id = ? ORDER BY id DESC";
        return PointWithdraw.dao.paginate(pageNumber, pageSize, select, from, userId);
    }

    /**
     * 1.首次购买：
     * A 普通推广 1.5倍 1%每天；
     * B推广1-3人 1.5倍 2%每天；
     * C推广4人以上 1.5倍  3%每天
     * D推广总销售额，不含自己的，小于自己首单，按1.5倍出局，大于等于自己的，按2倍出局
     * <p>
     * 2. 再次购买： 1.5倍，每天1%
     */
    public boolean invest(BigDecimal investAmount, Order order) {
        User user = User.dao.findById(order.getUserId());
        // 积分配置信息-常规赠送总积分是订单金额的倍数
        String nomalPointRateStr = ConfigService.me().findValue("point_config", "nomal_point_rate");
        // 1块钱可以抵的积分值
        String moneyToPointRate = ConfigService.me().findValue("point_config", "money_to_point_rate");

        // 钱转成积分, 针对total 做后续的计算
        BigDecimal total = investAmount.multiply(new BigDecimal(moneyToPointRate)).multiply(new BigDecimal(nomalPointRateStr));

        // 一个订单，对应一个投资记录，如果一个订单包含多个商品，也是一个订单对应一个投资记录
        return Db.tx(() -> {
            // 4.给自己添加投资积分收入-投资记录
            PointInvest pointInvest = addInvest(user.getUserId(), order.getOrderId(), investAmount, total);
            // 5.给自己添加积分收入-可签收但不可用状态（frozen)
            return addFrozen(user.getUserId(), pointInvest);
        });
    }

    /**
     * 添加一条投资记录
     */
    private PointInvest addInvest(Integer userId, Integer orderId, BigDecimal investAmount, BigDecimal amount) {
        PointInvest pointInvest = new PointInvest();
        pointInvest.setUserId(userId);
        pointInvest.setOrderId(orderId);
        // pointInvest.setInvestAmount(investAmount);
        pointInvest.setVerificationAmount(amount);
        pointInvest.save();
        return pointInvest;
    }

    /**
     * 冻结账户入账
     */
    private boolean addFrozen(int userId, PointInvest pointInvest) {
        PointFreeze freeze = new PointFreeze();
        freeze.setUserId(userId);
        // 类型（1核销入账 2领取出账）
        freeze.setType(1);
        freeze.setAmount(pointInvest.getVerificationAmount());
        /*freeze.setRemainAmount(pointInvest.getVerificationAmount());
        freeze.setInvestId(pointInvest.getId());*/
        return freeze.save() && frozenSum(userId, pointInvest.getVerificationAmount(), true);
    }

    /**
     * 删减积分
     */
    private boolean minusFrozen(Integer userId, BigDecimal amount) {
        PointFreeze freeze = new PointFreeze();
        freeze.setUserId(userId);
        freeze.setType(2);
        freeze.setAmount(amount);
//        freeze.setInvestId(0);
        // 汇总总的积分额度
        return freeze.save() && frozenSum(userId, amount, false);
    }

    /**
     * 汇总冻结积分
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param isPlus true 加  false 减
     * @return
     */
    private boolean frozenSum(int userId, BigDecimal amount, boolean isPlus) {
        PointSum sum = findByUserId(userId);
        if (isPlus) {
            sum.setFreezeRemain(sum.getFreezeRemain().add(amount));
        } else {
            sum.setFreezeRemain(sum.getFreezeRemain().subtract(amount));
        }
        return sum.update();
    }

    /**
     * 获取用户对应的积分汇总信息
     *
     * @param userId
     * @return
     */
    public PointSum findByUserId(Integer userId) {
        String sql = "SELECT * FROM butler_point_sum WHERE user_id = ? LIMIT 1";
        PointSum sum = PointSum.dao.findFirst(sql, userId);
        if (sum != null) {
            return sum;
        }
        return initSum(userId);
    }

    /**
     * 初始化我的积分汇总记录-和我的用户记录列表一一对应
     *
     * @param userId
     * @return
     */
    private PointSum initSum(int userId) {
        PointSum sum = new PointSum();
        sum.setUserId(userId);
        sum.setFreezeRemain(new BigDecimal("0"));
        sum.setUsableRemain(new BigDecimal("0"));
        sum.setWithdrawableRemain(new BigDecimal("0"));
        sum.setWithdrawableTotal(new BigDecimal("0"));
        sum.setConsumeTotal(new BigDecimal("0"));
        sum.save();
        return sum;
    }

    /**
     * 添加有效积分后，重新汇总
     */
    public boolean addEffectiveSum(int userId, BigDecimal withdrawAmount, BigDecimal effectiveAmount) {
        BigDecimal all = effectiveAmount.add(withdrawAmount);
        PointSum sum = findByUserId(userId);
        sum.setUsableRemain(sum.getUsableRemain().add(all));
        sum.setWithdrawableRemain(sum.getWithdrawableRemain().add(withdrawAmount));
        return sum.update();
    }

    /**
     * 新增一笔有效积分（按照配置比例，一部分可提现，一部分不可以提现）
     * 1. 新增一笔可用积分
     * 2. 新增一笔可提现积分
     * 3. 汇总总的积分额度
     *
     * @param userId
     * @param withdrawAmount
     * @param unWithdrawAmount
     * @return
     */
    public boolean addEffective(Integer userId, BigDecimal withdrawAmount, BigDecimal unWithdrawAmount, int businessType, int businessId) {
        BigDecimal amount = withdrawAmount.add(unWithdrawAmount);
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            return true;
        }
        return Db.tx(() ->
                        // 新增可提现积分
                        addEffective(userId, 1, businessType, 1, businessId, "", withdrawAmount)
                        // 新增可用积分
                        && addEffective(userId, 1, businessType, 2, businessId, "", unWithdrawAmount)
                        // 汇总总的积分额度
                        && addEffectiveSum(userId, withdrawAmount, unWithdrawAmount)
        );
    }

    /**
     * 添加一条收入的 有效积分
     *
     * @param userId
     * @param businessType
     * @param isWithdraw
     * @param businessId
     * @param amount
     * @return
     */
    public boolean addEffective(int userId, int type, int businessType, int isWithdraw, Integer businessId, String businessCode, BigDecimal amount) {
        // 金额是0 不做处理
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            return true;
        }
        PointEffective pointEffective = new PointEffective();
        pointEffective.setUserId(userId);
        // 1入账 2出账
        pointEffective.setType(type);
        pointEffective.setBusinessType(businessType);
        pointEffective.setIsWithdraw(isWithdraw);
        pointEffective.setBusinessId(businessId);
        pointEffective.setBusinessCode(businessCode);
        pointEffective.setAmount(amount);
        return pointEffective.save();
    }

    private List<PointFreeze> remainFrozen(Integer userId){
        return null;

        /*String sql = "SELECT * FROM butler_point_freeze WHERE type = 1 AND remain_amount > 0 AND user_id = ?";
        return PointFreeze.dao.find(sql, userId);
    }

    *//**
     * 分享商品领取积分
     *//*
    public BigDecimal checkIn(Integer userId) {
        User user = User.dao.findById(userId);
//        Integer levelId = user.getLevelId();
//        UserLevel level = UserLevel.dao.findById(levelId);
//        BigDecimal firstRate = level.getFirstRate();
        String normalRate = ConfigService.me().findValue("point_config", "checkin_order_rate");

        List<PointFreeze> pointFreezes = remainFrozen(userId);
        if(pointFreezes == null || pointFreezes.size() <= 0) {
            return new BigDecimal("0");
        }
        final BigDecimal[] checkTotal = {new BigDecimal("0")};
        pointFreezes.stream().sorted(Comparator.comparing(PointFreeze:: getId)).filter(f->{
            // 去重
            long count = pointFreezes.stream().filter(s -> s.getInvestId().equals(f.getInvestId())).count();
            return count == 1;
        }).forEach(f->{
            PointInvest invest = PointInvest.dao.findById(f.getInvestId());
            Order order = Order.dao.findById(invest.getOrderId());
            boolean firstPay = OrderService.me().isFirstPay(order);
            BigDecimal checkAmount = null;
            if(firstPay) {
                checkAmount = firstRate.multiply(invest.getInvestAmount());
            }else{
                checkAmount = new BigDecimal(normalRate).multiply(invest.getInvestAmount());
            }
            if(f.getRemainAmount().compareTo(checkAmount) < 0){
                checkAmount = f.getRemainAmount();
            }
            f.setRemainAmount(f.getRemainAmount().subtract(checkAmount));
            f.update();
            // 计算每次可以领取多少积分
            checkTotal[0] = checkTotal[0].add(checkAmount);
        });

        BigDecimal point = checkTotal[0];
        // 业务类型（1注册 2分享转发 3 取消订单 4提现 5消费 6下线首单奖励  8购买商品赠送 9提现审核拒绝退回 10线下录入）

        // 可提现占比
        String config = ConfigService.me().findValue("point_config", "withdraw_rate");
        // 平台手续费
        String chargeForTroubleRate = ConfigService.me().findValue("point_config", "charge_for_trouble");

        // 可提现的比例
        BigDecimal withdrawRate = new BigDecimal(config);
        // 平台手续费
        BigDecimal chargeForTrouble = point.multiply(new BigDecimal(chargeForTroubleRate));
        // 可提现积分
        BigDecimal withdrawAmount = point.multiply(withdrawRate);
        withdrawAmount = withdrawAmount.subtract(chargeForTrouble);
        // 可用积分
        BigDecimal unWithdrawAmount = point.subtract(withdrawAmount).subtract(chargeForTrouble);

        BigDecimal finalWithdrawAmount = withdrawAmount;
        Db.tx(()->{
            addEffective(userId, finalWithdrawAmount, unWithdrawAmount, 2, 0);
            // 删减可签收积分，frozzen积分
            minusFrozen(userId, point);
            return true;
        });
        return point;*/
    }

    /**
     * 购买消费积分
     */
    public boolean consume(Order order) {
        BigDecimal pointAsMoney = order.getPointAsMoney();
        // 购买的时候，把积分直接消费了，如果退款或者取消订单的时候，再把对应的积分还回去，不做中间状态
        // 也就是提交订单，即扣除积分。支付回调里面不做别的逻辑
        // 取消订单、退款，再把积分还回去
        // 扣去积分的逻辑：
        // 1. 在总汇总积分里面先做扣除
        // 1.1 分开扣除，先扣除usableRemain 再扣除withdrawableRemain
        // 2. 在有效积分里面 添加支出的记录
        // 2.1 添加两条，一条可用积分 一条可提现积分
        PointSum sum = findByUserId(order.getUserId());
        // 总的可用金额
        BigDecimal usableRemain = sum.getUsableRemain();
        // 总的可以提现的金额
        BigDecimal withdrawableRemain = sum.getWithdrawableRemain();
        // 不可提现部分消费的积分
        BigDecimal point = usableRemain.subtract(withdrawableRemain);
        if (point.compareTo(pointAsMoney) >= 0) {
            // 用来消费的积分，大于总的不可提现的积分
            sum.setUsableRemain(usableRemain.subtract(pointAsMoney));
            addEffective(sum.getUserId(), 2, 5, 2, order.getOrderId(), order.getOrderSn(), pointAsMoney);
        } else {
            // 计算花掉的可以提现的金额
            BigDecimal withdrawableConsume = pointAsMoney.subtract(point);
            sum.setWithdrawableRemain(withdrawableRemain.subtract(withdrawableConsume));
            sum.setUsableRemain(usableRemain.subtract(pointAsMoney));
            addEffective(sum.getUserId(), 2, 5, 2, order.getOrderId(), order.getOrderSn(), point);
            addEffective(sum.getUserId(), 2, 5, 1, order.getOrderId(), order.getOrderSn(), withdrawableConsume);
        }
        // 汇总消费记录
        sum.setConsumeTotal(sum.getConsumeTotal().add(pointAsMoney));
        return sum.update();
    }

    /**
     * 取消订单 对应的逻辑
     * 1. 把原来的积分还回去
     * 2. 把汇总积分重新算一遍
     */
    public boolean cancelOrder(Order order) {
        if (order.getPointAsMoney().compareTo(new BigDecimal("0")) <= 0) {
            return true;
        }
        return Db.tx(() -> {
            List<PointEffective> list = findEffecticeByGoodOrder(order);
            if (list == null || list.size() <= 0) {
                log.error("取消订单退还积分异常，对应订单不存在消费积分记录。订单详情：" + JsonKit.toJson(order));
                return false;
            }
            list.stream().forEach(effectice -> {
                BigDecimal amount = effectice.getAmount();
                // 是否可以提现（1可以2不可以）
                Integer isWithdraw = effectice.getIsWithdraw();
                // 添加有效积分
                addEffective(order.getUserId(), 1, 3, isWithdraw, order.getOrderId(), order.getOrderSn(), amount);
                // 重新汇总有效积分
                if (isWithdraw == 1) {
                    addEffectiveSum(order.getUserId(), amount, new BigDecimal("0"));
                } else {
                    addEffectiveSum(order.getUserId(), new BigDecimal("0"), amount);
                }
            });
            return minusEffectiveSum(order.getUserId(), order.getPointAsMoney());
        });
    }

    private boolean minusEffectiveSum(Integer userId, BigDecimal amount) {
        PointSum sum = findByUserId(userId);
        sum.setConsumeTotal(sum.getConsumeTotal().subtract(amount));
        return sum.update();
    }

    private List<PointEffective> findEffecticeByGoodOrder(Order order) {
        Integer orderId = order.getOrderId();
        String orderSn = order.getOrderSn();
        String sql = "SELECT * FROM  butler_point_effective WHERE type = 2 AND business_type = 5";
        if (orderId != null && orderId != 0) {
            sql += " AND business_id = ?";
            return PointEffective.dao.find(sql, orderId);
        }
        sql += " AND businessCode = ?";
        return PointEffective.dao.find(sql, orderSn);
    }

    /**
     * 积分提现
     *
     * @param sum
     * @param amount
     * @return
     */
    public boolean withdraw(PointSum sum, BigDecimal amount, User user) {
        return Db.tx(() -> {
            // 添加一条积分提现申请记录
            PointWithdraw withdraw = addWithdraw(sum.getUserId(), amount, user.getRealname(), user.getBankName(), user.getBankCard());
            // 扣减总额
            sum.setWithdrawableRemain(sum.getWithdrawableRemain().subtract(amount));
            sum.setUsableRemain(sum.getUsableRemain().subtract(amount));
            sum.setWithdrawableTotal(sum.getWithdrawableTotal().add(amount));
            // 添加有效积分明细
            return sum.update() && addEffective(sum.getUserId(), 2, 4, 1, withdraw.getId(), withdraw.getWithdrawSn(), amount);
        });
    }

    private PointWithdraw addWithdraw(int userId, BigDecimal amount, String name, String bankName, String bankCard) {
        PointWithdraw withdraw = new PointWithdraw();
        withdraw.setWithdrawSn(withdrawSn());
        withdraw.setUserId(userId);
        withdraw.setWithdrawStatus(1);
        withdraw.setAmount(amount);
        withdraw.setRemark("");
        withdraw.setName(name);
        withdraw.setBankName(bankName);
        withdraw.setBankCard(bankCard);
        withdraw.save();
        return withdraw;
    }

    private String withdrawSn() {
        String prefix = "WD";
        String datetime = DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
        String tail = RandomStringUtils.randomNumeric(5);
        return prefix + datetime + tail;
    }

    /**
     * 判断是否领取过 分享送积分
     *
     * @param userId
     * @param date
     * @return
     */
    public boolean hasCheckIn(int userId, Date date) {
        Date start = DateKit.firstSecondOfDay(date);
        Date end = DateKit.lastSecondOfDay(date);
        String sql = "SELECT COUNT(*) FROM butler_point_effective WHERE user_id = ? AND type = 1 AND business_type = 2 AND create_time >= ? AND create_time < ?";
        return Db.queryLong(sql, userId, start, end) > 0;
    }

    public boolean hasApply(Date date) {
        Date start = DateKit.firstSecondOfDay(date);
        Date end = DateKit.lastSecondOfDay(date);
        String sql = "SELECT COUNT(*) FROM butler_point_withdraw WHERE user_id = ? AND create_time >= ? AND create_time < ?";
        return Db.queryLong(sql, AppIdKit.getUserId(), start, end) > 0;
    }
}