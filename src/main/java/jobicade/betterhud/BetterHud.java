package jobicade.betterhud;

import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.events.RenderEvents;
import jobicade.betterhud.geom.LayoutManager;
import jobicade.betterhud.gui.GuiHudMenu;
import jobicade.betterhud.network.InventoryNameRequest;
import jobicade.betterhud.network.InventoryNameResponse;
import jobicade.betterhud.network.MessagePickup;
import jobicade.betterhud.network.MessageVersion;
import jobicade.betterhud.util.Tickable.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

@EventBusSubscriber
@Mod(BetterHud.MODID)
public class BetterHud {
    public static final String MODID = "betterhud";
    public static final String VERSION = "1.4.4";

    private static ArtifactVersion serverVersion;

    public BetterHud() {
        setServerVersion(null);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
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

        NET_WRAPPER.registerMessage(0, MessagePickup.class, MessagePickup::encode, MessagePickup::new, MessagePickup::handle);
        NET_WRAPPER.registerMessage(1, MessageVersion.class, MessageVersion::encode, MessageVersion::new, MessageVersion::handle);

        // Used to update inventory names
        NET_WRAPPER.registerMessage(2, InventoryNameRequest.class, InventoryNameRequest::encode, InventoryNameRequest::new, InventoryNameRequest::handle);
        NET_WRAPPER.registerMessage(3, InventoryNameResponse.class, InventoryNameResponse::encode, InventoryNameResponse::new, InventoryNameResponse::handle);

        Ticker.registerEvents();
    }

    private static ConfigManager configManager;
    private static KeyBinding menuKey = new KeyBinding("key.betterHud.open", GLFW.GLFW_KEY_U, "key.categories.misc");

    private void clientSetup(FMLClientSetupEvent event) {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(MODID + ".json");

        // Order is important: initialising config manager loads settings
        HudElement.loadAllDefaults();
        configManager = new ConfigManager(configPath, configPath.resolveSibling(BetterHud.MODID));

        IResourceManager manager = Minecraft.getInstance().getResourceManager();

        if(manager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableManager = (IReloadableResourceManager)manager;

            reloadableManager.registerReloadListener(new IResourceManagerReloadListener() {
                @Override
                public void onResourceManagerReload(IResourceManager p_195410_1_) {
                    HudElement.SORTER.markDirty(SortType.ALPHABETICAL);
                }
            });
            reloadableManager.registerReloadListener(configManager);
        } else {
            BetterHud.getLogger().warn("Unable to register alphabetical sort update on language change");
        }

        ClientRegistry.registerKeyBinding(menuKey);
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
        HudElement.initAll(event);
    }

    public static boolean isModEnabled() {
        return HudElement.GLOBAL.isEnabledAndSupported() && !(
            HudElement.GLOBAL.hideOnDebug()
            && Minecraft.getInstance().options.renderDebug
        );
    }

    public static HudConfig getConfig() {
        return configManager.getConfig();
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if (Minecraft.getInstance().overlay == null && (Minecraft.getInstance().screen == null || Minecraft.getInstance().screen.passEvents) && menuKey.consumeClick()) {
            Minecraft.getInstance().setScreen(new GuiHudMenu(configManager));
        }
    }

    /**
     * Triggered by a player connecting to the logical server.
     * Updates the version tracked by the proxy.
     */
    @SubscribeEvent
    public static void onPlayerConnected(PlayerLoggedInEvent e) {
        if(e.getPlayer() instanceof ServerPlayerEntity) {
            ArtifactVersion version = new DefaultArtifactVersion(VERSION);
            NET_WRAPPER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)e.getPlayer()), new MessageVersion(version));
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
            NET_WRAPPER.send(PacketDistributor.DIMENSION.with(event.getPlayer().getCommandSenderWorld()::dimension),
                    new InventoryNameResponse(event.getPos(), null));
        }
    }

    /**
     * Triggered on the logical server. Sends a message to the client for the
     * picked up item.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemPickup(ItemPickupEvent e) {
        if (!e.getStack().isEmpty() && e.getPlayer() instanceof ServerPlayerEntity) {
            NET_WRAPPER.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayerEntity)e.getPlayer())),
                    new MessagePickup(e.getStack()));
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
