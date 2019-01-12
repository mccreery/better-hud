package jobicade.betterhud.gui;

import java.io.IOException;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMenuScreen extends GuiScreen {
    private Point origin;
    private String title;

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button instanceof GuiActionButton) {
            ((GuiActionButton)button).actionPerformed();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.origin = new Point(width / 2, height / 16 + 20);
        super.setWorldAndResolution(mc, width, height);
    }

    protected Point getOrigin() {
        return origin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if(title != null) {
            GlUtil.drawString(title, origin.sub(0, 15), Direction.NORTH, Color.WHITE);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
