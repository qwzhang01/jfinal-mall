package cn.qw.render;

import com.jfinal.render.Render;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PdfRender extends Render {
    private static final String DEFAULT_FILE_CONTENT_TYPE = "application/octet-stream;charset=" + getEncoding();

    private byte[] pdfBytes;
    private String fileName;

    public PdfRender(byte[] pdfBytes, String fileName) {
        this.pdfBytes = pdfBytes;
        this.fileName = fileName;
    }

    @Override
    public void render() {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.addHeader("Content-Disposition", "attachment; filename=" + new String((fileName + ".pdf").getBytes("GB2312"), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        response.setContentType(DEFAULT_FILE_CONTENT_TYPE);
        try {
            response.getOutputStream().write(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
