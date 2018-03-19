package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.block.material.Material;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;

public class AirBar extends Bar {
	public AirBar() {
		super("airBar", null, new Bounds(25, 18, 9, 9), new Bounds(16, 18, 9, 9));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(1);
	}

	@Override
	protected int getCurrent() {
		int air = MC.player.getAir();

		int full = ((air - 2) * 10 + 299) / 300;
		int partial = (air * 10 + 299) / 300 - full;
		return full * 2 + partial;
	}

	@Override
	protected int getMaximum() {
		return 20;
	}

	@Override
	protected ElementType getType() {
		return ElementType.AIR;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.player.isInsideOfMaterial(Material.WATER);
	}
}
