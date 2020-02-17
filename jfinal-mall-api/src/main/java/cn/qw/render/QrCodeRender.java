package cn.qw.render;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QrCodeRender 生成二维码
 */
public class QrCodeRender extends Render {

    private String content;
    private String title;
    private int width;
    private int height;
    private String logo;

    public QrCodeRender(String content, String title, int width, int height, String logo) {
        if (StrKit.isBlank(content)) {
            throw new IllegalArgumentException("content 不能为空");
        }
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width与 height不能小于 0");
        }
        this.content = content;
        this.title = title;
        this.width = width;
        this.height = height;
        this.logo = logo;
    }

    private static BufferedImage scale(String srcImageFile, int height, int width, boolean hasFiller)
            throws IOException {
        double ratio = 0.0; // 缩放比例
        File file = new File(srcImageFile);
        BufferedImage srcImage = ImageIO.read(file);
        Image destImage = srcImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        // 计算比例
        if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        if (hasFiller) { // 补白
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == destImage.getWidth(null)) {
                graphic.drawImage(destImage, 0, (height - destImage.getHeight(null)) / 2, destImage.getWidth(null),
                        destImage.getHeight(null), Color.white, null);
            } else {
                graphic.drawImage(destImage, (width - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null),
                        destImage.getHeight(null), Color.white, null);
            }
            graphic.dispose();
            destImage = image;
        }
        return (BufferedImage) destImage;
    }

    @Override
    public void render() {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        try {
            ImageIO.write(genBarcode(content, title, width, height, logo), "png", response.getOutputStream());
        } catch (Exception e) {
            throw new RenderException(e);
        }
    }

    private BufferedImage genBarcode(String content, String title, int width, int height, String srcImagePath)
            throws WriterException, IOException, FontFormatException {
        int textH = height - width;
        int logoW = width / 4;
        int logoH = (height - textH) / 4;
        int logoHW = width / 8;
        int boderW = 2;
        BufferedImage scaleImage = scale(srcImagePath, logoW, logoH, true);
        int[][] srcPixels = new int[logoW][logoH];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }
        Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        hint.put(EncodeHintType.MARGIN, 0); // 去掉白色边框，极度重要，否则二维码周围的白边会很宽

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height - textH, hint);
        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];
        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                if (x > halfW - logoHW && x < halfW + logoHW && y > halfH - logoHW && y < halfH + logoHW) {
                    pixels[y * width + x] = srcPixels[x - halfW + logoHW][y - halfH + logoHW];
                } else if ((x > halfW - logoHW - boderW && x < halfW - logoHW + boderW && y > halfH - logoHW - boderW
                        && y < halfH + logoHW + boderW)
                        || (x > halfW + logoHW - boderW && x < halfW + logoHW + boderW && y > halfW - logoHW - boderW
                        && y < halfH + logoHW + boderW)
                        || (x > halfW - logoHW - boderW && x < halfW + logoHW + boderW && y > halfH - logoHW - boderW
                        && y < halfH - logoHW + boderW)
                        || (x > halfW - logoHW - boderW && x < halfW + logoHW + boderW && y > halfH + logoHW - boderW
                        && y < halfH + logoHW + boderW)) {
                    pixels[y * width + x] = 0xfffffff;// 在图片四周形成边框
                } else {
                    pixels[y * width + x] = matrix.get(x, y) ? 0x000000 : 0xffffff;
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = height - textH; j < height; j++) {
                pixels[j * width + i] = 0xffffff;
            }
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        File fi = new File(PathKit.getRootClassPath() + "/simhei.ttf");
        g.setFont(Font.createFont(Font.TRUETYPE_FONT, fi).deriveFont(Font.PLAIN, textH / 2));
        int strWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - strWidth) / 2, height - textH / 3);
        g.dispose();
        return image;
    }
}