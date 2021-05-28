package jobicade.betterhud.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.geom.Point;

public class GuiActionButton extends GuiButton {
    private ActionCallback callback;
    private boolean repeat;

    public boolean glowing;

    private String tooltip;

    public GuiActionButton(String buttonText) {
        super(0, 0, 0, buttonText);
    }

    @Override
    protected int func_146114_a(boolean mouseOver) {
        int state = super.func_146114_a(mouseOver);
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

    public GuiActionButton setId(int id) {
        this.field_146127_k = id;
        return this;
    }

    public GuiActionButton setBounds(Rect bounds) {
        this.field_146128_h = bounds.getX();
        this.field_146129_i = bounds.getY();
        this.field_146120_f = bounds.getWidth();
        this.field_146121_g = bounds.getHeight();

        return this;
    }

    public Rect getBounds() {
        return new Rect(field_146128_h, field_146129_i, field_146120_f, field_146121_g);
    }

    public final void actionPerformed() {
        if(callback != null) callback.actionPerformed(this);
    }

    protected void drawButton(Rect bounds, Point mousePosition, float partialTicks) {
        super.func_191745_a(Minecraft.getInstance(), mousePosition.getX(), mousePosition.getY(), partialTicks);
    }

    public void updateText(String name, String value) {
        this.field_146126_j = name + ": " + value;
    }

    public void updateText(String unlocalizedName, String valuePrefix, boolean value) {
        String valueDisplay;

        if(value) {
            valueDisplay = ChatFormatting.GREEN + I18n.get(valuePrefix + ".on");
        } else {
            valueDisplay = ChatFormatting.RED + I18n.get(valuePrefix + ".off");
        }
        updateText(I18n.get(unlocalizedName), valueDisplay);
        glowing = value;
    }

    /**
     * OpenGL side-effects: depth disabled, color set to white
     */
    @Override
    public final void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(!field_146125_m) return;

        Rect bounds = getBounds();
        field_146123_n = bounds.contains(mouseX, mouseY);

        drawButton(bounds, new Point(mouseX, mouseY), partialTicks);

        if(tooltip != null && field_146123_n) {
            GlStateManager.func_179126_j();
            GlStateManager.func_179094_E();

            GlStateManager.func_179109_b(0, 0, 1);
            Minecraft.getInstance().screen.func_146279_a(tooltip, mouseX, mouseY);

            GlStateManager.func_179121_F();
            GlStateManager.func_179097_i();
        }
        Color.WHITE.apply();
    }
}
