package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;

public class SettingBoolean extends Setting {
	protected GuiButton toggler;
	
	public SettingBoolean(String name) {
		super(name);
	}
	
	public boolean value;
	
	public boolean toggle() {
		set(!get());
		return get();
	}
	public void set(boolean bool) {value = bool;}
	public boolean get() {return value;}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		toggler = new GuiButton(0, width / 2 - 100, y, I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format(this.get() ? "options.on" : "options.off")));
		return new Gui[] {toggler};
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggle();
		button.displayString = I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format(this.get() ? "options.on" : "options.off"));
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {}
	
	@Override
	public String toString() {
		return String.valueOf(get());
	}
	@Override
	public void fromString(String val) {
		if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
			this.set(Boolean.parseBoolean(val));
		}
	}
	@Override
	public void otherAction(Collection<Setting> settings) {
		toggler.enabled = this.getEnabled();
	}
}