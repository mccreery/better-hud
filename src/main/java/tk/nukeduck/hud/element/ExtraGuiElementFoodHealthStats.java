package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glDisable;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;

public class ExtraGuiElementFoodHealthStats extends ExtraGuiElement {
	public ExtraGuiElementFoodHealthStats() {
		name = "foodHealthStats";
		modes = new String[] {"food.sat", "food.noSat"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		glDisable(GL_LIGHTING);
		
		mc.ingameGUI.drawString(fr, "" + ((float) mc.thePlayer.getFoodStats().getFoodLevel()) / 2, halfWidth + 95, height - 35, 0xffffff);
		mc.ingameGUI.drawString(fr, "" + ((float) Math.ceil(mc.thePlayer.getHealth()) / 2), halfWidth - 95 - fr.getStringWidth("" + ((float) Math.ceil(mc.thePlayer.getHealth()) / 2)), height - 35, 0xffffff);
		
		if(currentMode().equals("food.sat")) {
			String satText = I18n.format("betterHud.strings.saturation", new Object[0]).replace("*", String.valueOf(Math.round(mc.thePlayer.getFoodStats().getSaturationLevel() * 10.0) / 10.0));
			mc.ingameGUI.drawString(fr, satText, width - 5 - fr.getStringWidth(satText), BetterHud.getFromName(BetterHud.elements, "lightLevel").enabled ? height - 7 - (2 * fr.FONT_HEIGHT) : height - 5 - fr.FONT_HEIGHT, 0xffffffff);
		}
	}
}