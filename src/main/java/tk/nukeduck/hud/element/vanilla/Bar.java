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
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.bars.StatBar;

public abstract class Bar extends OverrideElement {
	protected final SettingPosition position = new SettingPosition("position", Options.BAR, Options.WEST_EAST);

	protected final SettingChoose side = new SettingChoose("side", "west", "east") {
		@Override
		public boolean enabled() {
			return position.isDirection(Direction.SOUTH);
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
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(Event event) {
		bar.setHost(MC.player);
		return bar.shouldRender() && super.shouldRender(event);
	}

	/** @return {@link Direction#WEST} or {@link Direction#EAST} */
	protected Direction getSide() {
		if(position.isDirection(Direction.SOUTH)) {
			return side.getIndex() == 1 ? Direction.EAST : Direction.WEST;
		} else {
			return position.getContentAlignment();
		}
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		Bounds bounds = new Bounds(bar.getSize());
		Direction alignment = getSide();

		if(position.isDirection(Direction.SOUTH)) {
			bounds = MANAGER.positionBar(bounds, alignment, 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		bar.render(bounds.getPosition(), alignment);
		return bounds;
	}
}
