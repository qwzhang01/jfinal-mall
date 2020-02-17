package com.qw.controller.app;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.model.Store;
import com.qw.model.User;
import com.qw.service.common.CaptchaService;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.store.StoreService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺管理
 */
public class StoreController extends RestController {

    /**
     * @param smsCode|短信验证码|String|必填
     * @title 开店申请验证
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void applyVerify() {
        Integer userId = AppIdKit.getUserId();
        User user = User.dao.findById(userId);
        Store userStore = StoreService.me().userStore(user);
        if (userStore != null) {
            if (userStore.getStoreState() == 1) {
                renderParamError("您已经开店，无法再次申请");
                return;
            }
            if (userStore.getStoreState() == 2) {
                renderParamError("您的店铺正在审核中，无法发起申请");
                return;
            }
        }
        // 获取参数
        String smsCode = getPara("smsCode");
        if (!CaptchaService.me().validateSmsCode(smsCode, user.getMobile())) {
            renderParamNull("短信验证码错误，请重新输入!");
            return;
        }
        renderSuccess("验证通过");
    }

    /**
     * @param storeName|店铺名称|String|必填
     * @param storeDesc|店铺信息|String|必填
     * @param storeAvatar|店铺头像|String|必填
     * @param type|1                                          个人店铺 2 企业店铺|int|必填
     * @param regionId|所在区域|int|必填
     * @param address|店铺地址|String|必填
     * @param categoryId|经营类目ID，字符串，拼接|String|必填
     * @param idFront|身份证正面|String|必填
     * @param idBack|身份证反面|String|必填
     * @param idHold|手持身份证|String|必填
     * @param businessLicense|营业执照照片|String|必填
     * @param unifiedSocialReferenceCode|营业执照统一社会编号|String|必填
     * @param validityPeriodType|1                            长期有效 2 固定期限|int|必填
     * @param startValidityPeriod|营业执照开始有效期|Date|必填
     * @param endValidityPeriod|营业执照结束有效期|Date|必填
     * @param cardNum|银行卡号|String|必填
     * @param cardFront|银行卡正面照片|String|必填
     * @param cardBack|银行卡反面照片|String|必填
     * @title 开店申请
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before(Tx.class)
    public void apply() {
        // 初步校验
        Integer userId = AppIdKit.getUserId();
        User user = User.dao.findById(userId);
        Store userStore = StoreService.me().userStore(user);
        if (userStore != null) {
            if (userStore.getStoreState() == 1) {
                renderParamError("您已经开店，无法再次申请");
                return;
            }
            if (userStore.getStoreState() == 2) {
                renderParamError("您的店铺正在审核中，无法发起申请");
                return;
            }
        }

        // 获取参数
        // 基本信息
        String storeName = getPara("storeName");// 店铺名称
        String storeDesc = getPara("storeDesc");// 店铺信息
        String storeAvatar = getPara("storeAvatar");// 店铺头像
        // 认证基本信息
        int type = getParaToInt("type", 1);// 1 个人店铺 2 企业店铺
        int regionId = getParaToInt("regionId", 0);// 所在区域
        String address = getPara("address");// 店铺地址
        String categoryId = getPara("categoryId");// 经营类目ID，字符串拼接 - 大类不能超过3个
        // 认证主体信息
        String idFront = getPara("idFront");// 身份证正面
        String idBack = getPara("idBack");// 身份证反面
        String idHold = getPara("idHold");// 手持身份证
        String businessLicense = getPara("businessLicense");// 营业执照照片
        String unifiedSocialReferenceCode = getPara("unifiedSocialReferenceCode");// 营业执照统一社会编号
        int validityPeriodType = getParaToInt("validityPeriodType", 1);// 1 长期有效 2 固定期限
        Date startValidityPeriod = getParaToDate("startValidityPeriod");
        Date endValidityPeriod = getParaToDate("endValidityPeriod");
        String cardNum = getPara("cardNum");// 银行卡号
        String cardFront = getPara("cardFront");// 银行卡正面
        String cardBack = getPara("cardBack");// 银行卡反面

        // 参数校验
        if (!StrKit.notBlank(storeName)) {
            renderParamError("店铺名称不能为空");
            return;
        }
        if (!StrKit.notBlank(storeDesc)) {
            renderParamError("店铺介绍不能为空");
            return;
        }
        if (!StrKit.notBlank(storeAvatar)) {
            renderParamError("店铺头像不能为空");
            return;
        }
        if (type != 1 && type != 2) {
            renderParamError("type错误，只能传入1或2");
            return;
        }
        if (regionId == 0) {
            renderParamError("区域ID不能为空");
            return;
        }
        if (!StrKit.notBlank(address)) {
            renderParamError("店铺详细地址不能为空");
            return;
        }
        if (!StrKit.notBlank(categoryId)) {
            renderParamError("店铺主营分类不能为空");
            return;
        }
        if (!StrKit.notBlank(idFront)) {
            renderParamError("身份证正面照片不能为空");
            return;
        }
        if (!StrKit.notBlank(idBack)) {
            renderParamError("身份证反面照片不能为空");
            return;
        }
        if (!StrKit.notBlank(idHold)) {
            renderParamError("手持身份证照片不能为空");
            return;
        }
        if (type == 2) {
            if (!StrKit.notBlank(businessLicense)) {
                renderParamError("营业执照照片不能为空");
                return;
            }
            if (!StrKit.notBlank(unifiedSocialReferenceCode)) {
                renderParamError("统一社会引用代码不能为空");
                return;
            }
        }
        if (!StrKit.notBlank(cardNum)) {
            renderParamError("银行卡号不能为空");
            return;
        }
        if (!StrKit.notBlank(cardFront)) {
            renderParamError("银行卡正面照片不能为空");
            return;
        }
        if (!StrKit.notBlank(cardBack)) {
            renderParamError("银行卡反面照片不能为空");
            return;
        }
        boolean apply = StoreService.me().apply(user, userStore, storeName, storeDesc, storeAvatar, type, regionId, address, categoryId, idFront, idBack, idHold, businessLicense, unifiedSocialReferenceCode, validityPeriodType, startValidityPeriod, endValidityPeriod, cardNum, cardFront, cardBack);
        if (!apply) {
            renderOperateError("申请失败，请重试");
            return;
        }
        renderSuccess("申请成功");
    }

    /**
     * @param storeId|店铺ID|int|必填
     * @title 审核失败原因
     * @respParam data|审核失败原因|String|必填
     */
    public void applyFailReason() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("参数错误，店铺不存在");
            return;
        }
        // 店铺状态，0关闭，1开启，2审核中
        Integer storeState = store.getStoreState();
        if (0 != storeState) {
            renderParamError("店铺不是审核失败或关闭状态，无法显示原因");
            return;
        }
        String storeCloseInfo = store.getStoreCloseInfo();
        renderJson(storeCloseInfo);
    }

    /**
     * @param storeId|店铺ID|int|必填
     * @title 店铺首页详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeName|店铺名称|String|必填
     * @respParam storeLogo|店铺LOGO|String|必填
     * @respParam storeAvatar|店铺头像|String|必填
     * @respParam storeBanner|店铺Banner|String|必填
     * @respParam servicePhone|服务电话|String|必填
     * @respParam saleCount|销量|int|必填
     * @respParam fansCount|关注数|int|必填
     * @respParam isCollect|是否收藏|boolean|必填
     * @respParam coupons|优惠券对象|List<Object>|必填
     * @respParam categoryHighLevel|一级分类|List<Object>|必填
     * @respParam category|分类名称|String|必填
     * @respParam categoryId|分类ID|int|必填
     * @respParam activityGood|活动商品|List<Object>|必填
     * @respParam id|商品ID|int|必填
     * @respParam originalImg|商品图片|String|必填
     * @respParam title|商品名称|String|必填
     * @respParam price|商品价格|double|必填
     * @respParam buyNum|购买数量|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void detail() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        Record result = new Record();
        result.set("storeId", store.getStoreId());
        result.set("storeName", store.getStoreName());
        result.set("storeLogo", StrKit.notBlank(store.getStoreLogo()) ? PropKit.get("fileHost") + store.getStoreLogo() : "");
        result.set("storeAvatar", StrKit.notBlank(store.getStoreAvatar()) ? PropKit.get("fileHost") + store.getStoreAvatar() : "");
        result.set("storeBanner", StrKit.notBlank(store.getStoreBanner()) ? PropKit.get("fileHost") + store.getStoreBanner() : "");
        result.set("servicePhone", store.getServicePhone());
        result.set("saleCount", StoreService.me().saleCount(store));
        result.set("fansCount", StoreService.me().fansCount(store));
        result.set("isCollect", StoreService.me().isCollect(store.getStoreId()));
        result.set("categoryHighLevel", StoreService.me().categoryHighLevel(store));
        result.set("coupons", StoreService.me().coupons(store));
        result.set("activityGood", StoreService.me().activityGood(store));

        renderJson(result);
    }

    /**
     * @param storeId|店铺ID|int|必填
     * @param brand|品牌ID逗号串|String|非必填
     * @param specification|规格ID逗号串|String|非必填
     * @param startMoney|价格区间开始值|double|非必填
     * @param endMoney|价格区间结束值|double|非必填
     * @param type|1                           推荐 2 新品 3 促销 4 商品分类|int|必填
     * @param categoryId|分类ID|int|必填
     * @param sortColumn|排序字段                  normal sale price|String|必填
     * @param sortType|排序方式                    ASC  DESC|String|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 店铺商品分页数据
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goods_name|商品名称|String|必填
     * @respParam image|商品图片|String|必填
     * @respParam cat_id3|分类ID|int|必填
     * @respParam goods_id|商品ID|int|必填
     * @respParam lotteryId|活动ID|int|必填
     * @respParam lotteryPrice|活动价格|double|必填
     * @respParam shop_price|价格|double|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void good() {
        // 获取参数
        int storeId = getParaToInt("storeId", 0);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        // 1 推荐 2 新品 3 促销 4 商品分类
        int type = getParaToInt("type", 1);
        int categoryId = getParaToInt("categoryId", 0);
        // 价格区间
        BigDecimal beginMoney = getParaToDecimal("startMoney");
        BigDecimal endMoney = getParaToDecimal("endMoney");
        // 品牌
        String brand = getPara("brand");
        // 规格
        String specification = getPara("specification");

        String sortColumn = getPara("sortColumn", "normal"); // sale price
        String sortType = getPara("sortType", "DESC");// ASC  DESC
        String goodName = getPara("goodName");

        // 校验参数
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        if (type != 1 && type != 2 && type != 3 && type != 4) {
            renderParamError("type 参数只能是 1、2、3、4");
            return;
        }
        if (!"normal".equals(sortColumn) && !"sale".equals(sortColumn) && !"price".equals(sortColumn)) {
            renderParamError("sortColumn 参数只能是 normal、sale、price");
            return;
        }
        if (!"ASC".equals(sortType) && !"DESC".equals(sortType)) {
            renderParamError("sortType 参数只能是 ASC、DESC");
            return;
        }
        if (type == 4 && categoryId == 0) {
            renderParamError("商品根据分类查询，必须传分类ID");
            return;
        }
        Page<Record> pageList = GoodService.me().storeGoodPageList(store, type, categoryId, sortColumn, sortType, goodName, beginMoney, endMoney, brand, specification, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @title 获取店铺收藏夹列表信息
     * @respParam collectId|收藏ID|int|必填
     * @respParam name|店铺名称/标题|String|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeAvatar|店铺头像|String|必填
     * @respParam collectNum|收藏数|int|必填
     */
    public void collectPage() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = StoreService.me().collectPage(pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param storeId|店铺ID|int|必填
     * @title 收藏店铺
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void collect() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        if (StoreService.me().isCollect(storeId)) {
            renderParamError("店铺已经收藏，无需重复收藏");
            return;
        }
        boolean bookmark = StoreService.me().collect(store);
        if (bookmark) {
            renderSuccess("关注成功");
        } else {
            renderOperateError("关注失败");
        }
    }

    /**
     * @param storeId|店铺ID|int|必填
     * @title 取消收藏店铺
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void cancelCollect() {
        int storeId = getParaToInt("storeId", 0);
        Store store = Store.dao.findById(storeId);
        if (store == null) {
            renderParamError("店铺不存在");
            return;
        }
        if (!StoreService.me().isCollect(storeId)) {
            renderParamError("店铺没有收藏，无法取消收藏");
            return;
        }
        boolean bookmark = StoreService.me().cancelCollect(store);
        if (bookmark) {
            renderSuccess("取消成功");
        } else {
            renderOperateError("取消失败");
        }
    }

    /**
     * @param storeIds|收藏夹商品id(由,拼接)|String|必填
     * @title 批量取消收藏店铺
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before(Tx.class)
    public void cancelCollectBatch() {
        String storeIds = getPara("storeIds");
        String[] collectIdsArr = storeIds.split(",");
        for (String storeId : collectIdsArr) {
            Store store = Store.dao.findById(storeId);
            if (store == null) {
                renderParamError("店铺不存在");
                return;
            }
            boolean operateResult = StoreService.me().cancelCollect(store);
            if (!operateResult) {
                renderOperateError("操作失败");
            }
        }
        renderSuccess("移出收藏夹成功！");
    }

    /**
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 推荐店铺
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam storeName|店铺名称|String|必填
     * @respParam storeAvatar|店铺图片|String|必填
     * @respParam storeSale|店铺销量|int|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam good|店铺商品|List<Object>|必填
     * @respParam marketPrice|市场价格|decimal|必填
     * @respParam shopPrice|优惠价格|decimal|必填
     * @respParam originalImg|店铺图片|decimal|必填
     * @respParam storeCredit|店铺评分|double|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void recommend() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> list = StoreService.me().recommend(pageNumber, pageSize);
        renderJson(list);
    }
}