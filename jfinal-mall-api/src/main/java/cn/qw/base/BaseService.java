package cn.qw.base;

import com.jfinal.log.Log;

import java.util.HashMap;
import java.util.Map;

public class BaseService {

    protected final Log log = Log.getLog(getClass());

    protected String CACHE_NAME;

    protected BaseService() {
        CACHE_NAME = getClass().getName();
    }

    protected Map<String, Object> searchParam(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    protected Map<String, Object> searchParam(Map<String, Object> map, String key, Object value) {
        map.put(key, value);
        return map;
    }


}
