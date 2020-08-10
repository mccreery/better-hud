package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class Experience extends OverlayElement {
    private SettingPosition position;

    public Experience() {
        super("experience");

        position = new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH);
        settings.addChild(position);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderExperiance
            && !ForgeIngameGui.renderJumpBar
            && !OverlayHook.pre(context.getEvent(), ElementType.EXPERIENCE)
            && MC.playerController.gameIsSurvivalOrAdventure();
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bgTexture = new Rect(0, 64, 182, 5);
        Rect fgTexture = new Rect(0, 69, 182, 5);

        Rect barRect = new Rect(bgTexture);

        if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
            barRect = MANAGER.position(Direction.SOUTH, barRect, false, 1);
        } else {
            barRect = position.applyTo(barRect);
        }
        GlUtil.drawTexturedProgressBar(barRect.getPosition(), bgTexture, fgTexture, MC.player.experience, Direction.EAST);

        if(MC.player.experienceLevel > 0) {
            String numberText = String.valueOf(MC.player.experienceLevel);
            Point numberPosition = new Rect(GlUtil.getStringSize(numberText))
                .anchor(barRect.grow(6), position.getContentAlignment().mirrorRow()).getPosition();

            GlUtil.drawBorderedString(numberText, numberPosition.getX(), numberPosition.getY(), new Color(128, 255, 32));
        }

        OverlayHook.post(context.getEvent(), ElementType.EXPERIENCE);
        return barRect;
    }
}
