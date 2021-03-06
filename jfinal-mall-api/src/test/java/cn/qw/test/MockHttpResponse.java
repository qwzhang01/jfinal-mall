package cn.qw.test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;

public class MockHttpResponse implements HttpServletResponse {

    private StringWriter resp;

    public MockHttpResponse(StringWriter resp) {
        this.resp = resp;
    }

    @Override
    public void addCookie(Cookie arg0) {

    }

    @Override
    public void addDateHeader(String arg0, long arg1) {

    }

    @Override
    public void addHeader(String arg0, String arg1) {

    }

    @Override
    public void addIntHeader(String arg0, int arg1) {

    }

    @Override
    public boolean containsHeader(String arg0) {

        return false;
    }

    @Override
    public String encodeRedirectUrl(String arg0) {

        return null;
    }

    @Override
    public String encodeRedirectURL(String arg0) {

        return null;
    }

    @Override
    public String encodeUrl(String arg0) {

        return null;
    }

    @Override
    public String encodeURL(String arg0) {

        return null;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public int getBufferSize() {

        return 0;
    }

    @Override
    public void setBufferSize(int arg0) {

    }

    @Override
    public String getCharacterEncoding() {

        return null;
    }

    @Override
    public void setCharacterEncoding(String arg0) {

    }

    @Override
    public String getContentType() {

        return null;
    }

    @Override
    public void setContentType(String arg0) {

    }

    @Override
    public Locale getLocale() {

        return null;
    }

    @Override
    public void setLocale(Locale arg0) {

    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter writer = new PrintWriter(resp);
        return writer;
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public void sendError(int arg0) throws IOException {

    }

    @Override
    public void sendError(int arg0, String arg1) throws IOException {

    }

    @Override
    public void sendRedirect(String arg0) throws IOException {

    }

    @Override
    public void setContentLength(int arg0) {

    }

    @Override
    public void setDateHeader(String arg0, long arg1) {

    }

    @Override
    public void setHeader(String arg0, String arg1) {

    }

    @Override
    public void setIntHeader(String arg0, int arg1) {

    }

    @Override
    public void setStatus(int arg0, String arg1) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void setStatus(int arg0) {

    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

}
