package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.SETTINGS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public class GuiTexturedButton extends GuiActionButton {
	private final Bounds disabled, inactive, active;

	public GuiTexturedButton(Bounds disabled) {
		this(disabled, disabled.getHeight());
	}

	public GuiTexturedButton(Bounds disabled, int pitch) {
		this(disabled,
			disabled.withY(disabled.getY() + pitch),
			disabled.withY(disabled.getY() + pitch * 2));
	}

	public GuiTexturedButton(Bounds disabled, Bounds inactive, Bounds active) {
		super("");

		this.disabled = disabled;
		this.inactive = inactive.withSize(disabled.getSize());
		this.active   = active.withSize(disabled.getSize());
	}

	@Override
	public GuiActionButton setBounds(Bounds bounds) {
		return super.setBounds(bounds.withSize(disabled.getSize()));
	}

	protected Bounds getTexture(int mouseX, int mouseY) {
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int hoverState = this.getHoverState(this.hovered);

		switch(hoverState) {
			case 0:  return disabled;
			case 2:  return active;
			case 1:
			default: return inactive;
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(this.visible) {
			mc.getTextureManager().bindTexture(SETTINGS);

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			GlUtil.color(Colors.WHITE);

			GlUtil.drawTexturedModalRect(getBounds(), getTexture(mouseX, mouseY));
		}
	}
}
