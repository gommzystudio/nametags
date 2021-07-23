package de.gommzy.nametags.api;

import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Badge {
    String uuid;

    public Badge(String uuid) {
        this.uuid = uuid;
    }

    public void renderBadge(double x, double y) {
        File file = new File(System.getProperty("user.dir") + "\\LabyMod\\badges\\"+uuid+".png");
        if (!file.exists()) {
            return;
        }

        try {
            File img = new File(System.getProperty("user.dir") + "\\LabyMod\\badges\\" + uuid + ".png");
            BufferedImage image = ImageIO.read(img);
            DynamicTexture dt = new DynamicTexture(image);
            ResourceLocation texture = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(".png", dt);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            LabyMod.getInstance().getDrawUtils().drawTexture(x, y, 255.0D, 255.0D, 8.0D, 8.0D, 1.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}