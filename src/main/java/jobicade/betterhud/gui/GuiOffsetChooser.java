package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;

public class GuiOffsetChooser extends GuiScreen {
    private final GuiElementSettings parent;
    private final SettingPosition setting;

    public GuiOffsetChooser(GuiElementSettings parent, SettingPosition setting) {
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            setting.set(null);
            Minecraft.getInstance().setScreen(parent);
        }
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        Point anchor = setting.getParent().getAnchor(setting.getAnchor());
        Point offset = new Point(mouseX, mouseY).sub(anchor);

        if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
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
            func_73730_a(mouse.getX() - SPACER, mouse.getX() + SPACER, mouse.getY(), Color.RED.getPacked());
            func_73728_b(mouse.getX(), mouse.getY() - SPACER, mouse.getY() + SPACER, Color.RED.getPacked());
        }

        String key = Keyboard.getKeyName(Keyboard.KEY_LCONTROL);
        GlUtil.drawString(I18n.get("betterHud.menu.unsnap", key), new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

        func_146279_a(offset.toPrettyString(), mouseX, mouseY);
    }
}
