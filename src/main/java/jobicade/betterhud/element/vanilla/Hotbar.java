package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class Hotbar extends OverlayElement {
	private SettingPosition position;

	public Hotbar() {
		setRegistryName("hotbar");
		setUnlocalizedName("hotbar");
		position.setEdge(true).setPostSpacer(2);

		settings.addChild(position = new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		// TODO make it work correctly with spectator mode
		return !Minecraft.getMinecraft().player.isSpectator()
			&& OverlayHook.mimicPre(context, ElementType.HOTBAR);
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
		OverlayHook.mimicPost(context, ElementType.HOTBAR);
		return bounds;
	}
}
