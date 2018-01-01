package tk.nukeduck.hud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUpDownButton extends GuiButton {
	private static final ResourceLocation settings = new ResourceLocation("hud", "textures/gui/settings.png");
	
	private int index;
	
	public GuiUpDownButton(int buttonId, int x, int y, int index) {
		super(buttonId, x, y, 20, 10, "");
		this.index = index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(this.visible) {
			mc.getTextureManager().bindTexture(settings);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			this.drawTexturedModalRect(this.x, this.y, index * 20, k * 10, this.width, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
		}
	}
}