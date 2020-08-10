package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.SettingSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider extends GuiButton {
    private final SettingSlider slider;
    public boolean dragging;

    public GuiSlider(int id, int x, int y, SettingSlider slider) {
        this(id, x, y, 150, 20, slider);
    }

    public void updateDisplayString() {
        displayString = slider.getDisplayString();
    }

    public GuiSlider(int id, int x, int y, int width, int height, SettingSlider slider) {
        super(id, x, y, width, height, "");
        this.slider = slider;
        updateDisplayString();
    }

    private void setNormalized(float value) {
        slider.setValue(slider.getMinimum() + value * (slider.getMaximum() - slider.getMinimum()));
        updateDisplayString();
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    /**
     * OpenGL side-effects: set texture to Gui.ICONS
     * <p>{@inheritDoc}
     */
    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if(this.visible) {
            if(this.dragging) {
                int mouseOffset = mouseX - (x + 4);
                setNormalized((float)mouseOffset / (width - 8));
            }
            int sliderOffset = (int)((slider.getValue() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) * (width - 8));

            MC.getTextureManager().bindTexture(BUTTON_TEXTURES);
            this.drawTexturedModalRect(x + sliderOffset,     y,   0, 66, 4, 20);
            this.drawTexturedModalRect(x + sliderOffset + 4, y, 196, 66, 4, 20);
            MC.getTextureManager().bindTexture(Gui.ICONS);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if(super.mousePressed(mc, mouseX, mouseY)) {
            dragging = true;
            mouseDragged(mc, mouseX, mouseY);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }
}
