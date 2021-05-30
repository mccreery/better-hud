package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class GuiActionButton extends Button {
    private ActionCallback callback;
    private boolean repeat;

    public boolean glowing;

    private String tooltip;

    public GuiActionButton(String buttonText) {
        super(0, 0, 0, 0, new StringTextComponent(buttonText), null);
    }

    @Override
    protected int getYImage(boolean mouseOver) {
        int state = super.getYImage(mouseOver);
        return glowing && state == 1 ? 2 : state;
    }

    public GuiActionButton setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public GuiActionButton setCallback(ActionCallback callback) {
        this.callback = callback;
        return this;
    }

    public GuiActionButton setRepeat() {
        repeat = true;
        return this;
    }

    public boolean getRepeat() {
        return repeat;
    }

    @Deprecated
    public GuiActionButton setId(int id) {
        return this;
    }

    public GuiActionButton setBounds(Rect bounds) {
        this.x = bounds.getX();
        this.y = bounds.getY();
        this.width = bounds.getWidth();
        this.height = bounds.getHeight();

        return this;
    }

    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    public final void actionPerformed() {
        if(callback != null) callback.actionPerformed(this);
    }

    protected void drawButton(MatrixStack matrixStack, Rect bounds, Point mousePosition, float partialTicks) {
        super.renderButton(matrixStack, mousePosition.getX(), mousePosition.getY(), partialTicks);
    }

    public void updateText(String name, String value) {
        this.setMessage(new StringTextComponent(name + ": " + value));
    }

    public void updateText(String unlocalizedName, String valuePrefix, boolean value) {
        String valueDisplay;

        if(value) {
            valueDisplay = TextFormatting.GREEN + I18n.get(valuePrefix + ".on");
        } else {
            valueDisplay = TextFormatting.RED + I18n.get(valuePrefix + ".off");
        }
        updateText(I18n.get(unlocalizedName), valueDisplay);
        glowing = value;
    }

    /**
     * OpenGL side-effects: depth disabled, color set to white
     */
    @Override
    public final void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(!visible) return;

        Rect bounds = getBounds();
        isHovered = bounds.contains(mouseX, mouseY);

        drawButton(matrixStack, bounds, new Point(mouseX, mouseY), partialTicks);

        if(tooltip != null && isHovered) {
            RenderSystem.enableDepthTest();
            matrixStack.pushPose();

            matrixStack.translate(0, 0, 1);
            Minecraft.getInstance().screen.renderTooltip(matrixStack, new StringTextComponent(tooltip), mouseX, mouseY);

            matrixStack.popPose();
            RenderSystem.disableDepthTest();
        }
        Color.WHITE.apply();
    }
}
