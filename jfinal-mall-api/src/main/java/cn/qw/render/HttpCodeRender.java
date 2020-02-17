package cn.qw.render;

import com.jfinal.render.Render;

/**
 * HttpCodeRender.
 */
public class HttpCodeRender extends Render {

    /**
     * HTTP Code
     */
    private int httpCode;

    public HttpCodeRender(int code) {
        this.httpCode = code;
    }

    @Override
    public void render() {
        response.setStatus(httpCode);
    }
}
