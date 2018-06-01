package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.bars.StatBar;
import tk.nukeduck.hud.util.bars.StatBarAir;

public class AirBar extends Bar {
	public AirBar() {
		super("airBar");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(1);
	}

	@Override
	protected ElementType getType() {
		return ElementType.AIR;
	}

	@Override
	public StatBar getBar() {
		return new StatBarAir(MC.player);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.shouldDrawHUD();
	}
}
