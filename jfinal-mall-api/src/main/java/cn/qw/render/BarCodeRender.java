package cn.qw.render;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import java.util.HashMap;
import java.util.Map;

/**
 * BarCodeRender 生成二维码
 */
public class BarCodeRender extends Render {

    private String content;
    private int width;
    private int height;

    /**
     * 构造方法
     *
     * @param content 二维码携带内容
     * @param width   二维码宽度
     * @param height  二维码高度
     */
    public BarCodeRender(String content, int width, int height) {
        if (StrKit.isBlank(content)) {
            throw new IllegalArgumentException("content 不能为空");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width 与 height 不能小于 0");
        }
        this.content = content;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render() {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0); // 去掉白色边框，极度重要，否则二维码周围的白边会很宽

        try {
            // MultiFormatWriter 可支持多种格式的条形码，在此直接使用 QRCodeWriter，通过查看源码可知少创建一个对象
            // BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
            // BarcodeFormat.QR_CODE, width, height, hints);
            Code128Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, "png", response.getOutputStream()); // format:
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }
}