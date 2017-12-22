package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.text.TextElement;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExperienceInfo extends TextElement {
	private SettingBoolean total;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		total.value = false;
	}
	
	public ExperienceInfo() {
		super("experienceInfo");
		this.settings.add(0, new Divider("position"));
		this.settings.add(0, new Legend("expInfoNotice"));
		this.settings.add(new Divider("misc"));
		this.settings.add(total = new SettingBoolean("total"));
	}
	
	public void update() {}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(!MC.player.capabilities.isCreativeMode && !(MC.player.getRidingEntity() != null && MC.player.getRidingEntity() instanceof EntityHorse)) {
			int has = Math.round(MC.player.experience * getExperienceWithinLevel(MC.player.experienceLevel));
			int needed = getExperienceWithinLevel(MC.player.experienceLevel) - has;
			drawBorderedString(MC.fontRenderer, String.valueOf(has), event.getResolution().getScaledWidth() / 2 - 90, event.getResolution().getScaledHeight() - 30, Colors.WHITE); // 30
			drawBorderedString(MC.fontRenderer, String.valueOf(needed), event.getResolution().getScaledWidth() / 2 + 90 - MC.fontRenderer.getStringWidth(String.valueOf(needed)), event.getResolution().getScaledHeight() - 30, Colors.WHITE);
		}
		
		if(total.value) {
			super.render(event, stringManager, layoutManager);
		}
	}
	
	public void drawBorderedString(FontRenderer fontrenderer, String s, int x, int y, int color) {
		fontrenderer.drawString(s, x + 1, y, 0, false);
		fontrenderer.drawString(s, x - 1, y, 0, false);
		fontrenderer.drawString(s, x, y + 1, 0, false);
		fontrenderer.drawString(s, x, y - 1, 0, false);
		fontrenderer.drawString(s, x, y, color, false);
	}
	
	public int getExperienceWithinLevel(int level) {
	    if (level >= 31) {
	        return 9 * level - 158;
	    } else if (level >= 16) {
	        return 5 * level - 38;
	    } else {
	        return 2 * level + 7;
	    }
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText() {
		if(!total.value) return new String[] {};
		
		int totalExp = Math.round(MC.player.experience * getExperienceWithinLevel(MC.player.experienceLevel));
		for(int i = 0; i < MC.player.experienceLevel; ++i) {
			totalExp += getExperienceWithinLevel(i);
		}
		return new String[] {I18n.format("betterHud.strings.total", String.valueOf(totalExp))};
	}
}
