package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;

public class ExtraGuiElementLightLevel extends ExtraGuiElement {
	public ExtraGuiElementLightLevel() {
		name = "lightLevel";
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
	    int j3 = MathHelper.floor_double(mc.thePlayer.posX);
	    int k3 = MathHelper.floor_double(mc.thePlayer.posY);
	    int l3 = MathHelper.floor_double(mc.thePlayer.posZ);
		int light = (mc.theWorld != null && mc.theWorld.blockExists(j3, k3, l3)) ? mc.theWorld.getChunkFromBlockCoords(j3, l3).getBlockLightValue(j3 & 15, k3, l3 & 15, 0) : 0;
		String lightLevelString = I18n.format("betterHud.strings.lightLevel", new Object[0]).replace("*", String.valueOf(light > 15 ? 15 : light));
		
		mc.ingameGUI.drawString(fr, lightLevelString, width - 5 - fr.getStringWidth(lightLevelString), height - 5 - fr.FONT_HEIGHT, 0xffffffff);
	}
}