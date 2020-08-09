package jobicade.betterhud.util.bars;

import jobicade.betterhud.geom.Rect;
import net.minecraft.entity.Entity;

public class StatBarAir extends StatBarBasic<Entity> {
    @Override
    protected int getCurrent() {
        int air = host.getAir();

        int full = ((air - 2) * 10 + 299) / 300;
        int partial = (air * 10 + 299) / 300 - full;
        return full * 2 + partial;
    }

    @Override
    protected Rect getIcon(IconType icon, int pointsIndex) {
        switch(icon) {
            case HALF: return new Rect(25, 18, 9, 9);
            case FULL: return new Rect(16, 18, 9, 9);
            case BACKGROUND: default: return null;
        }
    }

    @Override
    public boolean shouldRender() {
        return host.isInWater();
    }
}
