package jobicade.betterhud.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;

public class Label extends DefaultBoxed {
    private final MatrixStack matrixStack;
    private String text;
    private Size size;
    private boolean shadow = true;
    private Color color = Color.WHITE;

    public Label(MatrixStack matrixStack, ITextComponent text) {
        this(matrixStack, text.toString());
    }

    public Label(MatrixStack matrixStack, String text) {
        setText(text);
        this.matrixStack = matrixStack;
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
        if (shadow) {
            Minecraft.getInstance().font.drawShadow(matrixStack, text, position.getX(), position.getY(), color.getPacked());
        } else {
            Minecraft.getInstance().font.draw(matrixStack, text, position.getX(), position.getY(), color.getPacked());
        }

        // Restore OpenGL state as expected
        Color.WHITE.apply();
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        RenderSystem.disableAlphaTest();
    }
}
