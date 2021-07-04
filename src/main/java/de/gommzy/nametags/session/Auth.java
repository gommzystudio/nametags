package de.gommzy.nametags.session;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.gommzy.nametags.Main;
import de.gommzy.nametags.screens.DisallowGui;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.Sys;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Auth {
    enum RESPONSE{NOPERMISSIONS,DISALLOW,SUCCESS,FORBITTEN};
    public static String token;
    public static CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    public static RESPONSE changeNametag(String newTag) {
        try {
            if (token == null) {
                closeableHttpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("http://api.labytags.de/getToken.php?username=" + LabyMod.getInstance().getPlayerName());
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response = closeableHttpClient.execute((HttpUriRequest) httpPost);
                token = EntityUtils.toString(response.getEntity());
            }

            closeableHttpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://sessionserver.mojang.com/session/minecraft/join");
            httpPost.setHeader("Content-Type", "application/json");
            JsonObject request = new JsonObject();
            request.addProperty("accessToken",
                            LabyMod.getInstance().getAccountManager().getUserAccount(LabyMod.getInstance().getPlayerUUID()).getAccessToken());
            request.addProperty("selectedProfile", LabyMod.getInstance().getPlayerId());
            request.addProperty("serverId", token);
            httpPost.setEntity((HttpEntity)new StringEntity((new Gson()).toJson((JsonElement)request)));
            HttpResponse response = closeableHttpClient.execute((HttpUriRequest)httpPost);

            closeableHttpClient = HttpClients.createDefault();
            httpPost = new HttpPost("http://api.labytags.de/changeTag.php?username="+LabyMod.getInstance().getPlayerName()+"&uuid="+LabyMod.getInstance().getPlayerUUID().toString().replace("-","")+"&token="+token+"&tag="+java.net.URLEncoder.encode(newTag, StandardCharsets.UTF_8.name()));
            httpPost.setHeader("Content-Type", "application/json");
            response = closeableHttpClient.execute((HttpUriRequest)httpPost);
            String result = EntityUtils.toString(response.getEntity());
            if (result.contains("success")) {
                return RESPONSE.SUCCESS;
            } else if (result.contains("permissions")) {
                return RESPONSE.NOPERMISSIONS;
            } else if (result.contains("forbitten")) {
                Minecraft.getMinecraft().displayGuiScreen(new DisallowGui());
                Main.addon.getConfig().addProperty("nametag", "censored");
                Main.addon.saveConfig();
                return RESPONSE.FORBITTEN;
            } else {
                return RESPONSE.DISALLOW;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
