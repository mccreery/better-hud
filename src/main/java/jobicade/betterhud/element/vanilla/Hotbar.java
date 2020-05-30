package jobicade.betterhud.element.vanilla;

import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class Hotbar extends OverrideElement {
	private SettingPosition position;

	public Hotbar() {
		super("hotbar");
		position.setEdge(true).setPostSpacer(2);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(position = new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HOTBAR;
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		// TODO wut?
		return !GuiIngameForge.renderHotbar;
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		Rect barTexture = new Rect(182, 22);
		Rect bounds = position.applyTo(new Rect(barTexture));

		Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
		GlUtil.drawRect(bounds, barTexture);

		Rect slot = bounds.grow(-3).withWidth(16);

		float partialTicks = context.getPartialTicks();
		for(int i = 0; i < 9; i++, slot = slot.translate(Direction.EAST.scale(20))) {
			if(i == Minecraft.getMinecraft().player.inventory.currentItem) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
				GlUtil.drawRect(slot.grow(4), new Rect(0, 22, 24, 24));
			}

			GlUtil.renderHotbarItem(slot, Minecraft.getMinecraft().player.inventory.mainInventory.get(i), partialTicks);
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		return bounds;
	}
}
