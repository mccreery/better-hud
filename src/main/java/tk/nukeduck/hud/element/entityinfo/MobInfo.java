package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

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
		settings.set(true);
		players.set(true);
		mobs.set(true);

		compress.set(40.0);
	}

	private static ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");

	private boolean shouldCompress(int health) {
		return getMaxDisplay(health) < health;
	}

	private int getMaxDisplay(int health) {
		return compress.get() > 0 ? (int)Math.min(compress.get(), health) : health;
	}

	private Point getHealthSize(EntityLivingBase entity) {
		Point size = new Point(Math.min((int)entity.getMaxHealth() / 2, 10) * 8 + 1, 0);

		if(shouldCompress((int)entity.getHealth())) {
			if(entity.getHealth() % 20 == 0) {
				size.y = 9;
			} else {
				size.y = 18;
			}
		} else {
			int limit = getMaxDisplay((int)entity.getMaxHealth());
			size.y = (int)Math.ceil(limit / 20) * 9;
		}
		return size;
	}

	private void renderHealth(EntityLivingBase entity, Point position) {
		GlStateManager.enableTexture2D();
		MC.getTextureManager().bindTexture(icons);
		GlUtil.color(Colors.WHITE);

		int health    = (int)entity.getHealth();
		int maxHealth = (int)entity.getMaxHealth();
		position = new Point(position);

		if(shouldCompress((int)entity.getHealth())) {
			int rows = health / 20;
			health -= rows * 20;
			maxHealth -= rows * 20;

			int x = position.x;

			for(int i = 0; i < 10; i++, x += 4) {
				GlUtil.drawTexturedModalRect(x, position.y, 16, 0, 9, 9);
				GlUtil.drawTexturedModalRect(x, position.y, 52, 0, 9, 9);
			}

			MC.fontRenderer.drawString("x" + rows, x + 5 + SPACER, position.y, Colors.WHITE);
			MC.getTextureManager().bindTexture(icons);

			position.y += 9;
		}

		int limit = getMaxDisplay(maxHealth);

		while(limit > 0) {
			int row = Math.min(maxHealth, 20) / 2;

			for(int i = 0, x = position.x; i < row; i++, x += 8) {
				GlUtil.drawTexturedModalRect(x, position.y, 16, 0, 9, 9);

				if(health >= 2) {
					GlUtil.drawTexturedModalRect(x, position.y, 52, 0, 9, 9);
					health -= 2;
				} else if(health == 1) {
					GlUtil.drawTexturedModalRect(x, position.y, 52, 0, 5, 9);
					health = 0;
				} else {
					break;
				}
			}
			limit -= 20;
		}
	}

	public void render(EntityLivingBase entity, float partialTicks) {
		boolean isPlayer = entity instanceof EntityPlayer;
		if((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
			return;
		}

		List<String> text = new ArrayList<String>();
		text.add(String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, (int)entity.getHealth(), (int)entity.getMaxHealth()));

		if(isPlayer) {
			text.add(String.format("(%d/%d)", entity.getTotalArmorValue(), 20));
		}

		Point textSize   = getLinesSize(text);
		Point healthSize = getHealthSize(entity);

		if(healthSize.x > textSize.x) {
			textSize.x = healthSize.x;
		}

		PaddedBounds bounds = new PaddedBounds(new Bounds(textSize), new Bounds(0, healthSize.y), Bounds.PADDING);
		BetterHud.MANAGER.position(Direction.SOUTH, bounds);

		drawRect(bounds, Colors.TRANSLUCENT);
		drawLines(text, bounds.contentBounds(), Direction.NORTH_WEST, Colors.WHITE);
		renderHealth(entity, new Point(bounds.contentBounds().x(), bounds.contentBounds().bottom()));

		// TODO
		if(isPlayer) {
			int armor = entity.getTotalArmorValue();

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
				renderHolding(playerObj.getHeldItemMainhand(), bounds.bottom());
			}
		}
	}

	private List<String> getEnchantmentLines(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		List<String> enchantLines = new ArrayList<String>();

		for(Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
			enchantLines.add(ChatFormatting.LIGHT_PURPLE + enchantment.getKey().getTranslatedName(enchantment.getValue()));
		}
		return enchantLines;
	}

	private void renderHolding(ItemStack stack, int top) {
		final String holding = I18n.format("betterHud.strings.holding");
		MC.ingameGUI.drawString(MC.fontRenderer, holding, SPACER, top + SPACER, Colors.WHITE);

		List<String> enchantLines = getEnchantmentLines(stack);
		drawLines(enchantLines, new Bounds(0, top, 0, 0), Direction.NORTH_WEST, Colors.WHITE);

		String stackName = (stack.hasDisplayName() ? ChatFormatting.ITALIC : "") + "" + (stack.isItemEnchanted() ? ChatFormatting.AQUA : "") + stack.getDisplayName();
		MC.ingameGUI.drawString(MC.fontRenderer, stackName, 10 + 16 + MC.fontRenderer.getStringWidth(holding), top + 5, Colors.WHITE);
	}
}
