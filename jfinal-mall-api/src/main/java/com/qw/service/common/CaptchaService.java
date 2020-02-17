package com.qw.service.common;

import cn.qw.base.BaseService;
import com.qw.conf.QuantityConf;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 验证码
 *
 * @author qw
 */
public class CaptchaService extends BaseService {

    private static final String INDEX_PHONE_CODE = "CaptchaPhone";
    private static final String INDEX_PHONE_CODE_TIME = "CaptchaPhoneTime";

    private static CaptchaService service;

    private Cache cacheMoibleCode;
    private Cache cacheMobileCodeTimes;

    private CaptchaService() {
        cacheMoibleCode = Redis.use(QuantityConf.MOBILE_CODE);
        cacheMobileCodeTimes = Redis.use(QuantityConf.MOBILE_CODE);
    }

    public static synchronized CaptchaService me() {
        if (service == null) {
            service = new CaptchaService();
        }
        return service;
    }

    /**
     * 检查验证码在规定的时间内是否重复发送
     */
    public boolean checkSmsCodeTimes(String phone) {
        Integer times = cacheMobileCodeTimes.get(INDEX_PHONE_CODE_TIME + phone);
        return times == null || times == 0;
    }

    public void sendSmsCode(String phone) {
        String code = RandomStringUtils.random(4, false, true);
        SmsTemplateService.me().sendSmsCode(phone, code);
        cacheMoibleCode.setex(INDEX_PHONE_CODE + phone, 600, code);
        cacheMobileCodeTimes.setex(INDEX_PHONE_CODE_TIME + phone, 60, 1);
    }

    public boolean validateSmsCode(String smscode, String phone) {
        String cacheCode = cacheMoibleCode.get(INDEX_PHONE_CODE + phone);
        if (cacheCode != null && cacheCode.equals(smscode)) {
            cacheMoibleCode.del(INDEX_PHONE_CODE + phone);
            cacheMobileCodeTimes.del(INDEX_PHONE_CODE_TIME + phone);
            return true;
        }
        return false;
    }
}
