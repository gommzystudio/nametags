package de.gommzy.nametags.api;

import net.labymod.utils.texture.ThreadDownloadTextureImage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
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
                            CloseableHttpClient httpClient = HttpClients.custom()
                                    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                                    .loadTrustMaterial(null, new TrustStrategy() {
                                                        @Override
                                                        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                                            return true;
                                                        }
                                                    })
                                                    .build()
                                            )
                                    ).build();
                            HttpPost httpPost = new HttpPost("https://laby.net/api/user/"+ uuid+"/get-badges");
                            httpPost.setHeader("Content-Type", "application/json");
                            HttpResponse response = httpClient.execute((HttpUriRequest)httpPost);
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

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                    .loadTrustMaterial(null, new TrustStrategy() {
                                        @Override
                                        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                                            return true;
                                        }
                                    })
                                    .build()
                            )
                    ).build();
            HttpPost httpPost = new HttpPost("https://laby.net/texture/badge-small/"+uuid+".png");
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = httpClient.execute((HttpUriRequest)httpPost);
            InputStream in = new BufferedInputStream(httpResponse.getEntity().getContent());
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
