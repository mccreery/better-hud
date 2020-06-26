package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends Setting {
    private int value;

    public SettingInteger(String name) {
        super(name);
    }

    public Integer get() {
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
    public void loadStringValue(String save) {
        try {
            value = Integer.parseInt(save);
        } catch (NumberFormatException e) {
            BetterHud.getLogger().error(e);
            value = 0;
        }
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, GuiButton button) {
    }

    @Override
    public SettingInteger setHidden() {
        super.setHidden();
        return this;
    }
}
