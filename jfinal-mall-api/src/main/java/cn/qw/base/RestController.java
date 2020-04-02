package cn.qw.base;

import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qw.interceptor.ExceptionInterceptor;
import com.qw.interceptor.SecurityInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Before({SecurityInterceptor.class, ExceptionInterceptor.class})
public class RestController extends Controller {

    protected final Log log = Log.getLog(getClass());

    @Override
    public void renderJson(Object object) {
        ApiResult api = new ApiResult();
        if (object == null) {
            api = new ApiResult(ApiStatus.dataNull, "返回数据为空!");
        } else if (object instanceof ApiResult) {
            api = (ApiResult) object;
        } else {
            api.setData(object);
            api.setStatus(ApiStatus.success);
            api.setMsg("操作成功");
        }
        if (JFinal.me().getConstants().getDevMode()) {
            log.debug("renderJson:\n" + JsonKit.toJson(object));
        }
        super.renderJson(api);
    }

    public void renderJson(Object object, String msg) {
        ApiResult api = new ApiResult();
        if (object == null) {
            api = new ApiResult(ApiStatus.dataNull, "返回数据为空!");
        } else if (object instanceof ApiResult) {
            api = (ApiResult) object;
        } else {
            api.setData(object);
            api.setStatus(ApiStatus.success);
            api.setMsg(msg);
        }
        if (JFinal.me().getConstants().getDevMode()) {
            log.debug("renderJson:\n" + JsonKit.toJson(object));
        }
        super.renderJson(api);
    }

    @Override
    public void renderJson(String str) {
        ApiResult api = new ApiResult();
        if (str == null) {
            api = new ApiResult(ApiStatus.dataNull, "返回数据为空!");
        } else {
            api.setData(str);
            api.setStatus(ApiStatus.success);
            api.setMsg("操作成功");
        }
        if (JFinal.me().getConstants().getDevMode()) {
            log.debug("renderJson:\n" + str);
        }
        super.renderJson(api);
    }

    public void renderJsonText(String str) {
        super.renderJson(str);
    }

    public void renderApiResult(ApiResult api) {
        if (JFinal.me().getConstants().getDevMode()) {
            log.debug("renderJson:\n" + JsonKit.toJson(api));
        }
        super.renderJson(api);
    }

    /**
     * 请求成功时调用
     */
    public void renderSuccess(String info) {
        super.renderJson(new ApiResult(true, ApiStatus.success, info));
    }

    /**
     * 参数为空时调用
     */
    public void renderParamNull(String msg) {
        super.renderJson(new ApiResult(ApiStatus.paramNull, msg));
    }

    /**
     * 参数错误
     */
    public void renderParamError(String msg) {
        super.renderJson(new ApiResult(ApiStatus.paramError, msg));
    }

    /**
     * 操作失败
     */
    public void renderOperateError(String msg) {
        super.renderJson(new ApiResult(ApiStatus.operateError, msg));
    }

    /**
     * 系统错误
     */
    public void renderCommonError(int errorCode, String msg) {
        super.renderJson(new ApiResult(errorCode, msg));
    }

    /**
     * 继承getPara(),去掉空格
     */
    @Override
    public String getPara() {
        String value = super.getPara();
        return StrKit.isBlank(value) ? value : value.trim();
    }

    /**
     * 继承getPara(),去掉空格
     */
    @Override
    public String getPara(String name) {
        String value = super.getPara(name);
        return StrKit.isBlank(value) ? value : value.trim();
    }

    /**
     * 继承getPara(),去掉空格
     */
    @Override
    public String getPara(String name, String defaultValue) {
        String value = super.getPara(name, defaultValue);
        return StrKit.isBlank(value) ? value : value.trim();
    }

    @Override
    public Integer getParaToInt() {
        return super.getParaToInt();
    }

    @Override
    public Integer getParaToInt(String name) {
        return super.getParaToInt(name);
    }

    @Override
    public Integer getParaToInt(String name, Integer defaultValue) {
        return super.getParaToInt(name, defaultValue);
    }

    protected BigDecimal getParaToDecimal(String name) {
        String value = super.getPara(name);
        value = StrKit.isBlank(value) ? value : value.trim();
        if (StrKit.notBlank(value)) {
            return new BigDecimal(value);
        }
        return null;
    }

    @Override
    public Integer[] getParaValuesToInt(String name) {
        Integer[] values = super.getParaValuesToInt(name);
        if (values == null || values.length <= 0) {
            return super.getParaValuesToInt(name + "[]");
        }
        return values;
    }

    /**
     * 取Request中的数据对象
     */
    protected <T> T getVo(Class<T> valueType) {
        String data = HttpKit.readData(getRequest());
        return JSON.parseObject(data, valueType);
    }

    /**
     * 获取Record
     */
    protected Record getRecord() {
        Map<String, Object> map = JSON.parseObject(HttpKit.readData(getRequest()),
                new TypeReference<Map<String, Object>>() {
                });
        return new Record().setColumns(map);
    }

    protected <T> List<T> getList(Class<T> clasz) {
        List<T> list = JSON.parseArray(HttpKit.readData(getRequest()), clasz);
        return list;
    }

    protected String encodingFileName(String fileName) {
        String userAgent = getRequest().getHeader("user-agent");
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = StringUtils.replace(returnFileName, "+", "%20");// 处理空格
            if (returnFileName.length() > 150 || !StringUtils.contains(userAgent, "MSIE")) {// 非IE浏览器，或文件名超过150字节
                returnFileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
                if (StringUtils.contains(userAgent, "MSIE")) {// IE
                    returnFileName = StringUtils.replace(returnFileName, " ", "%20");// 处理空格
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            if (log.isWarnEnabled()) {
                log.info("Don't support this encoding ...");
            }
        }
        return returnFileName;
    }
}