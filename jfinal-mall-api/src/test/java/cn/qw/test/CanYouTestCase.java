package cn.qw.test;

import cn.qw.kit.Reflect;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.jfinal.config.JFinalConfig;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.AfterClass;
import org.junit.Before;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class CanYouTestCase<T extends JFinalConfig> {

    protected static ServletContext servletContext = new MockServletContext();
    protected static MockHttpRequest request;
    ;
    protected static MockHttpResponse response;
    protected static Handler handler;
    private static boolean configStarted = false;
    private static JFinalConfig configInstance;
    protected final Log log = Log.getLog(CanYouTestCase.class);
    private String actionUrl;
    private String bodyData;
    private Map<String, String> para = Maps.newHashMap();
    private File bodyFile;
    private File responseFile;
    private Class<? extends JFinalConfig> config;

    @SuppressWarnings("unchecked")
    public CanYouTestCase() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        Preconditions.checkArgument(genericSuperclass instanceof ParameterizedType,
                "Your ControllerTestCase must have genericType");
        config = (Class<? extends JFinalConfig>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    private static void initConfig(Class<JFinal> clazz, JFinal me, ServletContext servletContext, JFinalConfig config) {
        Reflect.on(me).call("init", config, servletContext);
        ShiroTestUtils.mockSubject("test");
    }

    public static void start(Class<? extends JFinalConfig> configClass) throws Exception {
        if (configStarted) {
            return;
        }
        Class<JFinal> clazz = JFinal.class;
        JFinal me = JFinal.me();
        configInstance = configClass.newInstance();
        initConfig(clazz, me, servletContext, configInstance);
        handler = Reflect.on(me).get("handler");
        configStarted = true;
        configInstance.afterJFinalStart();
    }

    @AfterClass
    public static void stop() {
        try {
            configInstance.beforeJFinalStop();
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }

    public Object findAttrAfterInvoke(String key) {
        return request.getAttribute(key);
    }

    private String getTarget(String url, MockHttpRequest request) {
        String target = url;
        if (url.contains("?")) {
            target = url.substring(0, url.indexOf("?"));
            String queryString = url.substring(url.indexOf("?") + 1);
            String[] keyVals = queryString.split("&");
            for (String keyVal : keyVals) {
                int i = keyVal.indexOf('=');
                String key = keyVal.substring(0, i);
                String val = keyVal.substring(i + 1);
                request.setParameter(key, val);
            }
        }
        return target;

    }

    @Before
    public void init() throws Exception {
        start(config);
    }

    public String invoke() {
        if (bodyFile != null) {
            List<String> req = null;
            try {
                req = Files.readLines(bodyFile, Charsets.UTF_8);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
            bodyData = Joiner.on("").join(req);
        }
        StringWriter resp = new StringWriter();
        request = new MockHttpRequest(bodyData, para);
        response = new MockHttpResponse(resp);
        Reflect.on(handler).call("handle", getTarget(actionUrl, request), request, response, new boolean[]{true});
        String response = resp.toString();
        if (responseFile != null) {
            try {
                Files.write(response, responseFile, Charsets.UTF_8);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return response;
    }

    public CanYouTestCase<T> post(File bodyFile) {
        this.bodyFile = bodyFile;
        return this;
    }

    public CanYouTestCase<T> post(String bodyData) {
        this.bodyData = bodyData;
        return this;
    }

    public CanYouTestCase<T> use(String actionUrl) {
        this.actionUrl = actionUrl;
        return this;
    }

    public CanYouTestCase<T> writeTo(File responseFile) {
        this.responseFile = responseFile;
        return this;
    }

    public CanYouTestCase<T> post(Map<String, String> para) {
        this.para = para;
        return this;
    }

    public CanYouTestCase<T> post(String key, String value) {
        para.put(key, value);
        return this;
    }
}
