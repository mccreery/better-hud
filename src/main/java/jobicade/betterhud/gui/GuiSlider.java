package jobicade.betterhud.gui;

import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.widget.AbstractSlider;

public class GuiSlider extends AbstractSlider {
    private final SettingSlider slider;
    public boolean dragging;

    public GuiSlider(int x, int y, int width, int height, SettingSlider slider) {
        super(x, y, width, height, MathUtil.inverseLerp(slider.getMinimum(), slider.getMaximum(), slider.getValue()));
        this.slider = slider;
    }

    @Override
    protected void updateMessage() {
        setMessage(slider.getDisplayString());
    }

    @Override
    protected void applyValue() {
        slider.setValue(MathUtil.lerp(slider.getMinimum(), slider.getMaximum(), (float)value));
    }
}
