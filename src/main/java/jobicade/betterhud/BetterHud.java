package jobicade.betterhud;

import org.apache.logging.log4j.Logger;

import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessagePickupHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION,
	updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/master/update.json",
	dependencies = "required-after:forge@[14.23.1.2557,)")
public class BetterHud {
	public static final String MODID = "betterhud";
	public static final String VERSION = "1.4.2";

	private static ArtifactVersion serverVersion;

	public BetterHud() {
		setServerVersion(null);
	}

	private static Logger logger;
	public static Logger getLogger() {
		return logger;
	}

	@SidedProxy(clientSide = "jobicade.betterhud.proxy.ClientProxy", serverSide = "jobicade.betterhud.proxy.ServerProxy")
	private static HudSidedProxy proxy;
	/**
	 * @return The singleton sided proxy instance.
	 */
	public static HudSidedProxy getProxy() {
		return proxy;
	}

	public static final LayoutManager MANAGER = new LayoutManager();

	public static final int SPACER = 5;

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Side.CLIENT);
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		Ticker.registerEvents();
		proxy.init(event);
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
