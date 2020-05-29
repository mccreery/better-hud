package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

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
	protected ElementType getType() {
		return ElementType.ARMOR;
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD();
	}
}
