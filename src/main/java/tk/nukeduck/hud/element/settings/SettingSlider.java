package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiOptionSliderA;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;

public class SettingSlider extends SettingAlignable {
	private final double min;
	private final double max;
	public final double accuracy;

	public double value = 0;

	private int displayPlaces;
	private String unlocalizedValue = null;

	public SettingSlider(String name, double min, double max) {
		this(name, min, max, -1);
	}
	public SettingSlider(String name, double min, double max, double accuracy) {
		super(name, Direction.CENTER);
		this.min = min;
		this.max = max;
		this.accuracy = accuracy;
		this.displayPlaces = accuracy == (int)accuracy ? 0 : 1;

		this.value = MathHelper.clamp(value, min, max);
	}

	public SettingSlider setAlignment(Direction alignment) {
		this.alignment = alignment;
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

	public double normalize(double value) {
		return (value - min) / (max - min);
	}

	public double denormalize(double normalized) {
		double toRound = normalized * (max - min) + min;
		return accuracy == -1 ? toRound : MathHelper.clamp(Math.round(toRound / accuracy) * accuracy, min, max);
	}

	public String getDisplayString() {
		return getLocalizedName() + ": " + getDisplayValue(value);
	}

	public String getDisplayValue(double value) {
		String s = FormatUtil.formatToPlaces(value, displayPlaces);

		if(unlocalizedValue != null) {
			s = I18n.format(unlocalizedValue, s);
		}
		return s;
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Bounds bounds) {
		parts.add(new GuiOptionSliderA(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), this));
		return bounds.bottom() + SPACER;
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting> settings) {}

	@Override
	public String save() {
		return String.valueOf(value);
	}

	@Override
	public void load(String val) {
		value = MathHelper.clamp(Double.parseDouble(val), min, max);
	}
}
