package com.qw.controller.app.order;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.DateKit;
import cn.qw.kit.IpKit;
import com.qw.conf.ButlerEmnu;
import com.qw.interceptor.ResubmitInterceptor;
import com.qw.model.*;
import com.qw.service.frontend.good.CommentService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.order.ReturnGoodsService;
import com.qw.service.frontend.member.PointService;
import com.qw.service.frontend.prom.FlashSaleService;
import com.qw.service.frontend.prom.LotteryService;
import com.qw.vo.order.*;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理
 */
public class OrderController extends RestController {

    /**
     * @param type|查询类型（0全部订单              1待付款2待发货 3待收货 4 待评价 5退换货）|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 订单列表
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam orderId|订单ID|int|必填
     * @respParam masterOrderSn|主订单号|String|必填
     * @respParam orderSn|订单号|String|必填
     * @respParam goodTotalNum|总商品数量|int|必填
     * @respParam orderAmount|商品总金额|decimal|必填
     * @respParam orderId|订单ID|int|必填
     * @respParam status|1待付款 2待发货 3待收货 4 待评价 5退换货|int|必填
     * @respParam shippingPrice|订单邮费|decimal|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeName|店铺名字|String|必填
     * @respParam orderGood|商品列表|List<Object>|必填
     * @respParam finalPrice|成交价格|decimal|必填
     * @respParam goodPrice|商品价格|decimal|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam specName|规格名称|String|必填
     * @respParam goodNum|购买数量|int|必填
     */
    public void list() {
        Integer type = getParaToInt("type", 0);
        Integer userId = AppIdKit.getUserId();
        if (userId == 0) {
            renderParamNull("用户ID不能为空");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = OrderService.me().pageList(userId, type, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param orderId |订单ID|int|必填
     * @title 获取订单详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam address|地址|String|必填
     * @respParam closeTime|订单关闭时间|String|必填
     * @respParam addTime|下单时间|String|必填
     * @respParam invoiceDesc|发票内容|String|必填
     * @respParam invoiceTitle|发票标题|String|必填
     * @respParam invoiceType|发票类型|String|必填
     * @respParam mobile|手机|String|必填
     * @respParam nickname|昵称|String|必填
     * @respParam goodTotalNum|总商品数量|int|必填
     * @respParam orderAmount|商品总金额|decimal|必填
     * @respParam orderId|订单ID|int|必填
     * @respParam orderSn|订单编号|String|必填
     * @respParam payName|支付方式|String|必填
     * @respParam shippingName|邮寄方式|String|必填
     * @respParam shippingPrice|订单邮费|decimal|必填
     * @respParam status|1待付款 2待发货 3待收货 4 待评价 5退换货|int|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeName|店铺名字|String|必填
     * @respParam orderGood|商品列表|List<Object>|必填
     * @respParam finalPrice|成交价格|decimal|必填
     * @respParam goodPrice|商品价格|decimal|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam specName|规格名称|String|必填
     * @respParam goodNum|购买数量|int|必填
     */
    public void detail() {
        Integer orderId = getParaToInt("orderId", 0);
        if (orderId == 0) {
            renderParamNull("订单ID不能为空");
            return;
        }
        Record detail = OrderService.me().detail(orderId);
        if (detail == null) {
            renderParamError("订单信息不存在");
            return;
        }
        // 计算订单关闭时间
        Date addTime = DateKit.stringtoDate(detail.getStr("addTime"), "yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(addTime);
        calendar.add(Calendar.HOUR_OF_DAY, +3);
        String closeTime = DateKit.dateToString(calendar.getTime(), "yyyy/MM/dd HH:mm:ss");
        detail.set("closeTime", closeTime);

        renderJson(detail);
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 根据订单id获取商品id和图片
     * @respParam goodId|商品ID|int|必填
     * @respParam goodImg|商品图片|String|必填
     */
    public void getGood() {
        Integer orderId = getParaToInt("orderId", 0);
        if (orderId == 0) {
            renderParamNull("订单ID不能为空");
            return;
        }
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        List<Record> goodList = OrderService.me().findGood(order);
        renderJson(goodList);
    }

    /*
     * 常规情况下的下单流程逻辑
     * 1. 发起预订单校验，校验用户状态，如果符合条件，返回true - （目前只在秒杀里面有这一步，其他情况下不用这一步）
     * 2. 根据商品，生成预订单信息（这里面也会校验一次，但是校验逻辑和上面的校验逻辑一致）
     * 3. 用户提交订单，生成真实订单信息，保存在对应的位置（数据库或redis等），返回客户端支付信息
     * 4. 用户唤起支付工具支付
     * 5. 支付回调，下单完成
     */

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @title 秒杀 -- 生成预订单前的参数和状态校验
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void preFlashOrderValid() {
        int flashGoodId = getParaToInt("flashGoodId", 0);
        if (flashGoodId == 0) {
            renderParamError("参数错误，秒杀商品参数异常");
            return;
        }
        // 校验当前用户是否可以下这个秒杀产品
        Record result = FlashSaleService.me().flashOrderValid(flashGoodId);
        Boolean isOk = result.getBoolean("isOk");
        if (isOk) {
            renderSuccess("第一步符合条件");
            return;
        }
        renderParamError(result.getStr("msg"));
    }

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @title 秒杀 - 生成预订单前（根据商品生成订单信息，但是不保存，给用户做二次确认使用）
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void preFlashOrder() {
        int flashGoodId = getParaToInt("flashGoodId", 0);
        if (flashGoodId == 0) {
            renderParamError("参数错误，秒杀商品参数异常");
            return;
        }
        // 校验当前用户是否可以下这个秒杀产品
        Record result = FlashSaleService.me().flashOrderValid(flashGoodId);
        if (!result.getBoolean("isOk")) {
            renderParamError(result.getStr("msg"));
            return;
        }
        OrderDetailVo detail = FlashSaleService.me().genFlashOrder(flashGoodId);
        renderJson(detail);
    }

    /**
     * @param flashGoodId|秒杀商品ID|int|必填
     * @param addressId|收货地址ID|int|必填
     * @param delivery|收货地址ID|int|必填
     * @param userNote|用户留言|String|必填
     * @title 秒杀 - 提交订单
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam message|提示信息|String|选填
     * @respParam realPayPrice|应支付总金额|Double|选填
     * @respParam pointAsMoney|消费积分|Double|必填
     * @respParam masterOrderSn|主订单号|String|必填
     */
    @Before(ResubmitInterceptor.class)
    public void subFlashOrder() {
        // 获取参数
        int flashGoodId = getParaToInt("flashGoodId", 0);
        int addressId = getParaToInt("addressId", 0);
        // 0包邮，其他情况为对应的快递模板,快递模板还没有做
        int delivery = getParaToInt("delivery", 0);
        String userNote = getPara("userNote");
        // 校验参数
        if (flashGoodId == 0) {
            renderParamError("参数错误，秒杀商品参数异常");
            return;
        }
        if (addressId == 0) {
            renderParamError("请先选择收货地址");
            return;
        }
        // 校验当前用户是否可以下这个秒杀产品
        Record result = FlashSaleService.me().flashOrderValid(flashGoodId);
        if (!result.getBoolean("isOk")) {
            renderParamError(result.getStr("msg"));
            return;
        }
        OrderModel pay = FlashSaleService.me().subFlashOrder(flashGoodId, addressId, delivery, userNote);
        if (pay == null) {
            renderParamError("秒杀失败");
            return;
        }
        if (pay.getOrder().getOrderAmount().compareTo(new BigDecimal("0")) == 0) {
            // 纯积分秒杀
            FlashSaleService.me().payCallback(pay.getOrder().getMasterOrderSn(), pay.getOrder().getMasterOrderSn(), new BigDecimal("0"), ButlerEmnu.PayType.None);
        }
        PointService.me().consume(pay.getOrder());
        result = new Record();
        result.set("message", "秒杀成功");
        result.set("realPayPrice", pay.getOrder().getOrderAmount());
        result.set("pointAsMoney", pay.getOrder().getPointAsMoney());
        result.set("masterOrderSn", pay.getOrder().getMasterOrderSn());
        renderJson(result);
    }

    /**
     * @param lotteryGoodId|拼团商品ID|int|必填
     * @title 抢购抽奖 - 生成预订单前（根据商品生成订单信息，但是不保存，给用户做二次确认使用）
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void preLotteryOrder(){
        int lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        if (lotteryGoodId == 0) {
            renderParamError("参数错误，商品参数异常");
            return;
        }
        // 校验当前用户是否可以下这个抽奖
            // TODO
        // 生成订单信息
        OrderDetailVo detail = LotteryService.me().genLotteryOrder(lotteryGoodId);
        renderJson(detail);
    }

    /**
     * @param lotteryGoodId|拼团商品ID|int|必填
     * @param addressId|收货地址ID|int|必填
     * @param delivery|收货地址ID|int|必填
     * @param userNote|用户留言|String|必填
     * @title 秒杀 - 提交订单
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam message|提示信息|String|选填
     * @respParam realPayPrice|应支付总金额|Double|选填
     * @respParam pointAsMoney|消费积分|Double|必填
     * @respParam masterOrderSn|主订单号|String|必填
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void subLotteryOrder(){
        // 获取参数
        int lotteryGoodId = getParaToInt("lotteryGoodId", 0);
        int addressId = getParaToInt("addressId", 0);
        // 0包邮，其他情况为对应的快递模板,快递模板还没有做
        int delivery = getParaToInt("delivery", 0);
        String userNote = getPara("userNote");
        // 校验参数
        LotteryGood lotteryGood = LotteryGood.dao.findById(lotteryGoodId);
        if(lotteryGood == null) {
            renderParamError("抽奖商品不存在");
            return;
        }
        Lottery lottery = Lottery.dao.findById(lotteryGood.getLotteryId());
        if(lottery == null) {
            renderParamError("抽奖活动不存在");
            return;
        }
        Good good = Good.dao.findById(lotteryGood.getGoodId());
        if(good == null) {
            renderParamError("商品已经下级，无法参与活动");
            return;
        }
        if(good.getIsOnSale() != 1) {
            renderParamError("商品已经下架，无法购买");
            return;
        }
        GoodSku sku = GoodSku.dao.findById(lotteryGood.getSpecId());
        if(sku == null){
            renderParamError("商品规格不存在，无法购买");
            return;
        }
        UserAddress address = UserAddress.dao.findById(addressId);
        if(address == null) {
            renderParamError("收货地址不存在");
            return;
        }

        renderSuccess("");
    }

    /**
     * @param cartId|购物车Id集合|int[]|必填
     * @title 普通商品 - 生成预订单前（根据商品规格ID生成订单信息，但是不保存，给用户做二次确认使用）
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void preCartOrder() {
        List<Integer> cartIds = getList(Integer.class);
        if (cartIds == null || cartIds.size() <= 0) {
            renderParamError("参数错误");
            return;
        }
        Map<Integer, List<Cart>> map = cartIds.stream().map(cartId -> Cart.dao.findById(cartId)).collect(Collectors.groupingBy(cart -> cart.getStoreId()));
        OrderDetailVo detailVo = OrderService.me().genOrderVo(map);
        renderJson(detailVo);
    }

    /**
     * @param skuId|商品规格ID|int|必填
     * @param buyNum|购买数量|int|必填
     * @title 普通商品 - 生成预订单前（根据商品规格ID生成订单信息，但是不保存，给用户做二次确认使用）
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void preOrder() {
        int skuId = getParaToInt("skuId", 0);
        int buyNum = getParaToInt("buyNum", 1);
        if (skuId == 0) {
            renderParamError("商品规格ID不能为空");
            return;
        }
        if (buyNum == 0) {
            renderParamError("商品数量不能为空");
            return;
        }
        GoodSku sku = GoodSku.dao.findById(skuId);
        if (sku == null) {
            renderParamError("SKU不存在");
            return;
        }
        if (!sku.getDisAbled()) {
            renderParamError("参数错误，规格不存在");
            return;
        }
        Good good = Good.dao.findById(sku.getGoodsId());
        if (good == null) {
            renderParamError("商品不存在");
            return;
        }
        if (good.getIsOnSale() != 1) {
            renderParamError("商品已下架");
            return;
        }
        OrderDetailVo detail = OrderService.me().genOrderVo(good, sku, buyNum, null, null);
        renderJson(detail);
    }

    /**
     * @param skuId|商品规格ID|int|必填
     * @param addressId|收货地址ID|int|必填
     * @param delivery|收货地址ID|int|必填
     * @param userNote|用户留言|String|必填
     * @title 普通商品 - 提交订单
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam message|提示信息|String|选填
     * @respParam realPayPrice|应支付总金额|Double|选填
     * @respParam pointAsMoney|消费积分|Double|必填
     * @respParam masterOrderSn|主订单号|String|必填
     */
    @Before({Tx.class, ResubmitInterceptor.class})
    public void subOrder() {
        SubOrderVo subOrderVo = getVo(SubOrderVo.class);
        if (subOrderVo == null) {
            renderParamError("参数错误");
            return;
        }
        // 收货地址
        Integer addressId = subOrderVo.getAddressId();
        UserAddress userAddress = UserAddress.dao.findById(addressId);
        if (userAddress == null) {
            renderParamError("收货地址不能为空");
            return;
        }
        Integer isPoint = subOrderVo.getIsPoint();
        if (isPoint != 1 && isPoint != 2) {
            renderParamError("isPoint 只能是1或2");
            return;
        }
        Integer isCart = subOrderVo.getIsCart();
        if (isPoint != 1 && isPoint != 2) {
            renderParamError("isCart 只能是1或2");
            return;
        }
        BigDecimal usableRemain = new BigDecimal("0");
        if (isPoint == 1) {
            usableRemain = PointService.me().findByUserId(AppIdKit.getUserId()).getUsableRemain();
        }

        BigDecimal[] finalUsableRemain = {usableRemain};
        List<OrderModel> orderModelList = subOrderVo.getOrderStoreList().stream().map(os -> {
            OrderModel orderModel = new OrderModel();
            orderModel.setOrderGoodList(os.getOrderGoodList().stream().map(og -> {
                OrderGoods orderGoood = new OrderGoods();
                orderGoood.setSkuId(og.getSkuId());
                orderGoood.setGoodsNum(og.getBuyNum());
                return orderGoood;
            }).collect(Collectors.toList()));
            OrderModel model = OrderService.me().genOrderModel(orderModel, userAddress, os.getUserNote(), finalUsableRemain[0]);
            finalUsableRemain[0] = finalUsableRemain[0].subtract(model.getOrder().getPointAsMoney());

            return model;
        }).collect(Collectors.toList());
        Record record = OrderService.me().save(orderModelList, isCart);
        renderJson(record);
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 提醒发货
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void remindShipping() {
        int orderId = getParaToInt("orderId", 0);
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamError("订单信息不存在");
            return;
        }
        if (!AppIdKit.getUserId().equals(order.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        // 是否完成支付
        if (order.getPayStatus() != 1) {
            renderParamError("您还未支付，请前往支付");
            return;
        }
        order.setRemind(3);
        if (order.update()) {
            renderJson("提醒成功！");
        } else {
            renderOperateError("提醒失败");
        }
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 查看物流
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam code|订单编号|String|必填
     * @respParam name|物流名称|String|必填
     * @respParam status|物流状态0未发货 1已发货 2已收货|int|必填
     * @respParam goodList|商品列表|List<Object|必填
     * @respParam goodId|商品ID|int|必填
     * @respParam goodImg|商品图片|String|必填
     * @respParam detail|物流信息|List<Object>|必填
     * @respParam process|进度说明|String|必填
     * @respParam date|时间|Date|必填
     */
    public void shipDetail() {
        int orderId = getParaToInt("orderId", 0);
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        if (order.getShippingStatus() != 1) {
            renderParamError("订单未发货，无法查看物流信息");
            return;
        }
        List<Record> goodList = OrderService.me().findGood(order);
        ExpressCompany expressCompany = ExpressCompany.dao.findById(order.getShippingName());

        Map<String, Object> result = new HashMap<>();
        result.put("code", order.getShippingCode());
        result.put("name", expressCompany.getName());
        result.put("status", order.getShippingStatus());
        result.put("phone", "400-836-8877");
        result.put("goodList", goodList);
        result.put("detail", new ArrayList<Record>());
        renderJson(result);
    }

    /**
     * @param orderId|订单ID|int|必填
     * @param sellerScore|服务评分|Decimal|必填
     * @param logisticsScore|配送评分|Decimal|必填
     * @param list|订单中的商品|List<Object>|必填
     * @param goodId|商品id|int|必填
     * @param descScore|商品评分|Decimal|必填
     * @param comment|评论内容|String|必填
     * @param anonymous|是否匿名评论0:是；1不是|int|必填
     * @param imgarr|评价图片|String[]|必填
     * @title 服务评价 (POST请求)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void comment() {
        CommentVo vo = getVo(CommentVo.class);

        Integer orderId = vo.getOrderId();
        // 服务评分
        BigDecimal sellerScore = vo.getSellerScore();
        // 配送评分
        BigDecimal logisticsScore = vo.getLogisticsScore();
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单信息不存在");
            return;
        }
        Integer userId = AppIdKit.getUserId();
        if (!userId.equals(order.getUserId())) {
            renderParamNull("订单信息错误，不是自己的订单无法操作");
            return;
        }
        if (order.getIsComment() == 1) {
            renderParamError("订单已平均，不能重复评价");
            return;
        }
        if (sellerScore == null || logisticsScore == null) {
            renderParamNull("评分不能为空");
            return;
        }
        List<OrderCommentVo> list = vo.getList();
        if (list == null || list.size() < 0) {
            renderParamError("评价商品数据不能为空");
            return;
        }

        List<Comment> commentList = list.stream().map(v -> {
            Comment comment = CommentService.me().genModel(order, v.getGoodId(), v.getComment(), v.getImgarr(),
                    v.getDescScore(), v.getAnonymous(), IpKit.getIpAddr(getRequest()));
            return comment;
        }).collect(Collectors.toList());

        boolean comment = OrderService.me().comment(order, commentList, sellerScore, logisticsScore);
        if (comment) {
            renderSuccess("评论成功");
        } else {
            renderOperateError("评论失败");
        }
    }

    /**
     * @param commentId|评价ID|int|必填
     * @title 给评价点赞
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void thumbsUp() {
        Integer commentId = getParaToInt("commentId");
        Comment comment = Comment.dao.findById(commentId);
        if (comment == null) {
            renderParamNull("评价信息不存在");
            return;
        }
        if (comment.getIsShow() == 0) {
            renderParamError("该评论已隐藏，不能进行点赞");
            return;
        }
        if (comment.getDeleted() == 1) {
            renderParamError("该评论已删除，不能进行点赞");
            return;
        }
        if (comment.getZanUserid().indexOf(AppIdKit.getUserId().toString()) != -1) {
            renderParamError("已为该评价点过赞，请勿重复点赞");
            return;
        }
        boolean refund = CommentService.me().thumbsUp(comment, AppIdKit.getUserId());
        if (refund) {
            renderSuccess("点赞成功");
        } else {
            renderOperateError("点赞失败");
        }
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 确定收货
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void confirm() {
        Integer orderId = getParaToInt("orderId");
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        if (order.getOrderStatus() != 0 && order.getPayStatus() != 1) {
            renderParamError("该订单不能收货确认");
            return;
        }
        boolean confirm = OrderService.me().confirm(order);
        if (confirm) {
            renderJson("确定成功！");
        } else {
            renderOperateError("确定失败");
        }
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 申请退款
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void refund() {
        Integer orderId = getParaToInt("orderId");
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        if (order.getShippingStatus() == 1) {
            renderParamError("该订单已经发货，请申请售后");
            return;
        }
        List<ReturnGoods> returnGood = OrderService.me().findReturn(order);
        if (returnGood != null && returnGood.size() > 0) {
            renderParamError("该订单中有商品正在申请售后");
            return;
        }
        boolean refund = OrderService.me().refund(order);
        if (refund) {
            renderSuccess("申请退款成功，客服正在处理中...");
        } else {
            renderOperateError("申请退款失败，请联系客服处理...");
        }
    }

    /**
     * @param orderId|订单ID|int|必填
     * @title 取消订单
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void cancel() {
        Integer orderId = getParaToInt("orderId");
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            renderParamNull("订单不存在");
            return;
        }
        if (!order.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，订单不是当前登录用户的ID");
            return;
        }
        if (order.getPayStatus() != 0 && order.getOrderStatus() != 0) {
            renderParamError("支付状态或订单状态不允许");
            return;
        }
        boolean cancel = OrderService.me().cancel(order);
        if (cancel) {
            renderSuccess("取消成功");
        } else {
            renderOperateError("取消失败");
        }
    }

    /**
     * @param userId|用户ID|int|必填
     * @param type|查询类型（0仅退款1退货退款          2换货 3维修）|int|必填
     * @param pageNumber|页码（默认第1页）|int|非必填
     * @param pageSize|一页个数（默认10条）|int|非必填
     * @title 退货退款/仅退款/换货 列表 (售后列表)
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|退换货id|int|必填
     * @respParam orderId|订单ID|int|必填
     * @respParam goodTotalNum|总商品数量|int|必填
     * @respParam imgPath|商品图片|String|必填
     * @respParam orderId|订单ID|int|必填
     * @respParam status|-2用户取消 -1不同意 0待审核 1通过 2已发货 3待退款 4换货完成 5退款完成6申诉仲裁|int|必填
     * @respParam storeId|店铺ID|int|必填
     * @respParam storeName|店铺名字|String|必填
     * @respParam type|退换货类型:0仅退款 1退货退款 2换货 3维修|int|必填
     * @respParam orderGood|商品列表|List<Object>|必填
     * @respParam finalPrice|成交价格|decimal|必填
     * @respParam goodPrice|商品价格|decimal|必填
     * @respParam goodName|商品名称|String|必填
     * @respParam specName|规格名称|String|必填
     * @respParam goodNum|购买数量|int|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    public void afterSaleList() {
        Integer type = getParaToInt("type", 0);
        Integer userId = getParaToInt("userId", 0);
        if (userId == 0) {
            renderParamNull("参数错误，无法查看当前用户售后列表信息");
            return;
        }
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = ReturnGoodsService.me().pageList(userId, type, pageNumber, pageSize);
        renderJson(pageList);
    }

    /**
     * @param id|退换货/退款申请表id|int|必填
     * @title 退货退款/仅退款/换货 查看详情
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam id|退货换货/退款id|int|必填
     * @respParam orderId|商品id|int|必填
     * @respParam orderSn|订单编号|String|必填
     * @respParam goodsId|商品id|int|必填
     * @respParam goodsImg|商品图片|String|必填
     * @respParam goodsName|商品名称/标题|String|必填
     * @respParam goodsNum|退货数量|int|必填
     * @respParam type|退货换货/退款类型: 0仅退款 1退货退款 2换货 3维修|int|必填
     * @respParam reason|退换货退款申请原因|String|int|必填
     * @respParam status|退换货退款状态:-2用户取消-1不同意0待审核1通过2已发货3待退款4换货完成5退款完成6申诉仲裁|int|必填
     * @respParam remark|卖家审核进度说明|String|选填
     * @respParam specKey|规格|String|必填
     * @respParam refundMoney|退款金额|int|必填
     * @respParam refundType|退款路径:0退到用户余额 1支付原路退回|int|选填
     * @respParam refundTime|退款时间(年-月-日 时:分:秒)|String|必填
     * @respParam addtime|(售后)申请时间(年-月-日 时:分:秒)|String|必填
     * @respParam checktime|卖家审核时间(年-月-日 时:分:秒)|String|必填
     * @respParam receivetime|卖家收货时间(年-月-日 时:分:秒)|String|必填
     */
    public void returnGoodsDetail() {
        Integer id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("订单ID不能为空");
            return;
        }
        Record returnGoodsDetail = OrderService.me().returnGoodsDetail(id);
        if (returnGoodsDetail == null) {
            renderParamError("订单信息不存在");
            return;
        }
        Good good = Good.dao.findById(returnGoodsDetail.getInt("goodsId"));
        returnGoodsDetail.set("goodsName", good.getGoodsName());
        returnGoodsDetail.set("goodsImg", good.getOriginalImg());

        renderJson(returnGoodsDetail);
    }

    /**
     * @param id|退货退款信息ID|int|必填
     * @title 取消 退货退款 (只针对status为：0待审核 3待退款 6申诉仲裁)
     * @respBody {"status":"0","data":true, "msg":"取消成功"}
     */
    public void cancelReturnGoods() {
        Integer id = getParaToInt("id");
        Record returnGoodsRecord = Db
                .findFirst("SELECT status+0 status, user_id userId FROM butler_return_goods WHERE id = ?", id);
        if (returnGoodsRecord == null) {
            renderParamNull("订单信息不存在");
            return;
        }
        Integer status = returnGoodsRecord.getInt("status");
        if (status != 0 && status != 3 && status != 6) {
            renderParamError("该订单已受理，无法申请取消");
            return;
        }
        if (returnGoodsRecord.getInt("userId").equals(AppIdKit.getUserId())) {
            renderParamError("参数错误，该订单不属于当前登录用户的");
            return;
        }
        int effectNum = Db.update("UPDATE butler_return_goods SET status = ?, canceltime = ? WHERE id = ?", -2,
                Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)), id);
        if (effectNum == 1) {
            renderSuccess("取消成功");
        } else {
            renderOperateError("取消失败");
        }
    }
}