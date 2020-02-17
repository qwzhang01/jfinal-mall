package com.qw.xxljob;

import cn.qw.kit.DateKit;
import com.qw.model.Lottery;
import com.qw.model.LotteryGood;
import com.qw.model.LotteryOrder;
import com.qw.service.frontend.prom.LotteryService;
import com.jfinal.log.Log;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时抽奖/开奖
 * 处理的情况：
 * 1. 定时开，查询到点的活动，取出所有有效的团，开奖，将无效的团退还钱
 * 2. 人满开，查询结束的活动，取出未开的团，判断是否服务开奖条件，开奖或关闭
 */
@JobHander
public class LotteryDrawJob extends IJobHandler {

    private final Log log = Log.getLog(getClass());

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        log.info("定时任务LotteryDrawJob执行喽-----------" + DateKit.today());

        // 获取活动已经结束，但是没有开奖的所有活动
        List<Lottery> todayNoEndLotteryList = LotteryService.me().findEndLottery();
        for (Lottery lottery : todayNoEndLotteryList) {
            // 获取活动对应的商品
            List<LotteryGood> lotteryGoodList = LotteryService.me().findGoodByLottery(lottery);
            for (LotteryGood lotteryGood : lotteryGoodList) {
                // 对每一个商品判断是否符合开奖条件，处理开奖结果
                // 获取指定商品的所有的订单
                List<LotteryOrder> orderList = LotteryService.me().findOrderByGood(lotteryGood);
                Map<Integer, List<LotteryOrder>> sense = orderList.stream().collect(Collectors.groupingBy(o -> o.getActivityNum()));
                // 开团方式：1 定时开、2 人满开
                Integer type = lottery.getType();
                if (type == 2) {
                    if (sense.size() > 1) {
                        throw new RuntimeException("人满开奖存在非法数据，超过1个开奖团未开奖");
                    }
                    if (sense.size() == 1) {
                        sense.keySet().forEach(k -> {
                            List<LotteryOrder> list = sense.get(k);
                            // 开奖
                            LotteryService.me().open(list, lotteryGood, lottery);
                        });
                    }
                } else if (type == 1) {
                    sense.keySet().forEach(k -> {
                        List<LotteryOrder> list = sense.get(k);
                        // 开奖
                        LotteryService.me().open(list, lotteryGood, lottery);
                    });
                } else {
                    throw new RuntimeException("开奖存在非法数据，只有定时开和人满开");
                }
            }
        }
        return ReturnT.SUCCESS;
    }
}
