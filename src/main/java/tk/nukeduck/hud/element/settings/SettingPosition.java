package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.Gui;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.Point;

public class SettingPosition extends SettingStub<Object> {
	private boolean edge = false;
	private int postSpacer = SPACER;

	private final SettingChoose mode;

	private final SettingDirection direction;
	private final SettingAbsolutePosition offset;

	private final SettingElement parent;
	private final SettingDirection anchor, alignment, contentAlignment;
	private final SettingLock lockAlignment, lockContent;

	public SettingPosition(String name) {
		this(name, Options.ALL, Options.ALL);
	}

	public SettingPosition(String name, Options directionOptions, Options contentOptions) {
		super(name);

		add(new Legend("position"));
		add(mode = new SettingChoose("position", "preset", "custom"));

		add(direction = new SettingDirection("direction", Direction.WEST, directionOptions) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 0 && super.enabled();
			}
		}.setHorizontal());

		add(parent = new SettingElement("parent", Direction.CENTER) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});

		add(anchor = new SettingDirection("anchor", Direction.WEST) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});
		add(alignment = new SettingDirection("alignment", Direction.CENTER) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && !lockAlignment.get() && super.enabled();
			}

			@Override
			public void updateGuiParts(Collection<Setting<?>> settings) {
				if(lockAlignment.get()) set(anchor.get());
				super.updateGuiParts(settings);
			}
		});
		add(contentAlignment = new SettingDirection("contentAlignment", Direction.EAST, contentOptions) {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && !lockContent.get() && super.enabled();
			}

			@Override
			public void updateGuiParts(Collection<Setting<?>> settings) {
				if(lockContent.get()) set(SettingPosition.this.alignment.get());
				super.updateGuiParts(settings);
			}
		});

		add(lockAlignment = new SettingLock("lockAlignment") {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});
		add(lockContent = new SettingLock("lockContent") {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}
		});

		add(offset = new SettingAbsolutePosition("origin") {
			@Override
			public boolean enabled() {
				return mode.getIndex() == 1 && super.enabled();
			}

			@Override
			public void pickMouse(Point mousePosition, HudElement element) {
				Bounds sourceBounds = element.getLastBounds().align(mousePosition, Direction.CENTER);

				if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					sourceBounds = snap(element, sourceBounds);
				}
				offset.set(sourceBounds.getAnchor(getAlignment()).sub(MANAGER.getScreen().getAnchor(getAnchor())));
			}

			@Override
			public Point getAbsolute() {
				return MANAGER.getScreen().getAnchor(anchor.get()).add(offset.get());
			}
		});
	}

	public boolean isDirection(Direction direction) {
		return !isCustom() && this.direction.get() == direction;
	}

	public boolean isCustom() {
		return mode.getIndex() == 1;
	}

	public Direction getDirection() {
		if(isCustom()) throw new IllegalStateException("Position is not preset");
		return direction.get();
	}

	public Point getOffset() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return offset.get();
	}

	public Direction getAnchor() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return anchor.get();
	}

	public Direction getAlignment() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return alignment.get();
	}

	public Direction getContentAlignment() {
		return isCustom() ? contentAlignment.get() : contentAlignment.getOptions().apply(direction.get());
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
		if(isCustom()) {
			Bounds parent = this.parent.get() != null ? this.parent.get().getLastBounds() : MANAGER.getScreen();
			return bounds.align(parent.getAnchor(anchor.get()).add(offset.get()), alignment.get());
		} else {
			return MANAGER.position(direction.get(), bounds, edge, postSpacer);
		}
	}

	public void setPreset(Direction direction) {
		mode.setIndex(0);
		this.direction.set(direction);

		// Reset custom
		offset.set(Point.ZERO);
		anchor.set(Direction.NORTH_WEST);
		alignment.set(Direction.NORTH_WEST);
		contentAlignment.set(Direction.NORTH_WEST);

		lockAlignment.set(true);
		lockContent.set(true);
	}

	public void setCustom(Direction anchor, Direction alignment, Direction contentAlignment, Point offset, boolean lockAlignment, boolean lockContent) {
		// Reset preset
		mode.setIndex(1);
		direction.set(Direction.NORTH_WEST);

		this.anchor.set(anchor);
		this.alignment.set(alignment);
		this.contentAlignment.set(contentAlignment);
		this.offset.set(offset);

		this.lockAlignment.set(lockAlignment);
		this.lockContent.set(lockContent);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
		Point lockOffset = new Point(30 + SPACER, 173);

		lockAlignment.setBounds(new Bounds(20, 10).align(origin.add(lockOffset.withX(-lockOffset.getX())), Direction.EAST));
		lockContent.setBounds(new Bounds(20, 10).align(origin.add(lockOffset), Direction.WEST));

		return super.getGuiParts(parts, callbacks, origin);
	}

	private static final int SNAP_RADIUS = 10;

	/** Aligns this bounds to the closest edge in {@code bounds}
	 * if any is less than {@link #SNAP_RADIUS} away */
	private static Bounds snap(HudElement element, Bounds source) {
		int snapX = source.getX(), snapY = source.getY(),
			snapRadiusX = SNAP_RADIUS, snapRadiusY = SNAP_RADIUS;

		for(Entry<HudElement, Bounds> entry : HudElement.getActiveBounds().entrySet()) {
			if(entry.getKey() == element) continue;

			Bounds bounds = entry.getValue();
			Bounds outer = bounds.grow(SPACER);
			int testRadius;

			// Snap outside Y
			if(linesOverlap(source.getLeft(), source.getRight(), outer.getLeft(), outer.getRight())) {
				testRadius = Math.abs(source.getBottom() - bounds.getY());

				if(testRadius < snapRadiusY) {
					snapY = outer.getY() - source.getHeight();
					snapRadiusY = testRadius;
				} else {
					testRadius = Math.abs(source.getY() - bounds.getBottom());

					if(testRadius < snapRadiusY) {
						snapY = outer.getBottom();
						snapRadiusY = testRadius;
					}
				}
			}

			// Snap inside Y
			testRadius = Math.abs(source.getBottom() - bounds.getBottom());

			if(testRadius < snapRadiusY) {
				snapY = bounds.getBottom() - source.getHeight();
				snapRadiusY = testRadius;
			} else {
				testRadius = Math.abs(source.getY() - bounds.getY());

				if(testRadius < snapRadiusY) {
					snapY = bounds.getY();
					snapRadiusY = testRadius;
				}
			}

			// Snap outside X
			if(linesOverlap(source.getTop(), source.getBottom(), outer.getTop(), outer.getBottom())) {
				testRadius = Math.abs(source.getRight() - outer.getX());

				if(testRadius < snapRadiusX) {
					snapX = outer.getX() - source.getWidth();
					snapRadiusX = testRadius;
				} else {
					testRadius = Math.abs(source.getX() - outer.getRight());

					if(testRadius < snapRadiusX) {
						snapX = outer.getRight();
						snapRadiusX = testRadius;
					}
				}
			}

			// Snap inside X
			testRadius = Math.abs(source.getRight() - bounds.getRight());

			if(testRadius < snapRadiusX) {
				snapX = bounds.getRight() - source.getWidth();
				snapRadiusX = testRadius;
			} else {
				testRadius = Math.abs(source.getX() - bounds.getX());

				if(testRadius < snapRadiusX) {
					snapX = bounds.getX();
					snapRadiusX = testRadius;
				}
			}
		}

		return snapX != source.getX() || snapY != source.getY() ?
			source.withPosition(snapX, snapY) : source;
	}

	private static boolean linesOverlap(int minX, int maxX, int minY, int maxY) {
		return minX < maxY && minY < maxX;
	}
}
