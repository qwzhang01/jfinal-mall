package cn.qw.kit;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptKit {

    public static final String KEY_ALGORITHM = "RSA";
    private static final Log log = Log.getLog(CryptKit.class);
    private static final int MAX_ENCRYPT_BLOCK = 117;
    // 安全密钥
    private static String SECURITY_TOKEN = "qw@2016";

    /**
     * MD5加密
     *
     * @param str 明文字符串
     * @return 加密后字符串
     * @throws Exception
     */
    public static String cymd5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

    /**
     * 缓存token加密
     *
     * @param str
     * @return
     */
    public static String qwCry(String str) {
        return CryptKit.cymd5(SECURITY_TOKEN + str + SECURITY_TOKEN);
    }

    public static String sha1(String str) {
        if (StrKit.isBlank(str)) {
            return null;
        }
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
            sha.update(str.getBytes("utf8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        byte[] bytes = sha.digest();
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(Character.forDigit((bytes[i] >> 4) & 0xf, 16));
            ret.append(Character.forDigit(bytes[i] & 0xf, 16));
        }
        return ret.toString();
    }

    /**
     * MD5加密
     *
     * @param str 明文字符串
     * @return 加密后字符串
     * @throws Exception
     */
    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 使用微信的公钥，RSA公钥加密
     *
     * @param text 加密字符串
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String wxRsaEncrypt(String text) {
        String publicKey = PropKit.get("wx_rsa_public_key");
        try {
            //base64编码的公钥
            byte[] decoded = Base64Util.decode(publicKey);
            RSAPublicKey pubKey = null;
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = null;
            cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] bytes = cipher.doFinal(text.getBytes("UTF-8"));
            String outStr = Base64Util.encode(bytes);
            return outStr;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (NoSuchPaddingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (InvalidKeyException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (BadPaddingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (IllegalBlockSizeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return "";
    }
}
