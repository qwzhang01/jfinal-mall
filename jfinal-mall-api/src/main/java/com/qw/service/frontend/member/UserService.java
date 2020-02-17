package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.Base64Encoder;
import cn.qw.kit.CryptKit;
import cn.qw.kit.EmojiKit;
import com.qw.conf.QuantityConf;
import com.qw.model.*;
import com.qw.service.bakend.store.StoreService;
import com.qw.service.common.ConfigService;
import com.qw.service.common.RegionService;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.prom.CouponService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.ApiResult;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 用户信息管理
 * @author qw
 */
public class UserService extends BaseService {
    private static UserService service;
    private Cache cache;

    private UserService() {
        this.cache = Redis.use(QuantityConf.TOKEN_REDIS);
    }

    public static synchronized UserService me() {
        if (service == null) {
            service = new UserService();
        }
        return service;
    }

    public Page<Record> inviteUserPageList(Integer userId, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT u.user_id userId, u.nickname, u.mobile, FROM_UNIXTIME(u.reg_time, '%Y-%m-%d') regTime, u.head_pic headImg");
        StringBuilder from = new StringBuilder(" FROM butler_user u");
        from.append(" WHERE u.first_leader = ?");

        List<Object> paras = new ArrayList<Object>();
        paras.add(userId);
        from.append(" ORDER BY u.user_id DESC");

        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        return page;
    }

    public Page<Record> sublinePage(Integer userId, int pageNumber, int pageSize) {
        String select = "SELECT u.user_id, u.nickname, u.mobile, DATE_FORMAT(u.create_time, '%Y-%m-%d') resgister_time,"
                + "(SELECT COUNT(*) FROM butler_order WHERE user_id = u.user_id AND order_status = 0 AND pay_status = 1) AS order_count";

        String from = " FROM butler_user u"
                + " LEFT JOIN butler_point_sum p ON p.user_id = u.user_id"
                + " WHERE u.first_leader = ? ORDER BY u.user_id DESC";

        return Db.paginate(pageNumber, pageSize, select, from, userId);
    }

    public User getByUserName(String username) {
        User user = User.dao.findFirst("SELECT * FROM butler_user WHERE mobile = ? OR email = ? LIMIT 1", username, username);
        return user;
    }

    public User findByMobile(String mobile) {
        User user = User.dao.findFirst("SELECT * FROM butler_user WHERE mobile = ? LIMIT 1", mobile);
        return user;
    }

