package jobicade.betterhud.util.bars;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import net.minecraft.entity.player.PlayerEntity;

public class StatBarArmor extends StatBarBasic<PlayerEntity> {
    @Override
    protected int getCurrent() {
        return host.getArmorValue();
    }

    @Override
    protected Rect getIcon(IconType icon, int pointsIndex) {
        switch(icon) {
            case BACKGROUND: return new Rect(16, 9, 9, 9);
            case HALF:       return new Rect(25, 9, 9, 9);
            case FULL:       return new Rect(34, 9, 9, 9);
            default:         return null;
        }
    }

    @Override
    public boolean shouldRender() {
        return getCurrent() > 0;
    }

    @Override
    public Direction getNativeAlignment() {
        return Direction.WEST;
    }
}
