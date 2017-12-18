package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElementFps extends ExtraGuiElement {
	public ExtraGuiElementFps() {
		name = "fpsCount";
		modes = new String[] {"left", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(currentMode().equals("right")) {
			rightStrings.add(FormatUtil.translatePre("strings.fps", mc.debug.split(" ")[0]));
		} else {
			leftStrings.add(FormatUtil.translatePre("strings.fps", mc.debug.split(" ")[0]));
		}
	}
}