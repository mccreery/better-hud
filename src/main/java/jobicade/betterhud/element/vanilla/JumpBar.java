package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import static jobicade.betterhud.BetterHud.MANAGER;

public class JumpBar extends OverrideElement {
    public JumpBar() {
        super("jumpBar", new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(2);
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.getInstance().player.isRidingJumpable() && super.shouldRender(event);
    }

    @Override
    protected Rect render(Event event) {
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);

        Rect bounds = new Rect(182, 5);
        if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
            bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
        } else {
            bounds = position.applyTo(bounds);
        }

        float charge = Minecraft.getInstance().player.getJumpRidingScale();
        int filled = (int)(charge * bounds.getWidth());

        GlUtil.drawRect(bounds, bounds.move(0, 84));

        if(filled > 0) {
            GlUtil.drawRect(bounds.withWidth(filled), new Rect(0, 89, filled, bounds.getHeight()));
        }
        return bounds;
    }

    @Override
    protected ElementType getType() {
        return ElementType.JUMPBAR;
    }
}
