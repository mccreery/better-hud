package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class PortalOverlay extends OverlayElement {
	public PortalOverlay() {
		setName("portal");
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return getTimeInPortal(context.getPartialTicks()) > 0
			&& !OverlayHook.mimicPre(context, ElementType.PORTAL);
	}

	private float getTimeInPortal(float partialTicks) {
		return MathUtil.lerp(
			Minecraft.getMinecraft().player.prevTimeInPortal,
			Minecraft.getMinecraft().player.timeInPortal, partialTicks);
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		float timeInPortal = getTimeInPortal(context.getPartialTicks());

		if(timeInPortal < 1) {
			timeInPortal *= timeInPortal;
			timeInPortal *= timeInPortal;

			timeInPortal = timeInPortal * 0.8f + 0.2f;
		}

		Color.WHITE.withAlpha(Math.round(timeInPortal * 255)).apply();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		TextureAtlasSprite texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.PORTAL.getDefaultState());
		Rect screen = MANAGER.getScreen();
		Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(0, 0, texture, screen.getWidth(), screen.getHeight());

		OverlayHook.mimicPost(context, ElementType.PORTAL);
		return null;
	}
}
