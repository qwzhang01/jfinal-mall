package com.qw.util;

import com.qw.QwWebRoutes;
import com.jfinal.config.Routes;
import com.nmtx.doc.core.api.jfinal.JFinalApiDocConfig;

public class JDocGenerator {

    public static void main(String[] args) {
        genWeb("web.jdoc.properties");
    }

    private static void genWeb(String propPath) {
        JFinalApiDocConfig jdocConfig = new JFinalApiDocConfig(propPath).setUseClearSuffix(false);
        QwWebRoutes routes = new QwWebRoutes();
        routes.config();
        for (Routes.Route route : routes.getRouteItemList()) {
            jdocConfig = jdocConfig.add(route.getControllerKey(), route.getControllerClass());
        }
        routes.clear();
        jdocConfig.start();
    }
}