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
import org.lwjgl.Sys;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Nametag {
    public static HashMap<String, String> nametags = new HashMap<String, String>();
    public static ArrayList<String> cooldown = new ArrayList<String>();

    public static void load(final String uuid) {
        if (cooldown.contains(uuid)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                cooldown.add(uuid);
                try {
                    CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                    HttpPost httpPost = new HttpPost("http://116.203.34.222/tags/"+ uuid.replace("-",""));
                    httpPost.setHeader("Content-Type", "application/json");
                    HttpResponse response = closeableHttpClient.execute((HttpUriRequest)httpPost);
                    nametags.put(uuid,EntityUtils.toString(response.getEntity()));
                    Thread.sleep(1000*10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cooldown.remove(uuid);
            }
        }).start();
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
