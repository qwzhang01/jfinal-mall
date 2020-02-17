package com.qw.controller.app.good;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.prom.LotteryService;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

/**
 * 抢购抽奖
 */
public class LotteryController extends RestController {

    /**
     * @param lotteryDate|活动日期(格式:yyyy年MM月dd日)|String|必填
     * @title 获取活动banner&活动时间列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam banner|活动banner图|String|必填
     * @respParam lotteryTimeList|时间集合|List<Object>|必填
     * @respParam fontTime|显示时间|String|必填
     * @respParam lotteryId|活动ID|int|必填
     * @respParam startTime|开始时间(yyyy-MM-dd HH:mm:ss)|String|必填
     * @respParam endTime|结束时间(yyyy-MM-dd HH:mm:ss)|String|必填
     * @respParam status|活动状态 1：活动正在参与中 2：活动即将开始|int|必填
     * @respParam availableList|有效活动商品集合|List<Object>|必填
     * @respParam lotteryId|活动ID|int|必填
     * @respParam lotteryGoodId|活动商品ID|int|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam title|商品活动标题|String|必填
     * @respParam lotteryPrice|活动抽奖价|decimal|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam shopPrice|商品原价|decimal|必填
     * @respParam sessionNum|活动场次|int|必填
     * @respParam isMax|参与人数是否已达上限 1:是、2:不是|int|必填
     * @respParam isSellOut|是否已售罄 1:是、2:不是|int|必填
     */
    public void list() {
        String lotteryDate = getPara("lotteryDate");
        Record resultRecord = new Record();
        //1、获取广告图、规则图
        Map<String, String> lotterySet = ConfigService.me().findMap("LotterySet");
        String banner = lotterySet.get("lottery_banner");
        String regulation = lotterySet.get("lottery_regulation");
        resultRecord.set("banner", PropKit.get("fileHost") + banner);
        resultRecord.set("regulation", PropKit.get("fileHost") + regulation);
        //2、获取当天的活动时间段
        resultRecord.set("lotteryTimeList", LotteryService.me().lotteryTime(lotteryDate));
        renderJson(resultRecord);
    }

    /**
     * @title 获取活动日期列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam lotteryDateList|活动日期集合|List<Object>|必填
     */
    public void selectDate() {
        List<String> lotteryDateList = LotteryService.me().selectDate();
        renderJson(lotteryDateList);
    }

    /**
     * @param lotteryId|活动ID|int|必填
     * @title 获取活动商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam lotteryId|活动ID|int|必填
     * @respParam lotteryGoodId|活动商品ID|int|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam title|商品活动标题|String|必填
     * @respParam lotteryPrice|活动抽奖价|decimal|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam shopPrice|商品原价|decimal|必填
     * @respParam sessionNum|活动场次|int|必填
     * @respParam isMax|参与人数是否已达上限 1:是、2:不是|int|必填
     * @respParam isSellOut|是否已售罄 1:是、2:不是|int|必填
     */
    public void goodList() {
        Integer lotteryId = getParaToInt("lotteryId", 0);
        List<Record> lotteryList = LotteryService.me().goodList(lotteryId);
        Record record = new Record();
        record.set("list", lotteryList);
        renderJson(record);
    }

