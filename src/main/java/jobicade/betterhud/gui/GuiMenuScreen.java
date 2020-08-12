package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class GuiMenuScreen extends Screen {
    public GuiMenuScreen(ITextComponent title) {
        super(title);
    }

    protected Point getOrigin() {
        return new Point(width / 2, height / 16 + 20);
    }

    protected void drawTitle() {
        if(title != null) {
            GlUtil.drawString(title.getFormattedText(), getOrigin().sub(0, 15), Direction.NORTH, Color.WHITE);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.drawTitle();
        super.render(mouseX, mouseY, partialTicks);
    }
}
