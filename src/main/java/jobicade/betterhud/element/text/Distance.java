package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.RayTraceResult;

public class Distance extends TextElement {
    private SettingChoose mode;

    public Distance() {
        super("distance");

        settings.addChildren(
            new Legend("misc"),
            mode = new SettingChoose(3)
        );
    }

    @Override
    protected Rect moveRect(Rect bounds) {
        if(position.isDirection(Direction.CENTER)) {
            return bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).sub(SPACER, SPACER), Direction.SOUTH_EAST);
        } else {
            return super.moveRect(bounds);
        }
    }

    @Override
    protected Rect getPadding() {
        return Rect.createPadding(border ? 2 : 0);
    }

    @Override
    protected Rect render(OverlayContext context, List<String> text) {
        border = mode.getIndex() == 2;
        return super.render(context, text);
    }

    @Override
    protected List<String> getText() {
        RayTraceResult trace = MC.getRenderViewEntity().rayTrace(200, 1.0F);

        if(trace != null) {
            long distance = Math.round(Math.sqrt(trace.getBlockPos().distanceSqToCenter(MC.player.posX, MC.player.posY, MC.player.posZ)));

            if(mode.getIndex() == 2) {
                return Arrays.asList(String.valueOf(distance));
            } else {
                return Arrays.asList(I18n.format("betterHud.hud.distance." + mode.getIndex(), String.valueOf(distance)));
            }
        } else {
            return null;
        }
    }
}
