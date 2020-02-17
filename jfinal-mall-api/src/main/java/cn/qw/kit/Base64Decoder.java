package cn.qw.kit;

import com.jfinal.log.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;

public class Base64Decoder extends FilterInputStream {
    private static final Log LOG = Log.getLog(Base64Decoder.class);

    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '+', '/'};

    private static final int[] INTS = new int[128];

    static {
        for (int i = 0; i < 64; i++) {
            INTS[CHARS[i]] = i;
        }
    }

    private int charCount;
    private int carryOver;

    public Base64Decoder(InputStream in) {
        super(in);
    }

    public static String decode(String encoded) {
        return new String(decodeToBytes(encoded));
    }

    public static byte[] decodeToBytes(String encoded) {
        Base64Decoder in = null;
        ByteArrayOutputStream out = null;
        try {
            byte[] bytes = encoded.getBytes("8859_1");
            in = new Base64Decoder(new ByteArrayInputStream(bytes));
            out = new ByteArrayOutputStream((int) (bytes.length * 0.67));
            byte[] buf = new byte[4 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (UnsupportedEncodingException ignored) {
            LOG.error(ExceptionUtils.getStackTrace(ignored));
            return null;
        } catch (IOException ignored) {
            LOG.error(ExceptionUtils.getStackTrace(ignored));
            return null;
        } finally {
            try {
                if (out != null) {

                    out.close();
                    out = null;
                }
                if (in != null) {
                    in.close();
                    in = null;

                }
            } catch (IOException e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public int read() throws IOException {
        int x;
        do {
            x = in.read();
            if (x == -1) {
                return -1;
            }
        } while (Character.isWhitespace((char) x));
        charCount++;

        if (x == '=') {
            return -1;
        }

        x = INTS[x];

        int mode = (charCount - 1) % 4;

        if (mode == 0) {
            carryOver = x & 63;
            return read();
        } else if (mode == 1) {
            int decoded = ((carryOver << 2) + (x >> 4)) & 255;
            carryOver = x & 15;
            return decoded;
        } else if (mode == 2) {
            int decoded = ((carryOver << 4) + (x >> 2)) & 255;
            carryOver = x & 3;
            return decoded;
        } else if (mode == 3) {
            int decoded = ((carryOver << 6) + x) & 255;
            return decoded;
        }
        return -1;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (buf.length < (len + off - 1)) {
            throw new IOException("The input buffer is too small: " + len + " bytes requested starting at offset " + off
                    + " while the buffer " + " is only " + buf.length + " bytes long.");
        }

        int i;
        for (i = 0; i < len; i++) {
            int x = read();
            if (x == -1 && i == 0) {
                return -1;
            } else if (x == -1) {
                break;
            }
            buf[off + i] = (byte) x;
        }
        return i;
    }
}