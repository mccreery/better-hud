package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;

public class SettingPosition extends Setting {
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
				return mode.index == 0 && super.enabled();
			}
		});
		add(position = new SettingAbsolutePosition("absolute") {
			@Override
			public boolean enabled() {
				return mode.index == 1 && super.enabled();
			}
		});
		add(anchor = new SettingDirection("anchor") {
			@Override
			public boolean enabled() {
				return mode.index == 1 && super.enabled();
			}
		});
	}

	public Direction getDirection() {
		return !isAbsolute() ? direction.value : null;
	}

	public Point getPosition() {
		return isAbsolute() ? position.position : null;
	}

	public Direction getAnchor() {
		return (isAbsolute() ? anchor : direction).value;
	}

	public boolean isAbsolute() {
		return mode.index == 1;
	}

	/** Moves the given bounds to the correct location and returns them */
	public <T extends Bounds> T applyTo(T bounds, LayoutManager manager) {
		if(mode.index == 0) {
			return manager.position(direction.value, bounds);
		} else {
			bounds.position = new Point(position.position);
			return direction.value.align(bounds);
		}
	}

	public void load(Direction direction) {
		mode.index = 0;
		this.direction.value = direction;
		position.position.x = 5;
		position.position.y = 5;
		anchor.value = direction;
	}

	// Never called on parent, always on children
	@Override public String save() {return null;}
	@Override public void load(String save) {}
	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting> settings) {}
}
