package cn.qw.kit;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ValidateKit {

    private static final String EMAIL_ADDRESS_PATTERN = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

    private static final String MOBILE_PATTERN = "\\b(1[3,4,5,7,8,9]\\d{9})\\b";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Validate Required.
     */
    public static boolean validateRequired(String value) {
        // 经测试,无输入时值为"",跳格键值为"\t",输入空格则为空格" "
        return (value != null && !("".equals(value.trim())));
    }

    /**
     * Validate integer.
     *
     * @return
     */
    public static boolean validateInteger(String value, int min, int max) {
        try {
            int temp = Integer.parseInt(value);
            if (temp > min && temp > max) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Validate long.
     *
     * @return
     */
    public static boolean validateLong(String value, long min, long max) {
        try {
            long temp = Long.parseLong(value);
            if (temp > min && temp < max) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Validate long.
     *
     * @return
     */
    public static boolean validateLong(String value) {
        try {
            Long.parseLong(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Validate double.
     *
     * @return
     */
    public static boolean validateDouble(String value, double min, double max) {
        try {
            double temp = Double.parseDouble(value);
            if (temp > min && temp < max) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Validate double.
     */
    public static boolean validateDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Validate date. Date formate: yyyy-MM-dd
     *
     * @return
     */
    public static boolean validateDate(String value, String min, String max) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            Date temp = sdf.parse(value);
            if (temp.after(sdf.parse(min)) && temp.before(sdf.parse(max))) {
                return true;
            }
        } catch (ParseException e) {
        }
        return false;
    }

    /**
     * Validate equal string.
     *
     * @return
     */
    public static boolean validateEqualString(String s1, String s2) {
        return !(s1 == null || s2 == null || (!s1.equals(s2)));
    }

    /**
     * Validate equal integer.
     *
     * @return
     */
    public static boolean validateEqualInteger(Integer i1, Integer i2) {
        return !(i1 == null || i2 == null || (i1.intValue() != i2.intValue()));
    }

    /**
     * Validate email.
     *
     * @return
     */
    public static boolean validateEmail(String value) {
        return validateRegex(value, EMAIL_ADDRESS_PATTERN, false);
    }

    /**
     * Validate mobile.
     *
     * @return
     */
    public static boolean validateMobile(String value) {
        return validateRegex(value, MOBILE_PATTERN, false);
    }

    /**
     * Validate URL.
     */
    public static boolean validateUrl(String value) {
        try {
            // URL doesn't understand the https protocol, hack it
            if (value.startsWith("https://")) {
                value = "http://" + value.substring(8);
            }
            new URL(value);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * Validate regular expression.
     */
    public static boolean validateRegex(String value, String regExpression, boolean isCaseSensitive) {
        if (value == null) {
            return false;
        }
        Pattern pattern = isCaseSensitive ? Pattern.compile(regExpression)
                : Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(value).matches();
    }

    /**
     * Validate regular expression and case sensitive.
     */
    public static boolean validateRegex(String value, String regExpression) {
        return validateRegex(value, regExpression, true);
    }

    public static boolean validateString(String value, int minLen, int maxLen) {
        return (value == null || value.trim().length() < minLen || value.trim().length() > maxLen);
    }
}
