package tk.nukeduck.hud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.FuncsUtil;

@SideOnly(Side.CLIENT)
public class GuiOptionSliderA extends GuiButton
{
	private double sliderValue = 0.0;
	public boolean dragging;
	//private static final String __OBFID = "CL_00000680";
	private SettingSlider setting;
	
	public GuiOptionSliderA(int p_i45016_1_, int p_i45016_2_, int p_i45016_3_, SettingSlider setting) {
		this(p_i45016_1_, p_i45016_2_, p_i45016_3_, 150, 20, setting);
	}
	
	public GuiOptionSliderA(int p_i45017_1_, int p_i45017_2_, int p_i45017_3_, int width, int height, SettingSlider setting) {
		super(p_i45017_1_, p_i45017_2_, p_i45017_3_, width, height, "");
		//Minecraft minecraft = Minecraft.getMinecraft();
		this.setting = setting;
		
		this.sliderValue = setting.normalize(setting.value);
		this.sliderValue = FuncsUtil.clamp(this.sliderValue, 0.0, 1.0);
		this.setting.value = setting.denormalize(this.sliderValue);
		
		this.displayString = setting.getSliderText();
	}
	
	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over
	 * this button and 2 if it IS hovering over
	 * this button.
	 */
	protected int getHoverState(boolean mouseOver) {
		return 0;
	}
	
	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if(this.visible) {
			if(this.dragging) {
				this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
				this.sliderValue = FuncsUtil.clamp(this.sliderValue, 0.0, 1.0);
				this.setting.value = setting.denormalize(this.sliderValue);
				this.sliderValue = setting.normalize(this.setting.value);
				
				this.displayString = setting.getSliderText();
			}
			
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
		}
	}
	
	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent
	 * e).
	 */
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(super.mousePressed(mc, mouseX, mouseY)) {
			this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
			this.sliderValue = FuncsUtil.clamp(this.sliderValue, 0.0, 1.0);
			this.setting.value = setting.denormalize(this.sliderValue);
			this.sliderValue = setting.normalize(this.setting.value);
			
			this.displayString = setting.getSliderText();
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int mouseX, int mouseY) {
		this.dragging = false;
	}
}