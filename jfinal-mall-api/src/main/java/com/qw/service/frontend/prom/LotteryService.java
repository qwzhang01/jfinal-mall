package com.qw.service.frontend.prom;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.qw.model.*;
import com.qw.service.common.SmsTemplateService;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.order.OrderService;
import com.qw.vo.order.OrderDetailVo;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 拼团抽奖
 * @author qw
 */
public class LotteryService extends BaseService {

    private static LotteryService service;

    private LotteryService() {
    }

    public static synchronized LotteryService me() {
        if (service == null) {
            service = new LotteryService();
        }
        return service;
    }

    /**
     * 抽奖列表-时间线
     */
    public List<Record> lotteryTime(String date) {
        if(StrKit.isBlank(date)) {
            String sql = "SELECT end_time FROM butler_lottery l "
                    + " WHERE EXISTS(SELECT * FROM butler_lottery_good WHERE butler_lottery_good.lottery_id = l.id)"
                    + " AND l.start_time <= ? AND l.end_time >= ? ORDER BY l.end_time DESC";
            List<Date> listIng = Db.query(sql, new Date(), new Date());
            return listIng.stream().map(d->{
                Record record = new Record();
                record.set("lotteryId", 0);
                record.set("startTime", new Date());
                record.set("endTime", listIng.get(0));
                record.set("fontTime", DateKit.dateToString(new Date(), "HH:mm"));
                record.set("status", 1);
                return record;
            }).collect(Collectors.toList());
        }
        Date start = DateKit.stringtoDate(date, "yyyy年MM月dd日");
        Date end = DateKit.lastSecondOfDay(start);
        // 查未开始的
        String sql = "SELECT l.id lotteryId, l.start_time startTime, l.end_time endTime, '2' as `status`, DATE_FORMAT(l.start_time, '%H: %i') `fontTime`"
                + " FROM butler_lottery l WHERE EXISTS(SELECT * FROM butler_lottery_good WHERE butler_lottery_good.lottery_id = l.id)"
                + " AND l.start_time >= ? AND end_time <= ? ORDER BY l.start_time ASC";
        List<Record> list = Db.find(sql, start, end);
        return list;
    }

    /**
     * 抽奖时间线
     */
    public List<String> selectDate() {
        String sql = "SELECT start_time FROM butler_lottery WHERE start_time >= ? ORDER BY start_time ASC";
        List<Date> lotteryDateList = Db.query(sql, DateKit.firstSecondOfDay(new Date()));
        return lotteryDateList.stream().map(v -> {
            String strDate = DateKit.dateToString(v, "yyyy年MM月dd日");
            return strDate;
        }).distinct().collect(Collectors.toList());
    }

    /**
     * 抢购抽奖商品详情
     * 除了常规信息，还有拼单中的一个小列表
     */
    public Record detail(Integer lotteryGoodId) {

        LotteryGood lotteryGood = LotteryGood.dao.findById(lotteryGoodId);
        Lottery lottery = Lottery.dao.findById(lotteryGood.getLotteryId());

        Good good = Good.dao.findById(lotteryGood.getGoodId());
        Record result = GoodService.me().detail(good);
        result.set("goods_spec_list", new ArrayList<>());
        result.set("spec_goods_price", Arrays.asList(GoodSku.dao.findById(lotteryGood.getSpecId())));

        // 整理商品活动信息
        Record activity = new Record();
        activity.set("lotteryGoodId", lotteryGood.getId());
        activity.set("lotteryId", lotteryGood.getLotteryId());
        activity.set("lotteryPrice", lotteryGood.getPrice());
        activity.set("lotteryBuyNum", lotteryGood.getBuyNum());
        activity.set("orderNum", lotteryGood.getOrderNum());
        activity.set("maxNum", lottery.getMaxNum());
        activity.set("minNum", lottery.getMinNum());
        activity.set("type", lottery.getType());
        activity.set("startTime", lottery.getStartTime());
        activity.set("endTime", lottery.getEndTime());
        // 获取参与人数
        // 场数, 活动商品ID
        Integer attendNum = attendNum(lotteryGood.getBuyNum() + 1, lotteryGoodId);
        activity.set("attendNum", attendNum);
        // 还差多少人开奖
        Date nowDate = new Date();
        if (activity.getDate("startTime").getTime() <= nowDate.getTime() && nowDate.getTime() <= activity.getDate("endTime").getTime()) {
            Integer absentNum = activity.getInt("maxNum") - attendNum;
            activity.set("absentNum", absentNum);
        }
        // 随机显示2位用户的头像和手机号码
        // 所有参与的userId
        List<Record> userIdList = userIdList(lotteryGoodId, activity.getInt("lotteryBuyNum") + 1);
        //取出前2个userId(的相关信息)
        List<Record> userList = getTwoUser(userIdList);
        activity.set("userList", userList);

        // 设置活动信息
        result.set("lotteryActivity", activity);

        return GoodService.me().detail(good, result);
    }

