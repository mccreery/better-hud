package jobicade.betterhud;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.KeyEvents;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameReq;
import jobicade.betterhud.network.InventoryNameRes;
import jobicade.betterhud.network.VersionHandler;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(BetterHud.MODID)
/* @Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION_STRING,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/develop/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)") */
public class BetterHud {
	public static final String MODID = "betterhud";

	public BetterHud() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

	public static VersionRange ALL;
	static {
		try {
			ALL = VersionRange.createFromVersionSpec("*");
		} catch(InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
	}

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

	static ResourceLocation loc = new ResourceLocation(MODID,"main_channel");
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static SimpleChannel NET_WRAPPER = NetworkRegistry.ChannelBuilder.named(loc).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();

	public void clientInit(FMLClientSetupEvent event) {
		MC = Minecraft.getInstance();
		logger = LogManager.getLogger();

		Logger log = getLogger();
		log.debug("path:"+loc.getNamespace());
		HudElement.loadAllDefaults();

		// TODO config overhaul
		// Path configPath = event.getSuggestedConfigurationFile().toPath();
		// CONFIG_MANAGER = new ConfigManager(configPath, configPath.resolveSibling(MODID));
		CONFIG_MANAGER = new ConfigManager(Paths.get("."), Paths.get("..", MODID));

		KeyEvents.registerEvents();
		RenderEvents.registerEvents();
		Ticker.registerEvents();

		HudElement.initAll(event);

		IResourceManager manager = MC.getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

			reloadableManager.addReloadListener(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
			reloadableManager.addReloadListener(getConfigManager());
		} else {
			logger.warn("Unable to register alphabetical sort update on language change");
		}
	}

	public void commonInit(FMLCommonSetupEvent event) {
		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(2, ArtifactVersion.class, VersionHandler::encode, VersionHandler::decode, VersionHandler::consume);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(3, InventoryNameReq.class, InventoryNameReq::encode, InventoryNameReq::decode, InventoryNameReq::consume);
		NET_WRAPPER.registerMessage(4, InventoryNameRes.class, InventoryNameRes::encode, InventoryNameRes::decode, InventoryNameRes::consume);
	}

	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(e.getPlayer() instanceof EntityPlayerMP) {
			NetworkManager netManager = ((EntityPlayerMP)e.getPlayer()).connection.netManager;
			//NET_WRAPPER.sendTo(VERSION, netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onPlayerDisconnected(PlayerLoggedOutEvent e) {
		serverVersion = ZERO;
	}
}
