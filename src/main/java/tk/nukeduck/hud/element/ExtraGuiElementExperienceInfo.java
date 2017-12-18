package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.passive.EntityHorse;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingText;
import tk.nukeduck.hud.element.text.ExtraGuiElementText;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementExperienceInfo extends ExtraGuiElementText {
	private ElementSettingBoolean total;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		total.value = false;
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return super.getBounds(resolution);
	}
	
	public ExtraGuiElementExperienceInfo() {
		this.settings.add(0, new ElementSettingDivider("position"));
		this.settings.add(0, new ElementSettingText("expInfoNotice"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(total = new ElementSettingBoolean("total"));
	}
	
	@Override
	public String getName() {
		return "experienceInfo";
	}
	
	public void update(Minecraft mc) {}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(!mc.thePlayer.capabilities.isCreativeMode && !(mc.thePlayer.getRidingEntity() != null && mc.thePlayer.getRidingEntity() instanceof EntityHorse)) {
			int has = Math.round(mc.thePlayer.experience * getExperienceWithinLevel(mc.thePlayer.experienceLevel));
			int needed = getExperienceWithinLevel(mc.thePlayer.experienceLevel) - has;
			drawBorderedString(mc.fontRendererObj, String.valueOf(has), resolution.getScaledWidth() / 2 - 90, resolution.getScaledHeight() - 30, RenderUtil.colorRGB(255, 255, 255)); // 30
			drawBorderedString(mc.fontRendererObj, String.valueOf(needed), resolution.getScaledWidth() / 2 + 90 - mc.fontRendererObj.getStringWidth(String.valueOf(needed)), resolution.getScaledHeight() - 30, RenderUtil.colorRGB(255, 255, 255));
		}
		
		if(total.value) {
			super.render(mc, resolution, stringManager, layoutManager);
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
	protected String[] getText(Minecraft mc) {
		if(!total.value) return new String[] {};
		
		int totalExp = Math.round(mc.thePlayer.experience * getExperienceWithinLevel(mc.thePlayer.experienceLevel));
		for(int i = 0; i < mc.thePlayer.experienceLevel; ++i) {
			totalExp += getExperienceWithinLevel(i);
		}
		return new String[] {FormatUtil.translatePre("strings.total", String.valueOf(totalExp))};
	}
}