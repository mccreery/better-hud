package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.ICONS;
import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.Direction.Options;
import jobicade.betterhud.util.bars.StatBar;

public abstract class Bar extends OverrideElement {
	protected SettingChoose side;

	private StatBar<? super EntityPlayerSP> bar;

	public Bar(String name, StatBar<? super EntityPlayerSP> bar) {
		super(name, new SettingPosition(Options.BAR, Options.CORNERS));
		this.bar = bar;
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(side = new SettingChoose("side", "west", "east") {
			@Override
			public boolean enabled() {
				return position.isDirection(Direction.SOUTH);
			}
		});
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(Event event) {
		bar.setHost(MC.player);
		return super.shouldRender(event) && bar.shouldRender();
	}

	/** @return {@link Direction#WEST} or {@link Direction#EAST} */
	protected Direction getContentAlignment() {
		if(position.isDirection(Direction.SOUTH)) {
			return side.getIndex() == 1 ? Direction.SOUTH_EAST : Direction.SOUTH_WEST;
		} else {
			return position.getContentAlignment();
		}
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);
		Direction contentAlignment = getContentAlignment();

		Bounds bounds = new Bounds(bar.getSize());

		if(position.isDirection(Direction.SOUTH)) {
			bounds = MANAGER.positionBar(bounds, contentAlignment.withRow(1), 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		bar.render(bounds.getPosition(), Direction.NORTH_WEST, contentAlignment);
		return bounds;
	}
}
