package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.ICONS;
import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.util.StringGroup;
import jobicade.betterhud.util.bars.StatBar;
import jobicade.betterhud.util.bars.StatBarArmor;

public class PlayerInfo extends EntityInfo {
	private StatBar<? super EntityPlayer> bar = new StatBarArmor();

	private SettingSlider tooltipLines;

	public PlayerInfo() {
		super("playerInfo");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(tooltipLines = new SettingSlider("tooltipLines", -1, 10, 1) {
			@Override
			public String getDisplayValue(double scaledValue) {
				if(scaledValue == -1) {
					return I18n.format("betterHud.value.unlimited");
				} else {
					return super.getDisplayValue(scaledValue);
				}
			}
		});
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && ((RenderMobInfoEvent)event).getEntity() instanceof EntityPlayer;
	}

	@Override
	public Rect render(Event event) {
		EntityPlayer player = (EntityPlayer)((RenderMobInfoEvent)event).getEntity();
		bar.setHost(player);
		List<String> tooltip = new ArrayList<String>();

		ItemStack held = player.getHeldItemMainhand();

		if(!held.isEmpty()) {
			tooltip.add(I18n.format("betterHud.hud.holding", getStackName(held)));
			getEnchantmentLines(held, tooltip);

			int lines = tooltipLines.getInt();
			if(lines != -1 && lines < tooltip.size()) {
				tooltip = tooltip.subList(0, lines);
			}
		}

		StringGroup group = new StringGroup(tooltip);
		Point size = group.getSize();
		if(size.getX() < 81) size = size.withX(81);

		Rect padding = Rect.createPadding(SPACER, SPACER, SPACER, SPACER + bar.getSize().getY());
		Rect bounds = new Rect(size).grow(padding);
		bounds = MANAGER.position(Direction.SOUTH, bounds);
		Rect contentRect = bounds.grow(padding.invert());

		GlUtil.drawRect(bounds, Color.TRANSLUCENT);
		group.draw(contentRect);

		MC.getTextureManager().bindTexture(ICONS);
		bar.render(contentRect.getAnchor(Direction.SOUTH_WEST), Direction.NORTH_WEST, Direction.NORTH_WEST);
		return null;
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
			if(enchantment.getKey() != null && enchantment.getValue() > 0) {
				dest.add(ChatFormatting.GRAY + enchantment.getKey().getTranslatedName(enchantment.getValue()));
			}
		}
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		tooltipLines.set(-1);
	}
}
