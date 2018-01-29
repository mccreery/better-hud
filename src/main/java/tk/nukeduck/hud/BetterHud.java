package tk.nukeduck.hud;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.gui.GuiHudMenu;
import tk.nukeduck.hud.network.InventoryNameQuery;
import tk.nukeduck.hud.network.MessageNotifyClientHandler;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.network.MessagePickupHandler;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.HudConfig;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Tickable.Ticker;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = "1.4")
public class BetterHud { // TODO thoroughly test GL, replace drawRect coords
	public static final String MODID = "hud";
	public static final Version VERSION = new Version(1, 4);

	@SideOnly(Side.CLIENT) public static Minecraft MC;
	@SideOnly(Side.CLIENT) public static GuiHudMenu MENU;

	@SideOnly(Side.CLIENT) // TODO not gonna work right
	private static final KeyBinding MENU_KEY = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");
	@SideOnly(Side.CLIENT)
	private static final KeyBinding TOGGLE_KEY = new KeyBinding("key.betterHud.disable", Keyboard.KEY_F3, "key.categories.misc");

	public static HudConfig CONFIG;
	public static final LayoutManager MANAGER = new LayoutManager();

	public static final int SPACER = 5;
	public static final Random RANDOM = new Random();

	public static Version serverVersion = Version.ZERO;

	public static boolean isEnabled() {
		return HudElement.GLOBAL.isEnabled();
	}

	public static void toggleEnabled() {
		HudElement.GLOBAL.settings.toggle();
	}

	// TODO allow reordering of elements to render some closer to top

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static EntityInfoRenderer entityInfoRenderer;
	public static PickupNotifier pickupNotifier;
	//public static BreedInfoNotifier breedNotifier;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Takes a SUSPICIOUSLY long time
		if(event.getSide() == Side.CLIENT) {
			HudElement.loadAllDefaults();
			CONFIG = new HudConfig(event.getSuggestedConfigurationFile());
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if(event.getSide() == Side.CLIENT) {
			MC = Minecraft.getMinecraft();
			MENU = new GuiHudMenu();

			ClientRegistry.registerKeyBinding(MENU_KEY);
			ClientRegistry.registerKeyBinding(TOGGLE_KEY);

			HudElement.initAll(event);
		}

		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Side.CLIENT);
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, Version.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		MinecraftForge.EVENT_BUS.register(entityInfoRenderer = new EntityInfoRenderer());
		MinecraftForge.EVENT_BUS.register(pickupNotifier = new PickupNotifier());
		//MinecraftForge.EVENT_BUS.register(breedNotifier = new BreedInfoNotifier());

		MinecraftForge.EVENT_BUS.register(this);
		//SettingsIO.loadSettings(BetterHud.LOGGER, this);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent event) {
		if(!isEnabled() || event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		MC.mcProfiler.startSection("betterHud");
		MANAGER.reset(event.getResolution());

		for(HudElement element : HudElement.ELEMENTS) {
			element.tryRender(event);
		}
		MC.mcProfiler.endSection();

		// Expected state by vanilla GUI
		MC.getTextureManager().bindTexture(Gui.ICONS);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKey(KeyInputEvent event) {
		if(MC.inGameHasFocus && MENU_KEY.isPressed()) {
			MC.displayGuiScreen(MENU);
		} else if(TOGGLE_KEY.isPressed()) {
			toggleEnabled();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		if(isEnabled()) {
			Ticker.FASTER.tick();
		}
	}

	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent e) {
		if(!(e.player instanceof EntityPlayerMP)) return;
		NET_WRAPPER.sendTo(VERSION, (EntityPlayerMP)e.player);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerDisconnected(ClientDisconnectionFromServerEvent e) {
		serverVersion = Version.ZERO;
	}
}
