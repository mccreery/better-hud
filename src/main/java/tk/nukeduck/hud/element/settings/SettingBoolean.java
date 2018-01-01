package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class SettingBoolean extends SettingAlignable {
	protected GuiToggleButton toggler;
	protected boolean value;

	public SettingBoolean(String name) {
		this(name, Direction.CENTER);
	}

	public SettingBoolean(String name, Direction alignment) {
		super(name, alignment);
	}

	public boolean get() {return value;}
	public void set(boolean value) {this.value = value;}
	public boolean toggle() {
		set(!get());
		return get();
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Bounds bounds) {
		toggler = new GuiToggleButton(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), getUnlocalizedName(), true) {
			@Override
			public boolean get() {
				return SettingBoolean.this.get();
			}

			@Override
			public void set(boolean value) {
				SettingBoolean.this.set(value);
			}
		};
		parts.add(toggler);
		return bounds.bottom() + SPACER;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		toggler.toggle();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public String save() {
		return String.valueOf(value);
	}

	@Override
	public void load(String save) {
		value = Boolean.parseBoolean(save);
	}

	@Override
	public void otherAction(Collection<Setting> settings) {
		toggler.enabled = enabled();
	}
}
