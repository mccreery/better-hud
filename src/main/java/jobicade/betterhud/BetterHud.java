package jobicade.betterhud;

import org.apache.logging.log4j.Logger;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/develop/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)")
public class BetterHud {
	public static final String MODID = "betterhud";
	public static final String VERSION = "1.4.1";

	private static ArtifactVersion serverVersion;

	public BetterHud() {
		setServerVersion(null);
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

	public static final LayoutManager MANAGER = new LayoutManager();

	public static final int SPACER = 5;

	public static boolean isEnabled() {
		return HudElement.GLOBAL.isEnabledAndSupported() && !(HudElement.GLOBAL.hideOnDebug() && Minecraft.getMinecraft().gameSettings.showDebugInfo);
	}

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		HudElement.loadAllDefaults();
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		HudElement.initAll(event);
		Ticker.registerEvents();
		proxy.init(event);
	}

	/**
     * Triggered by a player connecting to the logical server.
	 * Updates the version tracked by the proxy.
     */
    @SubscribeEvent
    public static void onPlayerConnected(PlayerLoggedInEvent e) {
        if(e.player instanceof EntityPlayerMP) {
            ArtifactVersion version = new DefaultArtifactVersion(VERSION);
            NET_WRAPPER.sendTo(new MessageVersion(version), (EntityPlayerMP)e.player);
        }
	}

	/**
	 * Triggered by a player disconnecting on the client only.
	 * Resets the version tracked by the proxy to none.
	 */
	@SubscribeEvent
    public static void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		setServerVersion(null);
	}

	/**
     * Updates the server version after a report is received.
     *
     * @param version The version or {@code null} to indicate no server.
     */
	public static void setServerVersion(ArtifactVersion version) {
		if (version == null) {
            serverVersion = new DefaultArtifactVersion("");
        } else {
            serverVersion = version;
        }
	}

	/**
     * @return The most recent version reported by the server.
     */
	public static ArtifactVersion getServerVersion() {
		return serverVersion;
	}
}
