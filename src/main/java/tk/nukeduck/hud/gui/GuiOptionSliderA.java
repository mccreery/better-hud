package tk.nukeduck.hud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.util.ISaveLoad.ISlider;

@SideOnly(Side.CLIENT)
public class GuiOptionSliderA extends GuiButton {
	private final ISlider slider;
	public boolean dragging;

	public GuiOptionSliderA(int id, int x, int y, ISlider slider) {
		this(id, x, y, 150, 20, slider);
	}

	public GuiOptionSliderA(int id, int x, int y, int width, int height, ISlider slider) {
		super(id, x, y, width, height, "");
		this.slider = slider;
		displayString = slider.getDisplayString();
	}

	private void setNormalized(double value) {
		slider.set(slider.getMinimum() + value * (slider.getMaximum() - slider.getMinimum()));
		displayString = slider.getDisplayString();
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			if(this.dragging) {
				int mouseOffset = mouseX - (x + 4);
				setNormalized((double)mouseOffset / (width - 8)); // TODO make text update
			}
			int sliderOffset = (int)((slider.get() - slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) * (width - 8));

			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(x + sliderOffset,     y,   0, 66, 4, 20);
			this.drawTexturedModalRect(x + sliderOffset + 4, y, 196, 66, 4, 20);
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
