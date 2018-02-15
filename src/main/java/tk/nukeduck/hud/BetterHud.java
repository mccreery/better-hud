package tk.nukeduck.hud;

import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
import tk.nukeduck.hud.element.entityinfo.EntityInfo;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.gui.GuiHudMenu;
import tk.nukeduck.hud.network.InventoryNameQuery;
import tk.nukeduck.hud.network.MessageNotifyClientHandler;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.network.MessagePickupHandler;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.HudConfig;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.Tickable.Ticker;

@Mod(modid = BetterHud.MODID, name = "Better HUD", version = "1.4")
public class BetterHud {
	public static final String MODID = "hud";
	public static final Version VERSION = new Version(1, 4);

	@SideOnly(Side.CLIENT) public static Minecraft MC;
	@SideOnly(Side.CLIENT) public static GuiHudMenu MENU;
	@SideOnly(Side.CLIENT) private static KeyBinding MENU_KEY, TOGGLE_KEY;

	public static final ResourceLocation HUD_ICONS = new ResourceLocation("hud", "textures/gui/icons_hud.png");
	public static final ResourceLocation ICONS     = new ResourceLocation("textures/gui/icons.png");
	public static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

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

	public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static PickupNotifier pickupNotifier;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
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

			ClientRegistry.registerKeyBinding(MENU_KEY = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc"));
			ClientRegistry.registerKeyBinding(TOGGLE_KEY = new KeyBinding("key.betterHud.disable", Keyboard.KEY_F3, "key.categories.misc"));

			HudElement.initAll(event);
		}

		// Message ID 0 reserved for ignored server presence message from [,1.4)
		NET_WRAPPER.registerMessage(MessagePickupHandler.class, MessagePickup.class, 1, Side.CLIENT);
		NET_WRAPPER.registerMessage(MessageNotifyClientHandler.class, Version.class, 2, Side.CLIENT);

		// Used to update inventory names
		NET_WRAPPER.registerMessage(InventoryNameQuery.ServerHandler.class, InventoryNameQuery.Request.class, 3, Side.SERVER);
		NET_WRAPPER.registerMessage(InventoryNameQuery.ClientHandler.class, InventoryNameQuery.Response.class, 4, Side.CLIENT);

		//MinecraftForge.EVENT_BUS.register(entityInfoRenderer = new EntityInfoRenderer());
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
	public void worldRender(RenderWorldLastEvent e) {
		if(!isEnabled()) return;

		MC.mcProfiler.startSection("betterHud");
		MANAGER.reset(Point.ZERO, 0);

		Entity entity = getMouseOver(HudElement.GLOBAL.getBillboardDistance(), e.getPartialTicks());
		if(entity == null || !(entity instanceof EntityLivingBase)) return;

		EntityLivingBase entityLiving = (EntityLivingBase)entity;

		GlStateManager.pushMatrix();
		GlUtil.setupBillboard(entity, e.getPartialTicks(), HudElement.GLOBAL.getBillboardScale());

		GlStateManager.disableDepth();
		GlUtil.color(Colors.WHITE);
		GlUtil.enableBlendTranslucent();

		for(EntityInfo element : HudElement.ENTITY_INFO) {
			element.tryRender(entityLiving, e.getPartialTicks());
		}

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();

		MC.mcProfiler.endSection();
	}

	/** Allows a custom distance
	 * @see net.minecraft.client.renderer.EntityRenderer#getMouseOver(float) */
	@SideOnly(Side.CLIENT)
	private Entity getMouseOver(double distance, float partialTicks) {
		if(MC.world == null) return null;
		Entity viewEntity = MC.getRenderViewEntity();
		if(viewEntity == null) return null;

		Entity pointedEntity = null;

		MC.mcProfiler.startSection("pick");

		RayTraceResult trace = viewEntity.rayTrace(distance, partialTicks);
		Vec3d eyePosition = viewEntity.getPositionEyes(partialTicks);
		Vec3d lookDelta = viewEntity.getLookVec().scale(distance);

		if(trace != null) {
			distance = trace.hitVec.distanceTo(eyePosition);
		}

		AxisAlignedBB range = viewEntity.getEntityBoundingBox().expand(lookDelta.x, lookDelta.y, lookDelta.z).grow(1, 1, 1);

		List<Entity> entitiesInRange = MC.world.getEntitiesInAABBexcluding(viewEntity, range, new Predicate<Entity>() {
			@Override
			public boolean apply(Entity entity) {
				return entity != null && entity.canBeCollidedWith();
			}
		});

		for(Entity entity : entitiesInRange) {
			AxisAlignedBB entityBox = entity.getEntityBoundingBox().grow(entity.getCollisionBorderSize());
			RayTraceResult entityTrace = entityBox.calculateIntercept(eyePosition, eyePosition.add(lookDelta));

			if(entityBox.contains(eyePosition)) {
				if(distance >= 0) {
					pointedEntity = entity;
					distance = 0;
				}
			} else if(entityTrace != null) {
				double entityDistance = eyePosition.distanceTo(entityTrace.hitVec);

				if(entityDistance < distance || distance == 0) {
					if(entity.getLowestRidingEntity() == viewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
						if(distance == 0) {
							pointedEntity = entity;
						}
					} else {
						pointedEntity = entity;
						distance = entityDistance;
					}
				}
			}
		}
		MC.mcProfiler.endSection();
		return pointedEntity;
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
		if(isEnabled()) Ticker.startTick();
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
