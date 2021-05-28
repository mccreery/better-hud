package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;

public class GuiMenuScreen extends Screen {
    private Point origin;
    private String title;

    @Override
    protected void func_146284_a(Button button) {
        if(button instanceof GuiActionButton) {
            ((GuiActionButton)button).actionPerformed();
        }
    }

    @Override
    public void func_146280_a(Minecraft mc, int width, int height) {
        this.origin = new Point(width / 2, height / 16 + 20);
        super.func_146280_a(mc, width, height);
    }

    protected Point getOrigin() {
        return origin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected void drawTitle() {
        if(title != null) {
            GlUtil.drawString(title, origin.sub(0, 15), Direction.NORTH, Color.WHITE);
        }
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_146276_q_();
        this.drawTitle();
        super.func_73863_a(mouseX, mouseY, partialTicks);
    }
}
