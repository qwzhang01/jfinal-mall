package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.qw.model.UserSign;

import java.util.Calendar;
import java.util.Date;

/**
 * 用户打卡签到
 */
public class UserSignService extends BaseService {
    private static UserSignService service;

    private UserSignService() {
    }

    public static synchronized UserSignService me() {
        if (service == null) {
            service = new UserSignService();
        }
        return service;
    }

    /**
     * 判断用户当天是否已经打卡签到
     *
     * @param userId
     */
    public boolean hasCheckIn(Integer userId, Date date) {
        Date start = DateKit.firstSecondOfDay(date);
        Date end = DateKit.lastSecondOfDay(date);
        String sql = "SELECT COUNT(*) FROM butler_user_sign WHERE user_id = ? AND create_time >= ? AND create_time < ?";
        return Db.queryLong(sql, userId, start, end) > 0;
    }

    public boolean checkIn(Integer userId) {
        return Db.tx(() -> {
            // 1. 获取最近的签到记录
            UserSign lastSign = findLastSign(userId);
            // 2. 添加签到记录
            add(userId, lastSign);
            // 3. 添加签到对应的积分逻辑
//            PointService.me().checkIn(userId);
            return true;
        });
    }

    private UserSign findLastSign(Integer userId) {
        String sql = "SELECT * FROM butler_user_sign WHERE user_id = ? ORDER BY  create_time DESC";
        return UserSign.dao.findFirst(sql, userId);
    }

    private UserSign add(Integer userId, UserSign last) {
        UserSign userSign = new UserSign();
        userSign.setUserId(userId);
        userSign.setDateMonth(DateKit.dateToString(new Date(), "yyyyMMdd"));
        if (last == null) {
            userSign.setSignTotal(1);
            userSign.setContinueSignCount(1);
        } else {
            userSign.setSignTotal(last.getSignTotal() + 1);
            // 计算连续签到天数
            Date createTime = last.getCreateTime();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterdayStart = DateKit.firstSecondOfDay(cal.getTime());
            if (createTime.compareTo(yesterdayStart) < 0) {
                userSign.setContinueSignCount(1);
            } else {
                userSign.setContinueSignCount(last.getContinueSignCount() + 1);
            }
        }
        userSign.save();
        return userSign;
    }
}
