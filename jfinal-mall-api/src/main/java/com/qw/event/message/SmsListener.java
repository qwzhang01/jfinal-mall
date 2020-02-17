package com.qw.event.message;

import cn.qw.kit.JsonKit;
import com.qw.model.SmsLog;
import com.jfinal.log.Log;
import net.dreamlu.event.core.EventListener;

/**
 * 发送短信发完以后监听
 * 1. 发完短息后，记录发送短信的结果到数据库
 */
public class SmsListener {

    private Log log = Log.getLog(getClass());

    @EventListener({SmsEvent.class})
    public void onApplicationEvent(SmsEvent event) {
        String code = event.getCode();
        String msg = event.getMsg();
        String result = event.getResult();
        String mobile = event.getMobile();

        SmsLog smsLog = new SmsLog();
        smsLog.setMobile(mobile);
        smsLog.setMsg(msg);
        smsLog.setCode(code);
        smsLog.setErrorMsg(result);
        smsLog.setAddTime(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        smsLog.setStatus(event.isOk() ? 1 : 0);
        smsLog.save(false);

        log.info(JsonKit.toJson(event));
    }
}