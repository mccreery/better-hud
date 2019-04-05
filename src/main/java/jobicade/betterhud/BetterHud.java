package jobicade.betterhud;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.KeyEvents;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(BetterHud.MODID)
/* @Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION_STRING,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/develop/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)") */
public class BetterHud {
	public static final String MODID = "betterhud";

	public static final VersionRange ALL = VersionRange.createFromVersionSpec("*"););
	public static final ArtifactVersion ZERO = new DefaultArtifactVersion("0.0");

	protected static final String VERSION_STRING = "1.4";
	public static final ArtifactVersion VERSION = new DefaultArtifactVersion(VERSION_STRING);

	public static ArtifactVersion serverVersion = ZERO;

	public static boolean serverSupports(VersionRange range) {
		return range.containsVersion(serverVersion);
	}

	private static Logger logger;
	public static Logger getLogger() {
		return logger;
	}

	@OnlyIn(Dist.CLIENT) public static Minecraft MC;

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
		return HudElement.GLOBAL.isEnabledAndSupported() && !(HudElement.GLOBAL.hideOnDebug() && MC.gameSettings.showDebugInfo);
	}

	public static final SimpleChannel NET_WRAPPER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main_channel")).simpleChannel();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		if(event.getSide() == Dist.CLIENT) {
			MC = Minecraft.getInstance();

			HudElement.loadAllDefaults();

			Path configPath = event.getSuggestedConfigurationFile().toPath();
			CONFIG_MANAGER = new ConfigManager(configPath, configPath.resolveSibling(MODID));
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if(event.getSide() == Dist.CLIENT) {
			KeyEvents.registerEvents();
			RenderEvents.registerEvents();
			Ticker.registerEvents();

			HudElement.initAll(event);
		}

		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Dist.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Dist.DEDICATED_SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Dist.CLIENT);

		MinecraftForge.EVENT_BUS.register(this);

		IResourceManager manager = MC.getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

			reloadableManager.addReloadListener(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
			reloadableManager.addReloadListener(getConfigManager());
		} else {
			logger.warn("Unable to register alphabetical sort update on language change");
		}
	}

	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(e.getPlayer() instanceof EntityPlayerMP) {
			NET_WRAPPER.sendTo(new MessageVersion(VERSION), (EntityPlayerMP)e.getPlayer());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		serverVersion = ZERO;
	}
}
