package tk.nukeduck.hud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.settings.SettingSlider;

@SideOnly(Side.CLIENT)
public class GuiOptionSliderA extends GuiButton {
	private double sliderValue = 0.0;
	public boolean dragging;
	private SettingSlider setting;

	public GuiOptionSliderA(int id, int x, int y, SettingSlider setting) {
		this(id, x, y, 150, 20, setting);
	}

	public GuiOptionSliderA(int id, int x, int y, int width, int height, SettingSlider setting) {
		super(id, x, y, width, height, "");
		this.setting = setting;

		this.sliderValue = setting.normalize(setting.value);
		this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0, 1.0);
		this.setting.value = setting.denormalize(this.sliderValue);
		
		updateText();
	}

	private void updateText() {
		displayString = setting.getDisplayString();
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			if(this.dragging) {
				this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
				this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0, 1.0);
				this.setting.value = setting.denormalize(this.sliderValue);
				this.sliderValue = setting.normalize(this.setting.value);
				
				updateText();
			}

			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(super.mousePressed(mc, mouseX, mouseY)) {
			this.dragging = true;
			mouseDragged(mc, mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
	}
}
