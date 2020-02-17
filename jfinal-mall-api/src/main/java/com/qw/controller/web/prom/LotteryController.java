package com.qw.controller.web.prom;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import com.qw.interceptor.ResubmitInterceptor;
import com.qw.model.Good;
import com.qw.model.GoodSku;
import com.qw.model.Lottery;
import com.qw.model.LotteryGood;
import com.qw.service.bakend.prom.LotteryService;
import com.qw.service.common.ConfigService;
import com.qw.service.frontend.good.GoodService;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抢购抽奖
 */
@RequiresAuthentication
public class LotteryController extends RestController {

    /**
     * @title 获取抢购抽奖设置信息
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam isShow|首页是否显示抢购抽奖|boolean|必填
     * @respParam homeImg|首页图片|String|必填
     * @respParam banner|活动页banner图片|String|必填
     * @respParam regulation|规则说明图片|String|必填
     */
    public void getSet() {

        Map<String, String> lotterySet = ConfigService.me().findMap("LotterySet");

        // true、false
        String isShow = lotterySet.get("lottery_is_show");
        String homeImg = lotterySet.get("lottery_home_img");
        // String homeGood = lotterySet.get("lottery_home_good");
        String banner = lotterySet.get("lottery_banner");
        // String bannerGood = lotterySet.get("lottery_banner_good");
        String regulation = lotterySet.get("lottery_regulation");

        Record result = new Record();
        result.set("isShow", Boolean.parseBoolean(isShow));
        result.set("homeImg", new Record() {{
            set("showPath", PropKit.get("fileHost") + homeImg);
            set("savePath", homeImg);
        }});
        result.set("banner", new Record() {{
            set("showPath", PropKit.get("fileHost") + banner);
            set("savePath", banner);
        }});
        result.set("regulation", new Record() {{
            set("showPath", PropKit.get("fileHost") + regulation);
            set("savePath", regulation);
        }});

        renderJson(result);
    }

