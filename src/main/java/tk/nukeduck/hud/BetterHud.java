package tk.nukeduck.hud;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.ExtraGuiElementArmorBars;
import tk.nukeduck.hud.element.ExtraGuiElementArrowCount;
import tk.nukeduck.hud.element.ExtraGuiElementBiome;
import tk.nukeduck.hud.element.ExtraGuiElementBlockViewer;
import tk.nukeduck.hud.element.ExtraGuiElementBlood;
import tk.nukeduck.hud.element.ExtraGuiElementBreedIndicator;
import tk.nukeduck.hud.element.ExtraGuiElementClock;
import tk.nukeduck.hud.element.ExtraGuiElementCompass;
import tk.nukeduck.hud.element.ExtraGuiElementConnection;
import tk.nukeduck.hud.element.ExtraGuiElementCoordinates;
import tk.nukeduck.hud.element.ExtraGuiElementDistance;
import tk.nukeduck.hud.element.ExtraGuiElementEnchantIndicator;
import tk.nukeduck.hud.element.ExtraGuiElementExperienceInfo;
import tk.nukeduck.hud.element.ExtraGuiElementFoodHealthStats;
import tk.nukeduck.hud.element.ExtraGuiElementFoodIndicator;
import tk.nukeduck.hud.element.ExtraGuiElementFps;
import tk.nukeduck.hud.element.ExtraGuiElementFullInvIndicator;
import tk.nukeduck.hud.element.ExtraGuiElementHandBar;
import tk.nukeduck.hud.element.ExtraGuiElementHealIndicator;
import tk.nukeduck.hud.element.ExtraGuiElementHidePlayers;
import tk.nukeduck.hud.element.ExtraGuiElementHorseInfo;
import tk.nukeduck.hud.element.ExtraGuiElementLightLevel;
import tk.nukeduck.hud.element.ExtraGuiElementMobInfo;
import tk.nukeduck.hud.element.ExtraGuiElementPotionBar;
import tk.nukeduck.hud.element.ExtraGuiElementSignReader;
import tk.nukeduck.hud.gui.GuiHUDMenu;
import tk.nukeduck.hud.util.RenderUtil;

@Mod(modid = BetterHud.modid, name = "NukeDuck's HUD", version = "1.2")

public class BetterHud {
	protected static final String modid = "hud";
	public static Logger logger = LogManager.getLogger(modid);
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static RenderItem ri = mc.getRenderItem();
	public static FontRenderer fr = mc.fontRendererObj;
	
	/** Mojang, please fix this. I want to be able to render items properly again. </3 */
	public static final Gui itemRendererGui = new Gui();
	
	public static ExtraGuiElement[] elements;
	
	static KeyBinding openMenu;
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		openMenu = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");
		ClientRegistry.registerKeyBinding(openMenu);
		FMLCommonHandler.instance().bus().register(new BetterHud());
		MinecraftForge.EVENT_BUS.register(new BetterHud());
		
		elements = new ExtraGuiElement[] {
			new ExtraGuiElementBlood(),
			new ExtraGuiElementCompass(),
			new ExtraGuiElementCoordinates(),
			new ExtraGuiElementEnchantIndicator(),
			new ExtraGuiElementArmorBars(),
			new ExtraGuiElementHandBar(),
			new ExtraGuiElementFoodHealthStats(),
			new ExtraGuiElementHealIndicator(),
			new ExtraGuiElementLightLevel(),
			new ExtraGuiElementPotionBar(),
			new ExtraGuiElementExperienceInfo(),
			new ExtraGuiElementClock(),
			new ExtraGuiElementFoodIndicator(),
			new ExtraGuiElementFps(),
			new ExtraGuiElementConnection(),
			new ExtraGuiElementSignReader(),
			new ExtraGuiElementBlockViewer(),
			new ExtraGuiElementArrowCount(),
			new ExtraGuiElementMobInfo(),
			new ExtraGuiElementHorseInfo(),
			new ExtraGuiElementDistance(),
			new ExtraGuiElementHidePlayers(),
			new ExtraGuiElementBiome(),
			new ExtraGuiElementBreedIndicator(),
			new ExtraGuiElementFullInvIndicator()
		};
		
		loadDefaults();
		loadSettings(logger);
		
