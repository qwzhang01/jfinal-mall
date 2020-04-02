package com.qw.service.common;

import cn.qw.base.BaseService;
import com.jfinal.plugin.ehcache.CacheKit;
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


    private CaptchaService() {}

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
        Integer times = CacheKit.get(CACHE_NAME, INDEX_PHONE_CODE_TIME + phone);
        return times == null || times == 0;
    }

    public void sendSmsCode(String phone) {
        String code = RandomStringUtils.random(4, false, true);
        // SmsTemplateService.me().sendSmsCode(phone, code); TODO 发短信
        CacheKit.put(CACHE_NAME, INDEX_PHONE_CODE + phone, code);
        CacheKit.put(CACHE_NAME, INDEX_PHONE_CODE_TIME + phone, 1);
    }

    public boolean validateSmsCode(String smscode, String phone) {
        String cacheCode = CacheKit.get(CACHE_NAME, INDEX_PHONE_CODE + phone);
        if (cacheCode != null && cacheCode.equals(smscode)) {
            CacheKit.remove(CACHE_NAME,INDEX_PHONE_CODE + phone);
            CacheKit.remove(CACHE_NAME, INDEX_PHONE_CODE_TIME + phone);
            return true;
        }
        return false;
    }
}
