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
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class ClientProxy implements HudSidedProxy {
    private ArtifactVersion serverVersion;
    private ConfigManager configManager;

    public ClientProxy() {
        setServerVersion(null);
    }

    @Override
    public ArtifactVersion getServerVersion() {
        return serverVersion;
    }

    @Override
    public void setServerVersion(ArtifactVersion version) {
        if (version == null) {
            serverVersion = new DefaultArtifactVersion("version");
        } else {
            serverVersion = version;
        }
    }

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
