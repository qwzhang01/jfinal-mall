package com.qw.service.bakend.good;

import cn.qw.base.BaseService;
import cn.qw.es.ES;
import com.qw.vo.good.EsGood;

import java.util.List;

/**
 * 商品后台ES service
 */
public class ESGoodService extends BaseService {
    private static ESGoodService service;

    private ESGoodService() {
    }

    public static synchronized ESGoodService me() {
        if (service == null) {
            service = new ESGoodService();
        }
        return service;
    }

    public void importAll() {
        List<EsGood> esProductList = GoodService.me().findEsAll();
        esProductList.stream().forEach(g->{
            create(g);
        });
    }


    public void delete(Long id) {
        ES.delDoc("good", "good", id);
    }

    public void create(EsGood good) {
        ES.addDoc("good", "good", good, String.valueOf(good.getId()));
    }
}