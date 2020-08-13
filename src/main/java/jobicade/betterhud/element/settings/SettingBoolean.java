package jobicade.betterhud.element.settings;

import java.util.Collection;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.SuperButton;
import net.minecraft.client.gui.widget.button.Button;

public class SettingBoolean extends SettingAlignable {
    public static final String VISIBLE = "betterHud.value.visible";

    protected SuperButton toggler;
    private String unlocalizedValue = "options";

    private boolean value = false;

    public SettingBoolean(HudElement<?> element, String name) {
        super(element, name);
    }

    public SettingBoolean(Setting parent, String name) {
        super(parent, name);
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void setValuePrefix(String value) {
        this.unlocalizedValue = value;
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        toggler = new SuperButton(b -> value = !value);
        toggler.setBounds(bounds);
        populator.add(toggler);
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        toggler.onPress();
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
    public void loadStringValue(String stringValue) {
        stringValue = stringValue.trim();

        if ("true".equalsIgnoreCase(stringValue)) {
            value = true;
            //return true;
        } else if ("false".equalsIgnoreCase(stringValue)) {
            value = false;
            //return true;
        } else {
            //return false;
        }
    }

    @Override
    public void updateGuiParts(Collection<Setting> settings) {
        super.updateGuiParts(settings);
        toggler.active = enabled();
        toggler.setMessage(getUnlocalizedName(), unlocalizedValue, value);
    }
}
