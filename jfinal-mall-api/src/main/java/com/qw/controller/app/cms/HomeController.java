package com.qw.controller.app.cms;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.frontend.cms.AdService;
import com.qw.service.frontend.cms.ArticleService;
import com.qw.service.frontend.cms.HomeSetService;
import com.qw.service.frontend.good.CategoryService;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.prom.PromOrderService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 首页管理
 */
public class HomeController extends RestController {
    /**
     * @title 所有分类
     * @respParam id|分类ID|int|必填
     * @respParam name|分类名称|String|必填
     * @respParam icon|分类图标|String|必填
     * @respParam image|分类banner|String|必填
     * @respParam children|子分类|List<Record>|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void category() {
        renderJson(CategoryService.me().all(1));
    }

    /**
     * @title 获取搜索关键字
     * @respParam data|搜索关键字|List<String>|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void searchKey() {
        List<String> result = new ArrayList<>();
        result.add("智能门锁");
        result.add("机器人");
        result.add("床");
        renderJson(result);
    }


    /**
     * @title 首页导航
     * @respParam id|分类ID|int|必填
     * @respParam name|分类名称|String|必填
     * @respParam image|分类图标|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void nav() {
        renderJson(CategoryService.me().homeNav());
    }

    /**
     * @title 首页头部（banner、背景色）
     * @respParam gotoType|跳转方式(1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀)|int|必填
     * @respParam gotoId|跳转参数ID|int|必填
     * @respParam backgroundColor|背景颜色|String|必填
     * @respParam img|banner图片|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void head() {
        renderJson(HomeSetService.me().findBanner());
    }

    /**
     * @title 首页金刚区
     * @respParam gotoType|跳转方式|int|必填
     * @respParam gotoId|跳转参数ID|int|必填
     * @respParam title|标题|String|必填
     * @respParam img|按钮图片|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void diamond() {
        renderJson(HomeSetService.me().findDiamond());
    }

    /**
     * @title 首页资讯
     * @respParam id|资讯ID|int|必填
     * @respParam title|资讯标题|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void info() {
        renderJson(ArticleService.me().findHome());
    }

    /**
     * @title 首页广告
     * @respParam bigAdvertise|大广告|Object|必填
     * @respParam smallAdvertise|小广告|List<Object>|必填
     * @respParam gotoType|跳转方式|int|必填
     * @respParam gotoId|跳转参数ID|int|必填
     * @respParam img|图片|String|必填
     * @respParam smallAdvertiseBackgroundColor|小广告位置背景颜色|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void advertise() {
        renderJson(HomeSetService.me().findAdvertise());
    }

    /**
     * @title 首页专场
     * @respParam gotoType|跳转方式|int|必填
     * @respParam gotoId|跳转参数ID|int|必填
     * @respParam img|专场图片|String|必填
     * @respParam good|专题对应商品|Object|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam title|商品标题|String|必填
     * @respParam goodImg|商品图片|String|必填
     * @respParam price|商品价格|double|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void specialShow() {
        renderJson(HomeSetService.me().specialShow());
    }

    /**
     * @param lng|经度|flout|非必填
     * @param lat|维度|flout|非必填
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
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void favourite() {
        //经度
        String lng = getPara("lng");
        //纬度
        String lat = getPara("lat");
        int pageNumber = getParaToInt("pageNumber", 1);
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        int pageSize = getParaToInt("pageSize", 10);
        if (pageSize <= 0) {
            pageSize = 10;
        }
        // 分页获取猜你喜欢
        Page<Record> pageList = GoodService.me().favouriteByUser(lng, lat, pageNumber, pageSize);
        renderJson(pageList);
    }

    /************************************以前的老接口****************************************************/

    /**
     * @title 获取首页banner(Deprecated)
     * @respParam image|图片地址|String|必填
     * @respParam jumpType|跳转方式(1.咨询 2活动  3: 商品 4：商品分类 5：web链接  6：模块分类)|int|必填
     * @respParam name|名称|String|必填
     * @respParam relateId|跳转地址|String|必填
     */
    @Deprecated
    @Clear(RestSecurityInterceptor.class)
    public void banner() {
        List<Record> banner = AdService.me().indexBanner();
        renderJson(banner);
    }

    /**
     * @title 推荐商品(Deprecated)
     * @respParam data|推荐商品|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void recommendGood() {
        String recommendGood = "";
        renderJson(recommendGood);
    }

    /**
     * @title 获取首页金刚区按钮(Deprecated)
     * @respParam id|模块ID|String|必填
     * @respParam image|商品图片|String|必填
     * @respParam jumpType|跳转方式|String|必填
     * @respParam level|等级|String|必填
     * @respParam relateId|跳转地址|String|必填
     * @respParam title|名称|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void module() {
        List<Record> module = CategoryService.me().findModule();
        renderJson(module);
    }

    /**
     * @title 获取首页资讯(Deprecated)
     * @respParam articleId|资讯ID|int|必填
     * @respParam title|资讯标题|String|必填     *
     * @respParam jump_type|跳转方式(0：预览 1.咨询 2活动  3: 商品 4：商品分类 5：web链接  6：模块分类)|String|必填     *
     * @respParam content|内容|String|必填     *
     */
    @Clear(RestSecurityInterceptor.class)
    public void news() {
        // 0：预览 1.咨询 2活动  3: 商品 4：商品分类 5：web链接  6：模块分类
        List<Record> news = ArticleService.me().findIndex();
        renderJson(news);
    }

