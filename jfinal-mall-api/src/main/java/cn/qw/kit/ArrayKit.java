package cn.qw.kit;

/**
 * 数组操作工具方法
 */
public class ArrayKit {
    /**
     * 将数组组合成字符串
     */
    public static <T> String join(T[] array) {
        return join(array, ",");
    }

    /**
     * 将数组组合成字符串
     */
    public static <T> String join(T[] array, String spit) {
        if (array == null || array.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString().trim() + spit);
        }
        int spltLength = spit.length();
        int sbLength = sb.length();
        return sb.substring(0, sbLength - spltLength);
    }

    /**
     * 数组截取
     */
    public static <T> T[] subArray(T[] source, int start, int end) {
        Object[] result = new Object[end - start + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = source[start];
            start++;
        }
        return (T[]) result;
    }
}