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
			int has = Math.round(mc.thePlayer.experience * getExperienceLevel(mc.thePlayer.experienceLevel));
			int needed = getExperienceLevel(mc.thePlayer.experienceLevel) - has;
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
	
	public int getExperienceLevel(int level) {
	    if (level >= 30) {
	        return 62 + (level - 30) * 7;
	    } else if (level >= 15) {
	        return 17 + (level - 15) * 3;
	    } else {
	        return 17;
	    }
	}
}