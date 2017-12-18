package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.passive.EntityHorse;

public class ExtraGuiElementExperienceInfo extends ExtraGuiElement {
	public ExtraGuiElementExperienceInfo() {
		name = "experienceInfo";
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(!mc.thePlayer.capabilities.isCreativeMode && !(mc.thePlayer.ridingEntity != null && mc.thePlayer.ridingEntity instanceof EntityHorse)) {
			int has = Math.round(mc.thePlayer.experience * getExperienceWithinLevel(mc.thePlayer.experienceLevel));
			int needed = getExperienceWithinLevel(mc.thePlayer.experienceLevel) - has;
			drawBorderedString(fr, String.valueOf(has), width / 2 - 90, height - 30, 0xffffff); // 30
			drawBorderedString(fr, String.valueOf(needed), width / 2 + 90 - fr.getStringWidth(String.valueOf(needed)), height - 30, 0xffffff);
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
}