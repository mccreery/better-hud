package tk.nukeduck.hud.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingWarnings;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;

public abstract class EquipmentDisplay extends HudElement {
	private SettingBoolean showName;
	private SettingBoolean showDurability;
	private SettingWarnings warnings;
	private SettingChoose durabilityMode;

	protected EquipmentDisplay(String name, SettingPosition position) {
		super(name, position);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(showName = new SettingBoolean("showName"));
		settings.add(showDurability = new SettingBoolean("showDurability", Direction.WEST));
		settings.add(durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, "points", "percentage") {
			@Override
			public boolean enabled() {
				return showDurability.get();
			}
		});
		settings.add(warnings = new SettingWarnings("damageWarning"));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		showName.set(true);
		showDurability.set(true);
		durabilityMode.setIndex(0);

		warnings.set(new Double[] {.45, .25, .1});
	}

	protected boolean hasText() {
		return showName.get() || showDurability.get();
	}

	protected String getText(ItemStack stack) {
		if(!hasText() || stack.isEmpty()) return null;
		ArrayList<String> parts = new ArrayList<String>();

		if(this.showName.get()) {
			parts.add(stack.getDisplayName());
		}

		int maxDurability = stack.getMaxDamage();
		int durability = maxDurability - stack.getItemDamage();

		float value = (float)durability / (float)maxDurability;

		if(stack.isItemStackDamageable() && this.showDurability.get()) {
			if(durabilityMode.getIndex() == 1) {
				parts.add(FormatUtil.formatToPlaces(value * 100, 1) + "%");
			} else {
				parts.add(durability + "/" + maxDurability);
			}
		}

		StringBuilder builder = new StringBuilder();

		FormatUtil.join(" - ", parts, builder);

		if(stack.isItemStackDamageable()) {
			int count = warnings.getWarning(value);

			if(count > 0) {
				builder.append(' ');
				builder.append(I18n.format("betterHud.setting.warning." + count));
			}
		}
		return builder.toString();
	}
}