		FMLCommonHandler.instance().bus().register(getFromName(elements, "connection"));
		MinecraftForge.EVENT_BUS.register(getFromName(elements, "connection"));
	}
	
	@SubscribeEvent
	public void entityRender(RenderLivingEvent.Pre e) {
		ExtraGuiElement hidePlayers = getFromName(elements, "hidePlayers");
		if(hidePlayers.enabled && e.entity instanceof EntityPlayer && !(hidePlayers.currentMode().equals("hidePlayers.onlyOthers") && e.entity.equals(mc.thePlayer))) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent e) {
		if(mc != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) mc.objectMouseOver.entityHit;
			
			/*EntityPlayer player = null;
			Vec3 start = Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
			Vec3 end = Vec3.createVectorHelper(mc.thePlayer.posX, mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
			Vec3 v = mc.thePlayer.getLookVec();
			while(start.distanceTo(end) < 200) {
				start.addVector(v.xCoord, v.yCoord, v.zCoord);
			}*/
			
			glPushAttrib(GL_ALL_ATTRIB_BITS); {
				((ExtraGuiElementMobInfo) getFromName(elements, "mobInfo")).renderInfo(entity, mc, e.partialTicks);
				((ExtraGuiElementHorseInfo) getFromName(elements, "horseInfo")).renderInfo(entity, mc, e.partialTicks);
				((ExtraGuiElementBreedIndicator) getFromName(elements, "breedIndicator")).renderInfo(entity, mc, e.partialTicks);
			}
			glPopAttrib();
		}
	}
	
	public static int currentLeftHeight;
	public static int currentRightHeight;
	
	public static int spacer = 5;
	
	//public static HashMap<UUID, Integer> bredEntities = new HashMap<UUID, Integer>();
	
	/*@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent e) {
		if(e.entityPlayer.equals(mc.thePlayer) && e.entity instanceof EntityAnimal) {
			if(((EntityAnimal) e.entity).isBreedingItem(mc.thePlayer.getCurrentEquippedItem())) {
				bredEntities
			}
			
			NBTTagCompound nbt = new NBTTagCompound();
			e.entity.writeToNBT(nbt);
			if(nbt.getInteger("Age") > 0) {
				bredEntities.put(e.entity.getUniqueID(), 6000);
			}
		}
	}*/
	
	@SubscribeEvent(priority = EventPriority.LOWEST) // We want this to render last out of everything
	public void onRenderTick(RenderGameOverlayEvent.Post e) {
		if(mc == null) mc = Minecraft.getMinecraft();
		if(fr == null) fr = mc.fontRendererObj;
		if(ri == null) ri = mc.getRenderItem();
		
		if(mc != null && mc.inGameHasFocus && mc.currentScreen == null && openMenu.isPressed()) {
			GuiHUDMenu gui = new GuiHUDMenu();
			gui.initGui();
			mc.displayGuiScreen(gui);
		}
		
		if(e.type == RenderGameOverlayEvent.ElementType.ALL) {
			if(mc != null && mc.ingameGUI != null && fr != null && mc.thePlayer != null) {
				mc.mcProfiler.startSection("extraGuiElements");
				glPushAttrib(GL_ALL_ATTRIB_BITS);
				glPushMatrix(); {
	                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			        int width = scaledresolution.getScaledWidth();
			        int height = scaledresolution.getScaledHeight();
			        
			        int halfWidth = width / 2;
			        
			        ArrayList<String> leftStrings = new ArrayList<String>();
			        ArrayList<String> rightStrings = new ArrayList<String>();
			        
			        currentLeftHeight = spacer;
			        currentRightHeight = spacer;
			        
			        // General functions, sure to fix a few lighting bugs!
			        GL11.glEnable(GL11.GL_BLEND);
			        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			        
			        RenderUtil.drawRect(5, 5, 105, 105, 0xffffffff);
			        
			        /*for(ExtraGuiElement element : elements) {
			        	if(element.enabled) {
				        	element.render(mc, fr, ri, width, halfWidth, height, leftStrings, rightStrings);
				        	currentLeftHeight += element.leftHeight + spacer;
				        	currentRightHeight += element.rightHeight + spacer;
				        	mc.mcProfiler.endSection();
			        	}
			        }*/
			        
			        mc.mcProfiler.startSection("drawStrings");
			        RenderUtil.renderStrings(fr, leftStrings, spacer, currentLeftHeight, 0xffffff);
			        RenderUtil.renderStrings(fr, rightStrings, width - spacer, currentRightHeight, 0xffffff, true);
			        mc.mcProfiler.endSection();
				}
				glPopMatrix();
				glPopAttrib();
				mc.mcProfiler.endSection();
				
				GL11.glEnable(GL11.GL_BLEND);
			}
		}
	}
	
	Random random = new Random();
	
	static float currentHealth = -1;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(ClientTickEvent e) { // Because LivingHurtEvent only calls server-side... :(
		/*for(UUID uuid : bredEntities.keySet()) {
			bredEntities.put(uuid, bredEntities.get(uuid) - 1);
			if(bredEntities.get(uuid) == 0) bredEntities.remove(uuid);
		}*/
		
		if(mc != null && mc.thePlayer != null/* && !mc.isSingleplayer()*/) {
			if(currentHealth == -1) currentHealth = mc.thePlayer.getHealth();
			if(mc.thePlayer.getHealth() < currentHealth) {
		        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		        int width = scaledresolution.getScaledWidth();
		        int height = scaledresolution.getScaledHeight();
		        
				for(int i = 0; i < 2 * ((currentHealth - mc.thePlayer.getHealth()) - 1) * (getFromName(elements, "bloodSplatters").mode + 1 * 2); i++) {
					bloodSplatters.add(new int[] {
						random.nextInt(width), 
						random.nextInt(height), 
						random.nextInt(250), // Opacity
						random.nextInt(256) + 128, // Size
						random.nextInt(4), // U
						random.nextInt(4), // V
						random.nextInt(360) // Rotation
					});
				}
				
				currentHealth = mc.thePlayer.getHealth();
			} else if(mc.thePlayer.getHealth() > currentHealth) currentHealth = mc.thePlayer.getHealth();
		}
	}
	
	/*@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerHit(LivingHurtEvent e) {
		if(this.mc != null && getFromName(elements, "bloodSplatters").enabled) {
	        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
	        int width = scaledresolution.getScaledWidth();
	        int height = scaledresolution.getScaledHeight();
	        
			if(e.entityLiving instanceof EntityPlayer && e.entityLiving.equals(mc.thePlayer)) {
				for(int i = 0; i < 2 * (int) Math.min(e.ammount, mc.thePlayer.getHealth()) * (getFromName(elements, "bloodSplatters").mode + 1 * 2); i++) {
					bloodSplatters.add(new int[] {
						random.nextInt(width), 
						random.nextInt(height), 
						random.nextInt(250), // Opacity
						random.nextInt(256) + 128, // Size
						random.nextInt(4), // U
						random.nextInt(4), // V
						random.nextInt(360) // Rotation
					});
				}
			}
		}
	}*/
	
	public static ArrayList<int[]> bloodSplatters = new ArrayList<int[]>();
	
	public static ExtraGuiElement getFromName(ExtraGuiElement[] elements, String name) {
		for(ExtraGuiElement el : elements) {
			if(name.equals(el.getName())) return el;
		}
		return null;
	}
	
	private static final String configPath = Loader.instance().getConfigDir() + File.separator + "hud.txt";
	private static final String lineSeparator = System.lineSeparator(), propertySeparator = "=", valueSeparator = ",";
	
	/**
	 * Saves Wall Jump settings using a custom system I created because Forge configuration files are so
	 * limited and aren't really geared towards these kinds of settings.
	 */
	public static void saveSettings(Logger logger) {
		try {
			logger.log(Level.INFO, "Saving HUD settings...");
			FileWriter settingsWriter = new FileWriter(configPath);
			for(int i = 0; i < elements.length; i++) {
				settingsWriter.write(elements[i].getName() + propertySeparator + String.valueOf(elements[i].enabled) + valueSeparator + String.valueOf(elements[i].mode) + lineSeparator);
			}
			settingsWriter.close();
		} catch (IOException e) {
			logger.log(Level.WARN, "Failed to save settings to " + configPath + "." + lineSeparator + e.getMessage());
		}
	}
	
	/**
	 * Locates and loads the settings file for use in-game.
	 */
	public static void loadSettings(Logger logger) {
		try {
			logger.log(Level.INFO, "Loading HUD settings...");
			String[] settings2 = readFileAsString(configPath).split(lineSeparator);
			for(int i = 0; i < settings2.length; i++) {
				String[] nameSetting = settings2[i].split(propertySeparator);
				ExtraGuiElement element = getFromName(elements, nameSetting[0]);
				if(element != null) {
					String[] setting = nameSetting[1].split(valueSeparator);
					element.enabled = Boolean.parseBoolean(setting[0]);
					int mode = Integer.parseInt(setting[1]);
					element.mode = mode < element.getModesSize() ? mode : 0;
				}
			}
		} catch (IOException e) {
			saveSettings(logger);
			logger.log(Level.WARN, "Failed to load settings from " + configPath + "." + lineSeparator + e.getMessage() + lineSeparator + "The default configuration was saved.");
		}
	}
	
	public static void loadDefaults() {
		for(ExtraGuiElement element : elements) {
			element.mode = element.defaultMode;
		}
	}
	
	/**
	 * Reads a file from the input directory on the file system and returns it as its string contents.
	 * @return String
	 * @throws java.io.IOException
	 */
	public static String readFileAsString(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		
		while((numRead=reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}