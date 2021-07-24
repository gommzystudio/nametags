package de.gommzy.nametags.api;

import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.io.File;

public class Badge {
   public  String uuid;

    public Badge(String uuid) {
        this.uuid = uuid;
    }

    public void renderBadge(double x, double y) {
        try {
            GlStateManager.enableBlend();
            if(BadgeReciver.badgeDynamicTextureManager == null) {
                BadgeReciver.badgeDynamicTextureManager = new BadgeDynamicTextureManager("labymodbadges", ModTextures.MISC_HEAD_QUESTION);
                BadgeReciver.badgeDynamicTextureManager.init();
            }
            Minecraft.getMinecraft().getTextureManager().bindTexture(BadgeReciver.badgeDynamicTextureManager.getTexture(uuid,"https://laby.net/texture/badge-small/"+uuid+".png"));
            LabyMod.getInstance().getDrawUtils().drawTexture(x, y, 255.0D, 255.0D, 8.0D, 8.0D, 1.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}