    /**
     * @param isShow|是否显示（true/false）|boolean|必填
     * @title 设置首页是否显示
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void setShow() {
        Boolean isShow = getParaToBoolean("isShow", false);
        boolean update = ConfigService.me().update("LotterySet", "lottery_is_show", isShow.toString());
        if (update) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * @param homeImg|首页图片地址|String|必填
     * @title 设置首页图片
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void setHomeImg() {
        String homeImg = getPara("homeImg");
        if (StrKit.isBlank(homeImg)) {
            renderParamNull("图片地址不能为空");
            return;
        }
        boolean update = ConfigService.me().update("LotterySet", "lottery_home_img", homeImg);
        if (update) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * @param banner|banner图片|String|必填
     * @title 设置活动banner图片
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void setBanner() {
        String banner = getPara("banner");
        if (StrKit.isBlank(banner)) {
            renderParamNull("图片地址不能为空");
            return;
        }
        boolean update = ConfigService.me().update("LotterySet", "lottery_banner", banner);
        if (update) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * @param regulation|规则图片|String|必填
     * @title 设置活动规则图片
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void setRegulation() {
        String regulation = getPara("regulation");
        if (StrKit.isBlank(regulation)) {
            renderParamNull("图片地址不能为空");
            return;
        }
        boolean update = ConfigService.me().update("LotterySet", "lottery_regulation", regulation);
        if (update) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * @param type|1即将开始               2进行中 3活动结束|int|必填
     * @param title|活动名称|String|必填
     * @param startTime|搜索开始时间|time|必填
     * @param endTime|搜索结束时间|time|必填
     * @title 获取抢购抽奖分页数据
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|主键|int|必填
     * @respParam title|活动名称|String|必填
     * @respParam startTime|开始时间|String|必填
     * @respParam endTime|结束时间|String|必填
     * @respParam maxNum|参团最大人数限制|int|必填
     * @respParam minNum|开团最小人数限制|String|必填
     * @respParam type|开始方式1 定时开、2 人满开|int|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    public void page() {
        int type = getParaToInt("type", 0);
        if (type != 1 && type != 2 && type != 3) {
            renderParamError("type只能是1 2 3");
            return;
        }
        String title = getPara("title");
        String beginTimeStr = getPara("startTime");
        Date beginTime = null;
        if (StrKit.notBlank(beginTimeStr)) {
            beginTime = DateKit.stringtoDate(beginTimeStr, "yyyy-MM-dd");
        }
        String endTimeStr = getPara("endTime");
        Date endTime = null;
        if (StrKit.notBlank(endTimeStr)) {
            endTime = DateKit.stringtoDate(endTimeStr, "yyyy-MM-dd");
        }

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> page = LotteryService.me().pageList(pageNumber, pageSize, type, title, beginTime, endTime);
        renderJson(page);
    }

    /**
     * @param id|主键，编辑时填写|int|非必填
     * @param title|活动名称|String|必填
     * @param maxNum|最大参团人数|int|必填
     * @param minNum|最小开团人数|String|必填
     * @param type|1定时开2人满开|int|必填
     * @param startTime|搜索开始时间|time|必填
     * @param endTime|搜索结束时间|time|必填
     * @title 新增/编辑抢购抽奖活动
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void form() {
        int lotteryId = getParaToInt("id", 0);
        int type = getParaToInt("type", 0);
        int maxNum = getParaToInt("maxNum", 0);
        int minNum = getParaToInt("minNum", 0);
        String title = getPara("title");
        String startTimeStr = getPara("startTime");
        String endTimeStr = getPara("endTime");
        if (!StrKit.notBlank(title, startTimeStr, endTimeStr)) {
            renderParamNull("所有参数不能为空");
            return;
        }
        if (type == 0 || maxNum == 0 || minNum == 0) {
            renderParamNull("所有参数不能为空");
            return;
        }
        Date startTime = DateKit.stringtoDate(startTimeStr, "yyyy-MM-dd HH:mm:ss");
        Date endTime = DateKit.stringtoDate(endTimeStr, "yyyy-MM-dd HH:mm:ss");
        boolean form = LotteryService.me().form(lotteryId, title, type, maxNum, minNum, startTime, endTime);
        if (form) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param lotteryId|活动ID|int|非必填
     * @title 获取抢购抽奖活动的商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam lotteryGoodId|活动商品ID|int|必填
     * @respParam goodsId|商品ID|int|必填
     * @respParam title|商品标题|String|必填
     * @respParam imgPath|图片地址|String|必填
     * @respParam price|活动价格|String|必填
     * @respParam num|商品数量|int|必填
     * @respParam buyNum|成交数量|int|必填
     * @respParam fakeAttendCount|虚假购买数量|String|必填
     * @respParam status|1待审核，2正常，3审核拒绝，4下架|String|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    public void good() {
        Lottery flash = Lottery.dao.findById(getParaToInt("lotteryId", 0));
        if (flash == null) {
            renderParamNull("活动ID不存在");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        String goodsName = getPara("goodsName");
        renderJson(LotteryService.me().goodList(pageNumber, pageSize, flash, goodsName));
    }

    /**
     * @param sortId|排序商品ID（id，id）|String|必填
     * @title 商品排序
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class})
    public void sort() {
        String sortId = getPara("sortId");
        if (StrKit.isBlank(sortId)) {
            renderParamNull("参数不能为空");
            return;
        }
        if (!sortId.contains(",")) {
            renderParamError("参数格式有误");
            return;
        }
        String[] split = sortId.split(",");
        if (split.length != 2) {
            renderParamError("参数格式错误");
            return;
        }
        boolean sort = LotteryService.me().sort(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        if (sort) {
            renderSuccess("排序成功");
        } else {
            renderOperateError("排序失败");
        }
    }

    /**
     * @param lotteryId|活动ID|int|必填
     * @param goodId|商品ID|int|必填
     * @param specId|商品规格ID|int|非必填
     * @param title|商品名称|int|必填
     * @param price|商品价格|int|必填
     * @param goodNum|商品数量|int|必填
     * @param imgPath|图片地址|int|必填
     * @title 添加商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({ResubmitInterceptor.class})
    public void addGood() {
        Lottery flash = Lottery.dao.findById(getParaToInt("lotteryId", 0));
        if (flash == null) {
            renderParamNull("活动ID不存在");
            return;
        }

        int goodId = getParaToInt("goodId", 0);
        int specId = getParaToInt("specId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品不存在");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品不是上架状态");
            return;
        }
        if (specId == 0) {
            GoodSku sku = GoodService.me().getGoodBySmallPrice(new Long(String.valueOf(goodId)));
            if (sku == null) {
                renderParamError("数据异常，产品数据错误");
                return;
            }
            specId = sku.getItemId().intValue();
        }
        String title = getPara("title");
        BigDecimal price = getParaToDecimal("price");
        int goodNum = getParaToInt("goodNum", 0);
        int buyNum = getParaToInt("buyNum", 0);
        String imgPath = getPara("imgPath");
        if (goodId == 0 || StrKit.isBlank(title) || price.compareTo(new BigDecimal("0")) <= 0 || goodNum <= 0 || buyNum <= 0 || StrKit.isBlank(imgPath)) {
            renderParamNull("必填参数不能为空");
            return;
        }
        LotteryGood lotteryGood = new LotteryGood();
        lotteryGood.setSpecId(specId);
        lotteryGood.setTitle(title);
        lotteryGood.setImgPath(imgPath);
        lotteryGood.setPrice(price);
        lotteryGood.setNum(goodNum);
        lotteryGood.setBuyNum(0);
        lotteryGood.setGoodId(goodId);

        boolean addGood = LotteryService.me().addGood(lotteryGood, flash);

        if (addGood) {
            renderSuccess("添加成功");
        } else {
            renderOperateError("添加失败");
        }
    }

    /**
     * @param lotteryGoodId|活动ID|int|必填
     * @title 删除商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class})
    public void delGood() {
        LotteryGood lotteryGood = LotteryGood.dao.findById(getParaToInt("lotteryGoodId", 0));
        if (lotteryGood == null) {
            renderParamNull("活动商品ID不存在");
            return;
        }
        Lottery lottery = Lottery.dao.findById(lotteryGood.getLotteryId());
        if (lottery == null) {
            renderParamNull("活动不存在");
            return;
        }
        if (new Date().compareTo(lottery.getStartTime()) >= 0) {
            renderParamError("已经开始的活动，商品无法删除");
            return;
        }
        if (lotteryGood.delete()) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }

    /**
     * @param lotteryGoodId|活动商品ID|int|必填
     * @title 上架
     */
    @Before({Tx.class})
    public void onSale() {
        LotteryGood flashSale = LotteryGood.dao.findById(getParaToInt("lotteryGoodId", 0));
        if (flashSale == null) {
            renderParamNull("商品ID不存在");
            return;
        }
        Integer end = flashSale.getStatus();
        if (2 == end) {
            renderParamError("上架状态的商品无法重复上架");
            return;
        }
        // 1待审核，2正常，3审核拒绝，4下架商品
        flashSale.setStatus(2);
        if (flashSale.update(false)) {
            renderSuccess("上架成功");
        } else {
            renderOperateError("上架失败");
        }
    }

