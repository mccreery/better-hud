package jobicade.betterhud.proxy;

import java.nio.file.Path;

import org.lwjgl.input.Keyboard;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.gui.GuiHudMenu;
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
        Path configPath = event.getSuggestedConfigurationFile().toPath();

        // Order is important: initialising config manager loads settings
        HudElement.loadAllDefaults();
        configManager = new ConfigManager(configPath, configPath.resolveSibling(BetterHud.MODID));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        IResourceManager manager = Minecraft.getInstance().func_110442_L();

        if(manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

            reloadableManager.func_110542_a(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
            reloadableManager.func_110542_a(configManager);
        } else {
            BetterHud.getLogger().warn("Unable to register alphabetical sort update on language change");
        }

        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        HudElement.initAll(event);
    }

    @Override
    public boolean isModEnabled() {
        return HudElement.GLOBAL.isEnabledAndSupported() && !(
            HudElement.GLOBAL.hideOnDebug()
            && Minecraft.getInstance().options.renderDebug
        );
    }

    @Override
    public HudConfig getConfig() {
        return configManager.getConfig();
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if (Minecraft.getInstance().field_71415_G && menuKey.consumeClick()) {
            Minecraft.getInstance().setScreen(new GuiHudMenu(configManager));
        }
    }
}
