package jobicade.betterhud.render;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class Quad extends DefaultBoxed {
    private static final float TEX_SCALE = 1f / 256f;

    private double zLevel;
    private Rect texture;
    private final Map<Direction, Color> colors;
    private boolean hasColor = false;

    public Quad() {
        colors = new EnumMap<>(Direction.class);
    }

    public Quad(Quad quad) {
        colors = new EnumMap<>(quad.colors);
        hasColor = quad.hasColor;
        setZLevel(quad.zLevel);
        setTexture(quad.texture);
    }

    public Quad setZLevel(double zLevel) {
        this.zLevel = zLevel;
        return this;
    }

    public Quad setTexture(Rect texture) {
        this.texture = texture;
        return this;
    }

    public Quad setColor(Color color) {
        setColors(color, color, color, color);
        return this;
    }

    public Quad setColor(Direction direction, Color color) {
        colors.put(direction, color);
        hasColor = true;
        return this;
    }

    public Quad setColors(Color northWest, Color northEast, Color southWest, Color southEast) {
        colors.put(Direction.NORTH_WEST, northWest);
        colors.put(Direction.NORTH_EAST, northEast);
        colors.put(Direction.SOUTH_WEST, southWest);
        colors.put(Direction.SOUTH_EAST, southEast);
        hasColor = true;
        return this;
    }

    public Quad noColor() {
        hasColor = false;
        return this;
    }

    /**
     * OpenGL side-effect: texture 2D is enabled.
     * <p>{@inheritDoc}
     */
    @Override
    public void render() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        VertexFormat format = new VertexFormat();

        format.func_181721_a(DefaultVertexFormats.ELEMENT_POSITION);
        if(texture != null) format.func_181721_a(DefaultVertexFormats.ELEMENT_UV0);
        if(hasColor) format.func_181721_a(DefaultVertexFormats.ELEMENT_COLOR);

        builder.begin(GL11.GL_QUADS, format);
        addVertex(bounds, builder, Direction.SOUTH_WEST);
        addVertex(bounds, builder, Direction.SOUTH_EAST);
        addVertex(bounds, builder, Direction.NORTH_EAST);
        addVertex(bounds, builder, Direction.NORTH_WEST);

        if(texture == null) {
            GlStateManager.func_179090_x();
            tessellator.end();
            GlStateManager.func_179098_w();
        } else {
            tessellator.end();
        }
    }

    private void addVertex(Rect bounds, BufferBuilder builder, Direction anchor) {
        for(VertexFormatElement element : builder.func_178973_g().func_177343_g()) {
            switch(element.getUsage()) {
                case POSITION: {
                    Point xy = bounds.getAnchor(anchor);
                    builder.func_181662_b(xy.getX(), xy.getY(), zLevel);
                    break;
                }
                case UV: {
                    Point uv = texture.getAnchor(anchor);
                    builder.func_187315_a(uv.getX() * TEX_SCALE, uv.getY() * TEX_SCALE);
                    break;
                }
                case COLOR: {
                    Color color = colors.get(anchor);
                    builder.func_181669_b(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    break;
                }
                default: throw new IllegalStateException("Unsupported builder element");
            }
        }
        builder.endVertex();
    }

    @Override
    public Size getPreferredSize() {
        return texture != null ? texture.getSize() : Size.zero();
    }
}
