package de.gommzy.nametags.api;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import net.labymod.support.util.Debug;
import net.labymod.support.util.Debug.EnumDebugMode;
import net.labymod.utils.Consumer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class BadgeThreadDownloadTextureImage extends SimpleTexture {
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private String imageUrl;
    private Consumer<Boolean> consumer;
    private String userAgent = "Unknown";
    private SSLSocketFactory socketFactory;
    private net.labymod.utils.texture.ThreadDownloadTextureImage.TextureImageParser textureImageParser;
    private EnumDebugMode debugMode;
    private BufferedImage bufferedImage;
    private boolean textureLoaded;

    public BadgeThreadDownloadTextureImage(String imageUrl, ResourceLocation textureResourceLocation, Consumer<Boolean> consumer, String userAgent) {
        super(textureResourceLocation);
        this.debugMode = EnumDebugMode.GENERAL;
        this.textureLoaded = false;
        this.imageUrl = imageUrl;
        this.consumer = consumer;
        this.userAgent = userAgent;
    }

    public int getGlTextureId() {
        int textureId = super.getGlTextureId();
        if (!this.textureLoaded && this.bufferedImage != null) {
            this.textureLoaded = true;
            TextureUtil.uploadTextureImage(textureId, this.bufferedImage);
        }

        return textureId;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.downloadTexture((Consumer)null);
    }

    public void downloadTexture(final Consumer<net.labymod.utils.texture.ThreadDownloadTextureImage> loadTextureCallback) {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    BufferedImage loadedImage;
                    if (imageUrl.startsWith("data:")) {
                        String[] parts = imageUrl.split(",");
                        if (parts.length >= 2) {
                            String data = parts[1];
                            loadedImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(data)));
                            if (textureImageParser != null) {
                                loadedImage = textureImageParser.parse(loadedImage);
                            }

                            bufferedImage = loadedImage;
                            if (bufferedImage == null) {
                                Debug.log(debugMode, "Failed to convert base64 texture " + data);
                            } else {
                                Debug.log(debugMode, "Converted texture from base64 with size " + loadedImage.getWidth() + "x" + loadedImage.getHeight());
                            }
                        }
                    } else {
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
                        HttpPost httpPost = new HttpPost(imageUrl);
                        httpPost.setHeader("Content-Type", "application/json");
                        HttpResponse httpResponse = httpClient.execute((HttpUriRequest)httpPost);
                        InputStream in = new BufferedInputStream(httpResponse.getEntity().getContent());

                            loadedImage = TextureUtil.readBufferedImage(in);
                            if (textureImageParser != null) {
                                loadedImage = textureImageParser.parse(loadedImage);
                            }

                            bufferedImage = loadedImage;
                            if (bufferedImage == null) {
                                Debug.log(debugMode, "Failed to download texture " + imageUrl );
                            } else {
                                Debug.log(debugMode, "Downloaded texture " + imageUrl);
                            }
                    }
                } catch (Exception var4) {
                    var4.printStackTrace();
                }

                consumer.accept(bufferedImage != null);

            }
        });
    }

    public void setSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public void setTextureImageParser(net.labymod.utils.texture.ThreadDownloadTextureImage.TextureImageParser textureImageParser) {
        this.textureImageParser = textureImageParser;
    }

    public void setDebugMode(EnumDebugMode debugMode) {
        this.debugMode = debugMode;
    }

    public interface TextureImageParser {
        BufferedImage parse(BufferedImage var1);
    }
}
