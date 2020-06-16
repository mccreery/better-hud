package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class Experience extends OverlayElement {
	private SettingPosition position;
	private SettingBoolean hideMount;

	public Experience() {
		super("experience");

		settings.addChildren(
			position = new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH),
			hideMount = new SettingBoolean("hideMount")
		);
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		return Minecraft.getMinecraft().playerController.shouldDrawHUD()
			&& (!hideMount.get() || !Minecraft.getMinecraft().player.isRidingHorse())
			&& !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.EXPERIENCE));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.setPriority(1);
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect bgTexture = new Rect(0, 64, 182, 5);
		Rect fgTexture = new Rect(0, 69, 182, 5);

		Rect barRect = new Rect(bgTexture);

		if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
			barRect = MANAGER.position(Direction.SOUTH, barRect, false, 1);
		} else {
			barRect = position.applyTo(barRect);
		}
		GlUtil.drawTexturedProgressBar(barRect.getPosition(), bgTexture, fgTexture, Minecraft.getMinecraft().player.experience, Direction.EAST);

		if(Minecraft.getMinecraft().player.experienceLevel > 0) {
			String numberText = String.valueOf(Minecraft.getMinecraft().player.experienceLevel);
			Point numberPosition = new Rect(GlUtil.getStringSize(numberText))
				.anchor(barRect.grow(6), position.getContentAlignment().mirrorRow()).getPosition();

			GlUtil.drawBorderedString(numberText, numberPosition.getX(), numberPosition.getY(), new Color(128, 255, 32));
		}

		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.EXPERIENCE));
		return barRect;
	}
}
