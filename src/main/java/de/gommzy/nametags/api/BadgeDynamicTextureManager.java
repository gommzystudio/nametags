package de.gommzy.nametags.api;

import com.mojang.authlib.GameProfile;
import net.labymod.core.LabyModCore;
import net.labymod.main.ModTextures;
import net.labymod.utils.Consumer;
import net.labymod.utils.manager.TagManager;
import net.labymod.utils.texture.DynamicModTexture;
import net.labymod.utils.texture.ThreadDownloadTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BadgeDynamicTextureManager {
    private final String resourceName;
    private final ResourceLocation defaultTexture;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36";
    private SSLSocketFactory socketFactory = null;
    private ThreadDownloadTextureImage.TextureImageParser textureImageParser = null;
    protected final Map<String, DynamicModTexture> resourceLocations = new HashMap();
    private Map<ResourceLocation, ITextureObject> mapTextureObjects = new HashMap();

    public BadgeDynamicTextureManager(String resourceName, ResourceLocation defaultTexture) {
        this.resourceName = resourceName;
        this.defaultTexture = defaultTexture;
    }

    public void init() {
        try {
            Field field = ReflectionHelper.findField(TextureManager.class, LabyModCore.getMappingAdapter().getMapTextureObjects());
            field.setAccessible(true);
            this.mapTextureObjects = (Map)field.get(Minecraft.getMinecraft().getTextureManager());
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        if (TagManager.SYMBOL - 10 != 9988) {
            TagManager.SYMBOL = 9898;
            TagManager.SYMBOL = (char)(TagManager.SYMBOL + 100);
        }

    }

    /** @deprecated */
    @Deprecated
    public ResourceLocation getHeadTexture(GameProfile gameProfile) {
        return ModTextures.MISC_HEAD_QUESTION;
    }

    public ResourceLocation getTexture(String identifier, String url) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        DynamicModTexture dynamicModTexture = (DynamicModTexture)this.resourceLocations.get(identifier);
        boolean unloadImage = dynamicModTexture != null && dynamicModTexture.getUrl() != null && !dynamicModTexture.getUrl().equals(url);
        if (dynamicModTexture == null || unloadImage) {
            if (unloadImage) {
                textureManager.deleteTexture(dynamicModTexture.getResourceLocation());
                dynamicModTexture.setResourceLocation(this.defaultTexture);
                dynamicModTexture.setUrl((String)null);
            } else {
                dynamicModTexture = new DynamicModTexture(this.defaultTexture, (String)null);
            }

            this.resourceLocations.put(identifier, dynamicModTexture);
            this.resolveImageTexture(identifier, url);
        }

        return dynamicModTexture.getResourceLocation();
    }

    private void resolveImageTexture(final String identifier, final String url) {
        if (identifier != null && url != null) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            final ResourceLocation resourceLocation = new ResourceLocation(this.resourceName + "/" + this.getHash(url));
            BadgeThreadDownloadTextureImage threadDownloadImageData = new BadgeThreadDownloadTextureImage(url, resourceLocation, new Consumer<Boolean>() {
                public void accept(Boolean accepted) {
                    resourceLocations.put(identifier, new DynamicModTexture(resourceLocation, url));
                }
            }, this.userAgent);
            threadDownloadImageData.setSocketFactory(this.socketFactory);
            threadDownloadImageData.setTextureImageParser(this.textureImageParser);
            textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        }
    }

    public void unloadAll() {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            public void run() {
                try {
                    Iterator var1 = resourceLocations.entrySet().iterator();

                    while(var1.hasNext()) {
                        Map.Entry<String, DynamicModTexture> entry = (Map.Entry)var1.next();
                        ResourceLocation resourceLocation = ((DynamicModTexture)entry.getValue()).getResourceLocation();
                        mapTextureObjects.remove(resourceLocation);
                    }

                    resourceLocations.clear();
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

            }
        });
    }

    private int getHash(String url) {
        int hash = 7;

        for(int i = 0; i < url.length(); ++i) {
            hash = hash * 31 + url.charAt(i);
        }

        return hash;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public ResourceLocation getDefaultTexture() {
        return this.defaultTexture;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public SSLSocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public ThreadDownloadTextureImage.TextureImageParser getTextureImageParser() {
        return this.textureImageParser;
    }

    public Map<String, DynamicModTexture> getResourceLocations() {
        return this.resourceLocations;
    }

    public Map<ResourceLocation, ITextureObject> getMapTextureObjects() {
        return this.mapTextureObjects;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public void setTextureImageParser(ThreadDownloadTextureImage.TextureImageParser textureImageParser) {
        this.textureImageParser = textureImageParser;
    }
}
