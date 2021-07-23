package de.gommzy.nametags;

import de.gommzy.nametags.render.RenderPlayerImplementation;
import net.labymod.api.LabyModAddon;
import net.labymod.core.*;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class Main extends LabyModAddon {
    public static LabyModAddon addon;

    @Override
    public void onEnable() {
        addon = this;
        System.out.println("[Badges] Badges enabled");
        final CoreAdapter old = LabyModCore.getCoreAdapter();
        LabyModCore.setCoreAdapter(new CoreAdapter() {
            public WorldRendererAdapter getWorldRendererImplementation() {
                return old.getWorldRendererImplementation();
            }

            public SoundAdapter getSoundImplementation() {
                return old.getSoundImplementation();
            }

            public ServerPingerAdapter getServerPingerImplementation() {
                return old.getServerPingerImplementation();
            }

            public RenderPlayerAdapter getRenderPlayerImplementation() {
                return new RenderPlayerImplementation();
            }

            public RenderAdapter getRenderImplementation() {
                return old.getRenderImplementation();
            }

            public MinecraftAdapter getMinecraftImplementation() {
                return old.getMinecraftImplementation();
            }

            public MathAdapter getMathImplementation() {
                return old.getMathImplementation();
            }

            public MappingAdapter getMappingAdapter() {
                return old.getMappingAdapter();
            }

            public ForgeAdapter getForgeImplementation() {
                return old.getForgeImplementation();
            }

            public ProtocolAdapter getProtocolAdapter() {
                return old.getProtocolAdapter();
            }
        });
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {

    }
}
