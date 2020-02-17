package com.qw.service.frontend.store;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import com.qw.model.Store;
import com.qw.model.StoreCollect;
import com.qw.model.User;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.prom.LotteryService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoreService extends BaseService {
    private static StoreService service;

    private StoreService() {
    }

    public static synchronized StoreService me() {
        if (service == null) {
            service = new StoreService();
        }
        return service;
    }

    /**
     * 获取推荐店铺列表
     */
    public Page<Record> recommend(int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT s.store_id storeId, s.store_name storeName");
        select.append(", s.store_avatar storeAvatar, s.store_credit storeCredit, s.store_desccredit storeDesccredit");
        select.append(", s.store_servicecredit storeServicecredit, s.store_deliverycredit storeDeliverycredit");
        select.append(", s.store_sales storeSale");

        StringBuilder from = new StringBuilder(" FROM butler_store s");
        // 推荐，0为否，1为是，默认为0
        from.append(" WHERE store_recommend = 1");
        from.append(" AND EXISTS(SELECT * FROM butler_good WHERE butler_good.store_id = s.store_id AND butler_good.is_on_sale = 1)");
        from.append(" ORDER BY s.store_id DESC");

        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString());
        List<Record> list = page.getList();

        for (Record r : list) {
            String storeAvatar = r.getStr("storeAvatar");
            if (StrKit.notBlank(storeAvatar)) {
                r.set("storeAvatar", PropKit.get("fileHost") + storeAvatar);
            }
            Integer storeId = r.getInt("storeId");
            r.set("good", GoodService.me().findByStore(storeId, 3));
        }
        return page;
    }

    public Page<Record> collectPage(int pageNumber, int pageSize) {
        String select = "SELECT c.log_id collectId, s.store_id storeId, s.store_avatar storeAvatar, s.store_name name, (select COUNT(*) from butler_store_collect WHERE butler_store_collect.store_id = c.store_id) AS collectNum";
        String from = " FROM butler_store_collect c";
        from += " LEFT JOIN butler_store s ON s.store_id = c.store_id";
        from += " WHERE c.user_id = ? ORDER BY c.log_id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, AppIdKit.getUserId());
        List<Record> list = page.getList();
        for (Record r : list) {
            String storeAvatar = r.getStr("storeAvatar");
            if (StrKit.notBlank(storeAvatar)) {
                r.set("storeAvatar", PropKit.get("fileHost") + storeAvatar);
            }
        }
        return page;
    }

    /**
     * 获取店铺销量
     */
    public Long saleCount(Store store) {
        String sql = "SELECT SUM(og.goods_num) FROM butler_order_goods og" +
                " LEFT JOIN butler_order o ON o.order_id = og.order_id" +
                " WHERE o.pay_status = 1 AND o.store_id = ?";
        Long count = Db.queryLong(sql, store.getStoreId());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    /**
     * 收藏店铺
     */
    public boolean collect(Store store) {
        User user = User.dao.findById(AppIdKit.getUserId());

        StoreCollect storeCollect = new StoreCollect();
        storeCollect.setUserId(user.getUserId());
        storeCollect.setStoreId(store.getStoreId());
        storeCollect.setStoreName(store.getStoreName());
        storeCollect.setUserName(user.getNickname());
        storeCollect.setAddTime(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));

        store.setStoreCollect(store.getStoreCollect() + 1);
        return Db.tx(() -> storeCollect.saveOrUpdate(false)
                && store.update(false));
    }

    /**
     * 取消收藏
     */
    public boolean cancelCollect(Store store) {
        Integer userId = AppIdKit.getUserId();
        StoreCollect storeCollect = StoreCollect.dao.searchFirst(searchParam(searchParam("user_id", userId), "store_id", store.getStoreId()));

        store.setStoreCollect(store.getStoreCollect() - 1);

        return Db.tx(() -> storeCollect.delete() && store.update(false));
    }

    /**
     * 获取店铺关注人数
     */
    public Long fansCount(Store store) {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            return 0L;
        }
        String sql = "SELECT COUNT(*) FROM butler_store_collect WHERE store_id = ? AND user_id = ?";
        return Db.queryLong(sql, store.getStoreId(), userId);
    }

    public Record simpleDetail(Integer storeId) {
        String sql = "SELECT store_id, store_name, store_avatar, store_logo FROM butler_store WHERE store_id = ? LIMIT 1";
        Record store = Db.findFirst(sql, storeId);
        store.set("totalGoodNum", totalGoodNum(storeId));
        store.set("is_collect", isCollect(storeId));

        String storeAvatar = store.getStr("store_avatar");
        if (StrKit.notBlank(storeAvatar)) {
            store.set("store_avatar", PropKit.get("fileHost") + storeAvatar.trim());
        }
        String storeLogo = store.getStr("store_logo");
        if (StrKit.notBlank(storeLogo)) {
            store.set("store_logo", PropKit.get("fileHost") + storeLogo.trim());
        }
        return store;
    }

    /**
     * 获取店铺总商品数
     */
    public Long totalGoodNum(Integer storeId) {
        String sql = "SELECT COUNT(*) FROM butler_good g WHERE g.goods_state = 1 AND g.is_on_sale = 1 AND g.store_id = ?";
        return Db.queryLong(sql, storeId);
    }

    /**
     * 判断是否收藏店铺
     */
    public boolean isCollect(Integer storeId) {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM butler_store_collect WHERE user_id = ? AND store_id = ?";
        return Db.queryLong(sql, userId, storeId) > 0;
    }

    /**
     * 获取店铺一级分类
     */
    public List<Record> categoryHighLevel(Store store) {
        String sql = "SELECT cat_id categoryId, cat_name category FROM butler_store_goods_class WHERE is_show = 1 AND parent_id = 0 AND store_id = ? ORDER BY cat_sort ASC";
        return Db.find(sql, store.getStoreId());
    }

    /**
     * 获取店铺优惠券
     */
    public List<Record> coupons(Store store) {
        // TODO 具体逻辑待实现
        return new ArrayList<>();
    }

    /**
     * 获取店铺参加活动的商品前10个
     */
    public List<Record> activityGood(Store store) {
        // 显示1元抢购抽奖进行中的活动商品
        return LotteryService.me().storeGood(store, 10);
    }

    /**
     * 判断用户是否已经开店
     */
    public Store userStore(User user) {
        String sql = "SELECT * FROM butler_store WHERE user_id = ?";
        return Store.dao.findFirst(sql, user.getUserId());
    }

    public Store userStore(Integer userId) {
        String sql = "SELECT * FROM butler_store WHERE user_id = ?";
        return Store.dao.findFirst(sql, userId);
    }

    /**
     * 开店申请
     */
    public boolean apply(User user, Store store, String storeName, String storeDesc, String storeAvatar // 基本信息
            , int type, int regionId, String address, String categoryId // 认证基本信息
            , String idFront, String idBack, String idHold // 身份证信息
            , String businessLicense, String unifiedSocialReferenceCode, int validityPeriodType, Date startValidityPeriod, Date endValidityPeriod  // 营业执照信息
            , String cardNum, String cardFront, String cardBack) { // 银行卡信息
        // 保存店铺信息
        if (store == null) {
            store = new Store();
        }
        store.setStoreName(storeName);
        store.setUserId(user.getUserId());
        store.setUserName(user.getMobile());
        store.setSellerName(user.getMobile());
        store.setRegionId(regionId);
        store.setStoreAvatar(storeAvatar);
        store.setStoreDesc(storeDesc);
        store.setStoreAddress(address);
        store.setStoreType(type);
        store.setIdFront(idFront);
        store.setIdBack(idBack);
        store.setIdHold(idHold);
        store.setBusinessLicense(businessLicense);
        store.setUnifiedSocialReferenceCode(unifiedSocialReferenceCode);
        store.setValidityPeriodType(validityPeriodType);
        store.setStartValidityPeriod(startValidityPeriod);
        store.setEndValidityPeriod(endValidityPeriod);
        store.setCardNum(cardNum);
        store.setCardFront(cardFront);
        store.setCardBack(cardBack);
        store.saveOrUpdate(false);
        return true;
    }

    /**
     * 获取用户开店状态
     */
    public int status(Integer userId) {
        String sql = "SELECT store_state FROM butler_store WHERE user_id = ?";
        //店铺状态，0关闭，1开启，2审核中
        Integer storeState = Db.queryInt(sql, userId);
        // 1未开店 2开店审核中 3开店成功 4审核失败
        if (storeState == null) {
            return 1;
        }
        if (2 == storeState) {
            return 2;
        }
        if (1 == storeState) {
            return 3;
        }
        if (0 == storeState) {
            return 4;
        }
        return 1;
    }
}