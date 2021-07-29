package de.gommzy.nametags.render;

import de.gommzy.nametags.Main;
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
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class RenderListener implements RenderEntityEvent {
    @Override
    public void onRender(Entity entity, double x, double y, double z, float partialTicks) {
        boolean canRender = Minecraft.isGuiEnabled() && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && entity.riddenByEntity == null;
        if (!canRender) {
            return;
        }
        if (!(LabyMod.getSettings()).showMyName && entity.getUniqueID().equals(LabyMod.getInstance().getPlayerUUID())) {
            return;
        }

        float fixedPlayerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX * (float) (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1);
        User user = entity instanceof EntityPlayer ? LabyMod.getInstance().getUserManager().getUser(entity.getUniqueID()) : null;
        if (user != null && !entity.isSneaking() && Main.addonEnabled && (Main.ownBadge || user.getUuid() != LabyMod.getInstance().getPlayerUUID()) && user.getUuid().version() == 4 && user.getUuid().getLeastSignificantBits() != 0 && user.getUuid().getMostSignificantBits() != 0) {
            LabyGroup labyGroup = user.getGroup();
            if (labyGroup != null) {
                int loops = 0;
                ArrayList<Badge> tempBadgeList = BadgeReciver.getBadge(user.getUuid().toString());
                if (tempBadgeList != null) {
                    ArrayList<Badge> badgeList = new ArrayList<Badge>();
                    for (Badge badge : tempBadgeList) {
                        if (!(badge.uuid.equals("cbcf5a7c-d325-4c5e-b918-adbc98343195") && labyGroup.getDisplayType() != EnumGroupDisplayType.ABOVE_HEAD) && !(labyGroup.getId() == 7 && badge.uuid.equals("a4d53924-7fa7-4d27-bda7-dd88ab72d10e"))) {
                            badgeList.add(badge);
                        }
                    }

                    double scale = (((float)Main.size)/100);
                    double badgeSize = 8;
                    double linewidth = badgeSize*scale*badgeList.size() + 2*(badgeList.size()-1);
                    double xPos = -(linewidth/2);

                    for (Badge badge : badgeList) {
                        double xOffset = xPos + badgeSize*scale*loops + 2*loops;
                        double yOffset = -10D;
                        if (labyGroup.getDisplayType() == EnumGroupDisplayType.ABOVE_HEAD) {
                            yOffset -= 6.5D;
                        }
                        double size;
                        if (user.getSubTitle() != null) {
                            size = user.getSubTitleSize();
                            yOffset -= size*6;
                        }

                        Scoreboard scoreboard = ((EntityPlayer) entity).getWorldScoreboard();
                        if (scoreboard != null) {
                            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
                            if (scoreobjective != null)
                                yOffset -= ((LabyMod.getInstance().getDrawUtils().getFontRenderer()).FONT_HEIGHT);
                        }

                        yOffset += badgeSize-(badgeSize*scale);

                        float maxNameTagHeight = LabyMod.getSettings().cosmetics ? user.getMaxNameTagHeight() : 0.0F;

                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float) x, (float) y + entity.height + 0.5F + maxNameTagHeight, (float) z);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.translate((float) 0, yOffset, (float) 0);
                        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-fixedPlayerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        badge.renderBadge(xOffset,0,scale,badgeSize);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.resetColor();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();

                        loops++;
                    }
                }
            }
        }
    }
}
