package tk.nukeduck.hud.element.settings;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;

public class SettingPosition extends SettingStub<Object> {
	private static final String[] MODES = {"setPos", "absolute"};

	public final SettingChoose mode;
	private final SettingDirection direction;
	private final SettingAbsolutePosition position;
	private final SettingDirection anchor;

	public SettingPosition(String name) {
		this(name, Direction.ALL);
	}

	public SettingPosition(String name, Direction... options) {
		this(name, Direction.flags(options));
	}

	public SettingPosition(String name, int options) {
		super(name);
		add(new Legend("position"));

		add(mode = new SettingChoose("position", MODES));
		add(direction = new SettingDirection("direction", options) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 0 && super.enabled();
			}
		});
		add(position = new SettingAbsolutePosition("absolute") {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});
		add(anchor = new SettingDirection("anchor") {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});
	}

	public Direction getDirection() {
		return !isAbsolute() ? direction.get() : null;
	}

	public Point getPosition() {
		return isAbsolute() ? position.get() : null;
	}

	public Direction getAnchor() {
		return (isAbsolute() ? anchor : direction).get();
	}

	public boolean isAbsolute() {
		return mode.getIndex() == 1;
	}

	/** Moves the given bounds to the correct location and returns them */
	public <T extends Bounds> T applyTo(T bounds, LayoutManager manager) {
		if(mode.getIndex() == 0) {
			return manager.position(direction.get(), bounds);
		} else {
			bounds.position = new Point(position.get());
			return direction.get().align(bounds);
		}
	}

	public void set(Direction direction) {
		mode.setIndex(0);
		this.direction.set(direction);
		position.set(new Point(5, 5));
		anchor.set(direction);
	}
}
