package jobicade.betterhud.gui;

import jobicade.betterhud.util.ISlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GuiSlider extends Button {
    private final ISlider slider;
    public boolean dragging;

    public GuiSlider(int id, int x, int y, ISlider slider) {
        this(id, x, y, 150, 20, slider);
    }

    public void updateDisplayString() {
        setMessage(new StringTextComponent(slider.getDisplayString()));
    }

    public GuiSlider(int id, int x, int y, int width, int height, ISlider slider) {
        super(x, y, width, height, StringTextComponent.EMPTY, null);
        this.slider = slider;
        updateDisplayString();
    }

    private void setNormalized(double value) {
        slider.set(slider.getMinimum() + value * (slider.getMaximum() - slider.getMinimum()));
        updateDisplayString();
    }

    @Override
    protected int getYImage(boolean mouseOver) {
        return 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.visible) {
            if(this.dragging) {
                int mouseOffset = (int)mouseX - (x + 4);
                setNormalized((double)mouseOffset / (width - 8));
            }
            int sliderOffset = (int)((slider.get() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) * (width - 8));

            Minecraft.getInstance().getTextureManager().bind(WIDGETS_LOCATION);
//            this.blit(x + sliderOffset,     y,   0, 66, 4, 20);
//            this.blit(x + sliderOffset + 4, y, 196, 66, 4, 20);
            Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(super.mouseClicked(mouseX, mouseY, button)) {
            dragging = true;
            mouseDragged(mouseX, mouseY, button, mouseX, mouseY);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        dragging = false;
        return true;
    }
}
