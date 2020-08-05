package jobicade.betterhud.element.settings;

import jobicade.betterhud.render.Color;

public class SettingColor extends SettingStub {
    protected final SettingSlider red, green, blue;

    public SettingColor(String name) {
        super(name);

        addChildren(
            red = new SettingSlider("red", 0, 255, 1),
            green = new SettingSlider("green", 0, 255, 1),
            blue = new SettingSlider("blue", 0, 255, 1)
        );
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
