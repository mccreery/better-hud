package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingWarnings;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;

public abstract class EquipmentDisplay extends HudElement {
	private final SettingBoolean showName = new SettingBoolean("showName");
	private final SettingBoolean showDurability = new SettingBoolean("showDurability", Direction.WEST);
	private final SettingWarnings warnings = new SettingWarnings("damageWarning");

	private final SettingChoose durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, "points", "percentage") {
		@Override
		public boolean enabled() {
			return showDurability.get();
		}
	};

	protected EquipmentDisplay(String name) {
		super(name);

		settings.add(showName);
		settings.add(showDurability);
		settings.add(durabilityMode);
		settings.add(warnings);
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
