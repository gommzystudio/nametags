package de.gommzy.nametags.api;

import net.labymod.main.ModTextures;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.Sys;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BadgeReciver {
    public static HashMap<String, String> badgeRessourceLocations = new HashMap<String, String>();
    public static ArrayList<String> downloaded = new ArrayList<String>();
    public static HashMap<String, ArrayList<Badge>> badges = new HashMap<String, ArrayList<Badge>>();
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    public static BadgeDynamicTextureManager badgeDynamicTextureManager;

    public static void load(final String uuid) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (badges.containsKey(uuid)) {
                    return;
                }
                badges.put(uuid,new ArrayList<Badge>());
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
                            HttpGet httpGet = new HttpGet("https://laby.net/api/user/"+ uuid+"/get-badges");
                            httpGet.setHeader("Content-Type", "application/json");
                            HttpResponse response = httpClient.execute((HttpUriRequest)httpGet);
                            String responseString = EntityUtils.toString(response.getEntity());
                            ArrayList<Badge> badgesList = new ArrayList<Badge>();
                            if (!responseString.contains("[]") && !responseString.contains("User not found")) {
                                try {
                                    for (String loop : responseString.split("\",\"name\":\"")) {
                                        String badgeuuid = loop.split("\"uuid\":\"")[1];
                                        Badge badge = new Badge(badgeuuid);
                                        badgesList.add(badge);
                                        badges.put(uuid, badgesList);
                                    }
                                } catch (Exception ignored) {}
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }
        });
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
