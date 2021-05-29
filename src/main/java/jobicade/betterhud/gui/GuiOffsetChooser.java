package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

import static jobicade.betterhud.BetterHud.SPACER;

public class GuiOffsetChooser extends Screen {
    private final GuiElementSettings parent;
    private final SettingPosition setting;

    public GuiOffsetChooser(GuiElementSettings parent, SettingPosition setting) {
        super(StringTextComponent.EMPTY);
        this.parent = parent;
        this.setting = setting;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 1) {
            setting.set(null);
            Minecraft.getInstance().setScreen(parent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft.getInstance().setScreen(parent);
        return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
            hLine(matrixStack, mouse.getX() - SPACER, mouse.getX() + SPACER, mouse.getY(), Color.RED.getPacked());
            vLine(matrixStack, mouse.getX(), mouse.getY() - SPACER, mouse.getY() + SPACER, Color.RED.getPacked());
        }

        // TODO localize
        //String key = Keyboard.getKeyName(Keyboard.KEY_LCONTROL);
        String key = "CTRL";
        GlUtil.drawString(matrixStack, I18n.get("betterHud.menu.unsnap", key), new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

        renderTooltip(matrixStack, new StringTextComponent(offset.toPrettyString()), mouseX, mouseY);
    }
}
