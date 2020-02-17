package cn.qw.render;

import cn.qw.kit.CryptKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码Render，这个验证码Render在构造函数里就已经创建好了随机码以及md5散列后的随机码。 保存md5RandonCode到session、
 * cookie或者其他地方 render(captchaRender); 基于JFinal的版本修改。
 */
public class CaptchaRender extends Render {

    /**
     * 默认存储时使用的key,将md5散列后的随机码保存至session，cookie时使用。
     */
    public static final String CAPTCHA_KEY = "_CAPTCHA_CODE_";
    private final int imgWidth;// 图片宽度
    private final int imgHeight;// 图片高度
    private final int imgRandNumber;// 随机生成字符数量
    private final String randonCode;// 生成的随机码
    private final String md5RandonCode;// md5散列后的随机码

    /**
     * 构造函数,随机生成4个字符。
     */
    public CaptchaRender() {
        this(4);
    }

    /**
     * 构造函数
     *
     * @param imgRandNumber 随机生成多少个字符,最少4个字符。
     */
    public CaptchaRender(int imgRandNumber) {
        if (imgRandNumber < 4) {
            imgRandNumber = 4;
        }
        this.imgWidth = 16 * imgRandNumber + 20;
        this.imgHeight = 38;
        this.imgRandNumber = imgRandNumber;
        this.randonCode = generateRandonCode();
        this.md5RandonCode = CryptKit.md5(randonCode);
    }

    /**
     * 验证码检查
     *
     * @param md5RandomCode   md5散列后的验证码
     * @param inputRandomCode 用户输入的验证码
     * @return 若二者一致，返回true，否则返回false
     */
    public static boolean validate(String md5RandomCode, String inputRandomCode) {
        if (StrKit.notBlank(md5RandomCode, inputRandomCode)) {
            inputRandomCode = inputRandomCode.toUpperCase();
            inputRandomCode = CryptKit.md5(inputRandomCode);
            return inputRandomCode.equals(md5RandomCode);
        }
        return false;
    }

    /**
     * 获取md5散列后的验证码，调用发需妥善保存此验证码。
     *
     * @return md5散列后的验证码
     */
    public String getMd5RandonCode() {
        return this.md5RandonCode;
    }

    /**
     * 依据字典生成随即码
     *
     * @return 随机码
     */
    private String generateRandonCode() {
        return RandomStringUtils.randomNumeric(imgRandNumber);
    }

    /**
     * 渲染图片
     */
    @Override
    public void render() {
        BufferedImage image = new BufferedImage(imgWidth, imgHeight,
                BufferedImage.TYPE_INT_RGB);
        drawGraphic(image);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            ImageIO.write(image, "jpeg", sos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 绘制验证码
     *
     * @param image BufferedImage对象
     */
    private void drawGraphic(BufferedImage image) {
        Graphics g = image.createGraphics();// 获取图形上下文
        g.setColor(Color.LIGHT_GRAY);// 设定背景色
        g.fillRect(0, 0, imgWidth, imgHeight);

        for (int i = 0; i < imgRandNumber; i++) {
            g.setColor(Color.BLACK);
            String rand = String.valueOf(randonCode.charAt(i));
            g.drawString(rand, 16 * i + 6, 21);// 将认证码显示到图象中
        }
        g.dispose();// 图象生效
    }
}