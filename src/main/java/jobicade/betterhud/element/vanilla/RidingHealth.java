package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarMount;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

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
			&& !OverlayHook.mimicPre(context, ElementType.HEALTHMOUNT);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		OverlayHook.mimicPost(context, ElementType.HEALTHMOUNT);
		return rect;
	}
}
