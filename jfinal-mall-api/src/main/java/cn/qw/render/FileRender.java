package cn.qw.render;

import cn.qw.kit.FileKit;
import com.jfinal.render.Render;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileRender extends Render {
    private static final String DEFAULT_FILE_CONTENT_TYPE = "application/octet-stream;charset=" + getEncoding();
    private String path;

    public FileRender(String path) {
        this.path = path;
    }

    @Override
    public void render() {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            String[] split = path.split("/");

            String cd =
                    "attachment; filename=" + new String((split[split.length - 1]).getBytes("GB2312"), "ISO-8859-1");

            response.addHeader("Content-Disposition", cd);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        response.setContentType(DEFAULT_FILE_CONTENT_TYPE);
        try {
            response.getOutputStream().write(FileKit.getBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
