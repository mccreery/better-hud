package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBar;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;

public abstract class Bar extends OverlayElement {
    protected SettingPosition position;
    protected SettingChoose side;

    private StatBar<? super ClientPlayerEntity> bar;

    public Bar(String name, StatBar<? super ClientPlayerEntity> bar) {
        super(name);
        this.bar = bar;

        position = new SettingPosition(DirectionOptions.BAR, DirectionOptions.CORNERS);

        side = new SettingChoose("side", "west", "east");
        side.setEnableOn(() -> position.isDirection(Direction.SOUTH));

        settings.addChildren(position, side);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        bar.setHost(MC.player);
        return bar.shouldRender();
    }

    /** @return {@link Direction#WEST} or {@link Direction#EAST} */
    protected Direction getContentAlignment() {
        if(position.isDirection(Direction.SOUTH)) {
            return side.getIndex() == 1 ? Direction.SOUTH_EAST : Direction.SOUTH_WEST;
        } else {
            return position.getContentAlignment();
        }
    }

    @Override
    public Rect render(OverlayContext context) {
        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
        Direction contentAlignment = getContentAlignment();

        Rect bounds = new Rect(bar.getPreferredSize());

        if(position.isDirection(Direction.SOUTH)) {
            bounds = MANAGER.positionBar(bounds, contentAlignment.withRow(1), 1);
        } else {
            bounds = position.applyTo(bounds);
        }

        bar.setContentAlignment(contentAlignment).setBounds(bounds).render();
        return bounds;
    }
}
