package jobicade.betterhud.element.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings.Populator;

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
    public JsonElement saveJson(Gson gson) {
        return gson.toJsonTree(value);
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        value = gson.fromJson(element, Integer.class);
    }

    @Override
    public Point getGuiParts(Populator populator, Point topAnchor) {
        return topAnchor;
    }
}
