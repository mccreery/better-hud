package jobicade.betterhud.render;

import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class Label extends DefaultBoxed {
    private String text;
    private Size size;
    private boolean shadow = true;
    private Color color = Color.WHITE;
    private Color background;

    public Label(String text) {
        setText(text);
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public String getText() {
        return text;
    }

    public Label setText(String text) {
        this.text = text;
        this.size = new Size(Minecraft.getInstance().fontRenderer.getStringWidth(text), Minecraft.getInstance().fontRenderer.FONT_HEIGHT);
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
        if (background != null) {
            GlUtil.drawRect(bounds, background);
        }

        /*
        * The font renderer for some reason ignores our request
        * and renders completely opaque if opacity < 4.
        */
        if(color.getAlpha() < 4) return;

        Point position = new Rect(size).anchor(bounds, Direction.CENTER).getPosition();

        if (shadow) {
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(text, position.getX(), position.getY(), color.getPacked());
        } else {
            Minecraft.getInstance().fontRenderer.drawString(text, position.getX(), position.getY(), color.getPacked());
        }

        // Restore OpenGL state as expected
        Color.WHITE.apply();
        Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.disableAlphaTest();
    }
}
