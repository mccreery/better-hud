package jobicade.betterhud;

import java.nio.file.Path;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.KeyEvents;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.util.Tickable.Ticker;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION_STRING,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/develop/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)")
public class BetterHud {
	public static final String MODID = "betterhud";

	public static final VersionRange ALL = VersionRange.newRange(null, Arrays.asList(Restriction.EVERYTHING));
	public static final ArtifactVersion ZERO = new DefaultArtifactVersion("0.0");

	protected static final String VERSION_STRING = "1.4.1";
	public static final ArtifactVersion VERSION = new DefaultArtifactVersion(VERSION_STRING);

	public static ArtifactVersion serverVersion = ZERO;

	public static boolean serverSupports(VersionRange range) {
		return range.containsVersion(serverVersion);
	}

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

	public static final ResourceLocation ICONS     = Gui.ICONS;
	public static final ResourceLocation WIDGETS   = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

	public static final ResourceLocation HUD_ICONS = new ResourceLocation(MODID, "textures/gui/icons_hud.png");
	public static final ResourceLocation SETTINGS  = new ResourceLocation(MODID, "textures/gui/settings.png");

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

	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(e.player instanceof EntityPlayerMP) {
			NET_WRAPPER.sendTo(new MessageVersion(VERSION), (EntityPlayerMP)e.player);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		serverVersion = ZERO;
	}
}
