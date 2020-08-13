package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class JumpBar extends OverlayElement {
    private SettingPosition position;

    public JumpBar() {
        super("jumpBar");

        position = new SettingPosition(this, "position");
        position.setDirectionOptions(DirectionOptions.BAR);
        position.setContentOptions(DirectionOptions.NORTH_SOUTH);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderJumpBar
            && !OverlayHook.pre(context.getEvent(), ElementType.JUMPBAR);
    }

    @Override
    public Rect render(OverlayContext context) {
        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

        Rect bounds = new Rect(182, 5);
        if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
            bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
        } else {
            bounds = position.applyTo(bounds);
        }

        float charge = MC.player.getHorseJumpPower();
        int filled = (int)(charge * bounds.getWidth());

        GlUtil.drawRect(bounds, bounds.move(0, 84));

        if(filled > 0) {
            GlUtil.drawRect(bounds.withWidth(filled), new Rect(0, 89, filled, bounds.getHeight()));
        }

        OverlayHook.post(context.getEvent(), ElementType.JUMPBAR);
        return bounds;
    }
}
