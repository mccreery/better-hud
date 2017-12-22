package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiOptionSliderA;
import tk.nukeduck.hud.util.FuncsUtil;

public class SettingSlider extends Setting {
	double minValue = 0.0;
	double maxValue = 1.0;
	public double value = 0.0;
	
	public double accuracy = -1;
	
	public SettingSlider(String name, double minValue, double maxValue) {
		super(name);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = FuncsUtil.clamp(value, minValue, maxValue);
	}
	
	public double normalize(double value) {
		return (value - minValue) / (maxValue - minValue);
	}
	
	public double denormalize(double normalized) {
		double toRound = normalized * (maxValue - minValue) + minValue;
		return accuracy == -1 ? toRound : FuncsUtil.clamp(Math.round(toRound / accuracy) * accuracy, minValue, maxValue);
	}
	
	public String getSliderText() {
		return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), this.value);
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		return new Gui[] {new GuiOptionSliderA(0, width / 2 - 100, y, 200, 20, this)};
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}
	
	@Override
	public void otherAction(Collection<Setting> settings) {}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	@Override
	public void fromString(String val) {
		try {
			this.value = FuncsUtil.clamp(Double.parseDouble(val), minValue, maxValue);
		} catch(NumberFormatException e) {}
	}
}