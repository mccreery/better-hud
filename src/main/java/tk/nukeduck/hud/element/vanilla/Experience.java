package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.List;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Experience extends OverrideElement {
	private SettingBoolean hideMount;

	public Experience() {
		super("experience", new SettingPosition(Options.BAR, Options.NORTH_SOUTH));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(hideMount = new SettingBoolean("hideMount"));
	}

	@Override
	protected ElementType getType() {
		return ElementType.EXPERIENCE;
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event)
			&& MC.playerController.shouldDrawHUD()
			&& (!hideMount.get() || !MC.player.isRidingHorse());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(1);
	}

	@Override
	protected Bounds render(Event event) {
		Bounds bgTexture = new Bounds(0, 64, 182, 5);
		Bounds fgTexture = new Bounds(0, 69, 182, 5);

		Bounds barBounds = new Bounds(bgTexture);

		if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
			barBounds = MANAGER.position(Direction.SOUTH, barBounds, false, 1);
		} else {
			barBounds = position.applyTo(barBounds);
		}
		GlUtil.drawTexturedProgressBar(barBounds.getPosition(), bgTexture, fgTexture, MC.player.experience, Direction.EAST);

		if(MC.player.experienceLevel > 0) {
			String numberText = String.valueOf(MC.player.experienceLevel);
			Point numberPosition = new Bounds(GlUtil.getStringSize(numberText))
				.anchor(barBounds.grow(6), position.getContentAlignment().mirrorRow()).getPosition();

			GlUtil.drawBorderedString(numberText, numberPosition.getX(), numberPosition.getY(), Colors.fromRGB(128, 255, 32));
		}
		return barBounds;
	}
}
