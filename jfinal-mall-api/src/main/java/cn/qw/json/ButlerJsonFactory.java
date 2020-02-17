package cn.qw.json;

import com.jfinal.json.JFinalJsonFactory;
import com.jfinal.json.Json;

public class ButlerJsonFactory extends JFinalJsonFactory {
    private static final ButlerJsonFactory me = new ButlerJsonFactory();

    public static ButlerJsonFactory me() {
        return me;
    }

    @Override
    public Json getJson() {
        return new ButlerlJson();
    }
}
