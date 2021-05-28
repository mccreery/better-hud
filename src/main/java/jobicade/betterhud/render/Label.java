package jobicade.betterhud.render;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Label extends DefaultBoxed {
    private String text;
    private Size size;
    private boolean shadow = true;
    private Color color = Color.WHITE;

    public Label(String text) {
        setText(text);
    }

    public String getText() {
        return text;
    }

    public Label setText(String text) {
        this.text = text;
        this.size = new Size(Minecraft.getInstance().font.width(text), Minecraft.getInstance().font.lineHeight);
        return this;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public Label setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Label setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public Size negotiateSize(Point size) {
        return this.size;
    }

    @Override
    public void render() {
        /*
        * The font renderer for some reason ignores our request
        * and renders completely opaque if opacity < 4.
        */
        if(color.getAlpha() < 4) return;

        Point position = new Rect(size).anchor(bounds, Direction.CENTER).getPosition();
        Minecraft.getInstance().font.func_175065_a(text, position.getX(), position.getY(), color.getPacked(), shadow);

        // Restore OpenGL state as expected
        Color.WHITE.apply();
        Minecraft.getInstance().getTextureManager().bind(Gui.field_110324_m);
        GlStateManager.func_179118_c();
    }
}
