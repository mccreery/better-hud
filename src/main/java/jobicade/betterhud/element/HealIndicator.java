package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.SPACER;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HealIndicator extends OverlayElement {
	private SettingPosition position;
	private SettingChoose mode;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_WEST);
		mode.setIndex(1);
	}

	public HealIndicator() {
		setRegistryName("heal_indicator");
		setUnlocalizedName("healIndicator");

		settings.addChildren(
			position = new SettingPosition(DirectionOptions.NONE, DirectionOptions.NONE),
			new Legend("misc"),
			mode = new SettingChoose(2)
		);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
			String healIndicator = I18n.format("betterHud.hud.healIndicator");
			Rect bounds = mode.getIndex() == 0 ? new Rect(Minecraft.getMinecraft().fontRenderer.getStringWidth(healIndicator), Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) : new Rect(9, 9);

			if(position.isCustom()) {
				bounds = position.applyTo(bounds);
			} else {
				Direction side = OverlayElements.HEALTH.getIndicatorSide();
				bounds = bounds.align(OverlayElements.HEALTH.getLastBounds().grow(SPACER, 0, SPACER, 0).getAnchor(side), side.mirrorCol());
			}

			if(mode.getIndex() == 0) {
				GlUtil.drawString(healIndicator, bounds.getPosition(), Direction.NORTH_WEST, Color.GREEN);
			} else {
				Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.HUD_ICONS);
				Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(bounds.getX(), bounds.getY(), 0, 80, 9, 9);
			}
			return bounds;
	}

	/** @see net.minecraft.util.FoodStats#onUpdate(net.minecraft.entity.player.EntityPlayer) */
	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.gameIsSurvivalOrAdventure()
			&& Minecraft.getMinecraft().world.getGameRules().getBoolean("naturalRegeneration")
			&& Minecraft.getMinecraft().player.getFoodStats().getFoodLevel() >= 18
			&& Minecraft.getMinecraft().player.shouldHeal();
	}
}
