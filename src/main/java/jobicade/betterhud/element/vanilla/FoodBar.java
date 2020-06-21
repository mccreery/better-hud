package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayContext;
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

		settings.addChild(hideMount = SettingBoolean.builder("hideMount").build());
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		settings.setPriority(3);
		side.setIndex(1);
		hideMount.set(true);
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		return super.shouldRender(context)
			&& Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& (!hideMount.get() || !Minecraft.getMinecraft().player.isRidingHorse())
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.FOOD));
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect rect = super.render(context);
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.FOOD));
		return rect;
	}
}
