package jobicade.betterhud.element.settings;

import jobicade.betterhud.render.Color;

public class SettingColor extends Setting {
    protected final SettingSlider red, green, blue;

    public SettingColor(Setting parent, String name) {
        super(parent, name);

        red = new SettingSlider(this, "red", 0, 255, 1);
        green = new SettingSlider(this, "green", 0, 255, 1);
        blue = new SettingSlider(this, "blue", 0, 255, 1);
    }

    public void set(Color color) {
        red.setValue(color.getRed());
        green.setValue(color.getGreen());
        blue.setValue(color.getBlue());
    }

    public Color get() {
        return new Color((int)red.getValue(), (int)green.getValue(), (int)blue.getValue());
    }
}
