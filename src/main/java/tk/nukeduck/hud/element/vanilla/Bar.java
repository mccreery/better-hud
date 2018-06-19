package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.bars.StatBar;

public abstract class Bar extends OverrideElement {
	protected final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.SOUTH.getFlag(), Direction.getFlags(Direction.WEST, Direction.EAST));

	protected final SettingChoose side = new SettingChoose("side", Direction.WEST.name, Direction.EAST.name) {
		@Override
		public boolean enabled() {
			return position.getDirection() == Direction.SOUTH;
		}
	};

	private StatBar<? super EntityPlayerSP> bar;

	public Bar(String name, StatBar<? super EntityPlayerSP> bar) {
		super(name);
		this.bar = bar;

		settings.add(position);
		settings.add(side);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(Event event) {
		bar.setHost(MC.player);
		return bar.shouldRender() && super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		Bounds bounds = new Bounds(bar.getSize());
		Direction alignment;

		if(position.getDirection() == Direction.SOUTH) {
			alignment = side.getIndex() == 1 ? Direction.EAST : Direction.WEST;
			bounds = MANAGER.positionBar(bounds, alignment, 1);
		} else {
			bounds = position.applyTo(bounds);
			alignment = position.getContentAlignment();
		}

		bar.render(bounds.getPosition(), alignment);
		return bounds;
	}
}
