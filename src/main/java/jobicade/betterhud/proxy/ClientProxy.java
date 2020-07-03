package jobicade.betterhud.proxy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.gui.GuiElementList;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.HudRegistry;
import jobicade.betterhud.registry.HudRegistryEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class ClientProxy implements HudSidedProxy {
    private ConfigManager configManager;
    private KeyBinding menuKey = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.post(new HudRegistryEvent());

        Path configPath = event.getSuggestedConfigurationFile().toPath();
        configManager = new ConfigManager(configPath, configPath.resolveSibling(BetterHud.MODID));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

        if(manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

            // Language dictates alphabetical order
            reloadableManager.registerReloadListener(configManager);
        } else {
            BetterHud.getLogger().warn("Unable to register alphabetical sort update on language change");
        }

        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(new RenderEvents()); // TODO remove entirely
        HudElement.initAll(event);
    }

    @Override
    public boolean isModEnabled() {
        // TODO disable the mod using a setting
        return !(
            HudElements.GLOBAL.hideOnDebug()
            && Minecraft.getMinecraft().gameSettings.showDebugInfo
        );
    }

    @Override
    public HudConfig getConfig() {
        return configManager.getConfig();
    }

    @Override
    public <T extends HudElement<?>> List<T> getEnabled(HudRegistry<T> registry) {
        List<HudElement<?>> selected = getConfig().getSelected();

        List<T> subclassSelected = new ArrayList<>();
        for (HudElement<?> element : selected) {
            T subclass = registry.getRegistered(element.getName());

            if (subclass != null) {
                subclassSelected.add(subclass);
            }
        }
        return subclassSelected;
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if (Minecraft.getMinecraft().inGameHasFocus && menuKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiElementList(configManager));
        }
    }
}
