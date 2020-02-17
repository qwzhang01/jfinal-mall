package cn.qw.json;

import com.jfinal.json.JFinalJson;

import java.math.BigDecimal;

public class ButlerlJson extends JFinalJson {
    @Override
    protected String toJson(Object value, int depth) {
        if (value == null) {
            if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof BigDecimal) {
                value = 0;
            } else if (value instanceof String) {
                value = "";
            } else {
                value = "";
            }
        }
        return super.toJson(value, depth);
    }
}
