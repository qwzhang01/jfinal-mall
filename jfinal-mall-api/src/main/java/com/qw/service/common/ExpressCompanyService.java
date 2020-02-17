package com.qw.service.common;

import com.qw.model.ExpressCompany;

import java.util.List;

/**
 * 物流公司管理
 *
 * @author qw
 */
public class ExpressCompanyService {
    private static ExpressCompanyService service;

    private ExpressCompanyService() {
    }

    public static synchronized ExpressCompanyService me() {
        if (service == null) {
            service = new ExpressCompanyService();
        }
        return service;
    }

    public List<ExpressCompany> available() {
        String sql = "SELECT * FROM oms_express_company WHERE isDisable = 1 ORDER BY name ASC";
        return ExpressCompany.dao.find(sql);
    }
}