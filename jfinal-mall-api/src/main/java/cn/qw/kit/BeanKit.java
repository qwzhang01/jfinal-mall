package cn.qw.kit;

import com.jfinal.plugin.activerecord.Record;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanKit {

    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }



    public static Map<String, String> fieldType(Object object) {
        Map<String, String> result = new HashMap<>();
        if(object == null) {
            return null;
        }
        Map<String, Object> map = null;
        if(object instanceof Record) {
            Record record = (Record) object;
            map = record.getColumns();

        }
        if(object instanceof Map) {
            map = (Map<String, Object>) object;
        }
        if(map != null) {
            for(String k: map.keySet()){
                Object v = map.get(k);
                String typeName = v.getClass().getName();
                result.put(k, typeName);
            }
            return result;
        }

        // 拿到该类
        Class<?> clz = object.getClass();
        // 获取实体类的所有属性，返回Field数组
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            String typeName = type.getName();
            String name = field.getName();
            result.put(name, toLowerCaseFirstOne(typeName));
        }
        return result;
    }

    private static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }
    /**
     * 把一个字符串的第一个字母大写、效率是最高的
     */
    private static String firstUp(String fildeName) {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    private static String firstLow(String fildeName) {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    public static Map<String, Object> toMap(Object object) {
        Class<?> clz = object.getClass();
        Field[] fields = clz.getDeclaredFields();
        try {
            Map<String, Object> map = new HashMap<>();
            for (Field field : fields) {
                field.setAccessible(true);
                    map.put(field.getName(), field.get(object));
            }
            return map;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}