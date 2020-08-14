package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;

public class SettingInteger extends Setting {
    private int value;

    public SettingInteger(HudElement<?> element, String name) {
        super(element, name);
    }

    public SettingInteger(Setting parent, String name) {
        super(parent, name);
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
}
