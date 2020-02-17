package com.qw.controller.web.good;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import cn.qw.render.PoiRender;
import com.qw.interceptor.ResubmitInterceptor;
import com.qw.model.Good;
import com.qw.model.GoodSku;
import com.qw.service.bakend.good.GoodService;
import com.qw.vo.good.GoodFormVo;
import com.qw.vo.good.GoodSkuVo;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理
 */
@RequiresAuthentication
@RequiresPermissions("商品管理")
public class GoodController extends RestController {

    /**
     * @title 后台商品管理列表页
     */
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        String goodName = getPara("goodName");
        String storeName = getPara("storeName");
        String brandName = getPara("brandName");
        String catName = getPara("catName");
        Integer saleStatus = getParaToInt("saleStatus", null);

        Page<Record> pageList = GoodService.me().pageList(pageNumber, pageSize, goodName, storeName, brandName, catName, saleStatus);
        renderJson(pageList);
    }

    /**
     * 商品下载
     */
    public void download() {
        String goodName = getPara("goodName");
        String storeName = getPara("storeName");
        String brandName = getPara("brandName");
        String catName = getPara("catName");
        Integer saleStatus = getParaToInt("saleStatus", null);

        Page<Record> pageList = GoodService.me().pageList(1, Integer.MAX_VALUE, goodName, storeName, brandName, catName, saleStatus);
        List<Record> list = pageList.getList();
        for (Record record : list) {
            //0待审核1审核通过2审核失败
            Integer goodState = record.getInt("goodState");
            record.remove("goodState");
            if (0 == goodState) {
                record.set("goodState", "待审核");
            } else if (1 == goodState) {
                record.set("goodState", "审核通过");
            } else if (2 == goodState) {
                record.set("goodState", "审核失败");
            } else {
                record.set("goodState", "状态异常");
            }
            //0下架1上架2违规下架
            Integer saleSta = record.getInt("saleStatus");
            record.remove("saleStatus");
            if (0 == saleSta) {
                record.set("saleStatus", "下架");
            } else if (1 == saleSta) {
                record.set("saleStatus", "上架");
            } else if (2 == saleSta) {
                record.set("saleStatus", "违规下架");
            } else {
                record.set("saleStatus", "状态异常");
            }
        }
        String[] headers = new String[]{"商品名称", "SPU", "店铺价格", "市场价格", "所属店铺", "商品状态", "上架状态", "上架时间", "商品品牌", "积分抵扣金额"};
        String[] columns = new String[]{"goodName", "goodSn", "shopPrice", "marketPrice", "storeName", "goodState", "saleStatus", "onTime", "brandName", "point_as_money"};

        render(PoiRender.me(list).fileName(encodingFileName(DateKit.dateToString(new Date(), "yyyy年MM月dd日") + "商品信息汇总表.xls")).headers(headers).columns(columns));
    }

    /**
     * @param goodName|商品名称|String|必填
     * @title 商品模糊查询
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goodId|商品ID|int|必填
     * @respParam name|商品名称|String|必填
     */
    public void search() {
        String goodName = getPara("goodName");
        List<Record> list = GoodService.me().search(goodName);
        list = list.stream().map(r -> r.remove("subName")).collect(Collectors.toList());
        renderJson(list);
    }

    public void skuPage() {
        long goodId = getParaToLong("goodId", 0L);
        if (goodId == 0) {
            renderParamError("商品呢ID不能为空");
            return;
        }
        List<GoodSku> skus = GoodService.me().spec(goodId);
        for (GoodSku s : skus) {
            String specImg = s.getSpecImg();
            if (StrKit.notBlank(specImg)) {
                s.setSpecImg(PropKit.get("fileHost") + specImg);
            }
        }
        renderJson(skus);
    }

    /**
     * @param goodId|商品ID|int|必填
     * @title 获取商品详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam goodId|商品ID|int|必填
     * @respParam name|商品名称|String|必填
     * @respParam imgPath|商品名称|String|必填
     * @respParam goodSpecList|商品所有规格|List<Object>|必填
     * @respParam specName|规格名称|String|必填
     * @respParam specList|具体规格|List<Object>|必填
     * @respParam itemId|规格ID|int|必填
     * @respParam itemName|具体规格名称|String|必填
     * @respParam specGoodPrice|规格SKU组合|List<Object>|必填
     * @respParam specConstituteId|规格SKU ID|int|必填
     * @respParam specConstitutePrice|规格SKU对应价格|String|必填
     * @respParam specImg|规格SKU对应图片|String|必填
     * @respParam specItemIdConstitute|规格itemId组合|String|必填
     */
    public void simpleDetail() {
        int goodId = getParaToInt("goodId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品信息不存在");
            return;
        }
        List<Record> specGoodPrice = com.qw.service.frontend.good.GoodService.me().getSpecConstituteByGoodId(goodId);
        List<Record> goodSpecList = com.qw.service.frontend.good.GoodService.me().getSpecByGoodId(goodId);

        Record record = new Record();
        record.set("goodId", good.getGoodsId());
        record.set("name", good.getGoodsName());
        record.set("imgPath", PropKit.get("fileHost") + good.getOriginalImg());
        record.set("specGoodPrice", specGoodPrice);
        record.set("goodSpecList", goodSpecList);

        renderJson(record);
    }

    public void detail() {
        int goodId = getParaToInt("goodId", 0);
        Good good = Good.dao.findById(goodId);
        if (good == null) {
            renderParamNull("商品信息不存在");
            return;
        }

        GoodFormVo vo = new GoodFormVo();
        // 商品详情
        vo.setGoodsId(good.getGoodsId());
        vo.setStoreId(good.getStoreId());
        List<Integer> tmp = new ArrayList<>();
        tmp.add(good.getCatId1());
        if (good.getCatId2() != null && good.getCatId2() != 0) {
            tmp.add(good.getCatId2());
        }
        if (good.getCatId3() != null && good.getCatId3() != 0) {
            tmp.add(good.getCatId3());
        }
        vo.setCatId(tmp.toArray(new Integer[tmp.size()]));
        vo.setGoodsSn(good.getGoodsSn());
        vo.setBrandId(good.getBrandId());
        vo.setGoodsName(good.getGoodsName());
        vo.setGoodsRemark(good.getGoodsRemark());
        vo.setKeywords(good.getKeywords());
        vo.setOriginalImg(good.getOriginalImg());
        vo.setOriginalImgShow(PropKit.get("fileHost") + good.getOriginalImg());
        vo.setDoorServiceStatus(good.getDoorServiceStatus());
        vo.setIsOnSale(good.getIsOnSale());
        vo.setIsRecommend(good.getIsRecommend());
        vo.setIsHot(good.getIsHot());
        vo.setIsFreeShipping(good.getIsFreeShipping());
        vo.setPointAsMoney(good.getPointAsMoney());
        vo.setIsEarnPoint(good.getIsEarnPoint());
        vo.setGiveIntegral(good.getGiveIntegral());
        vo.setDetailSaveImgList(GoodService.me().getContent(good.getGoodsContent()));
        vo.setImgAlbum(GoodService.me().gallery(good));
        vo.setSort(good.getSort());
        // TODO 以后加
        vo.setStoreCountWorn(0);
        // 规格列表
        List<GoodSku> spec = GoodService.me().spec(good.getGoodsId());
        vo.setSkuData(spec.stream().map(s -> {
            GoodSkuVo skuVo = new GoodSkuVo();
            skuVo.setSkuComputerCode(s.getKey());
            skuVo.setShopPrice(s.getPrice());
            skuVo.setSupplyPrice(s.getSupplyPrice());
            skuVo.setMarketPrice(s.getMarketPrice());
            skuVo.setStoreCount(new Long(s.getStoreCount()));
            skuVo.setSaveImgPath(s.getSpecImg());
            skuVo.setShowImgPath(PropKit.get("fileHost") + s.getSpecImg());
            skuVo.setDisAbled(s.getDisAbled());
            return skuVo;
        }).collect(Collectors.toList()));
        renderJson(vo);
    }

    /**
     * 保存
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void save() {
        // 获取所有数据
        GoodFormVo goodForm = getVo(GoodFormVo.class);
        // 参数校验
        if (goodForm.getStoreId() == null || goodForm.getStoreId() == 0) {
            renderParamNull("店铺ID不能为空");
            return;
        }
        if (goodForm.getCatId() == null || goodForm.getCatId().length <= 0) {
            renderParamNull("分类不能为空");
            return;
        }
        if (goodForm.getSkuData() == null || goodForm.getSkuData().size() <= 0) {
            renderParamNull("SKU不能为空");
            return;
        }
        boolean result = GoodService.me().saveOrUpdate(goodForm);
        if (result) {
            renderSuccess("保存成功！");
            return;
        }
        renderOperateError("保存失败！");
    }

    /**
     * 批量上下架
     */
    public void putOffSale() {
        List<Integer> goodIdList = getList(Integer.class);
        GoodService.me().putOffSale(goodIdList);
        renderSuccess("下架成功");
    }

    /**
     * 上架/下架
     */
    public void onOffSale() {
        Integer id = getParaToInt("id", 0);
        Integer isOnSale = getParaToInt("isOnSale", 0);
        if (isOnSale != 0 && isOnSale != 1) {
            renderParamError("参数错误，状态数据异常");
            return;
        }
        Good good = Good.dao.findById(id);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        good.setIsOnSale(isOnSale);
        good.setOnTime(System.currentTimeMillis() / 1000);
        good.update(false);
        renderSuccess("保存成功！");
    }

    /**
     * 推荐设置
     */
    public void recommend() {
        Integer id = getParaToInt("id", 0);
        Integer isRecommend = getParaToInt("isRecommend", 0);
        if (isRecommend != 0 && isRecommend != 1) {
            renderParamError("参数错误，状态数据异常");
            return;
        }
        Good good = Good.dao.findById(id);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        good.setIsRecommend(isRecommend);
        good.update(false);
        renderSuccess("保存成功！");
    }

    /**
     * 新品设置
     */
    public void newSave() {
        Integer id = getParaToInt("id", 0);
        Long isOnSale = getParaToLong("is_new", 0L);
        if (isOnSale != 0 && isOnSale != 1) {
            renderParamError("参数错误，状态数据异常");
            return;
        }
        Good good = Good.dao.findById(id);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        good.setIsNew(isOnSale);
        good.update(false);
        renderSuccess("保存成功！");
    }

    /**
     * 热卖设置
     */
    public void hotSave() {
        Integer id = getParaToInt("id", 0);
        Integer isOnSale = getParaToInt("is_hot", 0);
        if (isOnSale != 0 && isOnSale != 1) {
            renderParamError("参数错误，状态数据异常");
            return;
        }
        Good good = Good.dao.findById(id);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        good.setIsHot(isOnSale);
        good.update(false);
        renderSuccess("保存成功！");
    }

    /**
     * 设施是否赚取积分
     */
    public void earnPoint() {
        Integer id = getParaToInt("id", 0);
        Integer is_earn_point = getParaToInt("is_earn_point", 0);
        if (is_earn_point != 0 && is_earn_point != 1) {
            renderParamError("参数错误，状态数据异常");
            return;
        }
        Good good = Good.dao.findById(id);
        if (good == null) {
            renderParamNull("商品ID不能为空");
            return;
        }
        good.setIsEarnPoint(is_earn_point);
        good.update(false);
        renderSuccess("保存成功！");
    }

    /**
     * 删除
     */
    @Before(Tx.class)
    public void delete() {
        Long id = getParaToLong("id", 0L);
        if (id == 0) {
            renderParamNull("参数ID不能为空");
            return;
        }
        boolean result = GoodService.me().delete(id);
        if (result) {
            renderSuccess("删除成功");
            return;
        }
        renderOperateError("删除失败");
    }
}
