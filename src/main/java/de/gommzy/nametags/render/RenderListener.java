package de.gommzy.nametags.render;

import de.gommzy.nametags.api.Badge;
import de.gommzy.nametags.api.BadgeReciver;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.group.EnumGroupDisplayType;
import net.labymod.user.group.LabyGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderListener implements RenderEntityEvent {
    @Override
    public void onRender(Entity entity, double x, double y, double z, float partialTicks) {
        float fixedPlayerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX * (float) (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1);
        User user = entity instanceof EntityPlayer ? LabyMod.getInstance().getUserManager().getUser(entity.getUniqueID()) : null;
        if (user != null) {
            LabyGroup labyGroup = user.getGroup();
            if (labyGroup != null) {
                int loops = 0;
                ArrayList<Badge> badgeList = BadgeReciver.getBadge(user.getUuid().toString());
                if (badgeList != null) {
                    for (Badge badge : badgeList) {
                        loops++;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float) x, (float) y + entity.height + 0.5F, (float) z);
                        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(fixedPlayerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        double xOffset = badgeList.size() * 5d - (10d * loops) + 2 * (loops - 1);
                        double yOffset = -10D;
                        if (labyGroup != null && labyGroup.getDisplayType() == EnumGroupDisplayType.ABOVE_HEAD) {
                            yOffset -= 6.5D;
                        }
                        badge.renderBadge(xOffset, yOffset);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.resetColor();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }
}
