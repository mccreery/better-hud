package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarMount;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class RidingHealth extends Bar {
	public RidingHealth() {
		super("mountHealth", new StatBarMount());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(5);
		side.setIndex(1);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context, ElementType.HEALTHMOUNT));
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context, ElementType.HEALTHMOUNT));
		return rect;
	}
}
