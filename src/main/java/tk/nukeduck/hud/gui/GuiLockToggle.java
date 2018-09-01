package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.SETTINGS;

import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

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
	public void drawButton(Bounds bounds, Point mousePosition, float partialTicks) {
		GlMode.push(new TextureMode(SETTINGS));
		GlUtil.drawTexturedModalRect(bounds, new Bounds(0, 60 + getHoverState(this.hovered) * 10, 20, 10));
		GlMode.pop();
	}
}
