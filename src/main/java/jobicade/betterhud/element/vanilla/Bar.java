package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class Bar extends OverlayElement {
	protected SettingPosition position;
	protected SettingChoose side;

	private StatBar<? super EntityPlayerSP> bar;

	public Bar(StatBar<? super EntityPlayerSP> bar) {
		this.bar = bar;

		settings.addChildren(
			position = new SettingPosition(DirectionOptions.BAR, DirectionOptions.CORNERS),
			side = new SettingChoose("side", "west", "east").setEnableOn(() -> position.isDirection(Direction.SOUTH))
		);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		bar.setHost(Minecraft.getMinecraft().player);
		return bar.shouldRender();
	}

	/** @return {@link Direction#WEST} or {@link Direction#EAST} */
	protected Direction getContentAlignment() {
		if(position.isDirection(Direction.SOUTH)) {
			return side.getIndex() == 1 ? Direction.SOUTH_EAST : Direction.SOUTH_WEST;
		} else {
			return position.getContentAlignment();
		}
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		Direction contentAlignment = getContentAlignment();

		Rect bounds = new Rect(bar.getPreferredSize());

		if(position.isDirection(Direction.SOUTH)) {
			bounds = MANAGER.positionBar(bounds, contentAlignment.withRow(1), 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		bar.setContentAlignment(contentAlignment).setBounds(bounds).render();
		return bounds;
	}
}
