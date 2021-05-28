package jobicade.betterhud.gui;

import jobicade.betterhud.util.ISlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider extends GuiButton {
    private final ISlider slider;
    public boolean dragging;

    public GuiSlider(int id, int x, int y, ISlider slider) {
        this(id, x, y, 150, 20, slider);
    }

    public void updateDisplayString() {
        field_146126_j = slider.getDisplayString();
    }

    public GuiSlider(int id, int x, int y, int width, int height, ISlider slider) {
        super(id, x, y, width, height, "");
        this.slider = slider;
        updateDisplayString();
    }

    private void setNormalized(double value) {
        slider.set(slider.getMinimum() + value * (slider.getMaximum() - slider.getMinimum()));
        updateDisplayString();
    }

    @Override
    protected int func_146114_a(boolean mouseOver) {
        return 0;
    }

    /**
     * OpenGL side-effects: set texture to Gui.ICONS
     * <p>{@inheritDoc}
     */
    @Override
    protected void func_146119_b(Minecraft mc, int mouseX, int mouseY) {
        if(this.field_146125_m) {
            if(this.dragging) {
                int mouseOffset = mouseX - (field_146128_h + 4);
                setNormalized((double)mouseOffset / (field_146120_f - 8));
            }
            int sliderOffset = (int)((slider.get() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) * (field_146120_f - 8));

            Minecraft.getInstance().getTextureManager().bind(field_146122_a);
            this.func_73729_b(field_146128_h + sliderOffset,     field_146129_i,   0, 66, 4, 20);
            this.func_73729_b(field_146128_h + sliderOffset + 4, field_146129_i, 196, 66, 4, 20);
            Minecraft.getInstance().getTextureManager().bind(Gui.field_110324_m);
        }
    }

    @Override
    public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
        if(super.func_146116_c(mc, mouseX, mouseY)) {
            dragging = true;
            func_146119_b(mc, mouseX, mouseY);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void func_146118_a(int mouseX, int mouseY) {
        dragging = false;
    }
}
