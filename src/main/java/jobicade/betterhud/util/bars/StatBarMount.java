package jobicade.betterhud.util.bars;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class StatBarMount extends StatBarBasic<Entity> {
    @Override
    public boolean shouldRender() {
        return host.getRidingEntity() instanceof LivingEntity;
    }

    @Override
    protected Rect getIcon(IconType icon, int pointsIndex) {
        switch(icon) {
            case BACKGROUND: return new Rect(52, 9, 9, 9);
            case HALF:       return new Rect(97, 9, 9, 9);
            case FULL:       return new Rect(88, 9, 9, 9);
            default:         return null;
        }
    }

    @Override
    protected int getCurrent() {
        return MathHelper.ceil(((LivingEntity)host.getRidingEntity()).getHealth());
    }

    @Override
    protected int getMaximum() {
        return MathHelper.ceil(((LivingEntity)host.getRidingEntity()).getMaxHealth());
    }

    @Override
    public Direction getNativeAlignment() {
        return Direction.EAST;
    }
}
