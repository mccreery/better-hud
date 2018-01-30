package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Property.Type;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.ISaveLoad.ISlider;
import tk.nukeduck.hud.util.FormatUtil;

public class SettingSlider extends SettingAlignable<Double> implements ISlider {
	protected GuiSlider slider;
	private final double min, max, interval;

	private int displayPlaces;
	private String unlocalizedValue;

	private double value;
	private double displayScale = 1;

	public SettingSlider(String name, double min, double max) {
		this(name, min, max, -1);
	}

	public SettingSlider(String name, double min, double max, double interval) {
		super(name, Direction.CENTER);
		this.min = min;
		this.max = max;
		this.interval = interval;

		setDisplayPlaces(interval == (int)interval ? 0 : 1);
		set(getMinimum());
	}

	public SettingSlider setAlignment(Direction alignment) {
		this.alignment = alignment;
		return this;
	}

	public SettingSlider setDisplayScale(double displayScale) {
		this.displayScale = displayScale;
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

	@Override
	public String getDisplayString() {
		return I18n.format("betterHud.setting." + name) + ": " + getDisplayValue(get() * displayScale);
	}

	public String getDisplayValue(double scaledValue) {
		String displayValue = FormatUtil.formatToPlaces(scaledValue, displayPlaces);

		if(unlocalizedValue != null) {
			displayValue = I18n.format(unlocalizedValue, displayValue);
		}
		return displayValue;
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		slider = new GuiSlider(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), this);

		parts.add(slider);
		callbacks.put(slider, this);
		return bounds.bottom() + SPACER;
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting<?>> settings) {}

	@Override public Double get() {return value;}

	public int getInt() {
		return get().intValue();
	}

	@Override
	public void set(Double value) {
		this.value = ISlider.normalize(this, value);
		if(slider != null) slider.updateDisplayString();
	}

	@Override
	public String save() {
		return get().toString();
	}

	@Override
	public void load(String save) {
		set(Double.valueOf(save));

		if(slider != null) {
			slider.updateDisplayString();
		}
	}

	@Override
	protected Type getPropertyType() {
		return Type.DOUBLE;
	}

	@Override public Double getMinimum() {return min;}
	@Override public Double getMaximum() {return max;}
	@Override public Double getInterval() {return interval;}
}
