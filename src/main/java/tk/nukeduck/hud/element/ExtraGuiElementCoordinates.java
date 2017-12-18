package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;

public class ExtraGuiElementCoordinates extends ExtraGuiElement {
	public ExtraGuiElementCoordinates() {
		name = "coordinates";
		modes = new String[] {"grouped", "spaced", "rounded", "left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		String x = I18n.format("betterHud.strings.x", new Object[0]).replace("*", !currentMode().equals("rounded") ? String.valueOf((float) Math.round(mc.thePlayer.posX * 1000) / 1000) : String.valueOf((int) Math.floor(mc.thePlayer.posX)));
		String y = I18n.format("betterHud.strings.y", new Object[0]).replace("*", !currentMode().equals("rounded") ? String.valueOf((float) Math.round(mc.thePlayer.posY * 1000) / 1000) : String.valueOf((int) Math.floor(mc.thePlayer.posY)));
		String z = I18n.format("betterHud.strings.z", new Object[0]).replace("*", !currentMode().equals("rounded") ? String.valueOf((float) Math.round(mc.thePlayer.posZ * 1000) / 1000) : String.valueOf((int) Math.floor(mc.thePlayer.posZ)));
		
		if(currentMode().equals("spaced") || currentMode().equals("rounded")) {
			String[] letters = new String[] {x, y, z};
			/** This spacer is only between centers, not between ends - take note. */
			int spacer = Math.max(Math.max(fr.getStringWidth(x), fr.getStringWidth(y)), fr.getStringWidth(z)) + 16;
			for(int i = 0; i < 3; i++) {
				mc.ingameGUI.drawCenteredString(fr, letters[i], halfWidth + (i - 1) * spacer, 5, 0xffffff);;
			}
		} else if(currentMode().equals("grouped")) {
			String separator = I18n.format("betterHud.strings.separator");
			mc.ingameGUI.drawCenteredString(fr, x + separator + y + separator + z, halfWidth, 5, 0xffffff);
		} else if(currentMode().equals("left")) {
			leftStrings.add(x);
			leftStrings.add(y);
			leftStrings.add(z);
		} else {
			rightStrings.add(x);
			rightStrings.add(y);
			rightStrings.add(z);
		}
	}
}