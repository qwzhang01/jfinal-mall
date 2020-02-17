package com.qw.controller.app.good;

import cn.qw.base.RestController;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.frontend.prom.FlashSaleService;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * 秒杀商品
 */
public class FlashController extends RestController {

    /**
     * @title 获取秒杀时间
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam flashId|活动ID|int|必填
     * @respParam font|显示|String|必填
     * @respParam start_time|开始时间戳|long|必填
     * @respParam end_time|结束时间戳|long|必填
     * @respParam number|1进行中 2未开始|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void list() {
        List<Record> list = FlashSaleService.me().flashTime();
        renderJson(list);
    }

    /**
     * @param flashId|活动ID|int|必填
     * @title 获取首页秒杀商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam flashGoodId|秒杀商品ID|int|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam skuId|商品规格ID|int|必填
     * @respParam goods_name|商品名称|String|必填
     * @respParam shop_price|店铺价格|int|必填
     * @respParam price|秒杀价格|decimal|必填
     * @respParam percent|已经购买的百分比|int|必填
     * @respParam imgPath|秒杀图片地址|String|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void homeGood() {
        int flashId = getParaToInt("flashId", 0);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 3);
        Page<Record> page = FlashSaleService.me().goodPageList(flashId, pageNumber, pageSize);
        renderJson(page.getList());
    }

    /**
     * @param flashId|活动ID|int|必填
     * @title 获取秒杀商品列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam flashGoodId|秒杀商品ID|int|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam skuId|商品规格ID|int|必填
     * @respParam goods_name|商品名称|String|必填
     * @respParam shop_price|店铺价格|int|必填
     * @respParam price|秒杀价格|decimal|必填
     * @respParam percent|已经购买的百分比|int|必填
     * @respParam imgPath|秒杀图片地址|String|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void goodPage() {
        int flashId = getParaToInt("flashId", 0);
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        if(flashId == 0){
            renderParamError("参数错误，秒杀活动不存在");
            return;
        }
        Page<Record> page = FlashSaleService.me().goodPageList(flashId, pageNumber, pageSize);
        renderJson(page);
    }
}