package com.yunchuan.bilibili.client.httpclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HttpUtils {

    @Autowired
    CloseableHttpClient client;

    public String httpsGet(HttpUriRequest request) {

        String result = null;

        try {
            CloseableHttpResponse response = client.execute(request);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,"UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] httpsGetByte(HttpUriRequest request) {

        byte[] result = null;

        try {
            CloseableHttpResponse response = client.execute(request);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toByteArray(resEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}

class SSLClient extends DefaultHttpClient {
    //用于进行Https请求的HttpClient
    public SSLClient() throws Exception {
        super();
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException { }

            public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException { }

            public X509Certificate[] getAcceptedIssuers() {return null; }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }
}