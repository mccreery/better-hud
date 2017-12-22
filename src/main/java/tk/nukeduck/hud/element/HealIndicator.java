package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;
import tk.nukeduck.hud.util.constants.Textures;

public class HealIndicator extends HudElement {
	private SettingMode posMode;
	private SettingAnchoredPosition pos;
	private SettingAnchor anchor;
	private SettingMode mode;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.x = 5;
		pos.y = 5;
		mode.index = 1;
	}
	
	public HealIndicator() {
		super("healIndicator");
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos = new SettingAnchoredPosition("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(mode = new SettingMode("mode", new String[] {"1", "2"}));
	}
	
	public void update() {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
	}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(MC.player.getFoodStats().getFoodLevel() >= 18 && MC.player.getHealth() < MC.player.getMaxHealth()) {
			String healIndicator = I18n.format("betterHud.strings.healIndicator");
			
			int x = posMode.index == 0 ? event.getResolution().getScaledWidth() / 2 - 90 : pos.x;
			int y = posMode.index == 0 ? event.getResolution().getScaledHeight() - 50 - (MC.player.getTotalArmorValue() > 0 ? 10 : 0) : pos.y;
			this.bounds = new Bounds(x, y, 0, 0);
			
			if(mode.index == 0) {
				MC.ingameGUI.drawString(MC.fontRenderer, healIndicator, x, y, Colors.GREEN);
				this.bounds.setWidth(MC.fontRenderer.getStringWidth(healIndicator));
				this.bounds.setHeight(MC.fontRenderer.FONT_HEIGHT);
			} else {
				MC.getTextureManager().bindTexture(Textures.iconsHud);
				MC.ingameGUI.drawTexturedModalRect(x, y, 0, 80, 9, 9);
				this.bounds.setSize(new Point(9, 9));
			}

			this.pos.update(event.getResolution(), bounds);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}
