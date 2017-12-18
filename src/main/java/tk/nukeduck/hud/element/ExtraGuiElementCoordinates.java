package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElementCoordinates extends ExtraGuiElement {
	public ExtraGuiElementCoordinates() {
		name = "coordinates";
		modes = new String[] {"grouped", "spaced", "rounded", "left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		String currentMode = currentMode();
		if(currentMode.equals("grouped")) {
			mc.ingameGUI.drawCenteredString(fr, FormatUtil.translatePre("strings.xyz",
				String.valueOf((float) Math.round(mc.thePlayer.posX * 1000) / 1000),
				String.valueOf((float) Math.round(mc.thePlayer.posY * 1000) / 1000),
				String.valueOf((float) Math.round(mc.thePlayer.posZ * 1000) / 1000)), halfWidth, 5, 0xffffff);
		} else {
			boolean rounded = currentMode.equals("rounded");
			
			String x = FormatUtil.translatePre("strings.x", rounded ? String.valueOf((int) mc.thePlayer.posX) : String.valueOf((float) Math.round(mc.thePlayer.posX * 1000) / 1000));
			String y = FormatUtil.translatePre("strings.y", rounded ? String.valueOf((int) mc.thePlayer.posY) : String.valueOf((float) Math.round(mc.thePlayer.posY * 1000) / 1000));
			String z = FormatUtil.translatePre("strings.z", rounded ? String.valueOf((int) mc.thePlayer.posZ) : String.valueOf((float) Math.round(mc.thePlayer.posZ * 1000) / 1000));
			
			if(currentMode.equals("spaced") || currentMode.equals("rounded")) {
				String[] letters = new String[] {x, y, z};
				/** This spacer is only between centers, not between ends - take note. */
				int spacer = Math.max(Math.max(fr.getStringWidth(x), fr.getStringWidth(y)), fr.getStringWidth(z)) + 16;
				for(int i = 0; i < 3; i++) {
					mc.ingameGUI.drawCenteredString(fr, letters[i], halfWidth + (i - 1) * spacer, 5, 0xffffff);;
				}
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
}