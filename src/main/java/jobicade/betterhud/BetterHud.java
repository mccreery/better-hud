package jobicade.betterhud;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.events.ClientEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessagePickupHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.HudRegistryEvent;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@EventBusSubscriber
@Mod(BetterHud.MODID)
public class BetterHud {
    public static final String MODID = "betterhud";
    public static final String VERSION = "1.5-alpha";

    private static ArtifactVersion serverVersion;

    private static ModConfig config;
    public static ModConfig getModConfig() {
        return config;
    }

    private static ConfigManager configManager;

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Calling Minecraft.getInstance() in expressions causes a resource leak
     * warning in some IDEs since Minecraft is {@link AutoCloseable}. This is
     * false positive and using this field fixes it.
     */
    public static final Minecraft MC = Minecraft.getInstance();

    public BetterHud() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEvents::setupClient);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, HudConfig.CLIENT_SPEC);
        configManager = new ConfigManager();
    }

    private static final Logger logger = LogManager.getLogger();
    public static Logger getLogger() {
        return logger;
    }

    public static final LayoutManager MANAGER = new LayoutManager();

    public static final int SPACER = 5;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NET_WRAPPER = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private void setup(FMLCommonSetupEvent event) {
        setServerVersion(null);

        int index = 0;
        NET_WRAPPER.registerMessage(index++, MessagePickup.class, MessagePickup::encode, MessagePickup::new, new MessagePickupHandler());
        NET_WRAPPER.registerMessage(index++, MessageVersion.class, MessageVersion::encode, MessageVersion::new, BetterHud::onServerVersion);

        // Used to update inventory names
        NET_WRAPPER.registerMessage(index++, InventoryNameQuery.Request.class, InventoryNameQuery.Request::encode, InventoryNameQuery.Request::new, InventoryNameQuery.Request::handle);
        NET_WRAPPER.registerMessage(index++, InventoryNameQuery.Response.class, InventoryNameQuery.Response::encode, InventoryNameQuery.Response::new, InventoryNameQuery.Response::handle);

        Ticker.registerEvents();
    }

    public static void onServerVersion(MessageVersion message, Supplier<NetworkEvent.Context> context) {
        getLogger().info("Server reported version " + message.version.getQualifier());
        setServerVersion(message.version);
        context.get().setPacketHandled(true);
    }

    private void setupClient(FMLClientSetupEvent event) {
        IResourceManager resourceManager = event.getMinecraftSupplier().get().getResourceManager();

        if(resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager)resourceManager).addReloadListener(configManager);
        } else {
            BetterHud.getLogger().warn("Unable to register alphabetical sort update on language change");
        }

        MinecraftForge.EVENT_BUS.post(new HudRegistryEvent());

        Ticker.FASTER.register(OverlayElements.BLOOD_SPLATTERS);
        Ticker.FASTER.register(OverlayElements.WATER_DROPS);
        Ticker.FAST.register(OverlayElements.CPS);
    }

    public static boolean isModEnabled() {
        return DistExecutor.runForDist(() -> () -> !(
            HudElements.GLOBAL.hideOnDebug()
            && MC.gameSettings.showDebugInfo
        ), () -> () -> false);
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
