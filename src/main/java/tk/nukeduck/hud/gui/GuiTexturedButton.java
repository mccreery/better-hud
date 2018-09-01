package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.SETTINGS;

import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

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

	protected Bounds getTexture() {
		switch(getHoverState(this.hovered)) {
			case 0:  return disabled;
			case 2:  return active;
			case 1:
			default: return inactive;
		}
	}

	@Override
	protected void drawButton(Bounds bounds, Point mousePosition, float partialTicks) {
		GlMode.push(new TextureMode(SETTINGS));
		GlUtil.drawTexturedModalRect(bounds, getTexture());
		GlMode.pop();
	}
}
