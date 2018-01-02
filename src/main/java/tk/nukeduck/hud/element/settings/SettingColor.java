package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Colors;

public class SettingColor extends Setting {
	protected final SettingSlider red, green, blue;

	public SettingColor(String name) {
		super(name);
		add(red = new SettingSlider(name + "Red", 0, 255, 1));
		add(green = new SettingSlider(name + "Green", 0, 255, 1));
		add(blue = new SettingSlider(name + "Blue", 0, 255, 1));
	}

	public void set(int color) {
		red.value = Colors.red(color);
		green.value = Colors.green(color);
		blue.value = Colors.blue(color);
	}
	public int get() {
		return Colors.fromRGB((int)red.value, (int)green.value, (int)blue.value);
	}

	@Override public String save() {return null;}
	@Override public void load(String save) {}
	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting> settings) {}
}
