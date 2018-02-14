package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class PlayerInfo extends EntityInfo {
	private final SettingSlider tooltipLines = new SettingSlider("tooltipLines", -1, 10, 1) {
		@Override
		public String getDisplayValue(double scaledValue) {
			if(scaledValue == -1) {
				return I18n.format("betterHud.value.unlimited");
			} else {
				return super.getDisplayValue(scaledValue);
			}
		}
	};

	public PlayerInfo() {
		super("playerInfo");

		settings.add(tooltipLines);
	}

	@Override
	public boolean shouldRender(EntityLivingBase entity) {
		return entity instanceof EntityPlayer;
	}

	@Override
	public void render(EntityLivingBase entity, float partialTicks) {
		List<String> tooltip = new ArrayList<String>();

		ItemStack held = entity.getHeldItemMainhand();

		if(!held.isEmpty()) {
			tooltip.add(I18n.format("betterHud.hud.holding", getStackName(held)));
			getEnchantmentLines(held, tooltip);

			int lines = tooltipLines.getInt();
			if(lines != -1 && lines < tooltip.size()) {
				tooltip = tooltip.subList(0, lines);
			}
		}

		Point size = getLinesSize(tooltip);
		if(size.x < 81) size.x = 81;

		PaddedBounds bounds = new PaddedBounds(new Bounds(size), new Bounds(0, 9), Bounds.PADDING);
		MANAGER.position(Direction.SOUTH, bounds);

		drawRect(bounds, Colors.TRANSLUCENT);

		drawLines(tooltip, bounds.contentBounds(), Direction.NORTH_WEST, Colors.WHITE);
		MC.getTextureManager().bindTexture(ICONS);
		GlUtil.renderBar(entity.getTotalArmorValue(), 20, Direction.SOUTH_WEST.getAnchor(bounds.contentBounds()), new Bounds(16, 9, 9, 9), new Bounds(25, 9, 9, 9), new Bounds(34, 9, 9, 9));
	}

	/** @see ItemStack#getTooltip(EntityPlayer, net.minecraft.client.util.ITooltipFlag) */
	private String getStackName(ItemStack stack) {
		StringBuilder builder = new StringBuilder();

		if(stack.hasDisplayName()) {
			builder.append(TextFormatting.ITALIC);
		}
		if(stack.isItemEnchanted()) {
			builder.append(TextFormatting.AQUA);
		} else {
			builder.append(TextFormatting.GRAY);
		}
		builder.append(stack.getDisplayName());
		return builder.toString();
	}

	/** Adds strings representing the enchantments on {@code stack} to {@code dest} */
	private void getEnchantmentLines(ItemStack stack, List<String> dest) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		for(Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
			dest.add(ChatFormatting.GRAY + enchantment.getKey().getTranslatedName(enchantment.getValue()));
		}
	}

	@Override
	public void loadDefaults() {
		tooltipLines.set(-1.);
	}
}
