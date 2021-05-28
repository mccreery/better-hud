package jobicade.betterhud.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingWarnings;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.MathUtil;

public abstract class EquipmentDisplay extends HudElement {
    private SettingBoolean showName;
    private SettingBoolean showDurability;
    private SettingWarnings warnings;
    private SettingChoose durabilityMode;
    private SettingBoolean showUndamaged;

    protected EquipmentDisplay(String name, SettingPosition position) {
        super(name, position);
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(showName = new SettingBoolean("showName"));
        settings.add(showDurability = new SettingBoolean("showDurability", Direction.WEST));
        settings.add(durabilityMode = new SettingChoose("durabilityFormat", Direction.EAST, "points", "percentage") {
            @Override
            public boolean enabled() {
                return showDurability.get();
            }
        });
        settings.add(showUndamaged = new SettingBoolean("showUndamaged") {
            @Override
            public boolean enabled() {
                return showDurability.get();
            }
        }.setValuePrefix("betterHud.value.visible"));
        settings.add(warnings = new SettingWarnings("damageWarning"));
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
        return showDurability.get() && (showUndamaged.get() ? stack.isDamageableItem() : stack.isDamaged());
    }

    protected String getText(ItemStack stack) {
        if(!hasText() || stack.isEmpty()) return null;
        ArrayList<String> parts = new ArrayList<String>();

        if(this.showName.get()) {
            parts.add(stack.func_82833_r());
        }

        int maxDurability = stack.getMaxDamage();
        int durability = maxDurability - stack.getDamageValue();

        float value = (float)durability / (float)maxDurability;

        if(showDurability(stack)) {
            if(durabilityMode.getIndex() == 1) {
                parts.add(MathUtil.formatToPlaces(value * 100, 1) + "%");
            } else {
                parts.add(durability + "/" + maxDurability);
            }
        }

        String text = String.join(" - ", parts);

        if(stack.isDamageableItem()) {
            int count = warnings.getWarning(value);
            if(count > 0) text += ' ' + I18n.get("betterHud.setting.warning." + count);
        }
        return text;
    }
}
