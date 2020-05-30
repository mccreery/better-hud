package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class ArmorBar extends Bar {
	public ArmorBar() {
		super(new StatBarArmor());

		setRegistryName("armor");
		setUnlocalizedName("armor");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(4);
		side.setIndex(0);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& OverlayHook.mimicPre(context, ElementType.ARMOR);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		OverlayHook.mimicPost(context, ElementType.ARMOR);
		return rect;
	}
}
