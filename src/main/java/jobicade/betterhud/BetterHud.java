package jobicade.betterhud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfigNew;
import jobicade.betterhud.events.ClientEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.network.InventoryNameQuery;
import jobicade.betterhud.network.MessageNotifyClientHandler;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessagePickupHandler;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.proxy.HudSidedProxy;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.HudRegistryEvent;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@EventBusSubscriber
@Mod(BetterHud.MODID)
public class BetterHud {
    public static final String MODID = "betterhud";
    public static final String VERSION = "1.5-alpha";

    private static ArtifactVersion serverVersion;

    private static ModConfig config;
    private static ConfigManager configManager;

    public BetterHud() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEvents::setupClient);

        ModContainer container = ModLoadingContext.get().getActiveContainer();
        config = new ModConfig(Type.CLIENT, HudConfigNew.CLIENT_SPEC, container);
        container.addConfig(config);

        configManager = new ConfigManager(config.getFullPath(), config.getFullPath().resolveSibling(BetterHud.MODID));
    }

    @SubscribeEvent
    public static void onConfig(ModConfigEvent event) {
        if (event.getConfig() == config) {
            HudConfigNew.CLIENT.loadValues();
        }
    }

    private static final Logger logger = LogManager.getLogger();
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

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NET_WRAPPER = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private void setup(FMLCommonSetupEvent event) {
        setServerVersion(null);
        proxy.preInit(event);

        // Message ID 0 reserved for ignored server presence message from [,1.4)
        NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Side.CLIENT);
        NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, MessageVersion.class, 2, Side.CLIENT);

        // Used to update inventory names
        NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
        NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

        Ticker.registerEvents();
        proxy.init(event);
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
            && Minecraft.getInstance().gameSettings.showDebugInfo
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
