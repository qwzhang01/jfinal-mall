package com.qw.event.withdraw;

import com.jfinal.log.Log;
import com.qw.model.PointWithdraw;
import com.qw.service.common.SmsTemplateService;
import net.dreamlu.event.core.EventListener;

/**
 * 后台提现相关操作发起
 * 1. 审核通过发起-发送微信通知
 * 2. 审核失败发起-发送微信通知
 * 3. 打款发起-发送微信通知
 */
public class PointWithdrawListener {
    private Log log = Log.getLog(getClass());

    @EventListener({PointWithdrawEvent.class})
    public void onApplicationEvent(PointWithdrawEvent event) {
        int withdrawId = event.getWithdrawId();
        PointWithdraw withdraw = PointWithdraw.dao.findById(withdrawId);
        if (withdraw == null) {
            log.error("打完款后的事件发起失败");
            return;
        }
        if(!SmsTemplateService.me().withdrow(withdraw)) {
            log.error("流程正确，发送微信消息失败");
        }
    }
}