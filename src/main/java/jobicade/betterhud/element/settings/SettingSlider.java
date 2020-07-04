package jobicade.betterhud.element.settings;

import java.util.List;
import java.util.Map;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiSlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

public class SettingSlider extends SettingAlignable {
	protected GuiSlider guiSlider;

	private int displayPlaces;
	private String unlocalizedValue;

	private double displayScale = 1;

	public SettingSlider(String name, float min, float max) {
		this(name, min, max, -1);
	}

	public SettingSlider(String name, float minimum, float maximum, float interval) {
		super(name, Direction.CENTER);
		this.minimum = minimum;
		this.maximum = maximum;
		this.interval = interval;

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

	protected final float interval;
	public float getInterval() {
		return interval;
	}

	public SettingSlider setDisplayPercent() {
		return setUnlocalizedValue("betterHud.value.percent")
			.setDisplayScale(100)
			.setDisplayPlaces(0);
	}

	private void updateDisplayPlaces() {
		int places = interval != -1
			&& interval * displayScale == (int)(interval * displayScale) ? 0 : 1;
		setDisplayPlaces(places);
	}

	public SettingSlider setAlignment(Direction alignment) {
		this.alignment = alignment;
		return this;
	}

	public SettingSlider setDisplayScale(double displayScale) {
		this.displayScale = displayScale;
		updateDisplayPlaces();

		return this;
	}

	public SettingSlider setDisplayPlaces(int displayPlaces) {
		this.displayPlaces = displayPlaces;
		return this;
	}

	public SettingSlider setUnlocalizedValue(String unlocalizedValue) {
		this.unlocalizedValue = unlocalizedValue;
		return this;
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
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		guiSlider = new GuiSlider(0, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), this);

		parts.add(guiSlider);
		callbacks.put(guiSlider, this);
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}

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

		if(guiSlider != null) {
			guiSlider.updateDisplayString();
		}
	}
}
