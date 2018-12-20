package jobicade.betterhud.render;

import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class Quad extends GuiResizable {
    private static final float TEX_SCALE = 1f / 256f;

    private double zLevel;
    private Rect texture;
    private final Map<Direction, Color> colors;
    private boolean hasColor = false;

    public Quad(Point size) {
        super(size);
        colors = new EnumMap<>(Direction.class);
    }

    public Quad(Rect bounds) {
        super(bounds);
        colors = new EnumMap<>(Direction.class);
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

    public Quad(Quad quad) {
        super(quad.bounds);
        colors = new EnumMap<>(quad.colors);
        hasColor = quad.hasColor;
        setZLevel(quad.zLevel);
        setTexture(quad.texture);
    }

    /**
     * OpenGL side-effect: texture 2D is enabled.
     * <p>{@inheritDoc}
     */
    @Override
    public void render() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        VertexFormat format = new VertexFormat();

        format.addElement(DefaultVertexFormats.POSITION_3F);
        if(texture != null) format.addElement(DefaultVertexFormats.TEX_2F);
        if(hasColor) format.addElement(DefaultVertexFormats.COLOR_4UB);

        builder.begin(GL11.GL_QUADS, format);
        addVertex(builder, Direction.SOUTH_WEST);
        addVertex(builder, Direction.SOUTH_EAST);
        addVertex(builder, Direction.NORTH_EAST);
        addVertex(builder, Direction.NORTH_WEST);

        if(texture == null) {
            GlStateManager.disableTexture2D();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        } else {
            tessellator.draw();
        }
    }

    private void addVertex(BufferBuilder builder, Direction anchor) {
        for(VertexFormatElement element : builder.getVertexFormat().getElements()) {
            switch(element.getUsage()) {
                case POSITION: {
                    Point xy = getBounds().getAnchor(anchor);
                    builder.pos(xy.getX(), xy.getY(), zLevel);
                    break;
                }
                case UV: {
                    Point uv = texture.getAnchor(anchor);
                    builder.tex(uv.getX() * TEX_SCALE, uv.getY() * TEX_SCALE);
                    break;
                }
                case COLOR: {
                    Color color = colors.get(anchor);
                    builder.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    break;
                }
                default: throw new IllegalStateException("Unsupported builder element");
            }
        }
        builder.endVertex();
    }
}
