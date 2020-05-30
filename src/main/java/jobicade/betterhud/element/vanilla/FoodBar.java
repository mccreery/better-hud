package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class FoodBar extends Bar {
	private SettingBoolean hideMount;

	public FoodBar() {
		super(new StatBarFood());

		setRegistryName("food");
		setUnlocalizedName("food");

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
		return Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& (!hideMount.get() || !Minecraft.getMinecraft().player.isRidingHorse())
			&& OverlayHook.mimicPre(context, ElementType.FOOD);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect rect = super.render(context);
		OverlayHook.mimicPost(context, ElementType.FOOD);
		return rect;
	}
}
