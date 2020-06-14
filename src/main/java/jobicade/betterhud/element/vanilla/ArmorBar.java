package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class ArmorBar extends Bar {
	public ArmorBar() {
		super("armor", new StatBarArmor());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(0);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context, ElementType.ARMOR));
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context, ElementType.ARMOR));
		return rect;
	}
}
