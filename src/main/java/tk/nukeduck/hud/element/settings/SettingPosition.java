package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;

public class SettingPosition extends SettingStub<Object> {
	private final SettingChoose mode = new SettingChoose("position", "preset", "custom");

	private final SettingDirection direction;

	private final SettingAbsolutePosition offset = new SettingAbsolutePosition("origin") {
		@Override
		public boolean enabled() {
			return mode.getIndex() == 1 && super.enabled();
		}

		@Override
		public void pickMouse(Point mousePosition, Point resolution, HudElement element) {
			Bounds sourceBounds = new Bounds(element.getLastBounds());
			Direction.CENTER.align(sourceBounds, mousePosition);

			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				List<Bounds> targetBounds = new ArrayList<Bounds>();

				for(HudElement target : HudElement.ELEMENTS) {
					if(target == element || !target.isEnabled()) continue;
					Bounds bounds = target.getLastBounds();

					if(!bounds.isEmpty()) {
						targetBounds.add(bounds);
					}
				}

				//targetBounds.add(new Bounds(width, height).flip());
				sourceBounds = sourceBounds.snapped(targetBounds);
			}
			Direction anchor = SettingPosition.this.anchor.get();

			//Bounds desired = anchor.align(sourceBounds, sourceBounds.position);
			offset.set(anchor.getAnchor(sourceBounds).sub(anchor.getAnchor(resolution)));
		}

		@Override
		public Point getAbsolute() {
			return anchor.get().getAnchor(MANAGER.getResolution()).add(offset.get());
		}
	};

	private final SettingDirection anchor = new SettingDirection("anchor") {
		@Override
		public boolean enabled() {
			return mode.getIndex() == 1 && super.enabled();
		}
	};

	private boolean edge = false;
	private int postSpacer = SPACER;

	public SettingPosition(String name) {
		this(name, Direction.ALL);
	}

	public SettingPosition(String name, Direction... options) {
		this(name, Direction.getFlags(options));
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

	public Direction getAlignment() {
		return getAnchor();
	}

	public boolean isAbsolute() {
		return mode.getIndex() == 1;
	}

	public SettingPosition setEdge(boolean edge) {
		this.edge = edge;
		return this;
	}

	public SettingPosition setPostSpacer(int postSpacer) {
		this.postSpacer = postSpacer;
		return this;
	}

	/** Moves the given bounds to the correct location and returns them */
	public Bounds applyTo(Bounds bounds) {
		if(isAbsolute()) {
			bounds.position(anchor.get(), offset.get(), getAlignment());
			return bounds;
		} else {
			return MANAGER.position(direction.get(), bounds, edge, postSpacer);
		}
	}

	public void set(Direction direction) {
		mode.setIndex(0);
		this.direction.set(direction);
		anchor.set(direction);

		offset.set(new Point(5, 5));
	}
}
