package jobicade.betterhud.proxy;

import java.nio.file.Path;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.gui.GuiHudMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;

public class ClientProxy implements HudSidedProxy {
    private ConfigManager configManager;

    @Override
    public void initConfigManager(Path configFile, Path configDirectory) {
        configManager = new ConfigManager(configFile, configDirectory);
    }

    @Override
    public HudConfig getConfig() {
        return configManager.getConfig();
    }

    @Override
    public void registerReloadListeners() {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

        if(manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

            reloadableManager.registerReloadListener(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
            reloadableManager.registerReloadListener(configManager);
        } else {
            BetterHud.getLogger().warn("Unable to register alphabetical sort update on language change");
        }
    }

    @Override
    public void displayHudMenu() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiHudMenu(configManager));
    }
}
