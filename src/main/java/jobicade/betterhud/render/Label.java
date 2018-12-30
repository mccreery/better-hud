package jobicade.betterhud.render;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Label implements Boxed {
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
        this.size = new Size(MC.fontRenderer.getStringWidth(text), MC.fontRenderer.FONT_HEIGHT);
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
    public void render(Rect bounds) {
        Point position = new Rect(size).anchor(bounds, Direction.CENTER).getPosition();

        if(shadow) {
            MC.fontRenderer.drawStringWithShadow(text, position.getX(), position.getY(), color.getPacked());
        } else {
            MC.fontRenderer.drawString(text, position.getX(), position.getY(), color.getPacked());
        }
        // Restore OpenGL state as expected
        Color.WHITE.apply();
        MC.getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.disableAlpha();
    }
}
