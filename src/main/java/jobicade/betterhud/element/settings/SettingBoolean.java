package jobicade.betterhud.element.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.SuperButton;

public class SettingBoolean extends SettingAlignable {
    public static final String VISIBLE = "betterHud.value.visible";

    protected SuperButton toggler;
    private String unlocalizedValue = "options";

    private boolean value = false;

    public SettingBoolean(String name) {
        super(name);
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
    public JsonElement saveJson(Gson gson) {
        return new JsonPrimitive(value);
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            value = element.getAsJsonPrimitive().getAsBoolean();
        } else {
            throw new JsonSyntaxException("not a boolean");
        }
    }

    @Override
    public void updateGuiParts() {
        toggler.active = enabled();
        toggler.setMessage(getUnlocalizedName(), unlocalizedValue, value);
    }
}
