package cn.qw.kit;

import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceKit {
    private static final String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"
            + "|windows (phone|ce)|blackberry"
            + "|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"
            + "|laystation portable)|nokia|fennec|htc[-_]"
            + "|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";
    private static final String tableReg = "\\b(ipad|tablet|(Nexus 7)|up.browser"
            + "|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";

    private static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);
    private static Pattern tablePat = Pattern.compile(tableReg, Pattern.CASE_INSENSITIVE);

    public static int what(HttpServletRequest request) {
        String device = request.getHeader("device");
        if (StrKit.isBlank(device)) {
            device = request.getParameter("device");
        }
        if ("Android_APP".equalsIgnoreCase(device)) {
            return 1;
        }
        if ("IOS_APP".equalsIgnoreCase(device)) {
            return 2;
        }
        String userAgent = request.getHeader("user-agent").toLowerCase();
        if (userAgent.contains("micromessenger")) {
            return 3;
        }
        if (check(userAgent)) {
            return 4;
        }
        return 5;
    }

    private static boolean check(String userAgent) {
        if (null == userAgent) {
            userAgent = "";
        }
        // 匹配
        Matcher matcherPhone = phonePat.matcher(userAgent);
        Matcher matcherTable = tablePat.matcher(userAgent);
        if (matcherPhone.find() || matcherTable.find()) {
            return true;
        } else {
            return false;
        }
    }
}
