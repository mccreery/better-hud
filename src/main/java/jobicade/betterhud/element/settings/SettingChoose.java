package jobicade.betterhud.element.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class SettingChoose extends SettingAlignable<String, SettingChoose> {
	protected GuiButton last, next, backing;
	protected final String[] modes;

	private int index = 0;
	private int length;

	public SettingChoose(int length) {
		this("mode", length);
	}

	public SettingChoose(String name, int length) {
		this(name);
		this.length = length;
	}

	public SettingChoose(String name, String... modes) {
		this(name, Direction.CENTER, modes);
	}

	public SettingChoose(String name, Direction alignment, String... modes) {
		super(name, alignment);

		this.modes = modes;
		this.length = modes.length;

		if (length == 0) {
			throw new IllegalArgumentException("modes cannot be empty");
		}
	}

	@Override
	protected SettingChoose getThis() {
		return this;
	}

	public String get() {
		return modes[index];
	}

	public void set(String mode) {
		setIndex(ArrayUtils.indexOf(modes, mode));
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if(index >= 0 && index < length) {
			this.index = index;
		} else {
			throw new IndexOutOfBoundsException("mode: " + index + ", max: " + (length - 1));
		}
	}

	public void last() {
		int index = getIndex();

		if(index == 0) {
			index = length;
		}
		setIndex(--index);
	}

	public void next() {
		int index = getIndex() + 1;

		if(index == length) {
			index = 0;
		}
		setIndex(index);
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public String getStringValue() {
		return get();
	}

	@Override
	public String getDefaultValue() {
		return modes[0];
	}

	@Override
	public void loadStringValue(String save) {
		set(save);
	}

	@Override
	public void loadDefaultValue() {
		index = 0;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?, ?>> callbacks, Rect bounds) {
		parts.add(backing = new GuiButton(2, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), ""));
		parts.add(last = new GuiButton(0, bounds.getLeft(), bounds.getY(), 20, bounds.getHeight(), "<"));
		parts.add(next = new GuiButton(1, bounds.getRight() - 20, bounds.getY(), 20, bounds.getHeight(), ">"));
		backing.enabled = false;

		callbacks.put(last, this);
		callbacks.put(next, this);
	}

	protected String getUnlocalizedValue() {
		return "betterHud.value." + modes[getIndex()];
	}

	protected String getLocalizedValue() {
		int index = getIndex();

		if(index >= 0 && index < modes.length) {
			return I18n.format(getUnlocalizedValue());
		} else {
			return I18n.format("betterHud.value.mode", index);
		}
	}

	@Override
	public void draw() {
		Point center = new Point(backing.x + backing.width / 2, backing.y + backing.height / 2);
		GlUtil.drawString(getLocalizedValue(), center, Direction.CENTER, Color.WHITE);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		if(button.id == 0) last();
		else next();
	}

	@Override
	public void updateGuiParts(Collection<Setting<?, ?>> settings) {
		last.enabled = next.enabled = enabled();
	}
}
