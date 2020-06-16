package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends Setting<Integer, SettingInteger> {
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
    public boolean hasValue() {
        return true;
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
