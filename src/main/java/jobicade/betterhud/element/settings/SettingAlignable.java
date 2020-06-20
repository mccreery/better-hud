package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;

public abstract class SettingAlignable extends Setting {
	// TODO why protected?
	protected final Direction alignment;

	protected SettingAlignable(Builder<?, ?> builder) {
		super(builder);
		this.alignment = builder.alignment;
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
		origin = super.getGuiParts(parts, callbacks, origin);

		Rect bounds = new Rect(getSize());
		bounds = bounds.anchor(new Rect(getAlignmentWidth(), bounds.getHeight()).align(origin, Direction.NORTH), alignment);

		getGuiParts(parts, callbacks, bounds);
		return shouldBreak() ? origin.withY(bounds.getBottom() + SPACER) : origin;
	}

	protected int getAlignmentWidth() {
		return 300;
	}

	protected Point getSize() {
		return new Point(alignment == Direction.CENTER ? 200 : 150, 20);
	}

	protected boolean shouldBreak() {
		return alignment != Direction.WEST;
	}

	/** @see Setting#getGuiParts(List, Map, Point) */
	public abstract void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds);

	protected static abstract class Builder<T extends SettingAlignable, U extends Builder<T, U>> extends Setting.Builder<T, U> {
		protected Builder(String name) {
			super(name);
		}

		protected Direction alignment;
		public U setAlignment(Direction alignment) {
			this.alignment = alignment;
			return getThis();
		}
	}
}