    /**
     * @param lotteryGoodId|商品活动ID|int|必填
     * @param lotteryBuyNum|已抢购数量|int|必填
     * @title 获取活动参与人数
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam headPic|用户头像|String|必填
     * @respParam mobile|用户手机|String|必填
     * @respParam userId|用户ID|int|必填
     */
    public void attendLotteryList() {
        Integer lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        Integer lotteryBuyNum = getParaToInt("lotteryBuyNum", 0);
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("请先登录，登录状态下方可参看");
        }
        List<Record> attendLotteryList = LotteryService.me().attendLotteryList(lotteryGoodId, lotteryBuyNum);
        renderJson(attendLotteryList);
    }

    /**
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 获取用户抽奖活动列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goodId|商品ID|int|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam orderId|订单编号|String|必填
     * @respParam isTimeout|当前时间是否超过活动结束时间 1：超过 2：还没超过|int|必填
     * @respParam hasUserWin|是否有人中奖 1：有 2：没有|int|必填
     * @respParam isWin|是否中奖 1：中奖、2：未中|int|必填
     * @respParam lotteryGoodId|商品活动ID|int|必填
     * @respParam openTime|开奖时间|String|必填
     * @respParam title|商品活动标题|String|必填
     * @respParam lotteryOrderId|抽奖活动订单ID|int|必填
     */
    public void userLotteryList() {
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
            return;
        }
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = LotteryService.me().pageUserLotteryList(pageNumber, pageSize, userId);
        renderJson(pageList);
    }

    /**
     * @param lotteryGoodId|一元抢购活动商品ID|int|必填
     * @param activityNum|一元抢购活动商品场次|int|必填
     * @title 获取中奖名单
     * @respParam headPic|中奖用户头像|String|必填
     * @respParam mobile|中奖用户手机号码|String|必填
     * @respParam title|中奖商品标题|String|必填
     * @respParam shopPrice|中奖商品价格|double|必填
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void whoWin() {
        int lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        int activityNum = getParaToInt("activityNum", 0);
        Record result = LotteryService.me().whoWin(lotteryGoodId, activityNum);
        renderJson(result);
    }

    /**
     * @param lotteryOrderId|一元抢购订单ID|int|必填
     * @param lotteryGoodId|一元抢购商品ID|int|必填
     * @param activityNum|一元抢购商品活动场次|int|必填
     * @title 抽奖详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goodId|商品ID|int|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam orderId|订单编号|String|必填
     * @respParam isTimeout|当前时间是否超过活动结束时间 1：超过 2：还没超过|int|必填
     * @respParam hasUserWin|是否有人中奖 1：有 2：没有|int|必填
     * @respParam isWin|是否中奖 1：中奖、2：未中|int|必填
     * @respParam lotteryGoodId|商品活动ID|int|必填
     * @respParam endTime|开奖时间|String|必填
     * @respParam title|商品活动标题|String|必填
     * @respParam lotteryOrderId|抽奖活动订单ID|int|必填
     * @respParam userImgList|用户信息集合|List<Object>|必填
     * @respParam headPic|用户头像|String|必填
     * @respParam luckyUserHead|幸运用户头像|String|必填
     * @respParam luckyUserGoodInfo|幸运用户抽中的商品名|String|必填
     * @respParam moreLotteryGood|更多抽奖商品|List<Object>|必填
     * @respParam lotteryId|活动ID|int|必填
     * @respParam lotteryGoodId|活动商品ID|int|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam title|商品活动标题|String|必填
     * @respParam lotteryPrice|活动抽奖价|decimal|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam shopPrice|商品原价|decimal|必填
     * @respParam sessionNum|活动场次|int|必填
     * @respParam isMax|参与人数是否已达上限 1:是、2:不是|int|必填
     * @respParam isSellOut|是否已售罄 1:是、2:不是|int|必填
     */
    public void lotteryDetail() {
        Integer lotteryOrderId = getParaToInt("lotteryOrderId", 0);
        Integer lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        Integer activityNum = getParaToInt("activityNum", 0);
        Record result = LotteryService.me().lotteryDetail(lotteryOrderId, lotteryGoodId, activityNum);
        // 获取更多一元抢购抽奖商品
        List<Record> lotteryList = LotteryService.me().goodList(result.getInt("lotteryId"));
        result.set("moreLotteryGood", lotteryList);
        // 获取幸运儿信息(#非随机)
        Record luckyUserRecord = LotteryService.me().luckyUserInfo(lotteryGoodId, activityNum);
        if (luckyUserRecord != null) {
            result.set("luckyUserHead", luckyUserRecord.getStr("luckyUserHead"));
            result.set("luckyUserGoodInfo", luckyUserRecord.getStr("luckyUserGoodInfo"));
        } else {
            result.set("luckyUserHead", "");
            result.set("luckyUserGoodInfo", "");
        }
        renderJson(result);
    }
}
