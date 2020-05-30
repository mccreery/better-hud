package jobicade.betterhud.element;

import java.util.ArrayList;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingWarnings;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public abstract class EquipmentDisplay extends OverlayElement {
	private SettingBoolean showName;
	private SettingBoolean showDurability;
	private SettingWarnings warnings;
	private SettingChoose durabilityMode;
	private SettingBoolean showUndamaged;

	public EquipmentDisplay() {
		settings.addChildren(
			showName = new SettingBoolean("showName"),
			showDurability = new SettingBoolean("showDurability", Direction.WEST),
			durabilityMode = new SettingChoose("durabilityFormat", Direction.EAST, "points", "percentage") {
				@Override
				public boolean enabled() {
					return showDurability.get();
				}
			},
			showUndamaged = new SettingBoolean("showUndamaged") {
				@Override
				public boolean enabled() {
					return showDurability.get();
				}
			}.setValuePrefix("betterHud.value.visible"),
			warnings = new SettingWarnings("damageWarning")
		);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		showName.set(true);
		showDurability.set(true);
		durabilityMode.setIndex(0);
		showUndamaged.set(true);

		warnings.set(new Double[] {.45, .25, .1});
	}

	protected boolean hasText() {
		return showName.get() || showDurability.get();
	}

	protected boolean showDurability(ItemStack stack) {
		return showDurability.get() && (showUndamaged.get() ? stack.isItemStackDamageable() : stack.isItemDamaged());
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

		if(showDurability(stack)) {
			if(durabilityMode.getIndex() == 1) {
				parts.add(MathUtil.formatToPlaces(value * 100, 1) + "%");
			} else {
				parts.add(durability + "/" + maxDurability);
			}
		}

		String text = String.join(" - ", parts);

		if(stack.isItemStackDamageable()) {
			int count = warnings.getWarning(value);
			if(count > 0) text += ' ' + I18n.format("betterHud.setting.warning." + count);
		}
		return text;
	}
}
