package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Direction;

public class SettingWarnings extends Setting {
	private final SettingBoolean enabled = new SettingBoolean("enabled");
	private final SettingSlider[] sliders;

	public SettingWarnings(String name) {
		this(name, 3);
	}

	public SettingWarnings(String name, int warnings) {
		super(name);
		add(new Legend("damageWarning"));

		sliders = new SettingSlider[warnings];
		for(int i = 0; i < sliders.length; i++) {
			add(sliders[i] = new SettingPercentage(String.valueOf(i), 1) {
				@Override
				public boolean enabled() {
					return SettingWarnings.this.enabled.get() && super.enabled();
				}
			}.setAlignment((i & 1) == 1 ? Direction.EAST : Direction.WEST));
		}
	}

	public void set(boolean enabled, int... values) {
		this.enabled.set(enabled);

		for(int i = 0; i < values.length && i < sliders.length; i++) {
			if(values[i] >= 0) sliders[i].value = values[i];
		}
	}

	public int getWarning(float value) {
		if(!enabled.get()) return 0;

		for(int i = sliders.length - 1; i >= 0; i++) {
			if(value <= sliders[i].value) return i + 1;
		}
		return 0;
	}

	@Override public String save() {return null;}
	@Override public void load(String save) {}
	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting> settings) {}
}
