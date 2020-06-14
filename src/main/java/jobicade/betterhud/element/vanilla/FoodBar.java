package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class FoodBar extends Bar {
	private SettingBoolean hideMount;

	public FoodBar() {
		super("food", new StatBarFood());

		settings.addChild(hideMount = new SettingBoolean("hideMount"));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		settings.priority.set(3);
		side.setIndex(1);
		hideMount.set(true);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& (!hideMount.get() || !Minecraft.getMinecraft().player.isRidingHorse())
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context, ElementType.FOOD));
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context, ElementType.FOOD));
		return rect;
	}
}
