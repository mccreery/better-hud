package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Textures;

public class ExtraGuiElementHealIndicator extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingAbsolutePositionAnchored pos;
	private ElementSettingAnchor anchor;
	private ElementSettingMode mode;
	
	@Override
	public String getName() {
		return "healIndicator";
	}
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.x = 5;
		pos.y = 5;
		mode.index = 1;
	}
	
	public ExtraGuiElementHealIndicator() {
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos = new ElementSettingAbsolutePositionAnchored("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(mode = new ElementSettingMode("mode", new String[] {"1", "2"}));
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(mc.thePlayer.getFoodStats().getFoodLevel() >= 18 && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
			String healIndicator = FormatUtil.translatePre("strings.healIndicator");
			
			int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
			int y = posMode.index == 0 ? resolution.getScaledHeight() - 50 - (mc.thePlayer.getTotalArmorValue() > 0 ? 10 : 0) : pos.y;
			this.bounds = new Bounds(x, y, 0, 0);
			
			if(mode.index == 0) {
				mc.ingameGUI.drawString(mc.fontRendererObj, healIndicator, x, y, RenderUtil.colorRGB(0, 255, 0));
				this.bounds.setWidth(mc.fontRendererObj.getStringWidth(healIndicator));
				this.bounds.setHeight(mc.fontRendererObj.FONT_HEIGHT);
			} else {
				mc.getTextureManager().bindTexture(Textures.iconsHud);
				mc.ingameGUI.drawTexturedModalRect(x, y, 0, 80, 9, 9);
				this.bounds.setSize(new Point(9, 9));
			}

			this.pos.update(resolution, bounds);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}
