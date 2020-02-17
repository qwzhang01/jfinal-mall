package cn.qw.kit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonKit {

    public static <T> T toBean(String jsonStr, Class<T> clazz) {
        T object = JSON.parseObject(jsonStr, clazz);
        return object;
    }

    private static <T> Model<?> checkModel(Class<T> clazz) {
        Object model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (model instanceof Model) {
            return (Model<?>) model;
        } else {
            throw new RuntimeException(clazz.getSimpleName()
                    + " must be instanceof Model!");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T toModel(String text, Class<T> clazz) {
        if (StrKit.notBlank(text)) {
            Model<?> result = checkModel(clazz);
            Map<String, Object> map = JSON.parseObject(text,
                    new TypeReference<Map<String, Object>>() {
                    });
            for (Entry<String, Object> e : map.entrySet()) {
                result.set(e.getKey(), e.getValue());
            }
            return (T) result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String text, Class<T> clazz) {
        List<T> modelList = new ArrayList<T>();
        if (StrKit.notBlank(text)) {
            List<Map<String, Object>> mapList = JSON.parseObject(text,
                    new TypeReference<ArrayList<Map<String, Object>>>() {
                    });
            for (Map<String, Object> map : mapList) {
                Model<?> result = checkModel(clazz);
                for (Entry<String, Object> e : map.entrySet()) {
                    result.set(e.getKey(), e.getValue());
                }
                modelList.add((T) result);
            }
        }
        return modelList;
    }

    public static Record toRecord(String text) {
        if (StrKit.notBlank(text)) {
            Record result = new Record();
            Map<String, Object> map = JSON.parseObject(text,
                    new TypeReference<Map<String, Object>>() {
                    });
            for (Entry<String, Object> e : map.entrySet()) {
                result.set(e.getKey(), e.getValue());
            }
            return result;
        }
        return null;
    }

    public static List<Record> toList(String text) {
        List<Record> recodrList = new ArrayList<Record>();
        if (StrKit.notBlank(text)) {
            List<Map<String, Object>> mapList = JSON.parseObject(text,
                    new TypeReference<ArrayList<Map<String, Object>>>() {
                    });
            for (Map<String, Object> map : mapList) {
                Record result = new Record();
                for (Entry<String, Object> e : map.entrySet()) {
                    result.set(e.getKey(), e.getValue());
                }
                recodrList.add(result);
            }
        }
        return recodrList;
    }

    public static String toJson(Object value) {
        return com.jfinal.kit.JsonKit.toJson(value);
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer("\n");
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0
                    && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();
    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
}
