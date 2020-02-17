package com.qw.util;

import com.qw.QwRestRoutes;
import com.qw.QwWebRoutes;
import com.jfinal.config.Routes;
import com.nmtx.doc.core.api.jfinal.JFinalApiDocConfig;

public class JDocGenerator {

    public static void main(String[] args) {
        genApp("app.jdoc.properties");
//        genWeb("web.jdoc.properties");
//        genApp("common.jdoc.properties");
    }

    private static void genApp(String propPath) {
        JFinalApiDocConfig jdocConfig = new JFinalApiDocConfig(propPath).setUseClearSuffix(false);
        QwRestRoutes routes = new QwRestRoutes();
        routes.config();
        for (Routes.Route route : routes.getRouteItemList()) {
            jdocConfig = jdocConfig.add(route.getControllerKey(), route.getControllerClass());
        }
        routes.clear();
        jdocConfig.start();
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