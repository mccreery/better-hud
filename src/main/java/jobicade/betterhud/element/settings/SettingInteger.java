package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends FluentSetting<SettingInteger> implements IStringSetting {
    private int value;

    public SettingInteger(String name) {
        super(name);
    }

    @Override
    protected SettingInteger getThis() {
        return this;
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
}
