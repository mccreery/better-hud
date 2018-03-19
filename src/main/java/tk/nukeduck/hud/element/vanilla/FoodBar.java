package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import tk.nukeduck.hud.util.Bounds;

public class FoodBar extends Bar {
	public FoodBar() {
		super("food", new Bounds(16, 27, 9, 9), new Bounds(61, 27, 9, 9), new Bounds(52, 27, 9, 9));
	}

	@Override
	protected ElementType getType() {
		return ElementType.FOOD;
	}

	@Override
	protected int getCurrent() {
		return MC.player.getFoodStats().getFoodLevel();
	}

	@Override
	protected int getMaximum() {
		return 20;
	}
}
