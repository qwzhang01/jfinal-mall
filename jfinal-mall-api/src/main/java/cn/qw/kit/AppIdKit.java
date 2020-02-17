package cn.qw.kit;

import com.jfinal.plugin.activerecord.Record;

/**
 * 将 请求来源 公共参数 绑定到 ThreadLocal 的工具类，以方便在当前线程的各个地方获取
 */
public class AppIdKit {

    private static final ThreadLocal<Record> TL = new ThreadLocal<>();

    public static void removeThreadLocal() {
        TL.remove();
    }

    public static Integer getUserId() {
        Record record = TL.get();
        if (record == null) {
            return 0;
        }
        Object userId = record.get("userId");
        if (userId == null) {
            return 0;
        }
        return Integer.valueOf(userId.toString());
    }

    public static void setUserId(int userId) {
        Record record = TL.get();
        if (record == null) {
            record = new Record();
        }
        record.set("userId", userId);
        TL.set(record);
    }

    public static String getVersion() {
        Record record = TL.get();
        if (record == null) {
            return "";
        }
        Object version = record.get("version");
        if (version == null) {
            return "";
        }
        return version.toString();
    }

    public static void setVersion(String version) {
        Record record = TL.get();
        if (record == null) {
            record = new Record();
        }
        record.set("version", version);
        TL.set(record);
    }

    public static String getUniqueId() {
        Record record = TL.get();
        if (record == null) {
            return "";
        }
        Object uniqueId = record.get("uniqueId");
        if (uniqueId == null) {
            return "";
        }
        return uniqueId.toString();
    }

    public static void setUniqueId(String uniqueId) {
        Record record = TL.get();
        if (record == null) {
            record = new Record();
        }
        record.set("uniqueId", uniqueId);
        TL.set(record);
    }

    public static String getToken() {
        Record record = TL.get();
        if (record == null) {
            return "";
        }
        Object token = record.get("token");
        if (token == null) {
            return "";
        }
        return token.toString();
    }

    public static void setToken(String token) {
        Record record = TL.get();
        if (record == null) {
            record = new Record();
        }
        record.set("token", token);
        TL.set(record);
    }
}