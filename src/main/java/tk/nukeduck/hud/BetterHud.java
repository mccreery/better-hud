package tk.nukeduck.hud;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.gui.GuiHUDMenu;
import tk.nukeduck.hud.network.MessageNotifyClient;
import tk.nukeduck.hud.network.MessageNotifyClientHandler;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.network.MessagePickupHandler;
import tk.nukeduck.hud.network.ServerStatusHandler;
import tk.nukeduck.hud.network.proxy.ClientProxy;
import tk.nukeduck.hud.network.proxy.CommonProxy;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Constants;
import tk.nukeduck.hud.util.constants.Textures;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = "1.3.9")
public class BetterHud {
	@Instance(Constants.MOD_ID)
	public static BetterHud INSTANCE;
	@SidedProxy(clientSide="tk.nukeduck.hud.network.proxy.ClientProxy", serverSide="tk.nukeduck.hud.network.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper netWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);

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
	public void onRenderTick(RenderGameOverlayEvent e) {
		if(!proxy.elements.globalSettings.enabled) return;

		if(e.isCancelable()) {
			if(proxy.elements.potionBar.disableDefault.value && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
				e.setCanceled(true);
			}
			return;
		}
		if(e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		Minecraft mc = Minecraft.getMinecraft();

		if(mc != null && mc.ingameGUI != null && mc.player != null) {
			mc.mcProfiler.startSection("betterHud");
			//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_LIGHTING);

			GL11.glPushMatrix();
			ScaledResolution resolution = new ScaledResolution(mc);

			// General functions, sure to fix a few lighting bugs!
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			LayoutManager layoutManager = new LayoutManager();
			StringManager stringManager = new StringManager();

			for(ExtraGuiElement element : BetterHud.proxy.elements.elements) {
				if(element.shouldRender()) {
					if(element.shouldProfile()) {
						mc.mcProfiler.startSection(element.getName());
						element.render(mc, resolution, stringManager, layoutManager);
						mc.mcProfiler.endSection();
					} else {
						element.render(mc, resolution, stringManager, layoutManager);
					}
				}
			}

			mc.mcProfiler.startSection("drawStrings");
			/*for(Position p : new Position[] {Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT}) {
				int x = p == Position.TOP_LEFT || p == Position.BOTTOM_LEFT ? Constants.SPACER : resolution.getScaledWidth() - Constants.SPACER;
				int y = layoutManager.get(p);
				if(p == Position.BOTTOM_LEFT || p == Position.BOTTOM_RIGHT) y = resolution.getScaledHeight() - y;

				RenderUtil.renderStrings(mc.fontRendererObj, stringManager.get(p), x, y, p);
			}*/

			int right = resolution.getScaledWidth() - Constants.SPACER;
			RenderUtil.renderStrings(mc.fontRenderer, stringManager.get(Position.TOP_LEFT), Constants.SPACER, layoutManager.get(Position.TOP_LEFT), Position.TOP_LEFT);
			RenderUtil.renderStrings(mc.fontRenderer, stringManager.get(Position.TOP_RIGHT), right, layoutManager.get(Position.TOP_RIGHT), Position.TOP_RIGHT);
			RenderUtil.renderStrings(mc.fontRenderer, stringManager.get(Position.BOTTOM_LEFT), Constants.SPACER, resolution.getScaledHeight() - layoutManager.get(Position.BOTTOM_LEFT), Position.BOTTOM_LEFT);
			RenderUtil.renderStrings(mc.fontRenderer, stringManager.get(Position.BOTTOM_RIGHT), right, resolution.getScaledHeight() - layoutManager.get(Position.BOTTOM_RIGHT), Position.BOTTOM_RIGHT);

			mc.mcProfiler.endSection();
			GL11.glPopMatrix();
			// glPopAttrib();
			mc.mcProfiler.endSection();

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			//GL11.glPopAttrib();
		}
	}

	public static Random random = new Random();
	private static short updateTicker = 0;

	@SubscribeEvent
	@SideOnly(Side.CLIENT) // GuiScreen doesn't exist on server side
	public void clientTick(ClientTickEvent e) {
		Minecraft mc = Minecraft.getMinecraft();

		if(proxy.elements.globalSettings.enabled) {
			updateTicker++;
			if(updateTicker == 200) updateTicker = 0;
	
			for(ExtraGuiElement element : ExtraGuiElement.UpdateSpeed.FASTER.elements) {
				if(element.shouldRender()) element.update(mc);
			}
			if(updateTicker % 20 == 0) {
				for(ExtraGuiElement element : ExtraGuiElement.UpdateSpeed.FAST.elements) {
					if(element.shouldRender()) element.update(mc);
				}
				if(updateTicker % 100 == 0) {
					for(ExtraGuiElement element : ExtraGuiElement.UpdateSpeed.MEDIUM.elements) {
						if(element.shouldRender()) element.update(mc);
					}
					if(updateTicker == 0) {
						for(ExtraGuiElement element : ExtraGuiElement.UpdateSpeed.SLOW.elements) {
							if(element.shouldRender()) element.update(mc);
						}
					}
				}
			}
		}

		if(mc != null && mc.player != null && mc.inGameHasFocus && mc.currentScreen == null) {
			if(((ClientProxy)BetterHud.proxy).openMenu.isPressed()) {
				GuiHUDMenu gui = new GuiHUDMenu();
				gui.initGui();
				mc.displayGuiScreen(gui);
			} else if(((ClientProxy)BetterHud.proxy).disable.isPressed()) {
				((ClientProxy)BetterHud.proxy).elements.globalSettings.enabled = !((ClientProxy)BetterHud.proxy).elements.globalSettings.enabled;
			}
		}
	}
}
