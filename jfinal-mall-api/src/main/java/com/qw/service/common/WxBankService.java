package com.qw.service.common;


import cn.qw.base.BaseService;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.qw.conf.QuantityConf;
import com.qw.model.Wxbank;

import java.util.List;
import java.util.Optional;

public class WxBankService extends BaseService {
    private static WxBankService service;
    private Cache cache;

    private WxBankService() {
        this.cache = Redis.use(QuantityConf.PARAM_CATCHE);
    }

    public static synchronized WxBankService me() {
        if (service == null) {
            service = new WxBankService();
        }
        return service;
    }

    public String getId(String name){
        String[] bankName = name.split("银行");
        if(bankName.length <= 1) {
            return "";
        }
        List<Wxbank> wxbankList = cache.get(CACHE_NAME + "-findWxBank");
        if(wxbankList == null || wxbankList.size() <= 0) {
            wxbankList = Wxbank.dao.find("SELECT * FROM oms_wxbank");
            cache.setex(CACHE_NAME+ "-findWxBank", 60 * 60 * 24 * 1, wxbankList);
        }
        Optional<Wxbank> any = wxbankList.stream().filter(wx->{
            String nameDb = wx.getName();
            if(nameDb.contains(bankName[0])) {
                return true;
            }
            return false;
        }).findAny();
        if(any.isPresent()) {
            return String.valueOf(any.get().getId());
        }
        return "";
    }
}
