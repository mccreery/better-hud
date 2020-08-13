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

    public EquipmentDisplay(String name) {
        super(name);

        showName = new SettingBoolean(this, "showName");
        showDurability = new SettingBoolean(this, "showDurability");
        showDurability.setAlignment(Direction.WEST);

        durabilityMode = new SettingChoose(this, "durabilityFormat", "points", "percentage");
        durabilityMode.setAlignment(Direction.EAST);
        durabilityMode.setEnableOn(showDurability::get);

        showUndamaged = new SettingBoolean(this, "showUndamaged");
        showUndamaged.setEnableOn(showDurability::get);
        showUndamaged.setValuePrefix("betterHud.value.visible");

        warnings = new SettingWarnings(this, "damageWarning", 3);
    }

    protected boolean hasText() {
        return showName.get() || showDurability.get();
    }

    protected boolean showDurability(ItemStack stack) {
        return showDurability.get() && (showUndamaged.get() ? stack.isDamageable() : stack.isDamaged());
    }

    protected String getText(ItemStack stack) {
        if(!hasText() || stack.isEmpty()) return null;
        ArrayList<String> parts = new ArrayList<String>();

        if(this.showName.get()) {
            parts.add(stack.getDisplayName().getFormattedText());
        }

        int maxDurability = stack.getMaxDamage();
        int durability = maxDurability - stack.getDamage();

        float value = (float)durability / (float)maxDurability;

        if(showDurability(stack)) {
            if(durabilityMode.getIndex() == 1) {
                parts.add(MathUtil.formatToPlaces(value * 100, 1) + "%");
            } else {
                parts.add(durability + "/" + maxDurability);
            }
        }

        String text = String.join(" - ", parts);

        if(stack.isDamageable()) {
            int count = warnings.getWarning(value);
            if(count > 0) text += ' ' + I18n.format("betterHud.setting.warning." + count);
        }
        return text;
    }
}
