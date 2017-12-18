package tk.nukeduck.hud.element.entityinfo;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementMobInfo extends ExtraGuiElementEntityInfo {
	ElementSettingBoolean players;
	ElementSettingBoolean mobs;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		players.value = true;
		mobs.value = true;
		distance.value = 100;
	}
	
	@Override
	public String getName() {
		return "mobInfo";
	}
	
	public ExtraGuiElementMobInfo() {
		this.settings.add(players = new ElementSettingBoolean("players"));
		this.settings.add(mobs = new ElementSettingBoolean("mobs"));
		this.settings.add(distance = new ElementSettingSlider("distance", 5, 200) {
			@Override
			public String getSliderText() {
				return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("strings.distanceShort", String.valueOf((int) this.value)));
			}
		});
	}
	
	private static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled) {
			EntityPlayer player = mc.thePlayer;
			
			boolean isPlayer = entity instanceof EntityPlayer;
			if((isPlayer && !players.value) || (!isPlayer && !mobs.value)) {
				return;
			}
			
			GL11.glPushMatrix(); {
				Tessellator t = Tessellator.getInstance();
				RenderUtil.billBoard(entity, player, partialTicks);
				
				if(!(entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().isItemEnchanted())) {
					GL11.glTranslatef(0, 0.2F, 0);
				}
				
				String text = entity.getName() + " (" + (int) entity.getHealth() + "/" + (int) entity.getMaxHealth() + ")";
				float perLine = 10;
				
				int width = Math.max(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 1 + (entity instanceof EntityPlayer ? 5 + 8 * 10 : 0), mc.fontRendererObj.getStringWidth(text)) + 10;
				int height = mc.fontRendererObj.FONT_HEIGHT + 12 + 9 * (int) Math.ceil(Math.ceil(entity.getMaxHealth() / 2) / perLine);
				
				float scale = 1.0F / width;
				GL11.glScalef(scale, scale, scale);
				
				// Rendering starts
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				RenderUtil.renderQuad(t, 0, 0, width, height, 0, 0, 0, 0.5F);
				RenderUtil.zIncrease();
				mc.ingameGUI.drawString(mc.fontRendererObj, text, 5, 5, RenderUtil.colorRGB(255, 255, 255));
				
				if(isPlayer) {
					String text2 = "(" + entity.getTotalArmorValue() + "/20)";
					mc.ingameGUI.drawString(mc.fontRendererObj, text2, width - 5 - mc.fontRendererObj.getStringWidth(text2), 5, RenderUtil.colorRGB(255, 255, 255));
				}
				
				mc.renderEngine.bindTexture(icons);
				
				// Render health bar
				int i;
				for(i = 0; i < Math.round(entity.getMaxHealth() / 2); i++) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 16 / 256F, 0, 25 / 256F, 9 / 256F, 9, 9);
				}
				for(i = 0; i < (int) entity.getHealth() / 2; i++) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 52 / 256F, 0, 61 / 256F, 9 / 256F, 9, 9);
				}
				if(((int) entity.getHealth()) % 2 == 1) {
					RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 61 / 256F, 0, 70 / 256F, 9 / 256F, 9, 9);
				}
				
				if(isPlayer) {
					// Armor bar
					int armor = (int) entity.getTotalArmorValue();
					
					for(i = 0; i < 10; i++) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 16 / 256F, 9 / 256F, 25 / 256F, 18 / 256F, 9, 9);
					}
					for(i = 0; i < armor / 2; i++) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 34 / 256F, 9 / 256F, 43 / 256F, 18 / 256F, 9, 9);
					}
					if(armor % 2 == 1) {
						RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), mc.fontRendererObj.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 25 / 256F, 9 / 256F, 34 / 256F, 18 / 256F, 9, 9);
					}
					
					EntityPlayer playerObj = (EntityPlayer) entity;
					if(playerObj.getHeldItemMainhand() != null) {
						mc.ingameGUI.drawString(mc.fontRendererObj, "Holding:", 5, height + 5, RenderUtil.colorRGB(255, 255, 255));
						ItemStack is = playerObj.getHeldItemMainhand();
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						//BetterHud.ri.renderItemAndEffectIntoGUI(is, 7 + BetterHud.fr.getStringWidth("Holding:"), height);
						GL11.glPopAttrib();
						if(is.getEnchantmentTagList() != null && is.getEnchantmentTagList().tagCount() != 0) {
							for(int b = 0; b < is.getEnchantmentTagList().tagCount(); b++) {
								NBTTagCompound thisEnchant = is.getEnchantmentTagList().getCompoundTagAt(b);
								short short1 = thisEnchant.getShort("id");
								short short2 = thisEnchant.getShort("lvl");
								String display = ChatFormatting.LIGHT_PURPLE + Enchantment.getEnchantmentByID(short1).getTranslatedName(short2);
								
								mc.ingameGUI.drawString(mc.fontRendererObj, display, 5 + (100 * (int)(b / 4)), height + 10 + mc.fontRendererObj.FONT_HEIGHT + ((mc.fontRendererObj.FONT_HEIGHT + 2) * (b % 4)), RenderUtil.colorRGB(255, 255, 255));
							}
						}
						mc.ingameGUI.drawString(mc.fontRendererObj, (is.hasDisplayName() ? ChatFormatting.ITALIC : "") + "" + (is.isItemEnchanted() ? ChatFormatting.AQUA : "") + is.getDisplayName(), 10 + 16 + mc.fontRendererObj.getStringWidth("Holding:"), height + 5, RenderUtil.colorRGB(255, 255, 255));
					}
				}
			}
			GL11.glPopMatrix();
		}
	}
}