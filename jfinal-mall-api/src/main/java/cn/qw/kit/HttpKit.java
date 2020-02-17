package cn.qw.kit;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

public class HttpKit {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    protected static final Log log = Log.getLog(HttpKit.class);
    private static final String CHARSET = "UTF-8";

    private static final SSLSocketFactory SSL_SOCKET_FACTORY = initSSLSocketFactory();
    private static final TrustAnyHostnameVerifier HOSTNAME_VERIFIER = new HttpKit().new TrustAnyHostnameVerifier();

    private HttpKit() {
    }

    private static HttpURLConnection getHttpConnection(String url,
                                                       String method, Map<String, String> headers) throws IOException,
            NoSuchAlgorithmException, NoSuchProviderException,
            KeyManagementException {

        URL _url = new URL(url);
        HttpURLConnection conn = null;
        if (PropKit.getBoolean("devMode")) {
            SocketAddress skt = new InetSocketAddress("222.173.249.166", 9998);
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, skt);
            conn = (HttpURLConnection) _url.openConnection(proxy);
        } else {
            conn = (HttpURLConnection) _url.openConnection();
        }
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(SSL_SOCKET_FACTORY);
            ((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);
        }
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(19000);
        conn.setReadTimeout(19000);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 ]"
                        + "(KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (headers != null && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return conn;
    }

    /**
     * 发送 GET请求
     */
    public static String get(String url, Map<String, String> queryParas,
                             Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
                    GET, headers);
            conn.connect();
            return readResponseString(conn);
        } catch (KeyManagementException | NoSuchAlgorithmException
                | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    public static String get(String url, Map<String, String> queryParas) {
        return get(url, queryParas, null);
    }

    public static String get(String url) {
        return get(url, null, null);
    }


    /**
     * 发送 POST请求
     */
    public static String post(String url, Map<String, String> queryParas,
                              String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
                    POST, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(CHARSET));
            out.flush();
            out.close();
            return readResponseString(conn);
        } catch (KeyManagementException | NoSuchAlgorithmException
                | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String post(String url, Map<String, String> queryParas,
                              Map<String, String> datas, Map<String, String> headers) {
        return post(url, queryParas, buildPostData(datas), headers);
    }

    public static String post(String url, Map<String, String> queryParas,
                              String data) {
        return post(url, queryParas, data, null);
    }

    public static String post(String url, Map<String, String> queryParas,
                              Map<String, String> datas) {
        return post(url, queryParas, buildPostData(datas), null);
    }

    public static String post(String url, String data,
                              Map<String, String> headers) {
        return post(url, null, data, headers);
    }

    public static String post(String url, String data) {
        return post(url, null, data, null);
    }

    public static String post(String url, Map<String, String> datas) {
        return post(url, null, buildPostData(datas), null);
    }

    private static <T> Model<?> checkModel(Class<T> clazz) {
        Object model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (model instanceof Model) {
            return (Model<?>) model;
        } else {
            throw new RuntimeException(clazz.getSimpleName()
                    + " must be instanceof Model!");
        }
    }

    /**
     * 发送 PUT请求
     */
    public static String put(String url, Map<String, String> queryParas,
                             String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
                    PUT, headers);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes(CHARSET));
            out.flush();
            out.close();
            return readResponseString(conn);
        } catch (KeyManagementException | NoSuchAlgorithmException
                | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String put(String url, Map<String, String> queryParas,
                             Map<String, String> datas, Map<String, String> headers) {
        return put(url, queryParas, buildPostData(datas), headers);
    }

    public static String put(String url, Map<String, String> queryParas,
                             String data) {
        return put(url, queryParas, data, null);
    }

    public static String put(String url, Map<String, String> queryParas,
                             Map<String, String> datas) {
        return put(url, queryParas, buildPostData(datas), null);
    }

    public static String put(String url, String data,
                             Map<String, String> headers) {
        return put(url, null, data, headers);
    }

    public static String put(String url, String data) {
        return put(url, null, data, null);
    }

    public static String put(String url, Map<String, String> datas) {
        return put(url, null, buildPostData(datas), null);
    }

    /**
     * 发送 DELETE请求
     */
    public static String delete(String url, Map<String, String> queryParas,
                                Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
                    DELETE, headers);
            conn.connect();
            return readResponseString(conn);
        } catch (KeyManagementException | NoSuchAlgorithmException
                | NoSuchProviderException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String delete(String url, Map<String, String> queryParas) {
        return delete(url, queryParas, null);
    }

    public static String delete(String url) {
        return delete(url, null, null);
    }

    private static String readResponseString(HttpURLConnection conn)
            throws IOException {
        int http_status = conn.getResponseCode();
        if (http_status >= 200 && http_status < 300) {
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, CHARSET));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
        throw new RuntimeException(conn.getResponseMessage());
    }

    /**
     * 在 url 之后构造 queryString
     */
    public static String buildUrlWithQueryString(String url,
                                                 Map<String, String> queryParas) {
        if (queryParas == null || queryParas.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        boolean isFirst;
        if (url.indexOf("?") == -1) {
            isFirst = true;
            sb.append("?");
        } else {
            isFirst = false;
        }

        for (Entry<String, String> entry : queryParas.entrySet()) {
            String value = entry.getValue();
            if (StrKit.notBlank(value)) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }
                try {
                    value = URLEncoder.encode(value, CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                sb.append(entry.getKey()).append("=").append(value);
            }
        }
        return sb.toString();
    }

    /**
     * 构造 Post Data
     */
    public static String buildPostData(Map<String, String> datas) {
        if (datas == null || datas.isEmpty()) {
            return "";
        }

        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : datas.entrySet()) {
            String value = entry.getValue();
            if (StrKit.notBlank(value)) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append("&");
                }

                try {
                    value = URLEncoder.encode(value, CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                sb.append(entry.getKey()).append("=").append(value);
            }
        }
        return sb.toString();
    }

    public static String readIncommingRequestData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line = null; (line = br.readLine()) != null; ) {
                result.append(line).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static SSLSocketFactory initSSLSocketFactory() {
        try {
            TrustManager[] tm = {new HttpKit().new TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * https 域名校验
     */
    private class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * https 证书管理
     */
    private class TrustAnyTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }
}
