package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class GuiMenuScreen extends Screen {
    private Point origin;
    private String title;

    public GuiMenuScreen(ITextComponent title) {
        super(title);
    }

    @Override
    protected void actionPerformed(Button button) {
        if(button instanceof GuiActionButton) {
            ((GuiActionButton)button).actionPerformed();
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        this.origin = new Point(width / 2, height / 16 + 20);
        super.resize(mc, width, height);
    }

    protected Point getOrigin() {
        return origin;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void drawTitle() {
        if(title != null) {
            GlUtil.drawString(title, origin.sub(0, 15), Direction.NORTH, Color.WHITE);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawTitle();
        super.render(mouseX, mouseY, partialTicks);
    }
}
