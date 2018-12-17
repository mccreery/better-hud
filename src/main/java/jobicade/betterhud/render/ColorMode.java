package jobicade.betterhud.render;

public class ColorMode extends GlMode {
    private final Color color;

    public ColorMode(Color color) {
        this.color = color;
    }

    @Override
    protected void begin() {
        color.apply();
    }

    @Override
    protected void end() {
        Color.WHITE.apply();
    }
}
