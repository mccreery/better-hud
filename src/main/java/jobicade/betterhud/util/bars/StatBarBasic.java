package jobicade.betterhud.util.bars;

import jobicade.betterhud.geom.Rect;

import java.util.ArrayList;
import java.util.List;

public abstract class StatBarBasic<T> extends StatBar<T> {
    protected abstract Rect getIcon(IconType icon, int pointsIndex);
    protected abstract int getCurrent();

    @Override
    protected List<Rect> getIcons(int pointsIndex) {
        List<Rect> textures = new ArrayList<>(2);
        textures.add(getIcon(IconType.BACKGROUND, pointsIndex));
        int current = getCurrent();

        if(pointsIndex < current) {
            textures.add(getIcon(pointsIndex + 1 < current ? IconType.FULL : IconType.HALF, pointsIndex));
        }
        return textures;
    }

    protected enum IconType {
        BACKGROUND, HALF, FULL;
    }
}
