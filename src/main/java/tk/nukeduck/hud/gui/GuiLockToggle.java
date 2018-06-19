package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.SETTINGS;

import net.minecraft.client.Minecraft;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.GlUtil;

public class GuiLockToggle extends GuiSettingToggle {
	public GuiLockToggle(Setting<Boolean> setting) {
		super("", setting);
		setStaticText();
	}

	@Override
	public GuiActionButton setBounds(Bounds bounds) {
		return super.setBounds(bounds.withSize(20, 10));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(SETTINGS);
		GlUtil.color(Colors.WHITE);

		Bounds bounds = getBounds();
		hovered = bounds.contains(mouseX, mouseY);
		int k = this.getHoverState(this.hovered);

		/*GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);*/

		GlUtil.drawTexturedModalRect(bounds, new Bounds(0, 60 + k * 10, 20, 10));
	}
}
