package de.gommzy.nametags.screens;

import de.gommzy.nametags.Main;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.main.LabyMod;
import net.labymod.settings.LabyModSettingsGui;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class DisallowGui extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0,width/2-50,height/2+60,100,20,"Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            button.playPressSound(this.mc.getSoundHandler());
            Minecraft.getMinecraft().displayGuiScreen(new ModGuiMultiplayer(new GuiMainMenu()));
        }
    }

    @Override
    public void onGuiClosed() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.bindTexture(new ResourceLocation("nametags/textures/logo_nametag.png"));
        draw.drawTexture(width/2-20, height/2-85, 255.0D, 255.0D, 40, 40, 1F);

        drawCenteredString(fontRendererObj,"Nametags", width/2,height/2,16777215);
        drawCenteredString(fontRendererObj,"Your nametag \""+ Main.nametag+"\" isn't allowed!", width/2,height/2+15,16073282);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

