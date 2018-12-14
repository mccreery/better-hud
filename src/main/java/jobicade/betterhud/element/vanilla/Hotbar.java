package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.WIDGETS;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.Direction.Options;
import jobicade.betterhud.util.mode.GlMode;
import jobicade.betterhud.util.mode.TextureMode;
import jobicade.betterhud.util.GlUtil;

public class Hotbar extends OverrideElement {
	public Hotbar() {
		super("hotbar", new SettingPosition(Options.TOP_BOTTOM, Options.NONE));
		position.setEdge(true).setPostSpacer(2);
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
	public boolean shouldRender(Event event) {
		return !GuiIngameForge.renderHotbar && super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		GlMode.push(new TextureMode(WIDGETS));
		Bounds barTexture = new Bounds(182, 22);
		Bounds bounds = position.applyTo(new Bounds(barTexture));

		GlUtil.drawTexturedModalRect(bounds.getPosition(), barTexture);

		Bounds slot = bounds.grow(-3).withWidth(16);

		float partialTicks = getPartialTicks(event);
		for(int i = 0; i < 9; i++, slot = slot.shift(Direction.EAST, 20)) {
			GlUtil.renderHotbarItem(slot, MC.player.inventory.mainInventory.get(i), partialTicks);

			if(i == MC.player.inventory.currentItem) {
				MC.getTextureManager().bindTexture(WIDGETS);
				GlUtil.drawTexturedModalRect(slot.grow(4), new Bounds(0, 22, 24, 24));
			}
		}

		GlMode.pop();
		return bounds;
	}
}
