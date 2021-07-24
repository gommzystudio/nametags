package de.gommzy.nametags;

import de.gommzy.nametags.render.RenderListener;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.core.*;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.renderer.RenderList;

import java.util.List;

public class Main extends LabyModAddon {
    public static LabyModAddon addon;

    @Override
    public void onEnable() {
        addon = this;
        System.out.println("[Badges] Badges enabled");
        getApi().getEventManager().register((RenderEntityEvent)new RenderListener());
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

    }
}
