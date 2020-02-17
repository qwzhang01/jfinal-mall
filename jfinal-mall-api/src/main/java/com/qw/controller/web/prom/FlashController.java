package com.qw.controller.web.prom;

import cn.qw.base.RestController;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.qw.interceptor.ResubmitInterceptor;
import com.qw.model.FlashInfo;
import com.qw.model.FlashSale;
import com.qw.model.Good;
import com.qw.model.GoodSku;
import com.qw.service.bakend.prom.FlashSaleService;
import com.qw.service.frontend.good.GoodService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀管理
 */
@RequiresAuthentication
public class FlashController extends RestController {

    /**
     * @param type|1即将开始2秒杀中               3结束秒杀|int|必填
     * @param name|活动名称|String|必填
     * @param beginTime|搜索开始时间|datetime|必填
     * @param endTime|搜索结束时间|datetime|必填
     * @title 获取秒杀分页数据
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|主键|int|必填
     * @respParam name|活动名称|String|必填
     * @respParam buyLimit|购买限制|int|必填
     * @respParam beginTime|开始时间|String|必填
     * @respParam endTime|结束时间|String|必填
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
        String name = getPara("name");
//        Date beginTime = getParaToDate("beginTime");
//        Date endTime = getParaToDate("endTime");

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> page = FlashSaleService.me().pageList(pageNumber, pageSize, type, name);
        renderJson(page);
    }

    /**
     * @param id|主键，编辑时填写|int|非必填
     * @param name|活动名称|String|必填
     * @param beginTime|秒杀开始时间|datetime|必填
     * @param endTime|秒杀结束时间|datetime|必填
     * @param buyLimit|每人购买限制|int|必填
     * @title 新增/编辑秒杀活动
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void save() {
        // 获取参数
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        Date beginTime = getParaToDate("beginTime");
        Date endTime = getParaToDate("endTime");
        int buyLimit = getParaToInt("buyLimit", 0);
        // 校验参数
        if (!StrKit.notBlank(name)) {
            renderParamNull("活动名称不能为空");
            return;
        }
        if (beginTime == null || endTime == null) {
            renderParamError("活动时间不能为空");
            return;
        }
        if (new Date().compareTo(beginTime) >= 0) {
            renderParamError("开始时间不能小于当前时间");
            return;
        }
        FlashInfo flash = new FlashInfo();
        flash.setName(name);
        flash.setBeginTime(beginTime);
        flash.setEndTime(endTime);
        flash.setBuyLimit(buyLimit);
        if (id != 0) {
            flash.setId(id);
        }
        if (flash.saveOrUpdate(false)) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    /**
     * @param id|主键，编辑时填写|int|非必填
     * @param name|活动名称|String|必填
     * @param beginTime|秒杀开始时间|datetime|必填
     * @param endTime|秒杀结束时间|datetime|必填
     * @param buyLimit|每人购买限制|int|必填
     * @title 新增/编辑秒杀活动
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void copyFlash() {
        int id = getParaToInt("id", 0);
        String name = getPara("name");
        Date beginTime = getParaToDate("beginTime");
        Date endTime = getParaToDate("endTime");
        int buyLimit = getParaToInt("buyLimit", 0);
        // 校验参数
        if (!StrKit.notBlank(name)) {
            renderParamNull("活动名称不能为空");
            return;
        }
        if (beginTime == null || endTime == null) {
            renderParamError("活动时间不能为空");
            return;
        }
        if (new Date().compareTo(beginTime) >= 0) {
            renderParamError("开始时间不能小于当前时间");
            return;
        }
        FlashInfo flash = new FlashInfo();
        flash.setName(name);
        flash.setBeginTime(beginTime);
        flash.setEndTime(endTime);
        flash.setBuyLimit(buyLimit);
        flash.saveOrUpdate(false);

        FlashSaleService.me().addGood(flash, id);
        renderSuccess("复制成功");
    }

    /**
     * @param flashId|活动ID|int|非必填
     * @title 删除秒杀活动
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void delete() {
        int flashId = getParaToInt("flashId", 0);
        FlashInfo flashInfo = FlashInfo.dao.findById(flashId);
        if (flashInfo == null) {
            renderParamError("秒杀不存在");
            return;
        }
        Page<Record> page = FlashSaleService.me().goodList(1, 1, flashInfo, "");
        if (page.getList() != null && page.getList().size() > 0) {
            renderParamError("无法删除商品不为空的秒杀活动");
            return;
        }
        boolean delete = flashInfo.delete();
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderParamError("删除失败");
        }
    }

    /**
     * @param flashId|活动ID|int|非必填
     * @title 同步缓存数据
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void synchronization() {
        int flashId = getParaToInt("flashId", 0);
        FlashInfo flashInfo = FlashInfo.dao.findById(flashId);
        if (flashInfo == null) {
            renderParamError("秒杀不存在");
            return;
        }
        if (new Date().compareTo(flashInfo.getBeginTime()) >= 0) {
            renderParamError("开始的秒杀无法同步");
            return;
        }
        FlashSaleService.me().synchronization(flashInfo);
        renderSuccess("同步成功");
    }

    /**
     * @param flashId|活动ID|int|非必填
     * @title 获取秒杀活动的商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam flashId|活动ID|String|必填
     * @respParam goodsId|商品ID|int|必填
     * @respParam title|商品标题|String|必填
     * @respParam imgPath|图片地址|String|必填
     * @respParam price|秒杀价格|String|必填
     * @respParam goodNum|商品数量|int|必填
     * @respParam buyNum|成交数量|String|必填
     * @respParam buyLimit|购买限量|String|必填
     * @respParam fakeBuyCount|虚假购买数量|String|必填
     * @respParam saleStatus|活动状态（0正常1结束）|String|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    public void good() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Integer flashId = getParaToInt("flashId", 0);
        FlashInfo flash = FlashInfo.dao.findById(flashId);
        if (flash == null) {
            renderParamNull("秒杀活动ID不存在");
            return;
        }
        String goodsName = getPara("goodsName");
        renderJson(FlashSaleService.me().goodList(pageNumber, pageSize, flash, goodsName));
    }

    /**
     * @param sortId|排序商品ID（id，id）|String|必填
     * @title 商品排序
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
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
        boolean sort = FlashSaleService.me().sort(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        if (sort) {
            renderSuccess("排序成功");
        } else {
            renderOperateError("排序失败");
        }
    }

    /**
     * @param flashId|活动ID|int|必填
     * @param goodId|秒杀商品ID|int|必填
     * @param specId|秒杀商品规格ID|int|非必填
     * @param title|秒杀商品名称|int|必填
     * @param price|秒杀商品价格|int|必填
     * @param goodNum|秒杀商品数量|int|必填
     * @param buyLimit|购买限制|int|必填
     * @param imgPath|图片地址|int|必填
     * @param fakeBuyNum|虚假购买数量|int|必填
     * @param point|积分|BigDecimal|必填
     * @param pointLimit|积分限制|BigDecimal|必填
     * @param isFake|1不是虚假2是虚假商品|int|必填
     * @title 添加商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void addGood() {
        // 获取参数
        int flashId = getParaToInt("flashId", 0);
        long goodId = getParaToLong("goodId", 0L);
        int specId = getParaToInt("specId", 0);
        BigDecimal price = getParaToDecimal("price");
        int goodNum = getParaToInt("goodNum", 0);
        int buyLimit = getParaToInt("buyLimit", 0);
        String imgPath = getPara("imgPath");
        int fakeBuyNum = getParaToInt("fakeBuyNum", 0);
        int isFake = getParaToInt("isFake", 0);
        BigDecimal point = getParaToDecimal("point");
        BigDecimal pointLimit = getParaToDecimal("pointLimit");
        // 参数校验
        FlashInfo flash = FlashInfo.dao.findById(flashId);
        if (flash == null) {
            renderParamNull("秒杀活动ID不存在");
            return;
        }
        if (point == null) {
            renderParamError("消费金额不能为空");
            return;
        }
        if (pointLimit == null) {
            renderParamError("积分限制不能为空");
            return;
        }
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
            GoodSku sku = GoodService.me().getGoodBySmallPrice(goodId);
            if (sku == null) {
                renderParamError("参数错误，规格ID不能为空");
                return;
            }
            specId = sku.getItemId().intValue();
        }

        if (price.compareTo(new BigDecimal("0")) <= 0 || goodNum <= 0 || buyLimit <= 0 || StrKit.isBlank(imgPath)) {
            renderParamNull("必填参数不能为空");
            return;
        }
        FlashSale sale = new FlashSale();
        sale.setGoodId(goodId);
        sale.setSkuId(specId);
        sale.setPrice(price);
        sale.setGoodNum(goodNum);
        sale.setBuyLimit(buyLimit);
        sale.setFakeBuyNum(fakeBuyNum);
        sale.setImgPath(imgPath);
        sale.setIsFake(isFake);
        sale.setPoint(point);
        sale.setPointLimit(pointLimit);
        boolean addGood = FlashSaleService.me().addGood(flash, sale);
        if (addGood) {
            renderSuccess("添加成功");
        } else {
            renderOperateError("添加失败");
        }
    }

    /**
     * @param flashGoodId|活动ID|int|必填
     * @title 删除商品
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class})
    public void delGood() {
        FlashSale flash = FlashSale.dao.findById(getParaToInt("flashGoodId", 0));
        if (flash == null) {
            renderParamNull("秒杀商品ID不存在");
            return;
        }
        FlashInfo info = FlashInfo.dao.findById(flash.getFlashId());
        if (info == null) {
            renderParamNull("参数错误，秒杀活动不存在");
            return;
        }
        if (new Date().compareTo(info.getBeginTime()) > 0) {
            renderParamError("已经开始的秒杀活动，商品无法删除");
            return;
        }
        if (flash.delete()) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @param fakeBuyNum|虚假购买数量|int|必填
     * @title 设置虚假购买数量
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void setFakeBuyNum() {
        FlashSale flashSale = FlashSale.dao.findById(getParaToInt("flashGoodId", 0));
        int fakeBuyNum = getParaToInt("fakeBuyNum", 0);
        if (flashSale == null) {
            renderParamNull("参数错误，商品不存在");
            return;
        }
        if (fakeBuyNum > flashSale.getGoodNum()) {
            renderParamError("参数错误，虚假购买数量不能大于总秒杀库存");
            return;
        }
        flashSale.setFakeBuyNum(fakeBuyNum);
        if (flashSale.saveOrUpdate(false)) {
            renderSuccess("设置成功");
        } else {
            renderOperateError("设置失败");
        }
    }

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @title 上架
     */
    @Before({Tx.class})
    public void onSale() {
        int flashGoodId = getParaToInt("flashGoodId", 0);
        FlashSale flashSale = FlashSale.dao.findById(flashGoodId);
        if (flashSale == null) {
            renderParamNull("秒杀商品ID不存在");
            return;
        }
        // 1上架2下架
        Integer end = flashSale.getOffSaleStatus();
        if (1 == end) {
            renderParamError("上架状态的秒杀商品无法重复上架");
            return;
        }
        // 1上架2下架
        flashSale.setOffSaleStatus(1);
        if (flashSale.update(false)) {
            renderSuccess("上架成功");
        } else {
            renderOperateError("上架失败");
        }
    }

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @title 下架
     */
    @Before({Tx.class})
    public void offSale() {
        int flashGoodId = getParaToInt("flashGoodId", 0);
        FlashSale flashSale = FlashSale.dao.findById(flashGoodId);
        if (flashSale == null) {
            renderParamNull("秒杀商品ID不存在");
            return;
        }
        // 是否已结束 0正常 1结束
        Integer end = flashSale.getOffSaleStatus();
        if (2 == end) {
            renderParamError("下架状态的秒杀商品无法重复下架");
            return;
        }
        flashSale.setOffSaleStatus(2);
        if (flashSale.update(false)) {
            renderSuccess("下架成功");
        } else {
            renderOperateError("下架失败");
        }
    }

    /**
     * @param flashGoodId|秒杀商品ID逗号拼接|String|必填
     * @title 批量下架
     */
    @Before({Tx.class})
    public void offSaleBatch() {
        String flashGoodId = getPara("flashGoodId");
        if (StrKit.isBlank(flashGoodId)) {
            renderParamNull("flashGoodId 不能为空");
            return;
        }
        List<Integer> flashGoodIds = Arrays.stream(flashGoodId.split(",")).filter(f -> StrKit.notBlank(f)).map(f -> Integer.valueOf(f)).collect(Collectors.toList());
        boolean offSale = FlashSaleService.me().offSale(flashGoodIds);
        if (offSale) {
            renderSuccess("下架成功");
        } else {
            renderOperateError("下架失败");
        }
    }
}