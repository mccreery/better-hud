package tk.nukeduck.hud.element.settings;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.gui.GuiOptionSliderA;

public class ElementSettingSliderPositioned extends ElementSettingSlider {
	Position sliderPos;
	
	public ElementSettingSliderPositioned(String name, double minValue, double maxValue, Position sliderPos) {
		super(name, minValue, maxValue);
		this.sliderPos = sliderPos;
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		return new Gui[] {new GuiOptionSliderA(0, width / 2 - 155 + (sliderPos == Position.MIDDLE_LEFT ? 0 : sliderPos == Position.MIDDLE_CENTER ? 1 : 2) * 105, y, 100, 20, this)};
	}
	
	@Override
	public int getGuiHeight() {
		return sliderPos == Position.MIDDLE_RIGHT ? 20 : -5;
	}
}