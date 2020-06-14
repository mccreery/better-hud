package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends Setting<Integer, SettingInteger> {
    // TODO strange name?
    private int priority;

    public SettingInteger(String name) {
        super(name);
    }

    @Override
    protected SettingInteger getThis() {
        return this;
    }

    @Override
    public Integer get() {
        return priority;
    }

    @Override
    public void set(Integer value) {
        priority = value;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(priority);
    }

    @Override
    public String getDefaultValue() {
        return "0";
    }

    @Override
    public void loadStringValue(String save) {
        try {
            priority = Integer.parseInt(save);
        } catch (NumberFormatException e) {
            BetterHud.getLogger().error(e);
            priority = 0;
        }
    }

    @Override
    public void loadDefaultValue() {
        priority = 0;
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
