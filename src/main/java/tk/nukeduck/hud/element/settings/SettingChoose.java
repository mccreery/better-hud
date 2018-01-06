package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;

public class SettingChoose extends SettingAlignable<String> {
	protected GuiButton last, next, backing;
	private final String[] modes;
	private int index = 0;

	public SettingChoose(String name, String... modes) {
		this(name, Direction.CENTER, modes);
	}

	public SettingChoose(String name, Direction alignment, String... modes) {
		super(name, alignment);
		this.modes = modes;
	}

	public void setIndex(int index) {
		if(index >= 0 && index < modes.length) {
			this.index = index;
		}
	}

	public int getIndex() {
		return index;
	}

	public int last() {
		if(index == 0) {
			index = modes.length;
		}
		return --index;
	}

	public int next() {
		++index;

		if(index == modes.length) {
			index = 0;
		}
		return index;
	}

	@Override
	public void set(String value) {
		setIndex(ArrayUtils.indexOf(modes, value));
	}

	@Override
	public String get() {
		return modes[index];
	}

	@Override public String save() {return get();}
	@Override public void load(String save) {set(save);}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		parts.add(backing = new GuiButton(2, bounds.x(), bounds.y(), bounds.width(), bounds.height(), ""));
		parts.add(last = new GuiButton(0, bounds.left(), bounds.y(), 20, bounds.height(), "<"));
		parts.add(next = new GuiButton(1, bounds.right() - 20, bounds.y(), 20, bounds.height(), ">"));
		backing.enabled = false;

		callbacks.put(last, this);
		callbacks.put(next, this);

		return bounds.bottom() + SPACER;
	}

	@Override
	public void draw() {
		String value = I18n.format("betterHud.setting." + get());
		Point center = new Point(backing.x + backing.width / 2, backing.y + backing.height / 2);

		HudElement.drawString(value, center, Direction.CENTER, Colors.WHITE);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		if(button.id == 0) last();
		else next();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void otherAction(Collection<Setting<?>> settings) {
		last.enabled = next.enabled = enabled();
	}
}
