package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class SettingChoose extends Setting {
	protected GuiButton last, next, backing;
	protected GuiLabel label;
	protected final Direction alignment;
	public String[] modes;
	public int index = 0;

	public SettingChoose(String name, String... modes) {
		this(name, Direction.CENTER, modes);
	}

	public SettingChoose(String name, Direction alignment, String... modes) {
		super(name);
		this.modes = modes;
		this.alignment = alignment;
	}

	public int last() { // TODO find modulo solution maybe
		if(index == 0) {
			set(modes.length - 1);
		} else {
			set(index - 1);
		}
		return index;
	}
	public int next() {set((index + 1) % modes.length); return index;}

	public void set(String value) {
		set(ArrayUtils.indexOf(this.modes, value));
	}

	public void set(int index) {
		this.index = index;
		// TODO remove line pls
		label.addLine(I18n.format("betterHud.setting." + getValue()));
	}
	
	public String getValue() {
		return index < 0 || index >= modes.length ? String.valueOf(index) : this.modes[index];
	}
	
	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		Bounds bounds;

		if(alignment == Direction.CENTER) {
			bounds = new Bounds(width / 2 - 100, y, 200, 20);
		} else {
			bounds = alignment.anchor(new Bounds(0, 0, 150, 20), new Bounds(width / 2 - 100, y, 200, 20));
		}

		parts.add(last = new GuiButton(0, bounds.left(), bounds.y(), 20, bounds.height(), "<"));
		parts.add(next = new GuiButton(1, bounds.right() - 20, bounds.y(), 20, bounds.height(), ">"));
		parts.add(backing = new GuiButton(2, bounds.x(), bounds.y(), bounds.width(), bounds.height(), ""));
		backing.enabled = false;

		//parts.add(label = new GuiLabel(MC.fontRenderer, 0, width / 2 - 100, y, 200, 20, Colors.WHITE).setCentered());
		//label.addLine(I18n.format("betterHud.setting." + getValue())); TODO

		return alignment != Direction.WEST ? y + 20 : -1;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		if(button.id == 0) last();
		else next();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public String save() {
		return modes[index];
	}

	@Override
	public void load(String save) {
		index = ArrayUtils.indexOf(modes, save);
	}

	@Override
	public void otherAction(Collection<Setting> settings) {
		last.enabled = next.enabled = enabled();
	}
}
