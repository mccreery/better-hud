package jobicade.betterhud.element.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.render.Color;

public class SettingColor extends ParentSetting {
    private final SettingSlider red = new SettingSlider("red", 0, 255);
    private final SettingSlider green = new SettingSlider("green", 0, 255);
    private final SettingSlider blue = new SettingSlider("blue", 0, 255);

    {
        red.setInterval(1);
        green.setInterval(1);
        blue.setInterval(1);
    }

    public SettingColor(String name) {
        super(name);
        addChild(red);
        addChild(green);
        addChild(blue);
    }

    public void set(Color color) {
        red.setValue(color.getRed());
        green.setValue(color.getGreen());
        blue.setValue(color.getBlue());
    }

    public Color get() {
        return new Color((int)red.getValue(), (int)green.getValue(), (int)blue.getValue());
    }

    @Override
    public JsonElement saveJson(Gson gson) {
        return gson.toJsonTree(get());
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        set(gson.fromJson(element, Color.class));
    }
}
