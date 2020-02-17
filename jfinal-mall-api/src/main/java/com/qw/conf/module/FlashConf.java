package com.qw.conf.module;

/**
 * 秒杀相关的静态变量
 */
public interface FlashConf {
    /**
     * 秒杀时间段，是sorted set 全系统只有一个key,对应的具体的数据
     */
    String FLASH_TIME_SORTED_KEY = "FlashTimeSortedSet";
    /**
     * 秒杀详情，k-v  string，一个秒杀活动对应一个数据 + 秒杀活动ID
     */
    String FLASH_TIME_KEY_PREFIX = "FlashTimeKV-";
    /**
     * 每个秒杀对应的商品，是sorted set，一个秒杀活动对应一个  + 秒杀活动ID
     */
    String FLASH_GOOD_SORTEDSET_PREFIX = "FlashGoodSortedSet-";
    /**
     * 每个秒杀商品对应的商品详情，k-v String，一个秒杀商品对应一个记录  + 秒杀商品ID
     */
    String FLASH_GOOD_DETAIL_KEY_PREFIX = "FlashGoodDetailKV-";

    /**
     * 秒杀订单详情,k-v  String, 一个秒杀订单对应一个记录  + 用户ID + 秒杀商品ID + 秒杀订单主编号
     */
    String FLASH_ORDER_DETAIL_KEY_PREFIX = "FlashOrderDetailKV-";
    /**
     * 秒杀商品列表，一个秒杀商品对应一个列表  + 秒杀商品ID
     */
    String FLASH_ORDER_COUNT_KEY_PREFIX = "FlashOrderCountKV-";
    /**
     * 秒杀订单过期监听，一个秒杀订单对应一个 k-v String 一个秒杀订单对应一个，+ 用户ID + 秒杀商品ID + 秒杀订单主编号
     */
    String FLASH_ORDER_EXPIRE_KEY_PREFIX = "FlashOrderExpireKv-";

    /**
     * 拼接 订单详情键
     *
     * @param userId
     * @param flashGoodId
     * @param masterOrderSn
     * @return
     */
    static String orderDetailKey(int userId, int flashGoodId, String masterOrderSn) {
        return FlashConf.FLASH_ORDER_DETAIL_KEY_PREFIX + userId + "-" + flashGoodId + "-" + masterOrderSn;
    }
}