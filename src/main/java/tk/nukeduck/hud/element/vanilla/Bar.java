package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public abstract class Bar extends OverrideElement {
	protected final SettingPosition position = new SettingPositionAligned("position", Direction.CORNERS | Direction.SOUTH.flag(), Direction.getFlags(Direction.WEST, Direction.EAST));

	protected final SettingChoose side = new SettingChoose("side", Direction.WEST.name, Direction.EAST.name) {
		@Override
		public boolean enabled() {
			return position.getDirection() == Direction.SOUTH;
		}
	};

	private final Bounds bgTexture, halfTexture, fullTexture;

	public Bar(String name, Bounds bgTexture, Bounds halfTexture, Bounds fullTexture) {
		super(name);

		this.bgTexture = bgTexture;
		this.halfTexture = halfTexture;
		this.fullTexture = fullTexture;

		settings.add(position);
		settings.add(side);
	}

	protected abstract int getCurrent();
	protected abstract int getMaximum();

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.SOUTH);
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		Bounds bounds = new Bounds(GlUtil.getBarSize(getMaximum()));
		if(position.getDirection() == Direction.SOUTH) {
			bounds = MANAGER.positionBar(bounds, side.getIndex() == 1 ? Direction.EAST : Direction.WEST, 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		GlUtil.enableBlendTranslucent();
		GlUtil.renderBar(getCurrent(), getMaximum(), bounds.position, bgTexture, halfTexture, fullTexture);
		return bounds;
	}
}
