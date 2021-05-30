package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.widget.button.Button;

/** A default implementation of {@link Setting} which stores no value.<br>
 * It is used for settings which are for display only and which only store
 * the values of their children */
public class SettingStub<T> extends Setting<T> {
    public SettingStub() {
        this(null);
    }

    public SettingStub(String name) {
        super(name);
    }

    @Override public T get() {return null;}
    @Override public void set(T value) {}
    @Override public JsonElement save() {return null;}
    @Override public void load(JsonElement save) {}
    @Override public void actionPerformed(GuiElementSettings gui, Button button) {}
    @Override public boolean hasValue() {return false;}
}