    public List<Record> goodList(Integer lotteryId) {

        StringBuilder sql = new StringBuilder("SELECT lg.id lotteryGoodId, lg.lottery_id lotteryId, lg.good_id goodId");
        sql.append(",lg.title title, lg.price lotteryPrice, lg.num num, lg.buy_num lotteryBuyNum");
        sql.append(",sku.price shopPrice, g.original_img imgPath");
        sql.append(",l.max_num maxNum");
        sql.append(" FROM butler_lottery_good lg");
        sql.append(" LEFT JOIN butler_good g ON lg.good_id = g.goods_id");
        sql.append(" LEFT JOIN  butler_good_sku sku ON sku.item_id = lg.spec_id");
        sql.append(" LEFT JOIN butler_lottery l ON lg.lottery_id = l.id");
        sql.append(" WHERE lg.status = 2");
        List<Object> paras = new ArrayList<>();
        if (lotteryId != 0) {
            sql.append("  AND lg.lottery_id = ?");
            paras.add(lotteryId);
        } else {
            sql.append(" AND l.start_time <= ? AND l.end_time >= ?");
            paras.add(new Date());
            paras.add(new Date());
        }
        sql.append(" ORDER BY l.id DESC, lg.sort_id DESC");

        return Db.find(sql.toString(), paras.toArray())
                .stream()
                .map(v -> {
                    v.set("imgPath", PropKit.get("fileHost") + v.getStr("imgPath"));
                    //场次
                    v.set("sessionNum", v.getInt("lotteryBuyNum") + 1);
                    // 判断该活动商品是否已售罄
                    v = isSellOut(v);
                    //判断该活动商品当前场次参与人数是否已达上限
                    v = isMax(v);
                    return v;
                }).collect(Collectors.toList());
    }

    private Record isMax(Record v) {
        //判断该活动商品当前场次参与人数是否已达上限
        Integer attendNum = attendNum(v.getInt("lotteryBuyNum") + 1, v.getInt("lotteryGoodId"));
        if (attendNum >= v.getInt("maxNum")) {
            //已达上限
            v.set("isMax", 1);
        } else {
            v.set("isMax", 2);
        }
        return v;
    }

    private Record isSellOut(Record v) {
        if (v.getInt("lotteryBuyNum") >= v.getInt("num")) {
            //已售罄
            v.set("isSellOut", 1);
        } else {
            v.set("isSellOut", 2);
        }
        return v;
    }


    public List<Record> userIdList(Integer lotteryGoodId, Integer activityNum) {
        StringBuilder sql = new StringBuilder("SELECT lo.user_id userId, lo.lottery_good_id lotteryGoodId");
        sql.append(" FROM butler_lottery_order lo");
        sql.append(" LEFT JOIN butler_lottery_good lg ON lg.id = lo.lottery_good_id");
        sql.append(" WHERE lo.lottery_good_id = ? AND lo.activity_num = ?");
        List<Record> userIdList = Db.find(sql.toString(), lotteryGoodId, activityNum);
        return userIdList;
    }