    /**
     * @param lotteryGoodId|活动商品ID|int|必填
     * @title 下架
     */
    @Before({Tx.class})
    public void offSale() {
        LotteryGood flashSale = LotteryGood.dao.findById(getParaToInt("lotteryGoodId", 0));
        if (flashSale == null) {
            renderParamNull("商品ID不存在");
            return;
        }
        // 是否已结束 0正常 1结束
        Integer end = flashSale.getStatus();
        if (4 == end) {
            renderParamError("下架状态的商品无法重复下架");
            return;
        }
        flashSale.setStatus(4);
        if (flashSale.update(false)) {
            renderSuccess("下架成功");
        } else {
            renderOperateError("下架失败");
        }
    }

    /**
     * @param lotteryGoodId|商品ID逗号拼接|String|必填
     * @title 批量下架
     */
    @Before({Tx.class})
    public void offSaleBatch() {
        String lotteryGoodId = getPara("lotteryGoodId");
        if (StrKit.isBlank(lotteryGoodId)) {
            renderParamNull("lotteryGoodId 不能为空");
            return;
        }
        List<Integer> flashGoodIds = Arrays.stream(lotteryGoodId.split(",")).filter(f -> StrKit.notBlank(f)).map(f -> Integer.valueOf(f)).collect(Collectors.toList());
        boolean offSale = LotteryService.me().offSale(flashGoodIds);
        if (offSale) {
            renderSuccess("下架成功");
        } else {
            renderOperateError("下架失败");
        }
    }
}