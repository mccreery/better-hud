package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementChooser;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SettingElement extends SettingAlignable<HudElement> {
    private HudElement value;
    private GuiActionButton button;

    public SettingElement(String name, Direction alignment) {
        super(name, alignment);
    }

    @Override
    public HudElement get() {
        return value;
    }

    @Override
    public void set(HudElement value) {
        this.value = value;
    }

    @Override
    public JsonElement save() {
        return value != null ? new JsonPrimitive(value.getUnlocalizedName()) : JsonNull.INSTANCE;
    }

    @Override
    public void load(JsonElement save) {
        for(HudElement element : HudElement.ELEMENTS) {
            if(element.getUnlocalizedName().equals(save.getAsString())) {
                value = element;
                return;
            }
        }
        value = null;
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        Minecraft.getInstance().setScreen(new GuiElementChooser(gui, gui.element, this));
    }

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Rect bounds) {
        String text = getLocalizedName() + ": " + (value != null ? value.getLocalizedName() : I18n.get("betterHud.value.none"));
        button = new GuiActionButton(text);
        button.setBounds(bounds);

        parts.add(button);
        callbacks.put(button, this);
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        button.field_146124_l = enabled();
    }
}
