package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import tk.nukeduck.hud.util.bars.StatBar;
import tk.nukeduck.hud.util.bars.StatBarFood;

public class FoodBar extends Bar {
	public FoodBar() {
		super("food");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(3);
		side.setIndex(1);
	}

	@Override
	protected ElementType getType() {
		return ElementType.FOOD;
	}

	@Override
	public StatBar getBar() {
		return new StatBarFood(MC.player);
	}
}
