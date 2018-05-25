package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.bars.StatBar;

public abstract class Bar extends OverrideElement {
	protected final SettingPosition position = new SettingPositionAligned("position", Direction.CORNERS | Direction.SOUTH.flag(), Direction.getFlags(Direction.WEST, Direction.EAST));

	protected final SettingChoose side = new SettingChoose("side", Direction.WEST.name, Direction.EAST.name) {
		@Override
		public boolean enabled() {
			return position.getDirection() == Direction.SOUTH;
		}
	};

	private StatBar bar;

	public Bar(String name) {
		super(name);

		settings.add(position);
		settings.add(side);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.SOUTH);
	}

	public abstract StatBar getBar();

	@Override
	public boolean shouldRender(Event event) {
		if(bar == null) bar = getBar();

		return super.shouldRender(event) && bar.shouldRender();
	}

	@Override
	protected Bounds render(Event event) {
		if(bar == null) bar = getBar();
		MC.getTextureManager().bindTexture(ICONS);

		Bounds bounds = new Bounds(bar.getSize());
		Direction alignment;

		if(position.getDirection() == Direction.SOUTH) {
			alignment = side.getIndex() == 1 ? Direction.EAST : Direction.WEST;
			bounds = MANAGER.positionBar(bounds, alignment, 1);
		} else {
			bounds = position.applyTo(bounds);
			alignment = position.getAlignment();
		}

		bar.render(bounds.position, alignment);
		return bounds;
	}
}
