package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
			public void pickMouse(Point mousePosition, Point resolution, HudElement element) {
				Bounds sourceBounds = Direction.CENTER.align(element.getLastBounds(), mousePosition);

				if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					List<Bounds> targetBounds = new ArrayList<Bounds>();

					for(HudElement target : HudElement.ELEMENTS) {
						if(target == element || !target.isEnabled()) continue;
						Bounds bounds = target.getLastBounds();

						if(!bounds.isEmpty()) {
							targetBounds.add(bounds);
						}
					}
					sourceBounds = sourceBounds.snapped(targetBounds);
				}
				offset.set(getAlignment().getAnchor(sourceBounds).sub(getAnchor().getAnchor(resolution)));
			}

			@Override
			public Point getAbsolute() {
				return anchor.get().getAnchor(MANAGER.getResolution()).add(offset.get());
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
			return bounds.position(anchor.get(), offset.get(), alignment.get());
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
		Point lockOffset = new Point(30 + SPACER, 148);

		lockAlignment.setBounds(Direction.EAST.align(new Bounds(20, 10), origin.add(lockOffset.withX(-lockOffset.getX()))));
		lockContent.setBounds(Direction.WEST.align(new Bounds(20, 10), origin.add(lockOffset)));

		return super.getGuiParts(parts, callbacks, origin);
	}
}
