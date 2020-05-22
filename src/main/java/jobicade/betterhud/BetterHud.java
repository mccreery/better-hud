package jobicade.betterhud;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.KeyEvents;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/develop/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)")
public class BetterHud {
	public static final String MODID = "betterhud";
	public static final String VERSION = "1.4.1";

	private static Logger logger;
	public static Logger getLogger() {
		return logger;
	}

	@SidedProxy(clientSide = "jobicade.betterhud.proxy.ClientProxy", serverSide = "jobicade.betterhud.proxy.ServerProxy")
	public static HudSidedProxy proxy;
	/**
	 * @return The singleton sided proxy instance.
	 */
	public static HudSidedProxy getProxy() {
		return proxy;
	}

	private static ConfigManager CONFIG_MANAGER;
	public static final LayoutManager MANAGER = new LayoutManager();

	public static final int SPACER = 5;

	public static ConfigManager getConfigManager() {
		return CONFIG_MANAGER;
	}

	public static boolean isEnabled() {
		return HudElement.GLOBAL.isEnabledAndSupported() && !(HudElement.GLOBAL.hideOnDebug() && Minecraft.getMinecraft().gameSettings.showDebugInfo);
	}

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		if(event.getSide() == Side.CLIENT) {
			HudElement.loadAllDefaults();

			Path configPath = event.getSuggestedConfigurationFile().toPath();
			CONFIG_MANAGER = new ConfigManager(configPath, configPath.resolveSibling(MODID));
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if(event.getSide() == Side.CLIENT) {
			KeyEvents.registerEvents();
			RenderEvents.registerEvents();
			Ticker.registerEvents();

			HudElement.initAll(event);
		}

		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		MinecraftForge.EVENT_BUS.register(this);

		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

			reloadableManager.registerReloadListener(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
			reloadableManager.registerReloadListener(getConfigManager());
		} else {
			logger.warn("Unable to register alphabetical sort update on language change");
		}
	}
}
