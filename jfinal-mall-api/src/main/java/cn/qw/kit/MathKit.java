package cn.qw.kit;

import com.jfinal.kit.StrKit;

import java.text.DecimalFormat;

public class MathKit {

    /**
     * 格式化金额，并千分符化
     *
     * @param money
     */
    public static String formatMoney(Object money) {
        if (money == null) {
            return "0.00";
        }
        DecimalFormat d1 = new DecimalFormat("#,##0.##;(#)");
        return d1.format(money);
    }

    /**
     * 比较版本号大小
     * 前者大则返回一个正数,后者大返回一个负数,相等则返回0
     */
    public static int compareVersion(String version1, String version2) {
        if (!StrKit.notBlank(version1, version1)) {
            throw new RuntimeException("compareVersion error:illegal params.");
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }
}
