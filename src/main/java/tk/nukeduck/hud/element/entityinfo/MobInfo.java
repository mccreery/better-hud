package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MC;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
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
import tk.nukeduck.hud.events.EntityInfoRenderer;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public class MobInfo extends EntityInfo {
	private final SettingBoolean players = new SettingBoolean("players");
	private final SettingBoolean mobs = new SettingBoolean("mobs");

	private final SettingSlider compress = new SettingSlider("compress", 0, 200, 20) {
		@Override
		public String getDisplayValue(double value) {
			if(get() == 0.0) {
				return I18n.format("betterHud.value.never");
			} else {
				return super.getDisplayValue(value);
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

		distance.set(100.0);
		compress.set(40.0);
	}

	private static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");

	public void render(EntityLivingBase entity, float partialTicks) {
		EntityPlayer player = MC.player;
		if(player == null) return; // TODO is this necessary
		
		boolean isPlayer = entity instanceof EntityPlayer;
		if((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
			return;
		}
		
		GlStateManager.pushMatrix();
		Tessellator t = Tessellator.getInstance();
		EntityInfoRenderer.billBoard(entity, player, partialTicks);
		
		// seems irrelevant
		/*if(!(entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().isItemEnchanted())) {
			GL11.glTranslatef(0, 0.2F, 0);
		}*/
		
		String text = entity.getName() + " (" + (int) entity.getHealth() + "/" + (int) entity.getMaxHealth() + ")";
		float perLine = 10;
		
		int width = Math.max(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 1 + (isPlayer ? 5 + 8 * 10 : 0), MC.fontRenderer.getStringWidth(text)) + 10;
		int height;
		if(compress.get() != 0 && entity.getHealth() >= compress.get()) {
			height = 2 * MC.fontRenderer.FONT_HEIGHT + 12 + ((int)entity.getHealth() % 20 != 0 ? 9 : 0);
		} else {
			height = MC.fontRenderer.FONT_HEIGHT + 12;
			height += 9 * (compress.get() != 0 && entity.getMaxHealth() > compress.get() ? compress.get() / 20 : (int) Math.ceil(Math.ceil(entity.getMaxHealth() / 2) / perLine));
		}
		
		float scale = 1.0F / width;
		GlStateManager.scale(scale, scale, scale);
		
		// Rendering starts
		GlUtil.enableBlendTranslucent();

		Gui.drawRect(0, 0, width, height, Colors.fromARGB(85, 0, 0, 0));
		//zIncrease();
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
			
			
			GlUtil.enableBlendTranslucent();

			int i;
			for(i = 0; i < 10; i++) {
				float u2 = 25 / 256F;
				float v2 = 18 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 16 / 256F, v2 - 9 / 256F);
			}
			for(i = 0; i < armor / 2; i++) {
				float u2 = 43 / 256F;
				float v2 = 18 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 34 / 256F, v2 - 9 / 256F);
			}
			if(armor % 2 == 1) {
				float u2 = 34 / 256F;
				float v2 = 18 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(8 * Math.min(10, (int) entity.getMaxHealth() / 2) + 10 + (i * 8), MC.fontRenderer.FONT_HEIGHT + 7 + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 25 / 256F, v2 - 9 / 256F);
			}
			
			EntityPlayer playerObj = (EntityPlayer) entity;
			
			ItemStack mainHand = playerObj.getHeldItemMainhand();
			if(mainHand != null && !mainHand.isEmpty()) {
				renderHolding(playerObj.getHeldItemMainhand(), height);
			}
		}
		GlStateManager.popMatrix();
	}

	// TODO format
	private void renderHealth(Tessellator t, int health, int max) {
		int y = MC.fontRenderer.FONT_HEIGHT + 7;

		MC.renderEngine.bindTexture(icons);
		if(compress.get() != 0) {
			if(health >= compress.get()) {
				int rows = health / 20;
				for(int i = 0; i < 10; i++) {
					float u2 = 25 / 256F;
					float v2 = 9 / 256F;
					GlUtil.enableBlendTranslucent();
					Gui.drawModalRectWithCustomSizedTexture(5 + i*4, y, u2, v2, 9, 9, u2 - 16 / 256F, v2 - (float) 0);
					float u21 = 61 / 256F;
					float v21 = 9 / 256F;
					Gui.drawModalRectWithCustomSizedTexture(5 + i*4, y, u21, v21, 9, 9, u21 - 52 / 256F, v21 - (float) 0);
				}
				MC.ingameGUI.drawString(MC.fontRenderer, I18n.format("betterHud.strings.times", rows), 55, y, Colors.WHITE);
				y += MC.fontRenderer.FONT_HEIGHT;
	
				health -= rows * 20;
				max -= rows * 20;
				if(max > 20) max = 20;
			} else if(max > compress.get()) {
				max = compress.get().intValue();
			}
		}

		// changes made here to fix glitch where background doesn't show up on half a heart
		if(health != 0) {
			MC.getTextureManager().bindTexture(icons);
			GlUtil.enableBlendTranslucent();

			int i; // changes here
			for(i = 0; i < (max + 1) / 2; i++) { // Background
				float u2 = 25 / 256F;
				float v2 = 9 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(5 + ((i % 10) * 8), y + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 16 / 256F, v2 - (float) 0);
			}
			for(i = 0; i < health / 2; i++) { // Hearts
				float u2 = 61 / 256F;
				float v2 = 9 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(5 + ((i % 10) * 8), y + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 52 / 256F, v2 - (float) 0);
			}
			if(health % 2 == 1) { // Half heart
				float u2 = 70 / 256F;
				float v2 = 9 / 256F;
				Gui.drawModalRectWithCustomSizedTexture(5 + ((i % 10) * 8), y + (int) (i / 10) * 9, u2, v2, 9, 9, u2 - 61 / 256F, v2 - (float) 0);
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
