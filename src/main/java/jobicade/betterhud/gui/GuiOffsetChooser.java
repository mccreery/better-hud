package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiOffsetChooser extends Screen {
    private final GuiElementSettings parent;
    private final SettingPosition setting;

    public GuiOffsetChooser(GuiElementSettings parent, SettingPosition setting) {
        super(new StringTextComponent(""));
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            setting.setOffset(null);
            MC.displayGuiScreen(parent);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        MC.displayGuiScreen(parent);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Point anchor = setting.getParent().getAnchor(setting.getAnchor());
        Point offset = new Point(mouseX, mouseY).sub(anchor);

        if(!Screen.hasControlDown()) {
            int x = (offset.getX() + SPACER * 3 / 2) / SPACER - 1;
            if(x >= -1 && x <= 1) {
                offset = offset.withX(x * SPACER);
            }

            int y = (offset.getY() + SPACER * 3 / 2) / SPACER - 1;
            if(y >= -1 && y <= 1) {
                offset = offset.withY(y * SPACER);
            }
        }
        setting.setOffset(offset);

        GlUtil.drawBorderRect(setting.getParent(), Color.RED.withAlpha(63));

        Rect elementRect = parent.element.getLastBounds();
        if(!elementRect.isEmpty()) {
            GlUtil.drawBorderRect(parent.element.getLastBounds(), Color.RED);
        } else {
            Point mouse = offset.add(anchor);
            hLine(mouse.getX() - SPACER, mouse.getX() + SPACER, mouse.getY(), Color.RED.getPacked());
            vLine(mouse.getX(), mouse.getY() - SPACER, mouse.getY() + SPACER, Color.RED.getPacked());
        }

        String key = GLFW.glfwGetKeyName(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_LEFT_CONTROL));
        GlUtil.drawString(I18n.format("betterHud.menu.unsnap", key), new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

        renderTooltip(offset.toPrettyString(), mouseX, mouseY);
    }
}
