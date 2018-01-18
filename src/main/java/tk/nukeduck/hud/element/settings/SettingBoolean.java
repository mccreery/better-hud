package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.common.config.Property.Type;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class SettingBoolean extends SettingAlignable<Boolean> {
	protected GuiToggleButton toggler;
	protected boolean value = false;

	public SettingBoolean(String name) {
		this(name, Direction.CENTER);
	}

	public SettingBoolean(String name, Direction alignment) {
		super(name, alignment);
	}

	@Override
	protected Type getPropertyType() {
		return Type.BOOLEAN;
	}

	public Boolean get() {return enabled() && value;}
	public void set(Boolean value) {this.value = value;}

	public Boolean toggle() {
		set(!get());
		return get();
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		toggler = new GuiToggleButton(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), getUnlocalizedName(), true) {
			@Override
			public Boolean get() {
				return SettingBoolean.this.get();
			}

			@Override
			public void set(Boolean value) {
				SettingBoolean.this.set(value);
				updateText();
			}
		};
		parts.add(toggler);
		callbacks.put(toggler, this);
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
		return get().toString();
	}

	@Override
	public void load(String save) {
		set(Boolean.valueOf(save));
	}

	@Override
	public void otherAction(Collection<Setting<?>> settings) {
		toggler.enabled = enabled();
	}
}
