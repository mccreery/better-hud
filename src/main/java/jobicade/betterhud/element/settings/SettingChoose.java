package jobicade.betterhud.element.settings;

import java.util.Arrays;
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

public class SettingChoose extends SettingAlignable implements IStringSetting {
	protected GuiButton last, next, backing;
	protected final String[] modes;

	private int index = 0;

	private final String valuePrefix;

	protected SettingChoose(Builder builder) {
		super(builder);
		modes = builder.modes;
		valuePrefix = builder.valuePrefix;
	}

	public String get() {
		return modes[index];
	}

	public void set(String mode) {
		try {
			setIndex(ArrayUtils.indexOf(modes, mode));
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid mode " + mode + ". Valid modes are " + Arrays.toString(modes));
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		if(index >= 0 && index < modes.length) {
			this.index = index;
		} else {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + modes.length);
		}
	}

	public void setPrev() {
		index = Math.floorMod(index - 1, modes.length);
	}

	public void setNext() {
		index = Math.floorMod(index + 1, modes.length);
	}

	@Override
	public IStringSetting getStringSetting() {
		return this;
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
	public void loadStringValue(String save) throws SettingValueException {
		try {
			set(save);
		} catch (IllegalArgumentException e) {
			throw new SettingValueException(e);
		}
	}

	@Override
	public void loadDefaultValue() {
		index = 0;
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		parts.add(backing = new GuiButton(2, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), ""));
		parts.add(last = new GuiButton(0, bounds.getLeft(), bounds.getY(), 20, bounds.getHeight(), "<"));
		parts.add(next = new GuiButton(1, bounds.getRight() - 20, bounds.getY(), 20, bounds.getHeight(), ">"));
		backing.enabled = false;

		callbacks.put(last, this);
		callbacks.put(next, this);
	}

	protected String getUnlocalizedValue() {
		return valuePrefix + modes[getIndex()];
	}

	protected String getLocalizedValue() {
		return I18n.format(getUnlocalizedValue());
	}

	@Override
	public void draw() {
		Point center = new Point(backing.x + backing.width / 2, backing.y + backing.height / 2);
		GlUtil.drawString(getLocalizedValue(), center, Direction.CENTER, Color.WHITE);
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		if (button.id == 0) {
			setPrev();
		} else {
			setNext();
		}
	}

	@Override
	public void updateGuiParts(Collection<Setting> settings) {
		last.enabled = next.enabled = enabled();
	}

	public static Builder builder(String name, String... modes) {
		return new Builder(name, modes);
	}

	public static final class Builder extends SettingAlignable.Builder<SettingChoose, Builder> {
		private final String[] modes;

		protected Builder(String name, String[] modes) {
			super(name);

			if (modes.length == 0) {
				throw new IllegalArgumentException("modes cannot be empty");
			}
			this.modes = modes;
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public SettingChoose build() {
			return new SettingChoose(this);
		}

		private String valuePrefix = "betterHud.value.";
		public Builder setValuePrefix(String valuePrefix) {
			this.valuePrefix = valuePrefix;
			return this;
		}
	}
}
