package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class GuiMenuScreen extends Screen {
    private Point origin;
    private String title;

    public GuiMenuScreen() {
        super(StringTextComponent.EMPTY);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        this.origin = new Point(width / 2, height / 16 + 20);
        super.init(minecraft, width, height);
    }

    protected Point getOrigin() {
        return origin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected void drawTitle(MatrixStack matrixStack) {
        if(title != null) {
            GlUtil.drawString(matrixStack, title, origin.sub(0, 15), Direction.NORTH, Color.WHITE);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.drawTitle(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
