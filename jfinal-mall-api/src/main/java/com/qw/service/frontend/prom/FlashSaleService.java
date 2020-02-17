package com.qw.service.frontend.prom;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import com.qw.conf.ButlerEmnu;
import com.qw.conf.QuantityConf;
import com.qw.conf.module.FlashConf;
import com.qw.event.flash.FlashPayEvent;
import com.qw.model.*;
import com.qw.service.frontend.good.GoodService;
import com.qw.service.frontend.order.OrderService;
import com.qw.service.frontend.member.PointService;
import com.qw.vo.order.OrderDetailVo;
import com.qw.vo.order.OrderModel;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.IKeyNamingPolicy;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.serializer.ISerializer;
import net.dreamlu.event.EventKit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 秒杀活动
 *
 * @author qw
 */
public class FlashSaleService extends BaseService {
    private static FlashSaleService service;
    private Cache cache;

    private FlashSaleService() {
        this.cache = Redis.use(QuantityConf.FLASH_SALE);
    }

    public static synchronized FlashSaleService me() {
        if (service == null) {
            service = new FlashSaleService();
        }
        return service;
    }

    /**
     * 获取有效的秒杀时间
     */
    public List<Record> flashTime() {
        Set set = cache.zrange(FlashConf.FLASH_TIME_SORTED_KEY, 0, -1);
        return (List<Record>) set.stream().map(id -> {
            Record r = cache.get(FlashConf.FLASH_TIME_KEY_PREFIX + id);
            if (r == null) {
                // 如果过期，则删除秒杀详情
                cache.zrem(FlashConf.FLASH_TIME_SORTED_KEY, id);
                // 如果过期，则删除秒杀活动对应的商品
                cache.del(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + id);
            } else {
                Long beginTime = r.getLong("start_time");
                if (System.currentTimeMillis() / 1000 - beginTime >= 0) {
                    r.set("number", 1);
                } else {
                    r.set("number", 2);
                }
            }
            return r;
        }).filter(o -> o != null).filter(o -> System.currentTimeMillis() / 1000 - ((Record) o).getLong("end_time") <= 0).collect(Collectors.toList());
    }

    /**
     * 获取秒杀的商品信息
     */
    public Page<Record> goodPageList(int flashId, int pageNumber, int pageSize) {
        Long totalCount = cache.zcard(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flashId);

        // 抽取做redis list 的分页插件
        Long totalPage = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            totalPage += 1;
        }
        // 计算start end
        int start = (pageNumber - 1) * pageSize;
        int end = start + pageSize - 1;

