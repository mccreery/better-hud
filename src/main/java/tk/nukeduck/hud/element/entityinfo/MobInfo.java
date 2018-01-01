package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.RenderUtil;

public class MobInfo extends EntityInfo {
	private final SettingBoolean players = new SettingBoolean("players");
	private final SettingBoolean mobs = new SettingBoolean("mobs");

	private final SettingSlider compress = new SettingSlider("compress", 0, 200, 20) {
		@Override
		public String getSliderText() {
			if(value == 0) {
				return I18n.format("betterHud.menu.settingButton", getLocalizedName(), I18n.format("betterHud.setting.never"));
			} else {
				return I18n.format("betterHud.menu.settingButton", getLocalizedName(), (int)value);
			}
		}
	};

	public MobInfo() {
		super("mobInfo");

		settings.add(players);
		settings.add(mobs);
		settings.add(compress);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.set(true);
		players.set(true);
		mobs.set(true);
		distance.value = 100;
		compress.value = 40;
	}

	private static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");

	public void renderInfo(EntityLivingBase entity, float partialTicks) {
		EntityPlayer player = MC.player;
		if(player == null) return; // TODO is this necessary
		
		boolean isPlayer = entity instanceof EntityPlayer;
		if((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
			return;
		}
		
		GL11.glPushMatrix(); {
			Tessellator t = Tessellator.getInstance();
			RenderUtil.billBoard(entity, player, partialTicks);
			
			// seems irrelevant
			/*if(!(entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().isItemEnchanted())) {
				GL11.glTranslatef(0, 0.2F, 0);
			}*/
			
			String text = entity.getName() + " (" + (int) entity.getHealth() + "/" + (int) entity.getMaxHealth() + ")";
			float perLine = 10;
			
			int width = Math.max(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 1 + (isPlayer ? 5 + 8 * 10 : 0), MC.fontRenderer.getStringWidth(text)) + 10;
			int height;
			if(compress.value != 0 && entity.getHealth() >= compress.value) {
				height = 2 * MC.fontRenderer.FONT_HEIGHT + 12 + ((int)entity.getHealth() % 20 != 0 ? 9 : 0);
			} else {
				height = MC.fontRenderer.FONT_HEIGHT + 12;
				height += 9 * (compress.value != 0 && entity.getMaxHealth() > compress.value ? compress.value / 20 : (int) Math.ceil(Math.ceil(entity.getMaxHealth() / 2) / perLine));
			}
			
			float scale = 1.0F / width;
			GL11.glScalef(scale, scale, scale);
			
			// Rendering starts
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Gui.drawRect(0, 0, width, height, Colors.fromARGB(85, 0, 0, 0));
			zIncrease();
			MC.ingameGUI.drawString(MC.fontRenderer, text, 5, 5, Colors.WHITE);
			
			if(isPlayer) {
				String text2 = "(" + entity.getTotalArmorValue() + "/20)";
				MC.ingameGUI.drawString(MC.fontRenderer, text2, width - 5 - MC.fontRenderer.getStringWidth(text2), 5, Colors.WHITE);
			}
			
			// Render health bar
			renderHealth(t, (int)entity.getHealth(), (int)entity.getMaxHealth());
			
			if(isPlayer) {
				// Armor bar
				int armor = entity.getTotalArmorValue();
				
				int i;
				for(i = 0; i < 10; i++) {
					RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 16 / 256F, 9 / 256F, 25 / 256F, 18 / 256F, 9, 9);
				}
				for(i = 0; i < armor / 2; i++) {
					RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 34 / 256F, 9 / 256F, 43 / 256F, 18 / 256F, 9, 9);
				}
				if(armor % 2 == 1) {
					RenderUtil.renderQuadWithUV(t, 8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, 25 / 256F, 9 / 256F, 34 / 256F, 18 / 256F, 9, 9);
				}
				
				EntityPlayer playerObj = (EntityPlayer) entity;
				
				ItemStack mainHand = playerObj.getHeldItemMainhand();
				if(mainHand != null && !mainHand.isEmpty()) {
					renderHolding(playerObj.getHeldItemMainhand(), height);
				}
			}
		}
		GL11.glPopMatrix();
	}

	private void renderHealth(Tessellator t, int health, int max) {
		int y = MC.fontRenderer.FONT_HEIGHT + 7;

		MC.renderEngine.bindTexture(icons);
		if(compress.value != 0) {
			if(health >= compress.value) {
				int rows = health / 20;
				for(int i = 0; i < 10; i++) {
					RenderUtil.renderQuadWithUV(t, 5 + i*4, y, 16 / 256F, 0, 25 / 256F, 9 / 256F, 9, 9);
					RenderUtil.renderQuadWithUV(t, 5 + i*4, y, 52 / 256F, 0, 61 / 256F, 9 / 256F, 9, 9);
				}
				MC.ingameGUI.drawString(MC.fontRenderer, I18n.format("betterHud.strings.times", rows), 55, y, Colors.WHITE);
				y += MC.fontRenderer.FONT_HEIGHT;
	
				health -= rows * 20;
				max -= rows * 20;
				if(max > 20) max = 20;
			} else if(max > compress.value) {
				max = (int)compress.value;
			}
		}

		// changes made here to fix glitch where background doesn't show up on half a heart
		if(health != 0) {
			MC.renderEngine.bindTexture(icons);
			int i; // changes here
			for(i = 0; i < (max + 1) / 2; i++) { // Background
				RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), y + (int) (i / 10) * 9, 16 / 256F, 0, 25 / 256F, 9 / 256F, 9, 9);
			}
			for(i = 0; i < health / 2; i++) { // Hearts
				RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), y + (int) (i / 10) * 9, 52 / 256F, 0, 61 / 256F, 9 / 256F, 9, 9);
			}
			if(health % 2 == 1) { // Half heart
				RenderUtil.renderQuadWithUV(t, 5 + ((i % 10) * 8), y + (int) (i / 10) * 9, 61 / 256F, 0, 70 / 256F, 9 / 256F, 9, 9);
			}
		}
	}

	private void renderHolding(ItemStack stack, int y) {
		final String holding = I18n.format("betterHud.strings.holding");
		MC.ingameGUI.drawString(MC.fontRenderer, holding, 5, y + 5, Colors.WHITE);

		//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		//BetterHud.ri.renderItemAndEffectIntoGUI(is, 7 + BetterHud.fr.getStringWidth("Holding:"), height);
		//GL11.glPopAttrib();

		NBTTagList enchantments = stack.getEnchantmentTagList();

		if(enchantments != null) {
			for(int b = 0; b < enchantments.tagCount(); b++) {
				NBTTagCompound tag = enchantments.getCompoundTagAt(b);

				Enchantment enchantment = Enchantment.getEnchantmentByID(tag.getShort("id"));
				if (enchantment != null) {
					String display = ChatFormatting.LIGHT_PURPLE + enchantment.getTranslatedName(tag.getShort("lvl"));
					MC.ingameGUI.drawString(MC.fontRenderer, display, 5 + 100 * (b / 4), y + 10 + MC.fontRenderer.FONT_HEIGHT + ((MC.fontRenderer.FONT_HEIGHT + 2) * (b % 4)), Colors.WHITE);
				}
			}
		}

		String stackName = (stack.hasDisplayName() ? ChatFormatting.ITALIC : "") + "" + (stack.isItemEnchanted() ? ChatFormatting.AQUA : "") + stack.getDisplayName();
		MC.ingameGUI.drawString(MC.fontRenderer, stackName, 10 + 16 + MC.fontRenderer.getStringWidth(holding), y + 5, Colors.WHITE);
	}
}
