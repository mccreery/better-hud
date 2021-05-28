package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import static jobicade.betterhud.BetterHud.MANAGER;

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
        return super.shouldRender(event) && getTimeInPortal(event) > 0;
    }

    private float getTimeInPortal(Event event) {
        return Minecraft.getInstance().player.oPortalTime + (Minecraft.getInstance().player.portalTime - Minecraft.getInstance().player.oPortalTime) * getPartialTicks(event);
    }

    @Override
    protected Rect render(Event event) {
        float timeInPortal = getTimeInPortal(event);

        if(timeInPortal < 1) {
            timeInPortal *= timeInPortal;
            timeInPortal *= timeInPortal;

            timeInPortal = timeInPortal * 0.8f + 0.2f;
        }

        Color.WHITE.withAlpha(Math.round(timeInPortal * 255)).apply();
        Minecraft.getInstance().getTextureManager().bind(TextureMap.LOCATION_BLOCKS);

        TextureAtlasSprite texture = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        Rect screen = MANAGER.getScreen();
        Minecraft.getInstance().gui.func_175175_a(0, 0, texture, screen.getWidth(), screen.getHeight());

        return null;
    }
}
