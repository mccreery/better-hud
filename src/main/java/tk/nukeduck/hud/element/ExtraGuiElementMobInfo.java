package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementMobInfo extends ExtraGuiElement {
	public ExtraGuiElementMobInfo() {
		name = "mobInfo";
		modes = new String[] {"mobInfo.players", "mobInfo.mobs", "both"}; // TODO Fix the player part
	}
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled) {
			EntityPlayer player = mc.thePlayer;
			Tessellator t = Tessellator.instance;
			
			boolean isPlayer = entity instanceof EntityPlayer;
			if((isPlayer && currentMode().equals("mobInfo.mobs")) || (!isPlayer && currentMode().equals("mobInfo.players"))) {
				return;
			}
			
			glPushMatrix(); {
				RenderUtil.billBoard(entity, player, partialTicks);
				if(!(entity.getHeldItem() != null && entity.getHeldItem().isItemEnchanted())) {
					GL11.glTranslatef(0, 0.2F, 0);
				}
				
				String text = entity.getCommandSenderName() + " (" + (int) entity.getHealth() + "/" + (int) entity.getMaxHealth() + ")";
				
				float perLine = 10;
				
				int width = Math.max(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 1 + (entity instanceof EntityPlayer ? 5 + 8 * 10 : 0), mc.fontRenderer.getStringWidth(text)) + 10;
				int height = mc.fontRenderer.FONT_HEIGHT + 12 + 9 * (int) Math.ceil(Math.ceil(entity.getMaxHealth() / 2) / perLine);
				
				float scale = 1.0F / width;
				glScalef(scale, scale, scale);
				
				// Rendering starts
				
				RenderUtil.renderQuad(t, 0, 0, width, height, 0, 0, 0, 0.5F);
				RenderUtil.zIncrease();
				mc.ingameGUI.drawString(mc.fontRenderer, text, 5, 5, 0xffffff);
				
				if(isPlayer) {
					String text2 = "(" + entity.getTotalArmorValue() + "/20)";
					mc.ingameGUI.drawString(mc.fontRenderer, text2, width - 5 - mc.fontRenderer.getStringWidth(text2), 5, 0xffffff);
				}
				
				mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
				
				// Render health bar
				int i;
				for(i = 0; i < Math.round(entity.getMaxHealth() / 2); i++) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 16 / 256F, 0, 25 / 256F, 9 / 256F, 9, 9, 1, 1, 1, 1);
				}
				for(i = 0; i < (int) entity.getHealth() / 2; i++) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 52 / 256F, 0, 61 / 256F, 9 / 256F, 9, 9, 1, 1, 1, 1);
				}
				if(((int) entity.getHealth()) % 2 == 1) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 61 / 256F, 0, 70 / 256F, 9 / 256F, 9, 9, 1, 1, 1, 1);
				}
				
				if(isPlayer) {
					// Armor bar
					int armor = (int) entity.getTotalArmorValue();
					
					for(i = 0; i < 10; i++) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 16 / 256F, 9 / 256F, 25 / 256F, 18 / 256F, 9, 9, 1, 1, 1, 1);
					}
					for(i = 0; i < armor / 2; i++) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 34 / 256F, 9 / 256F, 43 / 256F, 18 / 256F, 9, 9, 1, 1, 1, 1);
					}
					if(armor % 2 == 1) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 25 / 256F, 9 / 256F, 34 / 256F, 18 / 256F, 9, 9, 1, 1, 1, 1);
					}
					
					EntityPlayer playerObj = (EntityPlayer) entity;
					if(playerObj.getHeldItem() != null) {
						mc.ingameGUI.drawString(mc.fontRenderer, "Holding:", 5, height + 5, 0xffffff);
						ItemStack is = playerObj.getHeldItem();
						glPushAttrib(GL_ALL_ATTRIB_BITS);
						((ExtraGuiElementBreedIndicator) BetterHud.getFromName(BetterHud.elements, "breedIndicator")).ri.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), is, 7 + mc.fontRenderer.getStringWidth("Holding:"), height);
						glPopAttrib();
						if(is.getEnchantmentTagList() != null && is.getEnchantmentTagList().tagCount() != 0) {
							for(int b = 0; b < is.getEnchantmentTagList().tagCount(); b++) {
								NBTTagCompound thisEnchant = is.getEnchantmentTagList().getCompoundTagAt(b);
								String display = ChatFormatting.LIGHT_PURPLE + I18n.format(Enchantment.enchantmentsList[(int) thisEnchant.getShort("id")].getName(), new Object[0]) + " " + I18n.format("enchantment.level." + thisEnchant.getShort("lvl"), new Object[0]);
								
								mc.ingameGUI.drawString(mc.fontRenderer, display, 5 + (100 * (int)(b / 4)), height + 10 + mc.fontRenderer.FONT_HEIGHT + ((mc.fontRenderer.FONT_HEIGHT + 2) * (b % 4)), 0xffffff);
							}
						}
						mc.ingameGUI.drawString(mc.fontRenderer, (is.hasDisplayName() ? ChatFormatting.ITALIC : "") + "" + (is.isItemEnchanted() ? ChatFormatting.AQUA : "") + is.getDisplayName(), 10 + 16 + mc.fontRenderer.getStringWidth("Holding:"), height + 5, 0xffffff);
					}
				}
			}
			glPopMatrix();
		}
	}
}