package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.realmsclient.gui.ChatFormatting;

import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.bars.StatBar;
import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class PlayerInfo extends EntityInfo {
	private StatBar<? super EntityPlayer> bar = new StatBarArmor();

	private SettingSlider tooltipLines;

	public PlayerInfo() {
		setRegistryName("player_info");
		setUnlocalizedName("playerInfo");

		settings.addChild(tooltipLines = new SettingSlider("tooltipLines", -1, 10, 1) {
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
	public boolean shouldRender(RenderMobInfoEvent context) {
		return context.getEntity() instanceof EntityPlayer;
	}

	@Override
	public Rect render(RenderMobInfoEvent context) {
		EntityPlayer player = (EntityPlayer)context.getEntity();
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

		List<Label> tooltipLabels = tooltip.stream().map(Label::new).collect(Collectors.toList());
		Grid<Label> grid = new Grid<Label>(new Point(1, tooltip.size()), tooltipLabels)
			.setCellAlignment(Direction.WEST).setGutter(new Point(2, 2));

		Rect bounds = new Rect(grid.getPreferredSize().add(10, 10));
		if(bar.shouldRender()) bounds = bounds.grow(0, 0, 0, bar.getPreferredSize().getY() + 2);
		bounds = MANAGER.position(Direction.SOUTH, bounds);
		GlUtil.drawRect(bounds, Color.TRANSLUCENT);

		Rect inner = bounds.grow(-5);
		grid.setBounds(new Rect(grid.getPreferredSize()).anchor(inner, Direction.NORTH_WEST)).render();
		if(bar.shouldRender()) bar.setBounds(new Rect(bar.getPreferredSize()).anchor(inner, Direction.SOUTH_WEST)).render();

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
