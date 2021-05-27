package jobicade.betterhud.element.settings;

import jobicade.betterhud.render.Color;

public class SettingColor extends SettingStub<Color> {
    protected final SettingSlider red, green, blue;

    public SettingColor(String name) {
        super(name);
        add(red = new SettingSlider("red", 0, 255, 1));
        add(green = new SettingSlider("green", 0, 255, 1));
        add(blue = new SettingSlider("blue", 0, 255, 1));
    }

    @Override
    public void set(Color color) {
        red.set(color.getRed());
        green.set(color.getGreen());
        blue.set(color.getBlue());
    }

    @Override
    public Color get() {
        return new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue());
    }
}
