package com.qw.event.user;

import com.qw.model.User;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.member.PointService;
import com.jfinal.log.Log;
import net.dreamlu.event.core.EventListener;

import java.math.BigDecimal;

/**
 * <p>注册成功监听器</p>
 * 1. 注册成功后，赠送不可以提现的积分
 *
 * @author qw
 */
public class SignUpListener {
    protected final Log log = Log.getLog(getClass());

    @EventListener({SignUpEvent.class})
    public void onApplicationEvent(SignUpEvent event) {
        Integer userId = event.getUserId();
        User user = User.dao.findById(userId);
        // 赠送积分
        givePoint(user);
    }

    /**
     * 注册赠送不可以提现的积分
     * 1. 送自己
     * 2. 送上级别
     */
    private void givePoint(User user) {
        String point = ConfigService.me().findValue("point_config", "register_point");
        String pointUp = ConfigService.me().findValue("point_config", "register_point_up");
        // 注册赠送积分值
        BigDecimal registerPoint = new BigDecimal(point);
        BigDecimal registerPointUp = new BigDecimal(pointUp);
        // 业务类型（1注册 2签到 3 取消订单 4提现 5消费 6首单奖励 7邀请用户注册）
        boolean addEffective = PointService.me().addEffective(user.getUserId(), new BigDecimal("0"), registerPoint, 1, 0);
        boolean addEffectiveUp = PointService.me().addEffective(user.getFirstLeader(), new BigDecimal("0"), registerPointUp, 7, user.getUserId());
        if (!addEffective || !addEffectiveUp) {
            log.error("注册赠送积分失败，失败用户ID为：" + user.getUserId());
        }
    }
}