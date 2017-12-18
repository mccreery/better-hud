package tk.nukeduck.hud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import tk.nukeduck.hud.element.*;
import tk.nukeduck.hud.gui.GuiHUDMenu;
import tk.nukeduck.hud.util.RenderUtil;

import com.ibm.icu.util.Calendar;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.*;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.*;

@Mod(modid = "hud", name = "NukeDuck's HUD", version = "1.1b")

public class BetterHud {
	static Minecraft mc;
	static RenderItem ri = new RenderItem();
	static FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	
	public static ExtraGuiElement[] elements;
	
	static KeyBinding openMenu;
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		openMenu = new KeyBinding("key.betterHud.open", Keyboard.KEY_U, "key.categories.misc");
		ClientRegistry.registerKeyBinding(openMenu);
		
		mc = Minecraft.getMinecraft();
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
			new ExtraGuiElementBreedIndicator()
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
			while(start.distanceTo(end) < 5) {
				start.addVector(v.xCoord, v.yCoord, v.zCoord);
			}*/
			
			glPushAttrib(GL_ALL_ATTRIB_BITS); {
				((ExtraGuiElementMobInfo) getFromName(elements, "mobInfo")).renderInfo(entity, mc, e.partialTicks);
				((ExtraGuiElementHorseInfo) getFromName(elements, "horseInfo")).renderInfo(entity, mc, e.partialTicks);
				((ExtraGuiElementBreedIndicator) getFromName(elements, "breedIndicator")).renderInfo(entity, mc, e.partialTicks);
			}
			glPopAttrib();
		} else if(mc != null && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
			System.out.println("We're looking at an entity, but we don't think it's a player or mob:\n" + mc.objectMouseOver.entityHit.toString());
		}
	}
	
	public static int currentLeftHeight;
	public static int currentRightHeight;
	
	public static int spacer = 5;
	
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Post e) {
		if(e.type == RenderGameOverlayEvent.ElementType.ALL) {
			if(mc != null && mc.inGameHasFocus && mc.currentScreen == null && openMenu.isPressed()) {
				GuiHUDMenu gui = new GuiHUDMenu();
				gui.initGui();
				mc.displayGuiScreen(gui);
			}
			
			if(mc != null && mc.ingameGUI != null && fr != null && mc.thePlayer != null/* && e.phase == Phase.END*/) {
				mc.mcProfiler.startSection("extraGuiElements");
				glPushMatrix(); {
	                ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			        int width = scaledresolution.getScaledWidth();
			        int height = scaledresolution.getScaledHeight();
			        
			        int halfWidth = width / 2;
			        
			        ArrayList<String> leftStrings = new ArrayList<String>();
			        ArrayList<String> rightStrings = new ArrayList<String>();
			        
			        currentLeftHeight = spacer;
			        currentRightHeight = spacer;
			        
			        //System.out.println("------------------");
			        
			        for(ExtraGuiElement element : elements) {
			        	if(element.enabled) {
				        	mc.mcProfiler.startSection(element.getName());
				        	//System.out.println("Doing " + I18n.format("element." + element.getName(), new Object[0]));
				        	element.render(mc, fr, ri, width, halfWidth, height, leftStrings, rightStrings);
				        	currentLeftHeight += element.leftHeight + spacer;
				        	currentRightHeight += element.rightHeight + spacer;
				        	//if(element.leftHeight != -5 || element.rightHeight != -5) System.out.println(I18n.format("element." + element.getName(), new Object[0]) + " added " + element.leftHeight + " and " + element.rightHeight + " to make " + currentLeftHeight + " and " + currentRightHeight);
				        	mc.mcProfiler.endSection();
			        	}
			        }
			        
			        mc.mcProfiler.startSection("drawStrings");
			        RenderUtil.renderStrings(fr, leftStrings, spacer, currentLeftHeight, 0xffffff);
			        RenderUtil.renderStrings(fr, rightStrings, width - spacer, currentRightHeight, 0xffffff, true);
			        mc.mcProfiler.endSection();
				}
				glPopMatrix();
				mc.mcProfiler.endSection();
			} else if(mc == null) {
				mc = Minecraft.getMinecraft();
			}
			if(fr == null) fr = mc.fontRenderer;
		}
	}
	
	Random random = new Random();
	
	static float currentHealth = -1;
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(ClientTickEvent e) { // Because LivingHurtEvent only calls server-side... :(
		if(mc != null && mc.thePlayer != null && !mc.isSingleplayer()) {
			if(currentHealth == -1) currentHealth = mc.thePlayer.getHealth();
			if(mc.thePlayer.getHealth() < currentHealth) {
				System.out.println("Hit me by " + String.valueOf(currentHealth - mc.thePlayer.getHealth()));
		        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		        int width = scaledresolution.getScaledWidth();
		        int height = scaledresolution.getScaledHeight();
		        
				for(int i = 0; i < 2 * ((currentHealth - mc.thePlayer.getHealth()) - 1) * (getFromName(elements, "bloodSplatters").mode + 1 * 2); i++) {
					int x, y;
					x = random.nextInt(width);
					y = random.nextInt(height);
					
					bloodSplatters.add(new int[] {
						x, 
						y, 
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
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerHit(LivingHurtEvent e) {
		if(this.mc != null) {
	        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
	        int width = scaledresolution.getScaledWidth();
	        int height = scaledresolution.getScaledHeight();
	        
			if(e.entityLiving instanceof EntityPlayer && e.entityLiving.equals(mc.thePlayer)) {
				for(int i = 0; i < 2 * (e.ammount - 1) * (getFromName(elements, "bloodSplatters").mode + 1 * 2); i++) {
					int x, y;
					x = random.nextInt(width);
					y = random.nextInt(height);
					
					bloodSplatters.add(new int[] {
						x, 
						y, 
						random.nextInt(250), // Opacity
						random.nextInt(256) + 128, // Size
						random.nextInt(4), // U
						random.nextInt(4), // V
						random.nextInt(360) // Rotation
					});
				}
			}
		}
	}
	
	public static ArrayList<int[]> bloodSplatters = new ArrayList<int[]>();
	
	public static ExtraGuiElement getFromName(ExtraGuiElement[] elements, String name) {
		for(ExtraGuiElement el : elements) {
			if(name.equals(el.getName())) return el;
		}
		return null;
	}
	
	/**
	 * Saves Wall Jump settings using a custom system I created because Forge configuration files are so
	 * limited and aren't really geared towards these kinds of settings.
	 */
	/*public static void saveSettings() {
		try {
			System.out.println("Saving HUD settings...");
			FileWriter settingsWriter = new FileWriter(Loader.instance().getConfigDir() + "\\hud.txt");
			for(int i = 0; i < elements.length; i++) {
				settingsWriter.write(elements[i].getName() + "=" + elements[i].enabled + "," + elements[i].mode + "\n");
			}
			settingsWriter.close();
		} catch (IOException e) {
			System.out.println("Failed to save settings to " + Loader.instance().getConfigDir() + "/hud.txt. " + e.getMessage());
		}
	}*/
	
	/**
	 * Locates and loads the settings file for use in-game.
	 */
	/*public static void loadSettings() {
		try {
			String[] settings2 = readFileAsString(Loader.instance().getConfigDir() + "\\hud.txt").split("\n");
			for(int i = 0; i < settings2.length; i++) {
				String name = settings2[i].split("=")[0];
				ExtraGuiElement element = getFromName(elements, name);
				if(element != null) {
					String[] setting = settings2[i].split("=")[1].split(",");
					element.enabled = Boolean.parseBoolean(setting[0]);
					int mode = Integer.parseInt(setting[1]);
					element.mode = mode < element.getModesSize() ? mode : 0;
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to load settings from " + Loader.instance().getConfigDir() + "\\hud.txt. \n" + e.getMessage() + "\nThe default configuration was saved.");
			saveSettings();
		}
	}*/
	
	public static Logger logger = LogManager.getLogger("hud");
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