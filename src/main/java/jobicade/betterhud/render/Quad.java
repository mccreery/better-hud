package jobicade.betterhud.render;

import java.util.function.Function;

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
    private Function<Direction, Color> colorFunction;

    public Quad(Point size) {
        super(size);
    }

    public Quad(Rect bounds) {
        super(bounds);
    }

    public void setZLevel(double zLevel) {
        this.zLevel = zLevel;
    }

    public void setTexture(Rect texture) {
        this.texture = texture;
    }

    public void setColor(Color color) {
        this.colorFunction = d -> color;
    }

    public void setColorFunction(Function<Direction, Color> colorFunction) {
        this.colorFunction = colorFunction;
    }

    public Quad(Quad quad) {
        this(quad.bounds);
        setZLevel(quad.zLevel);
        setTexture(quad.texture);
        setColorFunction(quad.colorFunction);
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
        if(colorFunction != null) format.addElement(DefaultVertexFormats.COLOR_4UB);

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
                    Color color = colorFunction.apply(anchor);
                    builder.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    break;
                }
                default: throw new IllegalStateException("Unsupported builder element");
            }
        }
        builder.endVertex();
    }
}
