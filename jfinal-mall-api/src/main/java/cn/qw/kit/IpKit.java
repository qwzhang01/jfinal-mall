package cn.qw.kit;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpKit {

    /**
     * 根据IP，返回省市 参考：http://ip.taobao.com/instructions.php
     *
     * @param ip
     * @return {省，市}
     */
    public static String[] getAddr(String ip) {
        String url = "http://ip.taobao.com/service/getIpInfo.php?ip=";
        String result = HttpKit.get(url + ip);
        Record record = JsonKit.parse(result, Record.class);
        if (null != record) {
            int code = record.getInt("Code");
            if (code == 0) {
                Record data = JsonKit.parse(record.getStr("Data"), Record.class);
                return new String[]{data.get("Region"), data.get("City")};
            }
        }
        return null;
    }

    /**
     * 获取IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StrKit.notBlank(ip)) {
            if (ip.contains(",")) {
                String[] ips = ip.split(",");
                return ips[0];
            }
        }
        return ip;
    }

    /**
     * 判断字符串是否是一个IP地址
     */
    public static boolean isIPAddr(String addr) {
        if (StringUtils.isEmpty(addr)) {
            return false;
        }
        String[] ips = StringUtils.split(addr, '.');
        if (ips.length != 4) {
            return false;
        }
        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);
            return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <= 255 && ipc >= 0 && ipc <= 255 && ipd >= 0 && ipd <= 255;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是否内网IP
     *
     * @param ip
     * @return
     */
    public static boolean isLanIP(String ip) {
        return ip.startsWith("10.") || ip.startsWith("192.168.") || "127.0.0.1".equals(ip);
    }
}