        Set set = cache.zrange(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flashId, start, end);
        List<Record> list = (List<Record>) set.stream().map(s -> {
            Integer flashGoodId = (Integer) s;
            Record r = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
            Good good = r.get("good");
            FlashSale flashGood = r.get("flashGood");
            GoodSku[] skus = r.get("spec_goods_price");
            Record activity = r.get("activity");

            Record result = new Record();
            result.set("flashGoodId", flashGood.getId());
            result.set("goodId", flashGood.getGoodId());
            result.set("skuId", flashGood.getSkuId());
            result.set("goods_name", good.getGoodsName());
            result.set("price", flashGood.getPrice());
            result.set("shop_price", skus[0].getPrice());
            result.set("imgPath", flashGood.getImgPath());
            result.set("buyLimit", flashGood.getBuyLimit());
            result.set("point", flashGood.getPoint());
            result.set("pointLimit", flashGood.getPointLimit());
            result.set("percent", calPercent(activity, flashGood));
            return result;
        }).collect(Collectors.toList());
        return new Page<>(list, pageNumber, pageSize, totalPage.intValue(), totalCount.intValue());
    }

    /**
     * 构建秒杀进度数据
     */
    private BigDecimal calPercent(Record info, FlashSale flashGood) {
        // 商品库存
        Integer goodNum = flashGood.getGoodNum();
        // 成交数量-缓存里面不管成交，只管下单
        // Integer buyNum = flashGood.getBuyNum();
        // 下单数量
        Integer orderNum = flashGood.getOrderNum();
        // 虚假下单数量
        Integer fakeBuyCount = flashGood.getFakeBuyNum();

        if (new Date().compareTo(info.getDate("startTime")) >= 0) {
            if (flashGood.getIsFake() == 2) {
                // 1真实2虚假
                // 秒杀订单，判断商品是不是虚假的秒杀商品，如果是，第一个人点击秒杀的那一刻，将商品销售库存给为已售罄
                orderNum = goodNum;
            } else {
                // 已经开始后，销售数量=销售数量+假的销售数量
                orderNum += fakeBuyCount;
                // 再加上下单的数量
                orderNum += orderCount(flashGood.getId());
            }
        }
        BigDecimal buy = new BigDecimal(String.valueOf(orderNum * 100)).setScale(BigDecimal.ROUND_HALF_UP, 2);
        BigDecimal total = new BigDecimal(String.valueOf(goodNum)).setScale(BigDecimal.ROUND_HALF_UP, 2);
        return buy.divide(total, 2);
    }

    private int orderCount(int flashId) {
        String orderCountKey = FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashId;
        // 订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单-有点类似于订单类型，标识订单处于哪个流程中
        Jedis jedis = cache.getJedis();
        String count = "0";
        try {
            String countStr = jedis.get(orderCountKey);
            if (StrKit.notBlank(countStr)) {
                count = countStr;
            }
        } finally {
            cache.close(jedis);
        }
        return Integer.parseInt(count);
    }

    /**
     * 获取秒杀商品详情
     */
    public Record detail(int flashGoodId) {
        Record result = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
        result.remove("flashGood");
        Good good = result.get("good");
        return GoodService.me().detail(good, result);
    }

    /**
     * 校验当前用户有没有资格购买这一秒杀商品
     * 返回结果：isOk（boolean）   msg（错误信息）
     */
    public Record flashOrderValid(int flashGoodId) {
        // 获取秒杀商品信息
        Record flashDetail = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
        FlashSale flashGood = flashDetail.get("flashGood");
        Record activity = flashDetail.get("activity");

        Record result = new Record();
        result.set("isOk", false);

        // 是否存在未支付的秒杀，如果存在，不允许参加另一个秒杀 TODO
        // 个人积分余额是否满足活动余额限度
        BigDecimal pointLimit = flashGood.getPointLimit();
        BigDecimal usableRemain = PointService.me().findByUserId(AppIdKit.getUserId()).getUsableRemain();
        if (pointLimit.compareTo(usableRemain) > 0) {
            result.set("msg", "积分余额不足最低限度");
            return result;
        }
        // 活动是否过期
        Date startTime = activity.getDate("startTime");
        Date endTime = activity.getDate("endTime");
        if (new Date().compareTo(startTime) < 0) {
            result.set("msg", "秒杀活动没有开始，无法购买");
            return result;
        }
        if (new Date().compareTo(endTime) >= 0) {
            result.set("msg", "秒杀活动已经结束，无法购买");
            return result;
        }
        // 活动限购个数
        long payOrderActivityCount = payOrderActivityCount(flashGood.getFlashId());
        if ((activity.getInt("activityBuyLimit") != 0) && (activity.getInt("activityBuyLimit").longValue() - payOrderActivityCount <= 0)) {
            result.set("msg", "该场秒杀活动限购" + activity.getInt("activityBuyLimit") + "个");
            return result;
        }
        // 商品限购个数
        long payOrderCount = payOrderGoodCount(flashGoodId);
        if ((flashGood.getBuyLimit() != 0) && (flashGood.getBuyLimit().longValue() - payOrderCount <= 0)) {
            result.set("msg", "该秒杀商品限购" + flashGood.getBuyLimit() + "个");
            return result;
        }
        // 商品库存是否够
        BigDecimal percent = calPercent(activity, flashGood);
        if (new BigDecimal("100").compareTo(percent) <= 0) {
            result.set("msg", "秒杀商品已售罄");
            return result;
        }
        result.set("isOk", true);
        return result;
    }

    private long payOrderActivityCount(Integer flashId) {
        Set<Integer> set = cache.zrange(FlashConf.FLASH_GOOD_SORTEDSET_PREFIX + flashId, 0, -1);
        long sum = set.stream()
                .map(flashGoodId -> payOrderGoodCount(flashGoodId)).mapToLong(Long::longValue).sum();
        return sum;
    }

    private long payOrderGoodCount(int flashGoodId) {
        Set<String> keys = cache.keys(FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + AppIdKit.getUserId() + "-" + flashGoodId + "*");
        long count = keys.stream().map(k -> cache.get(k)).filter(o -> {
            OrderModel order = (OrderModel) o;
            // 0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转
            Integer payStatus = order.getOrder().getPayStatus();
            return (1 == payStatus);
        }).count();
        return count;
    }

    /**
     * 生成秒杀订单详情
     */
    public OrderDetailVo genFlashOrder(int flashGoodId) {
        // 获取秒杀商品信息
        Record flashDetail = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
        Good good = flashDetail.get("good");
        FlashSale flashGood = flashDetail.get("flashGood");
        GoodSku[] spec_goods_price = flashDetail.get("spec_goods_price");
        GoodSku sku = spec_goods_price[0];

        return OrderService.me().genOrderVo(good, sku, 1, flashGood, null);
    }

    /**
     * 保存秒杀订单，具体逻辑如下：
     * 1.订单，重点是过期机制
     * 1.1 Order
     * 1.2 OrderGood
     * 2. 库存
     * 2.1 秒杀库存-确认订单后扣除
     * 2.2 真实库存-支付成功以后再扣除
     * 3. 优惠信息的逻辑
     * 3.1 如果消费了积分，扣减积分
     * 4. 附带逻辑
     * 4.1 发送微信公众提醒消息
     * 5. 数据同步
     */
    public OrderModel subFlashOrder(int flashGoodId, int addressId, int delivery, String userNote) {
        // 包邮以及寄送方式相关逻辑再考虑
        // 获取秒杀商品信息
        Record flashDetail = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
        Good good = flashDetail.get("good");
        FlashSale flashGood = flashDetail.get("flashGood");
        GoodSku[] spec_goods_price = flashDetail.get("spec_goods_price");
        GoodSku sku = spec_goods_price[0];

        OrderModel orderModel = OrderService.me().genOrderModel(good, sku, flashGood, addressId, userNote);
        Jedis jedis = cache.getJedis();
        ISerializer serializer = cache.getSerializer();
        IKeyNamingPolicy keyNamingPolicy = cache.getKeyNamingPolicy();

        String orderCountKey = FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGoodId;
        String orderDetailKey = FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + AppIdKit.getUserId() + "-" + flashGoodId + "-" + orderModel.getOrder().getMasterOrderSn();
        String orderExpireKey = FlashConf.FLASH_ORDER_EXPIRE_KEY_PREFIX + AppIdKit.getUserId() + "-" + flashGoodId + "-" + orderModel.getOrder().getMasterOrderSn();
        try {
            jedis.watch(orderCountKey, orderDetailKey, orderExpireKey);
            Transaction transaction = jedis.multi();
            // 保存订单详情
            transaction.set(serializer.keyToBytes(keyNamingPolicy.getKeyName(orderDetailKey)), serializer.valueToBytes(orderModel));
            // 订单数自增
            transaction.incr(serializer.keyToBytes(keyNamingPolicy.getKeyName(orderCountKey)));
            // 60秒没有支付，就过期，在redis里面设置
            transaction.setex(serializer.keyToBytes(keyNamingPolicy.getKeyName(orderExpireKey)), 60, serializer.valueToBytes("0"));
            // 执行事务
            List<Object> result = transaction.exec();
            // 判断事务是否成功
            if (result.isEmpty()) {
                return null;
            }
            // 解除监视
            jedis.unwatch();
        } finally {
            cache.close(jedis);
        }
        return orderModel;
    }

    /**
     * 订单过期
     */
    public void orderExpire(int userId, int flashGoodId, String masterOrderSn) {
        String orderCountKey = FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGoodId;
        String orderDetailKey = FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + userId + "-" + flashGoodId + "-" + masterOrderSn;
        OrderModel orderModel = cache.get(orderDetailKey);
        // 0待支付，1已支付，2部分支付，3已退款，4拒绝退款
        Integer payStatus = orderModel.getOrder().getPayStatus();
        // 订单过期
        if (0 == payStatus) {
            cache.decr(orderCountKey);
            // 0正常进行中的订单，3取消订单，4完成状态，5退款订单
            orderModel.getOrder().setOrderStatus(3);
            cache.set(orderDetailKey, orderModel);
            // 把积分退回去
            PointService.me().cancelOrder(orderModel.getOrder());
        }
    }

    /**
     * 根据订单编号获取对应的订单
     *
     * @param orderSn
     * @return
     */
    public Order findByOrderSn(String orderSn) {
        OrderModel orderModel = findOrderModel(orderSn);
        if (orderModel == null) {
            return null;
        }
        return orderModel.getOrder();
    }

    private OrderModel findOrderModel(String orderSn) {
        Set<String> keys = cache.keys(FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + "*" + "-" + orderSn);
        if (keys.size() != 1) {
            return null;
        }
        Object orderDetail = keys.toArray()[0];
        return cache.get(orderDetail);
    }

    /**
     * 秒杀支付回调-常规
     */
    public boolean payCallback(String tradeNo, String transId, BigDecimal payFee, ButlerEmnu.PayType payType) {
        Order order = findByOrderSn(tradeNo);
        if (order == null) {
            throw new RuntimeException("支付异常，支付对应的订单找不到了，缓存里面没有，看看啥情况，订单编号" + tradeNo);
        }
        BigDecimal orderAmount = order.getOrderAmount();
        if (payFee.compareTo(orderAmount) != 0) {
            log.error("支付异常，支付对应的订单找不到了，缓存里面没有，看看啥情况，订单编号" + tradeNo);
            return false;
        }
        order.setPayTime(System.currentTimeMillis() / 1000);
        order.setPayStatus(1);
        order.setTransactionId(transId);
        order.setPayCode(String.valueOf(payType.getValue()));
        order.setPayName(payType.getName());
        String key = updatePayStatus(order);
        if (StrKit.isBlank(key)) {
            return false;
        }
        EventKit.post(new FlashPayEvent(key));
        return true;
    }

    /**
     * 秒杀支付回调-订单已经过期
     * 0. 判断库存是否还有
     * 1. 保存缓存订单
     * 2. 添加订单数量
     *
     * @return
     */
    public boolean payCallback(int userId, int flashGoodId, String masterOrderSn) {
        String orderCountKey = FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGoodId;
        String orderDetailKey = FlashConf.orderDetailKey(userId, flashGoodId, masterOrderSn);
        OrderModel orderModel = cache.get(orderDetailKey);

        Record flashDetail = cache.get(FlashConf.FLASH_GOOD_DETAIL_KEY_PREFIX + flashGoodId);
        FlashSale flashGood = flashDetail.get("flashGood");

        Jedis jedis = cache.getJedis();
        ISerializer serializer = cache.getSerializer();
        IKeyNamingPolicy keyNamingPolicy = cache.getKeyNamingPolicy();
        boolean isBack = false;
        try {
            jedis.watch(orderCountKey, orderDetailKey);
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.get(orderCountKey);
            String countStr = response.get();
            int count = Integer.parseInt(countStr);
            int remain = flashGood.getGoodNum() - flashGood.getOrderNum() - flashGood.getFakeBuyNum() - count;
            if (remain > 0) {
                // 还有剩余的库存
                orderModel.getOrder().setOrderStatus(0);
                transaction.incr(serializer.keyToBytes(keyNamingPolicy.getKeyName(orderCountKey)));
            } else {
                isBack = true;
                // 没有剩余库存
                orderModel.getOrder().setOrderStatus(5);
            }
            transaction.set(serializer.keyToBytes(keyNamingPolicy.getKeyName(orderDetailKey)), serializer.valueToBytes(orderModel));
            List<Object> result = transaction.exec();
            if (result.isEmpty()) {
                isBack = true;
            }
            // 解除监视
            jedis.unwatch();
            // 保存订单详情
        } finally {
            cache.close(jedis);
        }
        return isBack;
    }

    private String updatePayStatus(Order order) {
        OrderModel orderModel = findOrderModel(order.getMasterOrderSn());
        if (orderModel == null) {
            return null;
        }
        orderModel.setOrder(order);
        String key = FlashConf.orderDetailKey(order.getUserId(), orderModel.getOrderGoodList().get(0).getActivityId(), order.getMasterOrderSn());
        String update = cache.set(key, orderModel);
        if (StrKit.notBlank(update)) {
            return key;
        }
        return null;
    }

    /**
     * 清除redis中的过期数据
     */
    public void flushRedis(Date time) {
        Set<String> keys = cache.keys(FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX);
        // 获取对应时间的过期商品
        List<FlashSale> flashGoodList = keys.stream().map(k -> k.split("-")[1]).map(k -> Integer.parseInt(k)).map(flashGoodId -> FlashSale.dao.findById(flashGoodId)).filter(flashGood -> {
            FlashInfo info = FlashInfo.dao.findById(flashGood.getFlashId());
            Date endTime = info.getEndTime();
            return endTime.compareTo(time) <= 0;
        }).collect(Collectors.toList());

        Jedis jedis = cache.getJedis();
        try {
            flashGoodList.stream().forEach(flashGood -> {
                // 删除 FLASH_ORDER_COUNT_KEY_PREFIX
                String count = jedis.get(FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGood.getFlashId());
                log.error("删除订单统计数量：key=" + FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGood.getFlashId() + "；value=" + count);
                jedis.del(FlashConf.FLASH_ORDER_COUNT_KEY_PREFIX + flashGood.getFlashId());
                // 删除过期没支付的订单
                Set<String> orderKeySet = cache.keys(FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + "*" + "-" + flashGood.getFlashId() + "-*");
                orderKeySet.stream().forEach(orderDetailKey -> {
                    log.error("删除缓存中的订单：key=" + orderDetailKey + "；value=" + jedis.get(orderDetailKey));
                    jedis.del(orderDetailKey);
                });
            });
        } finally {
            cache.close(jedis);
        }
    }
}