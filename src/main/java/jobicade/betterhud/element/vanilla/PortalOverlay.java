package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PortalOverlay extends OverlayElement {
    public PortalOverlay() {
        super("portal");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderPortal
            && !MC.player.isPotionActive(Effects.NAUSEA)
            && !OverlayHook.pre(context.getEvent(), ElementType.PORTAL)
            && getTimeInPortal(context.getPartialTicks()) > 0;
    }

    private float getTimeInPortal(float partialTicks) {
        return MathUtil.lerp(
            MC.player.prevTimeInPortal,
            MC.player.timeInPortal, partialTicks);
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
        MC.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

        TextureAtlasSprite texture = MC.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.NETHER_PORTAL.getDefaultState(), MC.world, BlockPos.ZERO);
        Rect screen = MANAGER.getScreen();
        AbstractGui.blit(0, 0, 0, screen.getWidth(), screen.getHeight(), texture);

        OverlayHook.post(context.getEvent(), ElementType.PORTAL);
        return null;
    }
}
