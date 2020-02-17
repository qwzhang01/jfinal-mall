package com.qw.controller.web.order;

import cn.qw.base.RestController;
import com.qw.model.ServiceWorker;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.util.HashMap;
import java.util.List;

/**
 * 上门服务人员管理
 */
@RequiresAuthentication
public class ServiceWorkerControler extends RestController {

    public void all() {
        List<ServiceWorker> search = ServiceWorker.dao.search(new HashMap<>());
        renderJson(search);
    }
}