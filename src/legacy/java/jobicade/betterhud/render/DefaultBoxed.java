package jobicade.betterhud.render;

import jobicade.betterhud.geom.Rect;

public abstract class DefaultBoxed implements Boxed {
    protected Rect bounds;

    @Override
    public Rect getBounds() {
        return bounds;
    }

    @Override
    public Boxed setBounds(Rect bounds) {
        this.bounds = bounds;
        return this;
    }
}
