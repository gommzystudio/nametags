package de.gommzy.nametags.api;

import de.gommzy.nametags.Main;
import de.gommzy.nametags.session.Auth;
import net.labymod.main.LabyMod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Nametag {
    public static HashMap<String, String> nametags = new HashMap<String, String>();
    public static HashMap<String, Date> cooldown = new HashMap<String, Date>();
    public static CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

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
                cooldown.put(uuid,new Date());
                try {
                    HttpPost httpPost = new HttpPost("http://api.labytags.de/tags/"+ uuid.replace("-",""));
                    httpPost.setHeader("Content-Type", "application/json");
                    HttpResponse response = closeableHttpClient.execute((HttpUriRequest)httpPost);
                    nametags.put(uuid,EntityUtils.toString(response.getEntity()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getNametag(String uuid) {
        try {
            load(uuid);
            if (nametags.containsKey(uuid)) {
                return nametags.get(uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
