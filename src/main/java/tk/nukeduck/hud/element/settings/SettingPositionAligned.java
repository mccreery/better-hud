package tk.nukeduck.hud.element.settings;

import tk.nukeduck.hud.util.Direction;

public class SettingPositionAligned extends SettingPosition {
	private final SettingDirection alignment;

	public SettingPositionAligned(String name) {
		this(name, Direction.ALL, Direction.ALL);
	}

	public SettingPositionAligned(String name, Direction[] directions, Direction[] alignments) {
		this(name, Direction.flags(directions), Direction.flags(alignments));
	}

	public SettingPositionAligned(String name, int anchors, int alignments) {
		super(name, anchors);

		add(alignment = new SettingDirection("alignment") {
			@Override
			public boolean enabled() {
				return isAbsolute();
			}
		});
	}

	@Override
	public Direction getAlignment() {
		return isAbsolute() ? alignment.get() : super.getAlignment();
	}
}