    /**
     * @title 获取首页推广信息(Deprecated)
     * @respParam name|活动图片|String|非必填
     * @respParam image|活动图片|String|非必填
     * @respParam jump_type|跳转方式|int|必填
     * @respParam relate_id|跳转地址|String|必填
     * @respParam level|是否存在|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void promote() {
        Record prom = PromOrderService.me().cashProm();
        if (prom == null) {
            renderParamError("没有营销活动");
            return;
        }
        String name = "满反现金";
        String imgPath = prom.getStr("prom_img");
        String jumpType = "4";
        Integer relateId = prom.getInt("id");
        Integer level = 4;

        Record record = new Record();
        record.set("name", name);
        record.set("image", StrKit.notBlank(imgPath) ? String.format("%s%s", PropKit.get("fileHost"), imgPath) : "");
        record.set("jump_type", jumpType);
        record.set("relate_id", relateId);
        record.set("level", level);
        renderJson(record);
    }

    /**
     * @title 秒杀团拼信息(Deprecated)
     * @respParam secKill|秒杀信息|Object|必填
     * @respParam teamShop|团拼信息|Object|必填
     * @respParam desc|说明|String|非必填
     * @respParam promtDesc|促销说明|String|必填
     * @respParam time|当前时间|String|必填
     * @respParam image|图片地址|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void fascinate() {
        Record result = new Record();
        Record secKill = new Record();
        secKill.set("desc", "限时秒杀");
        secKill.set("promtDesc", "1元抢购");

        String time = DateKit.dateToString(new Date(), "yyyy/MM/dd HH:mm:ss");
        List<Record> flash = FlashSaleService.me().flashTime();
        if (flash != null && flash.size() > 0) {
            Record record = flash.get(0);
            Long startTime = record.getLong("start_time");
            Long endTime = record.getLong("end_time");
            long now = System.currentTimeMillis() / 1000L;
            if (now < startTime) {
                Date date = new Date(startTime * 1000L);
                time = DateKit.dateToString(date, "yyyy/MM/dd HH:mm:ss");
            } else {
                Date date = new Date(endTime * 1000L);
                time = DateKit.dateToString(date, "yyyy/MM/dd HH:mm:ss");
            }
        }

        secKill.set("time", time);
        secKill.set("image", PropKit.get("fileHost") + "/public/upload/static/AAAHoAAABqCAMAAABEUbVNAAADAFB.png");
        result.set("secKill", secKill);

        Record teamShop = new Record();
        teamShop.set("desc", "低价优选");
        teamShop.set("promtDesc", "低至3折");
        teamShop.set("image", PropKit.get("fileHost") + "/public/upload/static/AAAHoAAABqCAMAAABEUbVNAAADAFB.png");
        result.set("teamShop", teamShop);

        renderJson(result);
    }

    /**
     * @title 获取主打分类产品(Deprecated)
     * @respParam image|图片地址|String|必填
     * @respParam jumpType|跳转方式|int|必填
     * @respParam name|名称|String|必填
     * @respParam relateId|跳转地址|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void flagshipCategory() {
        // 这个模块跳转的位置，是子类，跳转进去以后，请求接口，渲染页面
        renderJson(AdService.me().indexFlagshipCategory());
    }

    /**
     * @title 获取一级主打产品(Deprecated)
     * @respParam pid|推荐商品|String|必填
     * @respParam name|名称|String|必填
     * @respParam image|图片地址|String|必填
     * @respParam jump_type|跳转方式(3: 商品 4：商品分类 5：web链接  6：模块分类)|int|必填
     * @respParam relate_id|跳转方式|int|必填
     * @respParam level|跳转方式|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void flagshipMajor() {
        //  获取一级主打产品、目前是智慧家居类的商品
        renderJson(AdService.me().indexFlagshipMajor());
    }

    /**
     * @title 获取热销榜(Deprecated)
     * @respParam topHot|topHot|四个榜单|List<Object>|必填
     * @respParam rank_title|榜单名称|String|必填
     * @respParam rank_desc|排行描述|String|必填
     * @respParam image|图片地址|String|必填
     * @respParam promot_desc|营销描述|String|必填
     * @respParam jump_type|跳转方式(3: 商品 4：商品分类 5：web链接  6：模块分类)|int|必填
     * @respParam related_id|跳转关联ID|int|必填
     * @respParam image|图片地址|String|必填
     * @respParam topSellingGood|热销榜前3|List<Object>|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam name|商品名称|String|必填
     * @respParam image|图片地址|String|必填
     * @respParam orderCount|订单总数|int|必填
     * @respParam promotDesc|营销宣传说明|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void hotRank() {
        // 热销榜包含：
        // 单品热销榜
        // 单品人气榜(人气是收藏+浏览量最大的)
        // 单品好评榜
        // 分类热销榜
        // 分类人气榜
        // 分类好评榜
        Record result = new Record();
        result.set("topSellingGood", GoodService.me().topSeller(3));
        result.set("topHot", GoodService.me().topHot());
        renderJson(result);
    }

    /**
     * @title 获取二级主打产品信息(Deprecated)
     * @respParam pid|推荐商品|String|必填
     * @respParam name|名称|String|必填
     * @respParam image|图片地址|String|必填
     * @respParam jump_type|跳转方式(3: 商品 4：商品分类 5：web链接  6：模块分类)|int|必填
     * @respParam relate_id|跳转方式|int|必填
     * @respParam level|跳转方式|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void flagshipVice() {
        // 目前是（旅游出行）
        renderJson(AdService.me().indexFlagshipVice());
    }
}