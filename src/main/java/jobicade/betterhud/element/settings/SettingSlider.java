package jobicade.betterhud.element.settings;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiSlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

public class SettingSlider extends SettingAlignable {
    private GuiSlider guiSlider;

    private int displayPlaces;
    private String unlocalizedValue;

    private double displayScale = 1;

    public SettingSlider(String name, float minimum, float maximum) {
        super(name);
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
        return I18n.format("betterHud.setting." + getName()) + ": " + getDisplayValue(getValue() * displayScale);
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
        populator.add(guiSlider = new GuiSlider(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), this));
    }

    @Override
    public void updateGuiParts() {
        guiSlider.updateMessage();
    }

    @Override
    public JsonElement saveJson(Gson gson) {
        return gson.toJsonTree(getValue());
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        setValue(gson.fromJson(element, Float.class));
    }
}
