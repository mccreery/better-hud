package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;

public class Offhand extends OverlayElement {
    private SettingPosition position;

    public Offhand() {
        super("offhand");

        position = new SettingPosition(this, "position");
        position.setDirectionOptions(DirectionOptions.BAR);
        position.setContentOptions(DirectionOptions.NONE);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return !MC.player.getHeldItemOffhand().isEmpty()
            && !MC.playerController.isSpectatorMode();
    }

    @Override
    public Rect render(OverlayContext context) {
        ItemStack offhandStack = MC.player.getHeldItemOffhand();
        HandSide offhandSide = MC.player.getPrimaryHand().opposite();
        Direction offhand = offhandSide == HandSide.RIGHT ? Direction.EAST : Direction.WEST;

        Rect bounds = new Rect(22, 22);
        Rect texture = new Rect(24, 23, 22, 22);

        if(position.isDirection(Direction.SOUTH)) {
            bounds = bounds.align(OverlayElements.HOTBAR.getLastBounds().grow(SPACER).getAnchor(offhand), offhand.mirrorCol());
        } else {
            bounds = position.applyTo(bounds);
        }

        MC.getTextureManager().bindTexture(Textures.WIDGETS);
        GlUtil.drawRect(bounds, texture);

        GlUtil.renderHotbarItem(bounds.translate(3, 3), offhandStack, context.getPartialTicks());
        return bounds;
    }
}
