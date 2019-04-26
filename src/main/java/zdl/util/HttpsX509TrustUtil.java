package zdl.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 证书信任管理器
 * <p>
 * Created by ZDLegend on 2019/3/29 10:13
 */
public class HttpsX509TrustUtil implements X509TrustManager {

    public static SSLContext buildTrustSSLContext() throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] tm = {new HttpsX509TrustUtil()};
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2", "SunJSSE");
        sslContext.init(null, tm, new SecureRandom());
        return sslContext;
    }

    public static HttpClient buildHttpClient() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        return HttpClient.newBuilder()
                .sslContext(buildTrustSSLContext())
                .build();
    }

    private HttpsX509TrustUtil() {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
