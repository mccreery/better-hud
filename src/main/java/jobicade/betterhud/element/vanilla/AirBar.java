package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarAir;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class AirBar extends Bar {
	public AirBar() {
		super(new StatBarAir());

		setRegistryName("air_bar");
		setUnlocalizedName("airBar");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(1);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& !OverlayHook.mimicPre(context, ElementType.AIR);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		OverlayHook.mimicPost(context, ElementType.AIR);
		return rect;
	}
}