    /**
     * 抢购抽奖订单信息，除了常规订单，还有所在团用户相关信息
     * @param lotteryGoodId
     * @return
     */
    public OrderDetailVo genLotteryOrder(int lotteryGoodId) {
        LotteryGood lotteryGood = LotteryGood.dao.findById(lotteryGoodId);
        GoodSku sku = GoodSku.dao.findById(lotteryGood.getSpecId());
        Good good = Good.dao.findById(lotteryGood.getGoodId());
        OrderDetailVo detailVo = OrderService.me().genOrderVo(good, sku, 1, null, lotteryGood);
        OrderDetailVo.LotteryUser lotteryUser =  detailVo.new LotteryUser();
        // 获取剩余开奖人数 TODO
        lotteryUser.setAbsentNum(5);
        // 获取参与会员头像 TODO
        lotteryUser.setHeadPic(Arrays.asList("http://thirdwx.qlogo.cn/mmopen/vi_32/03CfQzuty8BGecS8SeO7RnrEH9wFFmTRxZJEyI2w2YN0HxRvFyks284IvW9DWYuuaBK0WUyP86C7rg5N9Xnk3A/132", "http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLwy91cM7wGk9ZWYicrom9MDjFGmyibWtVuzMGQm68dXHR3VQGWEnGZ9ClQTI9WibIMkTJYZib8Uecldg/132"));
        detailVo.setLotteryUser(lotteryUser);
        return detailVo;
    }

