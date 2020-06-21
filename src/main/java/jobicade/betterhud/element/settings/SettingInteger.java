package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends Setting implements IStringSetting {
    private int value;

    public SettingInteger(Builder builder) {
        super(builder);
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    @Override
    public IStringSetting getStringSetting() {
        return this;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }

    @Override
    public String getDefaultValue() {
        return "0";
    }

    @Override
    public void loadStringValue(String save) {
        try {
            value = Integer.parseInt(save);
        } catch (NumberFormatException e) {
            BetterHud.getLogger().error(e);
            value = 0;
        }
    }

    @Override
    public void loadDefaultValue() {
        value = 0;
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, GuiButton button) {
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder extends Setting.Builder<SettingInteger, Builder> {
        protected Builder(String name) {
            super(name);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SettingInteger build() {
            return new SettingInteger(this);
        }
    }
}
