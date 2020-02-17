package com.qw.service.frontend.order;

import cn.qw.base.BaseService;
import com.qw.model.Order;
import com.qw.model.OrderGoods;
import com.qw.model.ReturnGoods;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReturnGoodsService extends BaseService {

    private static ReturnGoodsService service;

    private ReturnGoodsService() {
    }

    public static synchronized ReturnGoodsService me() {
        if (service == null) {
            service = new ReturnGoodsService();
        }
        return service;
    }

    public void add(Order order) {
        // 如果是已经发货，生成一个退换货记录 TODO
        Integer shippingStatus = order.getShippingStatus();
        if (shippingStatus != 2) {
            return;
        }
        // TODO
    }

    public ReturnGoods genModel(Order order, Integer goodId, String specKey, String reason, Integer goodsNum,
                                Integer type, String consignee, String mobile, String address, String describe, String[] imgs) {

        Map<String, Object> searchParam = searchParam("goods_id", goodId);
        searchParam.put("order_id", order.getOrderId());
        OrderGoods orderGood = OrderGoods.dao.searchFirst(searchParam);

        ReturnGoods returnGoods = new ReturnGoods();
        //封装对象属性
        returnGoods.setOrderId(order.getOrderId());
        returnGoods.setGoodsId(orderGood.getGoodsId().intValue());
        returnGoods.setSpecKey(specKey);
        returnGoods.setReason(reason);
        returnGoods.setGoodsNum(goodsNum);
        returnGoods.setType(type);
        returnGoods.setConsignee(consignee);
        returnGoods.setMobile(mobile);
        returnGoods.setAddress(address);
        returnGoods.setDescribe(describe);
        //处理String[]
        StringBuilder testImgs = new StringBuilder(Arrays.toString(imgs).replace("[", "").replace("]", ""));
        String imgStr = testImgs + "";
        returnGoods.setImgs(imgStr);

        //完善对象属性
        returnGoods.setOrderSn(order.getOrderSn());
        returnGoods.setRecId(orderGood.getRecId());
        returnGoods.setUserId(order.getUserId());
        returnGoods.setStoreId(order.getStoreId());
        returnGoods.setAddtime(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));    //售后申请时间
        returnGoods.setRefundMoney(orderGood.getFinalPrice().multiply(BigDecimal.valueOf(goodsNum)));    //退款金额
        //returnGoods.setIsReceive(order.getOrderStatus()); 		//是否已收货

        return returnGoods;
    }

    /**
     * 退货换货/退款 列表
     */
    public Page<Record> pageList(Integer userId, int type, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT rg.id id, rg.order_id orderId, rg.goods_id goodsId, rg.type type, rg.status+0 status, rg.addtime addtime");
        select.append(", s.store_id storeId, s.store_name storeName");
        StringBuilder from = new StringBuilder(" FROM butler_return_goods rg");
        from.append(" LEFT JOIN  butler_store s ON s.store_id = rg.store_id");
        from.append(" WHERE rg.user_id = ?");

        List<Object> paras = new ArrayList<>();
        paras.add(userId);

        from.append(" ORDER BY rg.order_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> list = page.getList();
        for (Record o : list) {
            returnGood(o);
            //calStatus(o);
        }
        return page;
    }

    /**
     * 获取商品相关信息
     */
    private Record returnGood(Record returnGood) {
        Integer orderId = returnGood.getInt("orderId");
        String sql = "SELECT og.goods_name goodName, og.spec_key_name specName, og.goods_num goodNum, og.final_price finalPrice, og.goods_id goodId, og.goods_price goodPrice, g.original_img imgPath"
                + " FROM butler_order_goods og"
                + " LEFT JOIN butler_good g ON g.goods_id = og.goods_id"
                + " WHERE og.order_id = ? ORDER BY og.rec_id DESC";
        List<Record> list = Db.find(sql, orderId);

        for (Record r : list) {
            String imgPath = r.getStr("imgPath");
            if (StrKit.notBlank(imgPath)) {
                returnGood.set("imgPath", PropKit.get("fileHost") + imgPath);
            }
        }

        long goodNum = list.stream().map(o -> o.getInt("goodNum")).collect(Collectors.summarizingInt(n -> n.intValue())).getSum();
        returnGood.set("goodTotalNum", goodNum);
        returnGood.set("orderGood", list);
        return returnGood;
    }


}
