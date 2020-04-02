package cn.qw.test;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MockServletInputStream extends ServletInputStream {

    private InputStream is;

    public MockServletInputStream(String str)
            throws UnsupportedEncodingException {
        is = new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}