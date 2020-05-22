package jobicade.betterhud.gui;

import jobicade.betterhud.util.ISlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSlider extends GuiButton {
	private final ISlider slider;
	public boolean dragging;

	public GuiSlider(int id, int x, int y, ISlider slider) {
		this(id, x, y, 150, 20, slider);
	}

	public void updateDisplayString() {
		displayString = slider.getDisplayString();
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
				setNormalized((double)mouseOffset / (width - 8));
			}
			int sliderOffset = (int)((slider.get() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) * (width - 8));

			Minecraft.getMinecraft().getTextureManager().bindTexture(BUTTON_TEXTURES);
			this.drawTexturedModalRect(x + sliderOffset,     y,   0, 66, 4, 20);
			this.drawTexturedModalRect(x + sliderOffset + 4, y, 196, 66, 4, 20);
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
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
