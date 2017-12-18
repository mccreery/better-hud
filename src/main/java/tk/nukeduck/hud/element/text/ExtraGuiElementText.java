package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FuncsUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public abstract class ExtraGuiElementText extends ExtraGuiElement {
	protected ElementSettingMode posMode;
	protected ElementSettingPosition pos;
	protected ElementSettingAbsolutePositionAnchored pos2;
	protected ElementSettingAnchor anchor;
	protected ElementSettingSlider red, green, blue;
	
	public ExtraGuiElementText() {
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPosition("position", Position.CORNERS) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(red = new ElementSettingSlider("red", 0, 255));
		red.accuracy = 1;
		this.settings.add(green = new ElementSettingSlider("green", 0, 255));
		green.accuracy = 1;
		this.settings.add(blue = new ElementSettingSlider("blue", 0, 255));
		blue.accuracy = 1;
	}
	
	public int getColor() {
		return Colors.fromRGB((int) this.red.value, (int) this.green.value, (int) this.blue.value);
	}
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		this.posMode.index = 0;
		this.pos.value = Position.TOP_LEFT;
		this.pos2.x = 5;
		this.pos2.y = 5;
		this.red.value = 255;
		this.green.value = 255;
		this.blue.value = 255;
	}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		String[] text = this.getText(mc);
		if(posMode.index == 0) {
			stringManager.add(pos.value, this.getColor(), pos.value == Position.BOTTOM_LEFT || pos.value == Position.BOTTOM_RIGHT ? FuncsUtil.flip(text) : text);
			this.bounds = Bounds.EMPTY;
		} else {
			// TODO this is ugly because one frame will always be incorrect
			this.bounds = RenderUtil.renderStrings(mc.fontRenderer, text, pos2.x, pos2.y, Colors.fromRGB((int) red.value, (int) green.value, (int) blue.value), Position.TOP_LEFT);
			this.pos2.update(resolution, this.bounds);
		}
	}
	
	protected abstract String[] getText(Minecraft mc);
}