    public List<Record> getTwoUser(List<Record> userIdList) {
        //选2个
        if (userIdList != null && userIdList.size() >= 2) {
            //取出前2个
            List<Record> userList = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Record record = Db.findFirst("SELECT u.user_id userId, u.head_pic headPic, u.mobile mobile FROM butler_user u WHERE u.user_id = ?", userIdList.get(i).getInt("userId"));
                if (record.getStr("headPic").indexOf("http://") != -1) {
                    record.set("headPic", record.getStr("headPic"));
                } else {
                    record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
                }
                record.set("mobile", disposeMobile(record.getStr("mobile")));
                userList.add(record);
            }
            return userList;
        }
        if (userIdList != null && userIdList.size() == 1) {
            //取出唯一1个
            List<Record> userList = new ArrayList<>();
            Record record = Db.findFirst("SELECT u.user_id userId, u.head_pic headPic, u.mobile mobile FROM butler_user u WHERE u.user_id = ?", userIdList.get(0).getInt("userId"));
            if (record.getStr("headPic").indexOf("http://") != -1) {
                record.set("headPic", record.getStr("headPic"));
            } else {
                record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
            }
            record.set("mobile", disposeMobile(record.getStr("mobile")));
            userList.add(record);
            return userList;
        } else {
            return null;
        }
    }

    public String disposeMobile(String mobile) {
        if (mobile != null) {
            return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
        }
        return "该用户的个人信息没有填写手机号码...";
    }

    public List<Record> attendLotteryList(Integer lotteryGoodId, Integer lotteryBuyNum) {
        List<Record> userIdList = userIdList(lotteryGoodId, lotteryBuyNum + 1);
        List<Record> attendLotteryList = userIdList.stream().map(v -> {
            Record record = Db.findFirst("SELECT u.user_id userId, u.head_pic headPic, u.mobile mobile FROM butler_user u WHERE u.user_id = ?", v.getInt("userId"));
            if (record.getStr("headPic").indexOf("http://") != -1) {
                record.set("headPic", record.getStr("headPic"));
            } else {
                record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
            }
            record.set("mobile", disposeMobile(record.getStr("mobile")));
            return record;
        }).collect(Collectors.toList());
        return attendLotteryList;
    }

    public Page<Record> pageUserLotteryList(Integer pageNumber, Integer pageSize, Integer userId) {
        if (userId == 0) {
            return null;
        }
        StringBuilder selectSQL = new StringBuilder("SELECT lo.id lotteryOrderId, lo.activity_num activityNum, lo.order_id orderId, lo.is_win isWin, lo.open_time openTime,");
        selectSQL.append(" lg.title title, lg.good_id goodId, lg.id lotteryGoodId,");        //, lg.img_path imgPath
        selectSQL.append(" l.end_time endTime, l.id lotteryId, l.type type,");        //l.start_time startTime
        selectSQL.append(" g.original_img imgPath");
        StringBuilder fromSQL = new StringBuilder(" FROM butler_lottery_order lo");
        fromSQL.append(" LEFT JOIN butler_lottery_good lg ON lo.lottery_good_id = lg.id");
        fromSQL.append(" LEFT JOIN butler_lottery l ON lg.lottery_id = l.id");
        fromSQL.append(" LEFT JOIN butler_good g ON g.goods_id = lg.good_id");
        fromSQL.append(" WHERE lo.user_id = ?");
        fromSQL.append(" ORDER BY lo.id DESC");

        List<Object> paras = new ArrayList<>();
        paras.add(userId);
        Page<Record> page = Db.paginate(pageNumber, pageSize, selectSQL.toString(), fromSQL.toString(), paras.toArray());
        List<Record> userLotteryList = page.getList();
        for (Record record : userLotteryList) {
            record.set("imgPath", PropKit.get("fileHost") + record.getStr("imgPath"));
            //判断是否已有人中奖
            Integer result = hasUserWin(record.get("activityNum"), record.get("lotteryGoodId"));
            record.set("hasUserWin", result);
            if (record.getInt("type") == 2) {    //人满开
                //判断当前场次有没开奖
                String SQL = "SELECT * FROM butler_lottery_order WHERE lottery_good_id = ? AND activity_num = ? AND is_win = ?";
                Record isWinRecord = Db.findFirst(SQL, record.getInt("lotteryGoodId"), record.getInt("activityNum"), 1);
                if (isWinRecord != null) {
                    record.set("isTimeout", 1);        //有人中奖
                    record.set("openTime", DateKit.dateToString(record.getDate("openTime"), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    record.set("isTimeout", 2);        //没有人中奖
                    record.set("openTime", DateKit.dateToString(record.getDate("endTime"), "yyyy-MM-dd HH:mm:ss"));
                }
            } else {    //定时开
                if (result == 1) {    //已开奖
                    //判断当前时间是否已经超过活动的结束时间
                    if (System.currentTimeMillis() > record.getDate("endTime").getTime()) {        //超过
                        record.set("isTimeout", 1);
                        record.set("openTime", DateKit.dateToString(record.getDate("endTime"), "yyyy-MM-dd HH:mm:ss"));
                    } else {        //还没超过
                        record.set("isTimeout", 1);
                        record.set("openTime", DateKit.dateToString(record.getDate("openTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                } else {            //未开奖
                    //判断当前时间是否已经超过活动的结束时间
                    if (System.currentTimeMillis() > record.getDate("endTime").getTime()) {        //超过
                        record.set("isTimeout", 2);
                        record.set("openTime", DateKit.dateToString(record.getDate("endTime"), "yyyy-MM-dd HH:mm:ss"));
                    } else {        //还没超过
                        record.set("isTimeout", 2);
                        record.set("openTime", DateKit.dateToString(record.getDate("openTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
        }
        return page;
    }

    private Integer hasUserWin(Integer activityNum, Integer lotteryGoodId) {
        String sql = "SELECT * FROM butler_lottery_order lo WHERE lo.is_win = 1 AND lo.lottery_good_id = ? AND lo.activity_num = ?";
        Record result = Db.findFirst(sql, lotteryGoodId, activityNum);
        if (result != null) {
            return 1;
        } else {
            return 2;
        }
    }

    public Record whoWin(int lotteryGoodId, int activityNum) {
        if (lotteryGoodId == 0 || activityNum == 0) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT lo.user_id userId,");
        sql.append(" lg.title title,");
        sql.append(" g.shop_price shopPrice");
        sql.append(" FROM butler_lottery_order lo");
        sql.append(" LEFT JOIN butler_lottery_good lg ON lg.id = lo.lottery_good_id");
        sql.append(" LEFT JOIN butler_good g ON g.goods_id = lg.good_id");
        sql.append(" WHERE lo.lottery_good_id = ? AND lo.activity_num = ? AND lo.is_win = 1");
        Record result = Db.findFirst(sql.toString(), lotteryGoodId, activityNum);
        if (result == null) {
            return null;
        }
        //获取用户手机号(带*号) 和 头像
        Record record = getOwnInfo(result.getInt("userId"));
        result.set("headPic", record.getStr("headPic"));
        result.set("mobile", record.getStr("mobile"));
        return result;
    }

    private Record getOwnInfo(Integer userId) {
        String sql = "SELECT u.user_id userId, u.head_pic headPic, u.mobile mobile FROM butler_user u WHERE u.user_id = ?";
        Record record = Db.findFirst(sql, userId);
        if (record.getStr("headPic").indexOf("http://") != -1) {
            record.set("headPic", record.getStr("headPic"));
        } else {
            record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
        }
        record.set("mobile", disposeMobile(record.getStr("mobile")));
        return record;
    }

    public Record lotteryDetail(Integer lotteryOrderId, Integer lotteryGoodId, Integer activityNum) {
        StringBuilder sql = new StringBuilder("SELECT lo.order_id orderId, lo.is_win isWin, lo.activity_num activityNum, lo.open_time openTime,");
        sql.append(" lg.title title, lg.good_id goodId, lg.id lotteryGoodId,");        //, lg.img_path imgPath
        sql.append(" l.id lotteryId, l.end_time endTime, l.type type,");        //l.start_time startTime
        sql.append(" g.original_img imgPath");
        sql.append(" FROM butler_lottery_order lo");
        sql.append(" LEFT JOIN butler_lottery_good lg ON lo.lottery_good_id = lg.id");
        sql.append(" LEFT JOIN butler_lottery l ON lg.lottery_id = l.id");
        sql.append(" LEFT JOIN butler_good g ON g.goods_id = lg.good_id");
        sql.append(" WHERE lo.id = ?");
        Record result = Db.findFirst(sql.toString(), lotteryOrderId);
        result.set("imgPath", PropKit.get("fileHost") + result.getStr("imgPath"));
        if (result.getInt("type") == 2) {    //人满开
            //判断当前场次有开奖没
            String SQL = "SELECT * FROM butler_lottery_order WHERE lottery_good_id = ? AND activity_num = ? AND is_win = ?";
            Record isWinRecord = Db.findFirst(SQL, result.getInt("lotteryGoodId"), result.getInt("activityNum"), 1);
            if (isWinRecord != null) {
                result.set("isTimeout", 1);        //有人中奖
                result.set("openTime", result.getDate("openTime"));
            } else {
                result.set("isTimeout", 2);        //没有人中奖
                result.set("openTime", result.getDate("endTime"));
            }
        } else {    //定时开
            //判断当前时间是否已经超过活动的结束时间
            if (System.currentTimeMillis() > result.getDate("endTime").getTime()) {
                result.set("isTimeout", 1);        //超过
                result.set("openTime", result.getDate("openTime"));
            } else {
                result.set("isTimeout", 2);        //还没超过
                result.set("openTime", result.getDate("endTime"));
            }
        }
        //判断是否已有人中奖
        Integer hasUserWinResult = hasUserWin(result.get("activityNum"), result.get("lotteryGoodId"));
        result.set("hasUserWin", hasUserWinResult);
        //获取7个用户头像
        List<Record> userIdList = userIdList(lotteryGoodId, activityNum);
        List<Record> userImgList = getSevenUserImg(userIdList);
        result.set("userImgList", userImgList);
        return result;
    }

    private List<Record> getSevenUserImg(List<Record> userIdList) {
        //选7个
        String sql = "SELECT u.user_id userId, u.head_pic headPic FROM butler_user u WHERE u.user_id = ?";
        if (userIdList != null && userIdList.size() >= 7) {
            //取出前7个
            List<Record> userList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                Record record = Db.findFirst(sql, userIdList.get(i).getInt("userId"));
                if (record.getStr("headPic").indexOf("http://") != -1) {
                    record.set("headPic", record.getStr("headPic"));
                } else {
                    record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
                }
                record.set("mobile", disposeMobile(record.getStr("mobile")));
                userList.add(record);
            }
            return userList;
        }
        if (userIdList != null && userIdList.size() == 1) {
            //取出唯一1个
            List<Record> userList = new ArrayList<>();
            Record record = Db.findFirst(sql, userIdList.get(0).getInt("userId"));
            if (record.getStr("headPic").indexOf("http://") != -1) {
                record.set("headPic", record.getStr("headPic"));
            } else {
                record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
            }
            record.set("mobile", disposeMobile(record.getStr("mobile")));
            userList.add(record);
            return userList;
        }
        if (userIdList != null && userIdList.size() < 7) {
            //全取出来
            List<Record> userList = new ArrayList<>();
            for (int i = 0; i < userIdList.size(); i++) {
                Record record = Db.findFirst(sql, userIdList.get(i).getInt("userId"));
                if (record.getStr("headPic").indexOf("http://") != -1) {
                    record.set("headPic", record.getStr("headPic"));
                } else {
                    record.set("headPic", PropKit.get("fileHost") + record.getStr("headPic"));
                }
                userList.add(record);
            }
            return userList;
        } else {
            return null;
        }
    }

    /**
     * 获取存在未开奖，时间结束的拼团抽奖活动
     */
    public List<Lottery> findEndLottery() {
        String sql = "SELECT * FROM butler_lottery WHERE end_time <= ? AND EXISTS" +
                " (" +
                " SELECT * FROM butler_lottery_good INNER JOIN butler_lottery_order ON butler_lottery_good.id = butler_lottery_order.lottery_good_id " +
                " WHERE butler_lottery_good.lottery_id = butler_lottery.id AND butler_lottery_order.open_time IS NULL" +
                ")";
        return Lottery.dao.find(sql, new Date());
    }

    /**
     * 获取指定商品的所有活动
     */
    public List<LotteryGood> findGoodByLottery(Lottery lottery) {
        String sql = "SELECT * FROM butler_lottery_good WHERE lottery_id = ?";
        return LotteryGood.dao.find(sql, lottery.getId());
    }

    /**
     * 获取指定活动商品的所有支付订单， 能生成这个记录，就是支付状态。
     */
    public List<LotteryOrder> findOrderByGood(LotteryGood lotteryGood) {
        String sql = "SELECT * FROM butler_lottery_order WHERE lottery_good_id = ? AND open_time IS NULL";
        return LotteryOrder.dao.find(sql, lotteryGood.getId());
    }


    public Integer getMaxNumById(Integer lotteryId) {
        String sql = "SELECT max_num FROM butler_lottery WHERE id = ?";
        Integer maxNum = Db.queryInt(sql, lotteryId);
        return maxNum;
    }

    /**
     * 获取店铺参与活动的商品列表
     */
    public List<Record> storeGood(Store store, int num) {
        Date now = new Date();
        String sql = "SELECT lg.id, lg.good_id goodId, g.original_img originalImg, lg.title goodName, lg.price, lg.buy_num buyNum FROM butler_lottery_good lg" +
                " LEFT JOIN butler_good g ON lg.good_id = g.goods_id" +
                " LEFT JOIN butler_lottery l ON l.id = lg.lottery_id" +
                " WHERE lg.`status` = 2 AND g.store_id = ? AND l.start_time <= ? AND l.end_time  >= ?";
        sql += " ORDER BY lg.sort_id ASC";
        sql += " LIMIT " + num;
        List<Record> list = Db.find(sql, store.getStoreId(), now, now);
        for (Record r : list) {
            String originalImg = r.getStr("originalImg");
            if (StrKit.notBlank(originalImg)) {
                r.set("originalImg", PropKit.get("fileHost") + originalImg);
            }
        }
        return list;
    }

    /**
     * 开奖，开奖应该放在一个队列里面，一个一个来
     */
    public void open(List<LotteryOrder> list, LotteryGood lotteryGood, Lottery lottery) {
        // 超出部分，不开奖，直接退钱
        if (lotteryGood.getBuyNum().compareTo(lotteryGood.getNum()) < 0) {
            // 判断是否满足最低开团人数
            if (lottery.getMinNum().compareTo(list.size()) <= 0) {
                // 抽奖
                int i = RandomUtils.nextInt(0, list.size());
                LotteryOrder lotteryOrder = list.get(i);
                lotteryOrder.setIsWin(1);
                // 减库存
                lotteryGood.setBuyNum(lotteryGood.getBuyNum() + 1);
            }
        }
        // 新增开奖时间
        List<LotteryOrder> orderList = list.stream().map(o -> {
            o.setOpenTime(new Date());
            return o;
        }).collect(Collectors.toList());
        // 提交事务
        Db.tx(() -> {
            Db.batchUpdate(orderList, orderList.size());
            return lotteryGood.update(false);
        });
        // 开完奖后续事物
        orderList.forEach(o -> {
            if (o.getIsWin() == 1) {
                // 发短信
                SmsTemplateService.me().lotteryWinSms(o, lotteryGood);
            } else {
                // 退款
                OrderService.me().refundLanuch(o.getOrderId(), "拼团抽奖抽奖失败退款");
            }
        });
    }

    /**
     * 根据一个拼团抽奖订单，获取同一场的所有订单
     *
     * @param lotteryOrder
     * @return
     */
    public List<LotteryOrder> findScreen(LotteryOrder lotteryOrder) {
        String sql = "SELECT * FROM butler_lottery_order WHERE lottery_good_id = ? AND activity_num = ?";
        return LotteryOrder.dao.find(sql, lotteryOrder.getLotteryGoodId(), lotteryOrder.getActivityNum());
    }

    public boolean isMax(Integer activityNum, Integer lotteryGoodId) {
        LotteryGood lotteryGood = LotteryGood.dao.findById(lotteryGoodId);
        Integer maxNum = LotteryService.me().getMaxNumById(lotteryGood.getLotteryId());
        Integer attendNum = LotteryService.me().attendNum(activityNum, lotteryGoodId);
        if (attendNum >= maxNum) {
            // 人满
            return true;
        }
        return false;
    }


    public Record luckyUserInfo(Integer lotteryGoodId, Integer activityNum) {
        String sql = "SELECT * FROM butler_lottery_order WHERE lottery_good_id = ? AND activity_num = ? AND is_win = ?";
        //查询所有中奖订单
        Record lucyUserOrder = Db.findFirst(sql, lotteryGoodId, activityNum, 1);
        if (lucyUserOrder == null) {
            return null;
        }
        User luckUser = User.dao.findFirst(sql, lucyUserOrder.getInt("user_id"));
        StringBuffer sqlStrB = new StringBuffer("SELECT g.goods_name goodsName");
        sqlStrB.append(" FROM butler_lottery_order lo");
        sqlStrB.append(" LEFT JOIN butler_lottery_good lg ON lg.id = lo.lottery_good_id");
        sqlStrB.append(" LEFT JOIN butler_good g ON g.goods_id = lg.good_id");
        sqlStrB.append(" WHERE lo.id = ?");
        Record goodName = Db.findFirst(sqlStrB.toString(), lucyUserOrder.getInt("id"));
        Record returnRecord = new Record();
        returnRecord.set("luckyUserHead", PropKit.get("fileHost") + luckUser.getHeadPic());
        returnRecord.set("luckyUserGoodInfo", luckUser.getNickname() + " 抽中了 " + goodName.getStr("goodsName"));
        return returnRecord;
    }


    private Integer activityNumById(Integer lotteryGoodId) {
        String sql = "SELECT lottery_id, buy_num FROM butler_lottery_good WHERE id = ?";
        LotteryGood lotteryGood = LotteryGood.dao.findFirst(sql, lotteryGoodId);
        //判断当前buyNum对应的场次的参与人数有没有达到maxNum => 达到:则当前用户应该对应下个场次 未达到:则当前用户用这个场次
        Integer attendNum = attendNum(lotteryGoodId, lotteryGood.getBuyNum() + 1);
        Integer maxNum = LotteryService.me().getMaxNumById(lotteryGood.getLotteryId());
        //达到 且 num > buyNum ==》 因此，在一元抢购商品列表，要展示num > buyNum的LotteryGood
        if (attendNum >= maxNum) {
            return lotteryGood.getBuyNum() + 2;
        } else {    //未达到
            return lotteryGood.getBuyNum() + 1;
        }
    }

    private Integer attendNum(Integer activityNum, Integer lotteryGoodId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(lo.user_id)");
        sql.append(" FROM butler_lottery_order lo");
        sql.append(" WHERE lo.activity_num = ? AND lo.lottery_good_id = ?");
        Long attendNum = Db.queryLong(sql.toString(), activityNum, lotteryGoodId);
        return attendNum.intValue();
    }

    /**
     * 根据订单信息，新增拼团抽奖订单
     */
    public LotteryOrder addByOrder(Order order) {
        // 往butler_lottery_order插入数据
        Integer orderId = order.getOrderId();
        List<OrderGoods> orderGoods = OrderService.me().findOrderGood(order);
        OrderGoods orderGood = orderGoods.get(0);
        Integer lotteryGoodId = orderGood.getActivityId();
        // 获取当前的场次
        Integer activityNum = activityNumById(lotteryGoodId);

        LotteryOrder lotteryOrder = new LotteryOrder();
        lotteryOrder.setLotteryGoodId(lotteryGoodId);
        lotteryOrder.setActivityNum(activityNum);
        lotteryOrder.setUserId(order.getUserId());
        lotteryOrder.setOrderId(orderId);
        lotteryOrder.setIsWin(2);
        lotteryOrder.saveOrUpdate(false);
        return lotteryOrder;
    }
}