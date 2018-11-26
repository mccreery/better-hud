package tk.nukeduck.hud;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
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
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.events.KeyEvents;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.events.RenderEvents;
import tk.nukeduck.hud.network.InventoryNameQuery;
import tk.nukeduck.hud.network.MessageNotifyClientHandler;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.network.MessagePickupHandler;
import tk.nukeduck.hud.network.MessageVersion;
import tk.nukeduck.hud.util.HudConfig;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Tickable.Ticker;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION_STRING, updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/master/update.json")
public class BetterHud {
	public static final String MODID = "hud";

	public static final VersionRange ALL = VersionRange.newRange(null, Arrays.asList(Restriction.EVERYTHING));
	public static final ArtifactVersion ZERO = new DefaultArtifactVersion("0.0");

	protected static final String VERSION_STRING = "1.4-beta";
	public static final ArtifactVersion VERSION = new DefaultArtifactVersion(VERSION_STRING);

	public static ArtifactVersion serverVersion = ZERO;

	public static boolean serverSupports(VersionRange range) {
		return range.containsVersion(serverVersion);
	}

	private static Logger logger;
	public static Logger getLogger() {
		return logger;
	}

	@SideOnly(Side.CLIENT) public static Minecraft MC;

	public static final ResourceLocation ICONS     = Gui.ICONS;
	public static final ResourceLocation WIDGETS   = new ResourceLocation("textures/gui/widgets.png");
	public static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

	public static final ResourceLocation HUD_ICONS = new ResourceLocation("hud", "textures/gui/icons_hud.png");
	public static final ResourceLocation SETTINGS  = new ResourceLocation("hud", "textures/gui/settings.png");

	public static HudConfig CONFIG;
	public static final LayoutManager MANAGER = new LayoutManager();

	public static EntityLivingBase pointedEntity;

	public static final int SPACER = 5;

	public static boolean isEnabled() {
		return HudElement.GLOBAL.isEnabled() && !(HudElement.GLOBAL.hideOnDebug() && MC.gameSettings.showDebugInfo);
	}

	public static void toggleEnabled() {
		HudElement.GLOBAL.settings.toggle();
	}

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static PickupNotifier pickupNotifier;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		if(event.getSide() == Side.CLIENT) {
			MC = Minecraft.getMinecraft();

			HudElement.loadAllDefaults();
			CONFIG = new HudConfig(event.getSuggestedConfigurationFile());
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
		NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Side.CLIENT);
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		MinecraftForge.EVENT_BUS.register(pickupNotifier = new PickupNotifier());
		MinecraftForge.EVENT_BUS.register(this);

		IResourceManager manager = MC.getResourceManager();
		if(manager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)manager).registerReloadListener(m -> HudElement.SORTER.markDirty(SortType.ALPHABETICAL));
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
