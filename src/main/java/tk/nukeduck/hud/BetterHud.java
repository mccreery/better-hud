package tk.nukeduck.hud;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.gui.GuiHUDMenu;
import tk.nukeduck.hud.network.MessageNotifyClient;
import tk.nukeduck.hud.network.MessageNotifyClientHandler;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.network.MessagePickupHandler;
import tk.nukeduck.hud.network.ServerStatusHandler;
import tk.nukeduck.hud.network.proxy.CommonProxy;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.constants.Constants;
import tk.nukeduck.hud.util.constants.Textures;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = "1.3.9")
public class BetterHud { // TODO thoroughly test GL
	public static final String MODID = "hud";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final GuiScreen MENU = new GuiHUDMenu();
	public static final Random RANDOM = new Random();

	@SidedProxy(clientSide="tk.nukeduck.hud.network.proxy.ClientProxy", serverSide="tk.nukeduck.hud.network.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper netWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(BetterHud.MODID);

	private static int packetId = 0;
	public static final <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> message, Side side) {
		netWrapper.registerMessage(handler, message, packetId++, side);
	}

	public static EntityInfoRenderer entityInfoRenderer;
	public static ServerStatusHandler serverStatus;
	public static PickupNotifier pickupNotifier;
	//public static BreedInfoNotifier breedNotifier;

	@EventHandler
	public void init(FMLInitializationEvent e) {
		registerMessage(MessageNotifyClientHandler.class, MessageNotifyClient.class, Side.CLIENT);
		registerMessage(MessagePickupHandler.class, MessagePickup.class, Side.CLIENT);
		//registerMessage(MessageBreedingHandler.class, MessageBreeding.class, Side.CLIENT);

		BetterHud.proxy.init();
		Textures.init();
		BetterHud.proxy.initElements();

		MinecraftForge.EVENT_BUS.register(serverStatus = new ServerStatusHandler());
		MinecraftForge.EVENT_BUS.register(pickupNotifier = new PickupNotifier());
		//MinecraftForge.EVENT_BUS.register(breedNotifier = new BreedInfoNotifier());

		MinecraftForge.EVENT_BUS.register(this);

		BetterHud.proxy.initKeys();
		BetterHud.proxy.loadDefaults();
		BetterHud.proxy.loadSettings();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent event) {
		if(!proxy.elements.globalSettings.enabled) return;
		
		// Special case
		if(event.isCancelable()) {
			if(proxy.elements.potionBar.disableDefault.value && event.getType() == ElementType.POTION_ICONS) {
				event.setCanceled(true);
			}
			return;
		}
		if(event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		// TODO do we need to check all of these? investigate
		if(MC != null && MC.ingameGUI != null && MC.player != null) {
			MC.mcProfiler.startSection("betterHud");
			//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);

			GL11.glPushMatrix();

			// General functions, sure to fix a few lighting bugs!
			// TODO don't weasel out of work
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			// TODO static?
			LayoutManager layoutManager = new LayoutManager();
			StringManager stringManager = new StringManager();

			for(HudElement element : proxy.elements.elements) {
				if(element.shouldRender()) {
					boolean profile = element.shouldProfile();
					if(profile) MC.mcProfiler.startSection(element.name);

					element.render(event, stringManager, layoutManager);

					if(profile) MC.mcProfiler.endSection();
				}
			}

			MC.mcProfiler.startSection("drawStrings");
			/*for(Position p : new Position[] {Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT}) {
				int x = p == Position.TOP_LEFT || p == Position.BOTTOM_LEFT ? Constants.SPACER : resolution.getScaledWidth() - Constants.SPACER;
				int y = layoutManager.get(p);
				if(p == Position.BOTTOM_LEFT || p == Position.BOTTOM_RIGHT) y = resolution.getScaledHeight() - y;

				RenderUtil.renderStrings(mc.fontRendererObj, stringManager.get(p), x, y, p);
			}*/

			int right = event.getResolution().getScaledWidth() - Constants.SPACER;
			RenderUtil.renderStrings(MC.fontRenderer, stringManager.get(Position.TOP_LEFT), Constants.SPACER, layoutManager.get(Position.TOP_LEFT), Position.TOP_LEFT);
			RenderUtil.renderStrings(MC.fontRenderer, stringManager.get(Position.TOP_RIGHT), right, layoutManager.get(Position.TOP_RIGHT), Position.TOP_RIGHT);
			RenderUtil.renderStrings(MC.fontRenderer, stringManager.get(Position.BOTTOM_LEFT), Constants.SPACER, event.getResolution().getScaledHeight() - layoutManager.get(Position.BOTTOM_LEFT), Position.BOTTOM_LEFT);
			RenderUtil.renderStrings(MC.fontRenderer, stringManager.get(Position.BOTTOM_RIGHT), right, event.getResolution().getScaledHeight() - layoutManager.get(Position.BOTTOM_RIGHT), Position.BOTTOM_RIGHT);

			MC.mcProfiler.endSection();
			GL11.glPopMatrix();
			// glPopAttrib();
			MC.mcProfiler.endSection();

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			//GL11.glPopAttrib();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT) // GuiScreen doesn't exist on server side
	public void clientTick(ClientTickEvent e) {
		if(proxy.elements.globalSettings.enabled) {
			Ticker.FASTER.tick();
		}

		if(MC != null && MC.player != null && MC.inGameHasFocus && MC.currentScreen == null) {
			if(proxy.openMenu.isPressed()) {
				MC.displayGuiScreen(MENU);
			} else if(proxy.disable.isPressed()) {
				proxy.elements.globalSettings.enabled = !proxy.elements.globalSettings.enabled;
			}
		}
	}
}
