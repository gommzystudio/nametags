package de.gommzy.nametags;

import de.gommzy.nametags.render.RenderListener;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.utils.Material;

import java.util.List;

public class Main extends LabyModAddon {
    public static LabyModAddon addon;
    public static boolean addonEnabled;
    public static boolean ownBadge;
    public static int size;

    @Override
    public void onEnable() {
        addon = this;
        System.out.println("[Badges] Badges enabled");
        getApi().getEventManager().register((RenderEntityEvent)new RenderListener());
    }

    @Override
    public void loadConfig() {
        addonEnabled = !getConfig().has("addonEnabled") || getConfig().get("addonEnabled").getAsBoolean();
        ownBadge = !getConfig().has("ownBadge") || getConfig().get("ownBadge").getAsBoolean();
        size = getConfig().has( "size" ) ? getConfig().get( "size" ).getAsInt() : 100;
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        list.add( new BooleanElement( "Enabled", this, new ControlElement.IconData( Material.LEVER ), "addonEnabled", addonEnabled ));
        list.add( new SliderElement( "Badge Size (default: 100)", this, new ControlElement.IconData( Material.CLAY_BALL ), "size", size ).setRange( 20, 200 ));
        list.add( new BooleanElement( "Show own Badges", this, new ControlElement.IconData( Material.SKULL_ITEM ), "ownBadge", ownBadge ));
    }
}
