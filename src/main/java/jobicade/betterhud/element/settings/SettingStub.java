package jobicade.betterhud.element.settings;

import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.widget.button.Button;

/** A default implementation of {@link Setting} which stores no value.<br>
 * It is used for settings which are for display only and which only store
 * the values of their children */
public class SettingStub extends Setting {
    public SettingStub() {
        this(null);
    }

    public SettingStub(String name) {
        super(name);
    }

    @Override public String getStringValue() {return null;}
    @Override public void loadStringValue(String save) {}
    @Override public void actionPerformed(GuiElementSettings gui, Button button) {}
    @Override public boolean hasValue() {return false;}
}
