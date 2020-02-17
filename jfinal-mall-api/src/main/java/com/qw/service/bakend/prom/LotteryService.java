package com.qw.service.bakend.prom;

import cn.qw.base.BaseService;
import com.qw.model.Lottery;
import com.qw.model.LotteryGood;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抢购抽奖service
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

    public Page<Record> pageList(int pageNumber, int pageSize, int type, String title, Date beginTime, Date endTime) {
        // 1 定时开、2 人满开
        String select = "SELECT id, title, DATE_FORMAT(start_time, '%Y-%m-%d %H:%i') startTime, DATE_FORMAT(end_time, '%Y-%m-%d %H:%i') endTime, max_num maxNum, min_num minNum, type";
        String from = " FROM butler_lottery WHERE";
        List<Object> paras = new ArrayList<>();
        // type 1即将开始 2进行 3已经结束
        Date now = new Date();
        if (1 == type) {
            from += " start_time > ?";
            paras.add(now);
        }
        if (2 == type) {
            from += " start_time <=? AND end_time >= ?";
            paras.add(now);
            paras.add(now);
        }
        if (3 == type) {
            from += " end_time < ?";
            paras.add(now);
        }
        if (StrKit.notBlank(title)) {
            from += " AND title LIKE ?";
            paras.add("%" + title + "%");
        }
        if (beginTime != null) {
            from += " AND start_time >= ?";
            paras.add(beginTime);
        }
        if (endTime != null) {
            from += " AND end_time <= ?";
            paras.add(endTime);
        }
        return Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
    }

    public boolean form(int id, String title, int type, int maxNum, int minNum, Date startTime, Date endTime) {
        Lottery info = new Lottery();
        if (id != 0) {
            info.setId(id);
        }
        info.setTitle(title);
        info.setType(type);
        info.setMaxNum(maxNum);
        info.setMinNum(minNum);
        info.setStartTime(startTime);
        info.setEndTime(endTime);

        return info.saveOrUpdate(false);
    }

    public Page<Record> goodList(int pageNumber, int pageSize, Lottery lottery, String goodName) {
        String select = "SELECT lg.id lotteryGoodId, lg.title, lg.good_id goodId, lg.price" +
                ",lg.num, lg.description, lg.buy_num buyNum, lg.orderNum orderNum, lg.status status, lg.img_path imgPath";
        String from = " FROM butler_lottery_good lg"
                + " LEFT JOIN butler_good_sku sku ON sku.item_id = lg.spec_id"
                + " LEFT JOIN butler_good good ON lg.good_id = good.goods_id"
                + " WHERE lg.lottery_id = ?";
        List<Object> paras = new ArrayList<>();
        paras.add(lottery.getId());

        if (StrKit.notBlank(goodName)) {
            from += " AND lg.title LIKE ?";
            paras.add("%" + goodName + "%");
        }
        from += " ORDER BY lg.sort_id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                r.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }
        return page;
    }

    public boolean addGood(LotteryGood lotteryGood, Lottery lottery) {
        lotteryGood.setLotteryId(lottery.getId());
        lotteryGood.setStatus(2);
        return Db.tx(() -> {
            boolean save = lotteryGood.saveOrUpdate(false);
            lotteryGood.setSortId(lotteryGood.getId());
            boolean update = lotteryGood.update(false);
            return save && update;
        });
    }

    public boolean offSale(List<Integer> flashGoodIds) {
        List<String> collect = flashGoodIds.stream().map(f -> "?").collect(Collectors.toList());
        String join = StringUtils.join(collect, ",");
        String sql = "UPDATE butler_lottery_good SET status = 4 WHERE status = 2 AND id IN (" + join + ")";
        return Db.update(sql, flashGoodIds.toArray()) > 0;
    }

    public boolean sort(Integer flashGoodId1, Integer flashGoodId2) {
        LotteryGood lotteryGood1 = LotteryGood.dao.findById(flashGoodId1);
        LotteryGood lotteryGood2 = LotteryGood.dao.findById(flashGoodId2);
        int sort = lotteryGood1.getSortId();
        lotteryGood1.setSortId(lotteryGood2.getSortId());
        lotteryGood2.setSortId(sort);
        return Db.tx(() -> lotteryGood1.update(false) && lotteryGood2.update(false));
    }
}