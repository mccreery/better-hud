package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;

public class Experience extends OverrideElement {
    private SettingBoolean hideMount;

    public Experience() {
        super("experience", new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(hideMount = new SettingBoolean("hideMount"));
    }

    @Override
    protected ElementType getType() {
        return ElementType.EXPERIENCE;
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event)
            && Minecraft.getInstance().gameMode.canHurtPlayer()
            && (!hideMount.get() || !Minecraft.getInstance().player.isRidingJumpable());
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(1);
        position.setPreset(Direction.SOUTH);
    }

    @Override
    protected Rect render(Event event) {
        Rect bgTexture = new Rect(0, 64, 182, 5);
        Rect fgTexture = new Rect(0, 69, 182, 5);

        Rect barRect = new Rect(bgTexture);

        if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
            barRect = MANAGER.position(Direction.SOUTH, barRect, false, 1);
        } else {
            barRect = position.applyTo(barRect);
        }
        GlUtil.drawTexturedProgressBar(barRect.getPosition(), bgTexture, fgTexture, Minecraft.getInstance().player.experienceProgress, Direction.EAST);

        if(Minecraft.getInstance().player.experienceLevel > 0) {
            String numberText = String.valueOf(Minecraft.getInstance().player.experienceLevel);
            Point numberPosition = new Rect(GlUtil.getStringSize(numberText))
                .anchor(barRect.grow(6), position.getContentAlignment().mirrorRow()).getPosition();

            GlUtil.drawBorderedString(numberText, numberPosition.getX(), numberPosition.getY(), new Color(128, 255, 32));
        }
        return barRect;
    }
}
