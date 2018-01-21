package tk.nukeduck.hud.element.settings;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;

public class SettingPosition extends SettingStub<Object> {
	private final SettingChoose mode = new SettingChoose("position", "setPos", "absolute");

	private final SettingDirection direction;

	private final SettingAbsolutePosition offset = new SettingAbsolutePosition("origin") {
		@Override
		public boolean enabled() {
			return mode.getIndex() == 1 && super.enabled();
		}

		@Override
		public void pickMouse(Point mousePosition, Point resolution, HudElement element) {
			Bounds sourceBounds = GuiElementSettings.boundsCache.get(element);
			Direction.CENTER.align(sourceBounds, mousePosition);

			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				List<Bounds> targetBounds = new ArrayList<Bounds>();

				for(HudElement target : HudElement.ELEMENTS) {
					if(target == element || !target.isEnabled()) continue;
					Bounds bounds = GuiElementSettings.boundsCache.get(target);

					if(bounds != null && !bounds.isEmpty()) {
						targetBounds.add(bounds);
					}
				}

				//targetBounds.add(new Bounds(width, height).flip());
				sourceBounds.snap(targetBounds);
			}
			Direction anchor = SettingPosition.this.anchor.get();

			//Bounds desired = anchor.align(sourceBounds, sourceBounds.position);
			offset.set(anchor.getAnchor(sourceBounds).sub(anchor.getAnchor(resolution)));
		}
	};

	private final SettingDirection anchor = new SettingDirection("anchor") {
		@Override
		public boolean enabled() {
			return mode.getIndex() == 1 && super.enabled();
		}
	};

	public SettingPosition(String name) {
		this(name, Direction.ALL);
	}

	public SettingPosition(String name, Direction... options) {
		this(name, Direction.flags(options));
	}

	public SettingPosition(String name, int options) {
		super(name);

		add(new Legend("position"));
		add(mode);
		add(direction = new SettingDirection("direction", options) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 0 && super.enabled();
			}
		});
		add(offset);
		add(anchor);
	}

	public Direction getDirection() {
		return !isAbsolute() ? direction.get() : null;
	}

	public Point getOffset() {
		return isAbsolute() ? offset.get() : null;
	}

	public Direction getAnchor() {
		return (isAbsolute() ? anchor : direction).get();
	}

	public boolean isAbsolute() {
		return mode.getIndex() == 1;
	}

	/** Moves the given bounds to the correct location and returns them */
	public <T extends Bounds> T applyTo(T bounds, LayoutManager manager) {
		if(isAbsolute()) {
			Direction anchor = this.anchor.get();
			Point origin = anchor.getAnchor(manager.getResolution());

			bounds.position = origin.add(offset.get());
			return anchor.align(bounds);
		} else {
			return manager.position(direction.get(), bounds);
		}
	}

	public void set(Direction direction) {
		mode.setIndex(0);
		this.direction.set(direction);
		anchor.set(direction);

		offset.set(new Point(5, 5));
	}
}
