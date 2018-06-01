package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.bars.StatBar;
import tk.nukeduck.hud.util.bars.StatBarHealth;

public class HealthBar extends Bar {
	private final SettingBoolean hideMount = new SettingBoolean("hideMount");

	public HealthBar() {
		super("health");
		settings.add(hideMount);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(3);
		side.setIndex(0);
		hideMount.set(true);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTH;
	}

	@Override
	public StatBar getBar() {
		return new StatBarHealth();
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.shouldDrawHUD()
			&& (!hideMount.get() || !MC.player.isRidingHorse());
	}
}
