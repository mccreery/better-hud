package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementChooser;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.SuperButton;
import net.minecraft.client.resources.I18n;

public class SettingElement extends SettingAlignable {
    private HudElement<?> value;
    private SuperButton button;

    public SettingElement(String name) {
        super(name);
    }

    public HudElement<?> get() {
        return value;
    }

    public void set(HudElement<?> value) {
        this.value = value;
    }

    @Override
    public JsonElement saveJson(Gson gson) {
        return gson.toJsonTree(value);
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        value = gson.fromJson(element, HudElement.class);
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        String text = getLocalizedName() + ": " + (value != null ? value.getLocalizedName() : I18n.format("betterHud.value.none"));
        // TODO pass current gui into setting
        button = populator.add(new SuperButton(b -> MC.displayGuiScreen(new GuiElementChooser(MC.currentScreen, ((GuiElementSettings)MC.currentScreen).element, this))));
        button.setBounds(bounds);
        button.setMessage(text);
    }

    @Override
    public void updateGuiParts() {
        button.active = enabled();
    }
}
