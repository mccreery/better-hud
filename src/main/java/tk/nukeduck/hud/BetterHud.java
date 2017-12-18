package tk.nukeduck.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenForest;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "hud", name = "NukeDuck's HUD", version = "1.0")

public class BetterHud {
	Minecraft mc;
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		mc = Minecraft.getMinecraft();
		FMLCommonHandler.instance().bus().register(new BetterHud());
		MinecraftForge.EVENT_BUS.register(new BetterHud());
	}
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent e) {
		if(mc != null && mc.ingameGUI != null && mc.fontRenderer != null && mc.thePlayer != null && e.phase == Phase.END) {
			ResourceLocation blood = new ResourceLocation("alchemy", "textures/gui/blood.png");
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(blood);
			
			int[][] bloods = bloodSplatters.toArray(new int[bloodSplatters.size()][]);
			for(int[] coords : bloods) {
				GL11.glPushMatrix(); {
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float) coords[2]) / 500.0F);
					GL11.glTranslatef(coords[0] / 2, coords[1] / 2, 0.0F);
					GL11.glRotatef(coords[6], 0.0F, 0.0F, 1.0F);
					GL11.glScalef(((float) coords[3]) / 32F, ((float) coords[3]) / 32F, 1.0F);
					mc.ingameGUI.drawTexturedModalRect(0, 0, coords[4] * 16, coords[5] * 16, 16, 16);
					coords[2] -= 1;
					if(coords[2] < 0) bloodSplatters.remove(coords);
				}
				GL11.glPopMatrix();
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			ResourceLocation inventory = new ResourceLocation("textures/gui/container/inventory.png");
			
			int it = 0;
			for(Iterator i = mc.thePlayer.getActivePotionEffects().iterator(); i.hasNext(); it++) {
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(inventory);
				PotionEffect pe = (PotionEffect) i.next();
				Potion potion = Potion.potionTypes[pe.getPotionID()];
				
				int iIndex = potion.getStatusIconIndex();
				
				mc.ingameGUI.drawTexturedModalRect(mc.displayWidth / 4 + (it * 16), mc.displayHeight / 4 - 23, 18 * (iIndex % 8), 198 + ((iIndex / 8) * 18), 18, 18);
				mc.ingameGUI.drawString(mc.fontRenderer, StatCollector.translateToLocal("potion.potency." + pe.getAmplifier()), mc.displayWidth / 4 + (it * 16) + 4, mc.displayHeight / 4 - 35, 0xffffffff);
			}
			
			mc.ingameGUI.drawCenteredString(mc.fontRenderer, "X: " + (double)Math.round(mc.thePlayer.posX * 1000) / 1000 + ", Y: " + (double)Math.round(mc.thePlayer.posY * 1000) / 1000 + ", Z: " + (double)Math.round(mc.thePlayer.posZ * 1000) / 1000, mc.displayWidth / 4, 5, 0xffffff);
			
			int nTransparency = (int) Math.abs(Math.sin((mc.thePlayer.rotationYaw) / 360 * Math.PI) * 255);
			int wTransparency = (int) Math.abs(Math.sin((mc.thePlayer.rotationYaw + 90) / 360 * Math.PI) * 255);
			int sTransparency = (int) Math.abs(Math.sin((mc.thePlayer.rotationYaw + 180) / 360 * Math.PI) * 255);
			int eTransparency = (int) Math.abs(Math.sin((mc.thePlayer.rotationYaw + 270) / 360 * Math.PI) * 255);
			
			int nColor = 256*256*256*nTransparency + 256*256*255 + 256*0 + 0;
			int eColor = 256*256*256*eTransparency + 256*256*255 + 256*255 + 255;
			int sColor = 256*256*256*sTransparency + 256*256*0 + 256*0 + 255;
			int wColor = 256*256*256*wTransparency + 256*256*255 + 256*255 + 255;
			
			mc.ingameGUI.drawRect(mc.displayWidth / 4 - 90, 18, mc.displayWidth / 4 + 90, 30, 0xaa000000);
			mc.ingameGUI.drawRect(mc.displayWidth / 4 - 40, 18, mc.displayWidth / 4 + 40, 30, 0x55555555);
			
			GL11.glEnable(GL11.GL_BLEND);
			
			// TODO what
			GL11.glPushMatrix(); {
				if(nTransparency > 10) {
					GL11.glTranslatef(mc.displayWidth / 4 - (int) (Math.sin((mc.thePlayer.rotationYaw + 180) / 180 * Math.PI) * 100), 20, 0.0F);
					GL11.glScalef(((float) nTransparency) / 128F, ((float) nTransparency) / 128F, 1.0F);
					mc.ingameGUI.drawCenteredString(mc.fontRenderer, "N", 0, 0, nColor);
				}
			}
			GL11.glPopMatrix();
			
			GL11.glPushMatrix(); {
				if(eTransparency > 10) {
					GL11.glTranslatef(mc.displayWidth / 4 - (int) (Math.sin((mc.thePlayer.rotationYaw + 90) / 180 * Math.PI) * 100), 20, 0.0F);
					GL11.glScalef(((float) eTransparency) / 128F, ((float) eTransparency) / 128F, 1.0F);
					mc.ingameGUI.drawCenteredString(mc.fontRenderer, "E", 0, 0, eColor);
				}
			}
			GL11.glPopMatrix();
			
			GL11.glPushMatrix(); {
				if(sTransparency > 10) {
					GL11.glTranslatef(mc.displayWidth / 4 - (int) (Math.sin((mc.thePlayer.rotationYaw + 360) / 180 * Math.PI) * 100), 20, 0.0F);
					GL11.glScalef(((float) sTransparency) / 128F, ((float) sTransparency) / 128F, 1.0F);
					mc.ingameGUI.drawCenteredString(mc.fontRenderer, "S", 0, 0, sColor);
				}
			}
			GL11.glPopMatrix();
			
			GL11.glPushMatrix(); {
				if(wTransparency > 10) {
					GL11.glTranslatef(mc.displayWidth / 4 - (int) (Math.sin((mc.thePlayer.rotationYaw + 270) / 180 * Math.PI) * 100), 20, 0.0F);
					GL11.glScalef(((float) wTransparency) / 128F, ((float) wTransparency) / 128F, 1.0F);
					mc.ingameGUI.drawCenteredString(mc.fontRenderer, "W", 0, 0, wColor);
				}
			}
			GL11.glPopMatrix();
			
			mc.ingameGUI.drawRect(mc.displayWidth / 4 - 1, 15, mc.displayWidth / 4 + 1, 22, 0xffff0000);
			mc.ingameGUI.drawRect(mc.displayWidth / 4 - 90, 15, mc.displayWidth / 4 - 89, 22, 0xffff0000);
			mc.ingameGUI.drawRect(mc.displayWidth / 4 + 90, 15, mc.displayWidth / 4 + 89, 22, 0xffff0000);
			
			for(double i = 0.1; i < 0.9; i += 0.1) {
				int loc = (int) (Math.asin(i) / Math.PI * 180);
				mc.ingameGUI.drawRect(mc.displayWidth / 4 + loc - 91, 16, mc.displayWidth / 4 + loc - 90, 22, 0xffffffff);
				mc.ingameGUI.drawRect(mc.displayWidth / 4 - loc + 91, 16, mc.displayWidth / 4 - loc + 90, 22, 0xffffffff);
			}
			
			RenderItem ri = new RenderItem();
			
			for(int i = 0; i < 4;i++) {
				if(mc.thePlayer.getCurrentArmor(3 - i) != null) {
					GL11.glPushMatrix();
					
					float value = (float) (mc.thePlayer.getCurrentArmor(3 - i).getMaxDamage() - mc.thePlayer.getCurrentArmor(3 - i).getItemDamageForDisplay()) / (float) mc.thePlayer.getCurrentArmor(3 - i).getMaxDamage();
					byte red = (byte) (256 - (255 * value));
					byte green = (byte) (255 * value);
					mc.ingameGUI.drawRect(26, 16 + (18 * i), 90, 16 + (18 * i) + 2, 0xff000000);
					mc.ingameGUI.drawRect(26, 16 + (18 * i), Math.round(26 + (value * 64)), 16 + (18 * i) + 1, 256*256*256*255 + 256*256*red + 256*green + 0);
					
					ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), mc.thePlayer.getCurrentArmor(3 - i), 5, 5 + (18 * i));
					
					//System.out.println("" + 256*256*red+256*green);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glPopMatrix();
					String text = mc.thePlayer.getCurrentArmor(3 - i).getDisplayName() + " - " + (mc.thePlayer.getCurrentArmor(3 - i).getMaxDamage() - mc.thePlayer.getCurrentArmor(3 - i).getItemDamageForDisplay()) + "/" + mc.thePlayer.getCurrentArmor(3 - i).getMaxDamage();
					mc.ingameGUI.drawString(mc.fontRenderer, text, 26, 5 + (18 * i), 0xffffff);
					if(value <= 0.3F) {
						int amount = 3 - (int) (value * 10);
						String exclamation = "";
						for(int i2 = 0; i2 < amount; i2++) exclamation += "! ";
						mc.ingameGUI.drawString(mc.fontRenderer, exclamation, mc.fontRenderer.getStringWidth(text) + 32, 5 + (18 * i), 0xffff0000);
					}
				}
			}
			
			if(mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getMaxDamage() > 0) {
				GL11.glPushMatrix();
				
				float value = (float) (mc.thePlayer.getCurrentEquippedItem().getMaxDamage() - mc.thePlayer.getCurrentEquippedItem().getItemDamageForDisplay()) / (float) mc.thePlayer.getCurrentEquippedItem().getMaxDamage();
				byte red = (byte) (256 - (255 * value));
				byte green = (byte) (255 * value);
				
				String text = mc.thePlayer.getCurrentEquippedItem().getDisplayName() + " - " + (mc.thePlayer.getCurrentEquippedItem().getMaxDamage() - mc.thePlayer.getCurrentEquippedItem().getItemDamageForDisplay()) + "/" + mc.thePlayer.getCurrentEquippedItem().getMaxDamage();
				
				// Bar
				mc.ingameGUI.drawRect(mc.displayWidth / 4 - 90, mc.displayHeight / 2 - 68, mc.displayWidth / 4 + 90, mc.displayHeight / 2 - 66, 0xff000000);
				mc.ingameGUI.drawRect(mc.displayWidth / 4 - 90, mc.displayHeight / 2 - 68, Math.round(mc.displayWidth / 4 - 90 + (value * 180)), mc.displayHeight / 2 - 67, 256*256*256*255 + 256*256*red + 256*green + 0);
				
				ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), mc.thePlayer.getCurrentEquippedItem(), mc.displayWidth / 4 - (mc.fontRenderer.getStringWidth(text) / 2) - 16, mc.displayHeight / 2 - 84);
				
				//System.out.println("" + 256*256*red+256*green);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glPopMatrix();
				
				mc.ingameGUI.drawString(mc.fontRenderer, text, mc.displayWidth / 4 - (mc.fontRenderer.getStringWidth(text) / 2) + 8, mc.displayHeight / 2 - 80, 0xffffff);
			}
			
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			if(mc.thePlayer.experienceLevel > 30) {
				GL11.glPushMatrix();
				ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(Items.enchanted_book), mc.displayWidth / 4 - 8, mc.displayHeight / 2 - 50);
				GL11.glPopMatrix();
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
			}
			GL11.glDisable(GL11.GL_LIGHTING);
			
			mc.ingameGUI.drawString(mc.fontRenderer, "" + ((float) mc.thePlayer.getFoodStats().getFoodLevel()) / 2, mc.displayWidth / 4 + 95, mc.displayHeight / 2 - 35, 0xffffff);
			mc.ingameGUI.drawString(mc.fontRenderer, "" + ((float) Math.ceil(mc.thePlayer.getHealth()) / 2), mc.displayWidth / 4 - 95 - mc.fontRenderer.getStringWidth("" + ((float) Math.ceil(mc.thePlayer.getHealth()) / 2)), mc.displayHeight / 2 - 35, 0xffffff);
			
			mc.ingameGUI.drawString(mc.fontRenderer, "Saturation: " + mc.thePlayer.getFoodStats().getSaturationLevel(), mc.displayWidth / 2 - 5 - mc.fontRenderer.getStringWidth("Saturation: " + mc.thePlayer.getFoodStats().getSaturationLevel()), mc.displayHeight / 2 - 10 - (2 * mc.fontRenderer.FONT_HEIGHT), 0xffffffff);
			
			int light = 0;
			String lightLevelString = "Light Level: " + ((light = mc.theWorld.getBlockLightValue((int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ) - 1) > 15 ? 15 : light);
			mc.ingameGUI.drawString(mc.fontRenderer, lightLevelString, mc.displayWidth / 2 - 5 - mc.fontRenderer.getStringWidth(lightLevelString), mc.displayHeight / 2 - 5 - mc.fontRenderer.FONT_HEIGHT, 0xffffffff);
			
			if(mc.thePlayer.getFoodStats().getFoodLevel() >= 18 && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
				mc.ingameGUI.drawString(mc.fontRenderer, "+", mc.displayWidth / 4 - 100 - mc.fontRenderer.getStringWidth("+" + ((float) Math.ceil(mc.thePlayer.getHealth()) / 2)), mc.displayHeight / 2 - 35, 0x00ff00);
			}
			
			if(mc.thePlayer.getFoodStats().needFood()) {
				if(mc.thePlayer.getFoodStats().getFoodLevel() == 0) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.sin((System.currentTimeMillis() % 10) / 10F * Math.PI));
					ri.renderWithColor = false;
					ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(Items.cooked_beef), mc.displayWidth / 4 + 5, mc.displayHeight / 4 + 5);
					ri.renderWithColor = true;
				} else {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.sin((System.currentTimeMillis() % (100 * mc.thePlayer.getFoodStats().getFoodLevel())) / (float) (100F * mc.thePlayer.getFoodStats().getFoodLevel()) * Math.PI));
					ri.renderWithColor = false;
					ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(Items.cooked_beef), mc.displayWidth / 4 + 5, mc.displayHeight / 4 + 5);
					ri.renderWithColor = true;
				}
			}
			
			//Vec3 posVec = mc.theWorld.getWorldVec3Pool().getVecFromPool(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
			//Vec3 lookVec = mc.thePlayer.getLookVec();
			//MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(posVec, lookVec);
			
			//if(mc.theWorld.getBlockLightValue(mop.blockX, mop.blockY, mop.blockZ) > 0) {
				
			//}
		} else if(mc == null) {
			mc = Minecraft.getMinecraft();
		}
	}
	
	@SubscribeEvent
	public void onPlayerHit(LivingHurtEvent e) {
		//System.out.println("Wha");
		//if(Minecraft.getMinecraft().thePlayer != null && e.entityLiving instanceof EntityPlayerSP) {
		if(e.entityLiving instanceof EntityPlayer) {
			for(int i = 0; i < 3 * e.ammount - 2; i++) {
				bloodSplatters.add(new int[] {
					new Random().nextInt(Minecraft.getMinecraft().displayWidth), // X
					new Random().nextInt(Minecraft.getMinecraft().displayHeight), // Y
					new Random().nextInt(250), // Opacity
					new Random().nextInt(256) + 128, // Size
					new Random().nextInt(4), // U
					new Random().nextInt(4), // V
					new Random().nextInt(360)
				});
			}
		}
		//}
	}
	
	private static ArrayList<int[]> bloodSplatters = new ArrayList<int[]>();
}