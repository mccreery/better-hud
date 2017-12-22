package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FuncsUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public abstract class TextElement extends HudElement {
	protected SettingMode posMode;
	protected SettingPosition pos;
	protected SettingAnchoredPosition pos2;
	protected SettingAnchor anchor;
	protected SettingSlider red, green, blue;
	
	public TextElement(String name) {
		super(name);
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPosition("position", Position.CORNERS) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(red = new SettingSlider("red", 0, 255));
		red.accuracy = 1;
		this.settings.add(green = new SettingSlider("green", 0, 255));
		green.accuracy = 1;
		this.settings.add(blue = new SettingSlider("blue", 0, 255));
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
		return bounds;
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		String[] text = getText();
		if(posMode.index == 0) {
			stringManager.add(pos.value, this.getColor(), pos.value == Position.BOTTOM_LEFT || pos.value == Position.BOTTOM_RIGHT ? FuncsUtil.flip(text) : text);
			this.bounds = Bounds.EMPTY;
		} else {
			// TODO this is ugly because one frame will always be incorrect
			this.bounds = RenderUtil.renderStrings(MC.fontRenderer, text, pos2.x, pos2.y, Colors.fromRGB((int) red.value, (int) green.value, (int) blue.value), Position.TOP_LEFT);
			this.pos2.update(event.getResolution(), this.bounds);
		}
	}
	
	protected abstract String[] getText();
}
