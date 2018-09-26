package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.WIDGETS;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;

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
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(WIDGETS);

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

		return bounds;
	}
}
