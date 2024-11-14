package cn.lary.module.auth;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class LaryHttpManager {

    private static final int MAX_TIMEOUT = 400;
    private static final CloseableHttpClient httpClient;
    private static final Logger log = LoggerFactory.getLogger(LaryHttpManager.class);
    private static final HttpClientResponseHandler<String> handler;

    static {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", buildSSLConnSocketFactory())
                .build();
        // set connect pool
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(registry);
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal()); // 设置每条路由的最大并发连接数

        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(MAX_TIMEOUT))                    // 传输超时
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(MAX_TIMEOUT))         // 设置从连接池获取连接实例的超时
                .build();

        httpClient = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy())  // 重试 1 次，间隔1s
                .setDefaultRequestConfig(requestConfig)
                .build();
        handler = new BasicHttpClientResponseHandler();

    }
    /**
     * do http post
     * @param url u
     * @return response
     */
    public static String post(String url) {
        try {
            return httpClient.execute(new BasicClassicHttpRequest(Method.POST, url), handler);
        } catch (IOException e) {
            log.error("lary post http error,message:{},url:{}", e.getMessage(), url);
        }
        return null;
    }
    public static String post(String url, Map<String, String> params) {
        return null;
    }
    /**
     * do http get
     * @param url u
     * @return response
     */
    public static String get(String url) {
        try {
            return httpClient.execute(new BasicClassicHttpRequest(Method.GET,url),handler);
        }catch (IOException e) {
            log.error("lary get http error,message:{},url:{}", e.getMessage(), url);
        }
        return null;
    }
    private static SSLConnectionSocketFactory buildSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    //trust all
                    return true;
                }
            });
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage(), e);
        }
        return sslsf;
    }
}
