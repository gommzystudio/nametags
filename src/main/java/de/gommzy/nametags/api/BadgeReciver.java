package de.gommzy.nametags.api;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BadgeReciver {
    public static HashMap<String, ArrayList<Badge>> badges = new HashMap<String, ArrayList<Badge>>();
    public static HashMap<String, Date> cooldown = new HashMap<String, Date>();
    public static HttpClient HttpClient;
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    public static void load(final String uuid) {
        if (cooldown.containsKey(uuid)) {
            long diffInMillies = Math.abs(new Date().getTime() - cooldown.get(uuid).getTime());
            long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff < 10) {
                return;
            }
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cooldown.put(uuid,new Date());
                        try {
                            HttpPost httpPost = new HttpPost("https://laby.net/api/user/"+ uuid+"/get-badges");
                            httpPost.setHeader("Content-Type", "application/json");
                            if (HttpClient == null) {
                                HttpClient = getNewHttpClient();
                            }
                            HttpResponse response = HttpClient.execute((HttpUriRequest)httpPost);
                            String responseString = EntityUtils.toString(response.getEntity());
                            ArrayList<Badge> badgesList = new ArrayList<Badge>();
                            if (!responseString.contains("[]") && !responseString.contains("User not found")) {
                                try {
                                    for (String loop : responseString.split("\",\"name\":\"")) {
                                        String badgeuuid = loop.split("\"uuid\":\"")[1];
                                        //if (!badgeuuid.contains("cbcf5a7c-d325-4c5e-b918-adbc98343195")) {
                                        downloadImage(badgeuuid);
                                        Badge badge = new Badge(badgeuuid);
                                        badgesList.add(badge);
                                        badges.put(uuid, badgesList);
                                        //}
                                    }
                                } catch (Exception ignored) {}
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public static ArrayList<String> blacklist = new ArrayList<String>();

    public static void downloadImage(String uuid) {
        try {
            if (blacklist.contains(uuid)) {
                return;
            }
            blacklist.add(uuid);

            HttpPost httpPost = new HttpPost("https://laby.net/texture/badge-small/"+uuid+".png");
            httpPost.setHeader("Content-Type", "application/json");
            if (HttpClient == null) {
                HttpClient = getNewHttpClient();
            }
            HttpResponse httpResponse = HttpClient.execute((HttpUriRequest)httpPost);
            InputStream in = httpResponse.getEntity().getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            File directory = new File(System.getProperty("user.dir") + "\\LabyMod\\badges");
            if (! directory.exists()){
                directory.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "\\LabyMod\\badges\\"+uuid+".png");
            fos.write(response);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    public static ArrayList<Badge> getBadge(final String uuid) {
        try {
            if (badges.containsKey(uuid)) {
                return badges.get(uuid);
            } else {
                load(uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
