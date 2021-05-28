package jobicade.betterhud;

import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessagePickupHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.INameable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@EventBusSubscriber
@Mod(modid = BetterHud.MODID, name = "Better HUD", version = BetterHud.VERSION,
    updateJSON = "https://raw.githubusercontent.com/mccreery/better-hud/master/update.json",
    dependencies = "required-after:forge@[14.23.1.2557,)")
public class BetterHud {
    public static final String MODID = "betterhud";
    public static final String VERSION = "1.4.4";

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
        NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Dist.CLIENT);
        NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Dist.CLIENT);

        // Used to update inventory names
        NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Dist.SERVER);
        NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Dist.CLIENT);

        Ticker.registerEvents();
        proxy.init(event);
    }

    /**
     * Triggered by a player connecting to the logical server.
     * Updates the version tracked by the proxy.
     */
    @SubscribeEvent
    public static void onPlayerConnected(PlayerLoggedInEvent e) {
        if(e.player instanceof ServerPlayerEntity) {
            ArtifactVersion version = new DefaultArtifactVersion(VERSION);
            NET_WRAPPER.sendTo(new MessageVersion(version), (ServerPlayerEntity)e.player);
        }
    }

    /**
     * Triggered by a player disconnecting on the client only.
     * Resets the version tracked by the proxy to none.
     */
    @SubscribeEvent
    public static void onPlayerDisconnected(LoggedOutEvent e) {
        setServerVersion(null);
    }

    /**
     * Triggered on the logical server. Sends a message to remove any cached
     * inventory name in the block viewer.
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().getBlockEntity(event.getPos()) instanceof INameable) {
            NET_WRAPPER.sendToDimension(
                new InventoryNameQuery.Response(event.getPos(), null),
                event.getWorld().dimension.getDimension()
            );
        }
    }

    /**
     * Triggered on the logical server. Sends a message to the client for the
     * picked up item.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemPickup(ItemPickupEvent e) {
        if (!e.getStack().isEmpty() && e.player instanceof ServerPlayerEntity) {
            BetterHud.NET_WRAPPER.sendTo(
                new MessagePickup(e.getStack()),
                (ServerPlayerEntity)e.player
            );
        }
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
