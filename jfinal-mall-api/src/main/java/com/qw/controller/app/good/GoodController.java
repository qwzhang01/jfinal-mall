package com.qw.controller.app.good;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.IpKit;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.qw.event.good.GoodVisitEvent;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.model.Comment;
import com.qw.model.Good;
import com.qw.model.Order;
import com.qw.model.OrderGoods;
import com.qw.service.frontend.good.*;
import com.qw.service.frontend.prom.FlashSaleService;
import com.qw.service.frontend.prom.LotteryService;
import net.dreamlu.event.EventKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通商品
 */
public class GoodController extends RestController {

    @Clear(RestSecurityInterceptor.class)
    public void search() {
        List<Record> search = GoodSearchService.me().search("",  "megacorp", "employee");
        renderJson(search);
    }


    /**
     * @param storeId|店铺ID|int|非必填
     * @param categoryId|分类ID|int|非必填
     * @param goodName|商品名称|String|非必填
     * @title 自定义查询条件
     * @respParam brand|品牌|List<Record>|必填
     * @respParam brand|品牌|List<Record>|必填
     * @respParam id|品牌ID|int|必填
     * @respParam name|品牌名称|String|必填
     * @respParam specification|规格|List<Record>|必填
     * @respParam name|规格名称|String|必填
     * @respParam value|规格值|List<Record>|必填
     * @respParam id|规格ID|String|必填
     * @respParam content|规格名|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void searchForm() {
        String goodName = getPara("goodName");
        int storeId = getParaToInt("storeId", 0);
        int categoryId = getParaToInt("categoryId", 0);
        Record result = new Record();
        result.set("brand", BrandService.me().searchForm(goodName, storeId, categoryId));
        result.set("specification", SpecificationService.me().searchForm(goodName, storeId, categoryId));
        renderJson(result);
    }

    /**
     * @param categoryId|分类ID|int|非必填
     * @param goodName|商品名字|String|非必填
     * @param brand|品牌ID逗号串|String|非必填
     * @param specification|规格ID逗号串|String|非必填
     * @param startMoney|价格区间开始值|double|非必填
     * @param endMoney|价格区间结束值|double|非必填
     * @param sortColumn|排序字段(goodCommentRate、saleNum、shopPrice)|String|非必填
     * @param sortType|排序方式(ASC、DESC)|String|非必填
     * @param pageNumber|页码(默认第1页)|int|非必填
     * @param pageSize|页个数(默认10条)|int|非必填
     * @title 商品搜索
     * @respParam image|商品图片|String|必填
     * @respParam goodCommentNum|商品评论数|int|必填
     * @respParam goodCommentRate|商品好评率|float|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam goodSpu|商品spu编码|String|必填
     * @respParam shopPrice|商品价格|double|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void searchPage() {
        // 分类
        int categoryId = getParaToInt("categoryId", 0);
        // 搜索
        String goodName = getPara("goodName", "");
        // 价格区间
        BigDecimal beginMoney = getParaToDecimal("startMoney");
        BigDecimal endMoney = getParaToDecimal("endMoney");
        // 品牌
        String brand = getPara("brand");
        // 规格
        String specification = getPara("specification");
        // 1 只搜索可以赚取积分的商品
        int isEarnPoint = getParaToInt("isEarnPoint", 0);
        // true 可以使用积分兑换的
        boolean pointAsMoney = getParaToBoolean("pointAsMoney", false);
        // 排序
        // goodCommentRate  saleNum  shopPrice
        String sortColumn = getPara("sortColumn", "normal");
        // ASC  DESC
        String sortType = getPara("sortType", "DESC");
        // 分页
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        // 参数校验
        if (!"normal".equals(sortColumn) && !"goodCommentRate".equals(sortColumn) && !"saleNum".equals(sortColumn) && !"shopPrice".equals(sortColumn)) {
            renderParamError("sortColumn 参数只能是 normal、goodCommentRate、saleNum、shopPrice");
            return;
        }
        if ("normal".equals(sortColumn)) {
            sortColumn = "saleNum, goodCommentRate";
        }
        if (!"ASC".equals(sortType) && !"DESC".equals(sortType)) {
            renderParamError("sortType 参数只能是 ASC、DESC");
            return;
        }

        Page<Record> pageList = GoodService.me()
                // 基本搜索信息
                .searchPage(pageNumber, pageSize, categoryId, goodName
                        , isEarnPoint, pointAsMoney
                        // 筛选搜索信息
                        , beginMoney, endMoney, brand, specification
                        // 排序信息
                        , sortColumn, sortType);
        renderJson(pageList);
    }

    /**
     * @param goodId|商品ID|int|必填
     * @title 获取商品详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam totalGoodNum|店铺总商品数|String|必填
     * @respParam cartNum|用户购物车收藏商品数量|int|必填
     * @respParam shop_price|店铺价格|String|必填
     * @respParam activity|活动|Object|必填
     * @respParam flashPrice|团购价格|decimal|必填
     * @respParam startTime|开始时间|long|必填
     * @respParam specId|规格ID|int|必填
     * @respParam specName|规格名称|String|必填
     * @respParam specKey|规格键|String|必填
     * @respParam lotteryActivity|一元抢购抽奖活动|List<Object>|必填
     * @respParam activityNum|当前场次|int|必填
     * @respParam attendNum|已参与人数|int|必填
     * @respParam absentNum|还查多少人|int|必填
     * @respParam imgPath|活动商品图片|String|必填
     * @respParam title|活动商品标题|String|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam lotteryId|活动ID|int|必填
     * @respParam lotteryGoodId|活动商品ID|int|必填
     * @respParam startTime|活动开始时间|String|必填
     * @respParam endTime|活动结束时间|String|必填
     * @respParam lotteryPrice|商品活动价格|decimal|必填
     * @respParam userList|参与活动用户集合|List<Object>|必填
     * @respParam headPic|用户头像|String|必填
     * @respParam mobile|用户手机号码|String|必填
     * @respParam buyNum|已经被卖掉的数量|int|必填
     * @respParam activityType|1普通商品2秒杀商品3抢购抽奖商品|int|必填
     * @respParam goodDetail|商品详情图片数组|String[]|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void detail() {
        // 获取参数
        long goodId = getParaToLong("goodId", 0L);
        // 秒杀商品ID
        int flashGoodId = getParaToInt("flashGoodId", 0);
        // 一元抢购抽奖活动商品ID
        int lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        int shareUserId = getParaToInt("shareUserId", 0);

        // 秒杀商品
        if (flashGoodId != 0) {
            // 秒杀商品信息
            Record result = FlashSaleService.me().detail(flashGoodId);
            result.set("activityType", 2);
            renderJson(result);
            return;
        }
        // 拼团商品
        if (lotteryGoodId != 0) {
            Record result = LotteryService.me().detail(lotteryGoodId);
            result.set("activityType", 3);
            renderJson(result);
            return;
        }

        // 普通商品
        Record result = GoodService.me().detail(goodId);
        if (result == null) {
            renderParamNull("商品已下架，请返回浏览其他商品");
            return;
        }
        result.set("activityType", 1);
        // 新增访问记录
        EventKit.post(new GoodVisitEvent(goodId, AppIdKit.getUserId(), shareUserId));
        renderJson(result);
    }

    /**
     * @param orderGoodId|订单商品ID|int|必填
     * @param goodScore|商品评分|Decimal|必填
     * @param isAnonymous|是否匿名评价0:是；1不是|int|必填
     * @param content|评价内容|String|必填
     * @param img|评价图片地址|String|必填
     * @title 评价商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void comment() {
        Integer recId = getParaToInt("recId", 0);

        String goodRank = getPara("goodScore");
        Integer isAnonymous = getParaToInt("isAnonymous", 0);
        String content = getPara("content");
        String img = getPara("img");
        Integer userId = AppIdKit.getUserId();
        // 参数校验
        BigDecimal goodRankDecimal = new BigDecimal(goodRank);

        OrderGoods orderGood = OrderGoods.dao.findById(recId);
        if (orderGood == null) {
            renderParamNull("参数错误，订单不存在");
            return;
        }
        Order order = Order.dao.findById(orderGood.getOrderId());
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(userId)) {
            renderParamError("非法操作");
            return;
        }

        if ((order.getOrderStatus() != 0 || order.getOrderStatus() != 4) && order.getPayStatus() != 1) {
            renderParamError("该笔订单还未完成");
            return;
        }
        Comment comment = CommentService.me().findByRecId(recId);
        if (comment != null) {
            renderParamError("您已经评论过该商品");
            return;
        }

        boolean add = CommentService.me().add(order, orderGood, content, img, goodRankDecimal, isAnonymous, IpKit.getIpAddr(getRequest()));
        if (add) {
            renderSuccess("评论成功");
        } else {
            renderOperateError("评论失败");
        }
    }

    /**
     * @param goodId|商品ID|int|必填
     * @param type|1                全部 2好评 3 中评 4差评 5晒图|int|必填
     * @param pageNumber|页码|int|必填
     * @param pageSize|每页记录数|int|必填
     * @title 获取商品评价
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void commentPage() {
        Integer goodId = getParaToInt("goodId", 0);
        Integer type = getParaToInt("type", 0);//  // 1 全部 2好评 3 中评 4差评 5晒图
        if (type != 1 && type != 2 && type != 3 && type != 4 && type != 5) {
            renderParamError("type只能是1 2 3 4 5");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> page = CommentService.me().pageList(pageNumber, pageSize, goodId, type);
        renderJson(page);
    }

    /**
     * @title 获取商品收藏夹列表信息
     * @respParam collectId|商品收藏ID|int|必填
     * @respParam name|商品名称/标题|String|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam shopPrice|商品原价|double|必填
     * @respParam collectNum|商品收藏数|int|必填
     */
    public void collectPage() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = GoodService.me().collectPage(pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param goodId|商品ID|int|必填
     * @title 收藏商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void collect() {
        Integer goodId = getParaToInt("goodId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品不存在");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品已下架");
            return;
        }
        if (GoodService.me().isCollect(good.getGoodsId())) {
            renderParamError("商品已经收藏，无需重复收藏");
            return;
        }
        boolean collect = GoodService.me().collect(good);
        if (collect) {
            renderSuccess("收藏成功");
        } else {
            renderOperateError("收藏失败");
        }
    }