    public User signUp(String mobile, String password, int firstLeader) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(CryptKit.butlerMd5(password));
        user.setRegTime(System.currentTimeMillis() / 1000L);
        user.setNickname("会员" + RandomUtils.nextInt(1000, 5000));
        user.setFirstLeader(firstLeader);
        user.saveOrUpdate();
        return user;
    }

    public Map<String, Object> login(User user, String openId, ApiResult userInfo) {
        // openId 已经绑定过其他的用户账号以后，会返回false
        boolean wxBind = wxBind(user, openId, userInfo);
        if (!wxBind) {
            return null;
        }
        String accessToken = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        String userKey = CACHE_NAME + CryptKit.cymd5(CryptKit.cyCry(accessToken));
        // 登录保存时间为半个月
        cache.setex(userKey, 60 * 60 * 24 * 15, user.getUserId());

        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("userId", user.getUserId());
        map.put("openId", openId);
        return map;
    }

    /**
     * 校验用户登录状态
     */
    public boolean validToken(String accessToken, Integer userId) {
        if (userId == 0) {
            return false;
        }
        accessToken = CACHE_NAME + CryptKit.cymd5(CryptKit.cyCry(accessToken));
        return userId.equals(cache.get(accessToken));
    }

    /**
     * 绑定微信
     */
    private boolean wxBind(User user, String openId, ApiResult userInfo) {
        if (StrKit.isBlank(openId)) {
            return true;
        }
        User openUser = getByOpenId(openId);
        if (openUser != null && !openUser.getUserId().equals(user.getUserId())) {
            return false;
        }
        user.setOpenId(openId);
        if (userInfo != null) {
            String nickname = userInfo.getStr("nickname");
            // 去掉微信昵称特殊符号
            user.setNickname(EmojiKit.filterEmoji(nickname));
            if (StrKit.isBlank(user.getHeadPic())) {
                user.setHeadPic(userInfo.getStr("headimgurl"));
            }
            user.setSex(userInfo.getLong("sex"));
        }
        return user.update();
    }

    public User getByOpenId(String openId) {
        return User.dao.searchFirst(searchParam("open_id", openId));
    }

    public Record simpleDetail() {
        Integer userId = AppIdKit.getUserId();
        String sql = "SELECT user_id userId, mobile FROM butler_user WHERE user_id = ? LIMIT 1";
        Record simpleUser = Db.findFirst(sql, userId);
        if (simpleUser == null) {
            return null;
        }
        String headPic = simpleUser.getStr("headPic");
        if (StrKit.notBlank(headPic) && !headPic.startsWith("http")) {
            simpleUser.set("headPic", PropKit.get("fileHost") + headPic);
        }
        simpleUser.set("usableRemain", 0);
        simpleUser.set("usableRemain", PointService.me().findByUserId(userId).getUsableRemain());
        return simpleUser;
    }

    public Record detail(Integer userId) {
        User user = User.dao.findById(userId);
        if (user == null) {
            return null;
        }
        Record record = user.toRecord();
        String headPic = user.getHeadPic();
        if (StrKit.notBlank(headPic) && !headPic.startsWith("http")) {
            record.set("head_pic", PropKit.get("fileHost") + headPic);
        }
        record.set("coupon_count", CouponService.me().availableCount(userId));

        record.set("collect_count", GoodService.me().collectCount(userId));
        record.set("focus_count", StoreService.me().focusCount(userId));
        record.set("visit_count", GoodService.me().visitCount(userId));
        record.set("return_count", GoodService.me().returnCount(userId));

        record.set("waitPay", OrderService.me().waitPayCount(userId));
        record.set("waitSend", OrderService.me().waitSendCount(userId));
        record.set("waitReceive", OrderService.me().waitReciveCount(userId));

        record.set("cart_goods_num", OrderService.me().cartCount(userId));
        record.set("uncomment_count", OrderService.me().unCommentCount(userId));

        record.setColumns(address(record));

        // 推广级别
        /*UserLevel level = UserLevel.dao.findById(user.getLevelId());
        record.set("level", level.getLevelId());
        record.set("levelName", level.getLevelName());*/

        // 邀请二维码
        String url = ConfigService.me().findBasic("register_url");
        url += user.getMobile();
        url = Base64Encoder.encode(url);
        record.set("qr", PropKit.get("host") + "/tomcat/api/qr/code?title=" + user.getNickname() + "&width=200&height=200&url=" + url);

        return record;
    }

    private Record address(Record user) {
        Integer provinceId = user.getInt("province_id");
        Integer cityId = user.getInt("city_id");
        Integer districtId = user.getInt("district_id");
        Region province = Region.dao.findById(provinceId);
        Region city = Region.dao.findById(cityId);
        Region district = Region.dao.findById(districtId);

        Record record = new Record();
        if (province != null) {
            record.set("province_name", province.getName());
        }
        if (city != null) {
            record.set("city_name", city.getName());
        }
        if (district != null) {
            record.set("district_name", district.getName());
        }

        return record;
    }

    public Long count(Date begin, Date end) {
        String sql = "SELECT COUNT(*) FROM butler_user WHERE 1=1 ";
        List<Date> paras = new ArrayList<>();
        if(begin != null){
            sql += " AND create_time >= ?";
            paras.add(begin);
        }
        if(end != null) {
            sql += " AND create_time <= ?";
            paras.add(end);
        }
        return Db.queryLong(sql, paras.toArray());
    }
    public Long inviteCount(int userId) {
        String sql = "SELECT COUNT(*) FROM butler_user WHERE first_leader = ?";
        return Db.queryLong(sql, userId);
    }

    public boolean edit(Integer userId, String headImg, String nickname, Integer sex, Date birthday, Integer districtId) {
        User user = User.dao.findById(userId);
        if (StrKit.notBlank(headImg)) {
            user.setHeadPic(headImg);
        }
        if (StrKit.notBlank(nickname)) {
            user.setNickname(nickname);
        }
        if (sex != null) {
            user.setSex(new Long(sex));
        }
        if (birthday != null) {
            user.setBirthday(new Integer(String.valueOf(birthday.getTime() / 1000)));
        }
        if (districtId != null) {
            user.setDistrictId(districtId);
            Region district = Region.dao.findById(districtId);
            if (district != null) {
                user.setCityId(district.getParentId());
                Region city = Region.dao.findById(district.getParentId());
                if (city != null) {
                    user.setProvinceId(city.getParentId());
                }
            }
        }
        return user.update();
    }

    /**
     * 获取默认收货地址
     */
    public Record defaultConsignee(Integer userId) {
        Record record = new Record();
        UserAddress userAddress = UserAddress.dao.searchFirst(searchParam(searchParam("user_id", userId), "is_default", 1));
        if (userId == null || userId == 0 || userAddress == null) {
            record.set("address_id", 0);
            record.set("address", "请选择收货人");
            record.set("district", 0);
            return record;
        }
        String address = RegionService.me().orderAddress(userAddress.getProvince(), userAddress.getCity(), userAddress.getDistrict(), userAddress.getTwon()) + userAddress.getAddress();
        record.set("address_id", userAddress.getAddressId());
        record.set("address", address);
        record.set("district", userAddress.getDistrict());
        return record;
    }

    public boolean calLevel(Integer userId) {
        User user = User.dao.findById(userId);
        //if(user.getLevelId() >= 3) {
            return true;
        // }
        // Long count = subordinateCount(userId);
        // int level = 1;
        // if(count >= 1 && count <= 3) {
        //  level = 2;
        // }else if(count > 3){
        //  level = 3;
        //  }
//        user.setLevelId(level);
    // return user.update();
    }

    private Long subordinateCount(Integer userId) {
        String sql = "SELECT COUNT(*) FROM butler_user u WHERE u.first_leader = ?";
        sql += "  AND EXISTS (";
        sql += " SELECT * FROM butler_order WHERE butler_order.user_id = u.user_id AND butler_order.order_status = 0 AND butler_order.pay_status = 1)";
        return Db.queryLong(sql, userId);
    }
}