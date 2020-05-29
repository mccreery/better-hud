package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.util.bars.StatBarAir;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class AirBar extends Bar {
	public AirBar() {
		super("airBar", new StatBarAir());
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
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD();
	}
}
