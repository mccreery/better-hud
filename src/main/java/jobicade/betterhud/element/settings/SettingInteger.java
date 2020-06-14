package jobicade.betterhud.element.settings;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.GuiButton;

public class SettingInteger extends Setting<Integer> {
    private int priority;

    public SettingInteger(String name) {
        super(name);
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
    public String save() {
        return String.valueOf(priority);
    }

    @Override
    public void load(String save) {
        try {
            priority = Integer.parseInt(save);
        } catch (NumberFormatException e) {
            BetterHud.getLogger().error(e);
            priority = 0;
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
