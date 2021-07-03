package de.gommzy.nametags;

import de.gommzy.nametags.api.Nametag;
import de.gommzy.nametags.render.RenderPlayerImplementation;
import de.gommzy.nametags.session.Auth;
import net.labymod.api.LabyModAddon;
import net.labymod.core.*;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

import java.util.HashMap;
import java.util.List;

public class Main extends LabyModAddon {
    public static LabyModAddon addon;
    public static String nametag;
    public static boolean changed;

    @Override
    public void onEnable() {
        addon = this;
        System.out.println("[Nametags] Nametags enabled");
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

        LabyMod.getInstance().getEventManager().registerOnJoin(new Consumer<ServerData>() {
            @Override
            public void accept(ServerData serverData) {
                Nametag.nametags = new HashMap<String, String>();
            }
        });

        getApi().registerForgeListener( new Main() );
    }

    @Override
    public void loadConfig() {
        nametag = getConfig().has("nametag") ? getConfig().get("nametag").getAsString() : "";
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        StringElement nameTag = new StringElement("Nametag", new ControlElement.IconData(Material.NAME_TAG), nametag, new Consumer<String>() {
            public void accept(String accepted) {
                nametag = accepted;
                changed = true;
                getConfig().addProperty("nametag", accepted);
                saveConfig();
            }
        });
        list.add(nameTag);
    }

    @SubscribeEvent
    public void onClick( GuiScreenEvent.MouseInputEvent event ) {
        if (changed) {
            changed = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Auth.changeNametag(nametag);
                }
            }).start();
        }
    }
}
