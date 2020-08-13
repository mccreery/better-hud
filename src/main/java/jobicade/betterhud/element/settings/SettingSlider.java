package jobicade.betterhud.element.settings;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiSlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

public class SettingSlider extends SettingAlignable {
    //protected GuiSlider guiSlider; // TODO

    private int displayPlaces;
    private String unlocalizedValue;

    private double displayScale = 1;

    public SettingSlider(HudElement<?> element, String name, float minimum, float maximum) {
        super(element, name);
        this.minimum = minimum;
        this.maximum = maximum;

        updateDisplayPlaces();
        setValue(getMinimum());
    }

    public SettingSlider(Setting parent, String name, float minimum, float maximum) {
        super(parent, name);
        this.minimum = minimum;
        this.maximum = maximum;

        updateDisplayPlaces();
        setValue(getMinimum());
    }

    protected float value;
    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        if (getInterval() != -1) {
            value = roundMultiple(value - getMinimum(), getInterval()) + getMinimum();
        }
        this.value = MathHelper.clamp(value, getMinimum(), getMaximum());
    }

    protected float roundMultiple(float x, float y) {
        return Math.round(x / y) * y;
    }

    protected final float minimum;
    public float getMinimum() {
        return minimum;
    }

    protected final float maximum;
    public float getMaximum() {
        return maximum;
    }

    protected float interval;
    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public void setDisplayPercent() {
        setUnlocalizedValue("betterHud.value.percent");
        setDisplayScale(100);
        setDisplayPlaces(0);
    }

    private void updateDisplayPlaces() {
        int places = interval != -1
            && interval * displayScale == (int)(interval * displayScale) ? 0 : 1;
        setDisplayPlaces(places);
    }

    public void setDisplayScale(double displayScale) {
        this.displayScale = displayScale;
        updateDisplayPlaces();
    }

    public void setDisplayPlaces(int displayPlaces) {
        this.displayPlaces = displayPlaces;
    }

    public void setUnlocalizedValue(String unlocalizedValue) {
        this.unlocalizedValue = unlocalizedValue;
    }

    public String getDisplayString() {
        return I18n.format("betterHud.setting." + name) + ": " + getDisplayValue(getValue() * displayScale);
    }

    public String getDisplayValue(double scaledValue) {
        String displayValue = MathUtil.formatToPlaces(scaledValue, displayPlaces);

        if(unlocalizedValue != null) {
            displayValue = I18n.format(unlocalizedValue, displayValue);
        }
        return displayValue;
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        populator.add(new GuiSlider(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), this));
    }

    @Override public void actionPerformed(GuiElementSettings gui, Button button) {}

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(getValue());
    }

    @Override
    public void loadStringValue(String save) {
        setValue(Float.valueOf(save));

        /*if(guiSlider != null) {
            guiSlider.updateDisplayString();
        }*/
    }
}
