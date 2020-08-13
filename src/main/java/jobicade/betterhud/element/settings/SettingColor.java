package jobicade.betterhud.element.settings;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.render.Color;

public class SettingColor extends Setting {
    private final SettingSlider red = new SettingSlider(this, "red", 0, 255);
    private final SettingSlider green = new SettingSlider(this, "green", 0, 255);
    private final SettingSlider blue = new SettingSlider(this, "blue", 0, 255);

    {
        red.setInterval(1);
        green.setInterval(1);
        blue.setInterval(1);
    }

    public SettingColor(HudElement<?> element, String name) {
        super(element, name);
    }

    public SettingColor(Setting parent, String name) {
        super(parent, name);
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
