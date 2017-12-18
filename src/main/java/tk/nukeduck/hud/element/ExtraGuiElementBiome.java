package tk.nukeduck.hud.element;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.BlockPos;

public class ExtraGuiElementBiome extends ExtraGuiElement {
	public ExtraGuiElementBiome() {
		name = "biome";
		modes = new String[] {"left", "middle", "right"};
		defaultMode = 1;
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		int y = 17 + (BetterHud.getFromName(BetterHud.elements, "compass").enabled ? 23 : 0);
		String m = currentMode();
		String b = FormatUtil.translatePre("strings.biome", mc.theWorld.getBiomeGenForCoords(new BlockPos((int) mc.thePlayer.posX, 0, (int) mc.thePlayer.posZ)).biomeName);
		if(m.equals("left")) {
			leftStrings.add(b);
		} else if(m.equals("right")) {
			rightStrings.add(b);
		} else {
			mc.ingameGUI.drawCenteredString(fr, b, halfWidth, y, 0xffffff);
		}
	}
}