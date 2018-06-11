package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class PortalOverlay extends OverrideElement {
	public PortalOverlay() {
		super("portal");
	}

	@Override
	protected ElementType getType() {
		return ElementType.PORTAL;
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.player.prevTimeInPortal + (MC.player.timeInPortal - MC.player.prevTimeInPortal) * ((RenderGameOverlayEvent)event).getPartialTicks() > 0;
	}

	@Override
	protected Bounds render(Event event) {
		float ticks = MC.player.prevTimeInPortal + (MC.player.timeInPortal - MC.player.prevTimeInPortal) * ((RenderGameOverlayEvent)event).getPartialTicks();

		if(ticks < 1) {
			ticks *= ticks;
			ticks *= ticks;

			ticks = ticks * 0.8f + 0.2f;
		}

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, ticks);
		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		TextureAtlasSprite texture = MC.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PORTAL.getDefaultState());
		Point resolution = MANAGER.getResolution();
		MC.ingameGUI.drawTexturedModalRect(0, 0, texture, resolution.x, resolution.y);

		GlUtil.color(Colors.WHITE);
		return null;
	}
}