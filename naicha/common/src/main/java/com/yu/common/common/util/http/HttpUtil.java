package com.yu.common.common.util.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送http和https
 * lastEditor: 陈亚如,2414402332@qq.com; updatedTime: 2021/8/19 17:53
 */
@Slf4j
public class HttpUtil {
    // 返回k1=v1&k2=v2
    public static String toQueryStr(Map<String, String> params) {
        if (params.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            sb.append(k).append('=').append(v).append('&');
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    // param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
    public static String get(String url) {
        InputStream in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setConnectTimeout(1000 * 5);
            conn.setReadTimeout(1000 * 5);
            conn.connect();
            in = conn.getInputStream();
            return readAll(conn.getInputStream());
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return "";
    }

    public static String post(String url, String jsonParams) {
        InputStream in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(1000 * 2);
            conn.setReadTimeout(1000 * 2);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(jsonParams.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
            in = conn.getInputStream();
            return readAll(in);
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    public static String postHttps(String url, String jsonParams) {
        InputStream in = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{ new TrustAnyTrustManager() }, new java.security.SecureRandom());

            URL console = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(1000 * 2);
            conn.setReadTimeout(1000 * 2);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(jsonParams.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
            in = conn.getInputStream();
            return readAll(in);
        } catch (Exception e) {
            System.out.println("postHttps请求失败, " + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    private static String readAll(InputStream in) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int len = 0;
        while ((len = in.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        return outStream.toString("UTF-8");
    }
}