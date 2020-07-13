package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class PortalOverlay extends OverlayElement {
    public PortalOverlay() {
        super("portal");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return GuiIngameForge.renderPortal
            && !Minecraft.getMinecraft().player.isPotionActive(MobEffects.NAUSEA)
            && !OverlayHook.pre(context.getEvent(), ElementType.PORTAL)
            && getTimeInPortal(context.getPartialTicks()) > 0;
    }

    private float getTimeInPortal(float partialTicks) {
        return MathUtil.lerp(
            Minecraft.getMinecraft().player.prevTimeInPortal,
            Minecraft.getMinecraft().player.timeInPortal, partialTicks);
    }

    @Override
    public Rect render(OverlayContext context) {
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

        OverlayHook.post(context.getEvent(), ElementType.PORTAL);
        return null;
    }
}