    /**
     * @param goodId|商品ID|int|必填
     * @title 取消收藏商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void cancelCollect() {
        Integer goodId = getParaToInt("goodId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品不存在");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品已下架");
            return;
        }
        boolean collect = GoodService.me().isCollect(good.getGoodsId());
        if (!collect) {
            renderParamError("商品没有收藏，无法取消收藏");
            return;
        }
        boolean cancelCollect = GoodService.me().cancelCollect(good);
        if (cancelCollect) {
            renderSuccess("取消成功");
        } else {
            renderOperateError("取消失败");
        }
    }

    /**
     * @param goodIds|收藏夹商品id(由,拼接)|String|必填
     * @title 批量取消收藏商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before(Tx.class)
    public void cancelCollectBatch() {
        String goodIds = getPara("goodIds");
        String[] collectIdsArr = goodIds.split(",");
        for (String goodId : collectIdsArr) {
            Good good = Good.dao.findById(goodId);
            boolean operateResult = GoodService.me().cancelCollect(good);
            if (!operateResult) {
                renderOperateError("操作失败");
            }
        }
        renderSuccess("移出收藏夹成功！");
    }

    /**
     * @param goodId|商品ID|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 猜你喜欢
     * @respParam goods_id|商品ID|String|必填
     * @respParam goods_name|商品名称|String|必填
     * @respParam is_virtual|是否是虚拟商品|String|必填
     * @respParam shop_price|店铺价格|String|必填
     * @respParam cat_id3|分类ID|String|必填
     * @respParam image|图片地址|String|必填
     * @respParam store_id|店铺ID|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void favourite() {
        int goodId = getParaToInt("goodId", 0);

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品不存在");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品已下架");
            return;
        }
        // 分页获取猜你喜欢
        Page<Record> pageList = GoodService.me().favouriteByGood(good, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param goodId|商品ID|int|非必填
     * @title 获取服务说明
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam title|标题|String|必填
     * @respParam content|内容|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void serviceInfo() {
        int goodId = getParaToInt("goodId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品已下架");
            return;
        }

        // TODO 临时解决，返回数据
        List<Record> result = new ArrayList<>();
        Record record = new Record();
        record.set("title", "7天无理由退换货（安装后不支持）");
        record.set("content", "7天无理由退换货（安装后不支持）");
        result.add(record);
        renderJson(result);
    }

    /**
     * @param beginAmount|价格区间开始值|int|非必填
     * @param endAmount|价格区间结束值|int|非必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 凑单商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goodId|商品ID|int|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam storeCount|商品库存|String|必填
     * @respParam sku|sku|String|必填
     * @respParam goodSn|商品编号|String|必填
     * @respParam shopPrice|商品价格|double|必填
     * @respParam thumbPath|预览图片|String|必填
     * @respParam spec|规格数组|List<Objecte>|必填
     * @respParam specList|规格具体数据数组|String|必填
     * @respParam specName|规格类型名称|String|非必填
     * @respParam itemName|规格名称|String|必填
     * @respParam imgPath|规格对应图片|String|必填
     * @respParam itemId|具体规格ID|String|必填
     * @respParam specId|规格类型ID|String|必填
     * @respParam specName|规格类型名称|String|必填
     * @respParam specConstitute|规格组合信息数组|List<Object>|必填
     * @respParam specConstituteId|规格组合ID|int|必填
     * @respParam specConstitutePrice|规格组合价格|double|必填
     * @respParam specConstituteStoreCount|规格组合库存|int|必填
     * @respParam specConstituteSupplyPrice|规格组合供货价|double|必填
     * @respParam specImg|规格组合图片地址|String|必填
     * @respParam specItemIdConstitute|组合的规格ID字符串|String|必填
     */
    public void combineOrderGood() {
        Integer beginAmount = getParaToInt("beginAmount", 0);
        Integer endAmount = getParaToInt("endAmount", 0);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = GoodService.me().combineOrderGood(beginAmount, endAmount, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @title 获取所有经营类目
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|类目ID|int|必填
     * @respParam name|类目名称|String|必填
     * @respParam children|子类目|List<Object>|必填
     */
    public void parentTree() {
        List<Record> list = CategoryService.me().parentTree();
        renderJson(list);
    }
}