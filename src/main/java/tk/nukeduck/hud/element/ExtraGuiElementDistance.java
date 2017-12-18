package tk.nukeduck.hud.element;

import java.util.ArrayList;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;

public class ExtraGuiElementDistance extends ExtraGuiElement {
	public ExtraGuiElementDistance() {
		name = "distance";
		modes = new String[] {"1", "2"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		MovingObjectPosition mop = mc.renderViewEntity.rayTrace(200, 1.0F);
		Block te = mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
		if(te != null && !te.equals(Blocks.air)) {
			double x = (mop.blockX + 0.5) - mc.thePlayer.posX;
			double y = (mop.blockY + 0.5) - mc.thePlayer.posY;
			double z = (mop.blockZ + 0.5) - mc.thePlayer.posZ;
			double xz = Math.sqrt(x*x + z*z);
			int distance = (int) Math.round(Math.sqrt(xz*xz + y*y));
			String s = currentMode().equals("1") ? ChatFormatting.GREEN + "[" + ChatFormatting.RESET + String.valueOf(distance) + ChatFormatting.GREEN + "]" : String.valueOf(distance) + "m away";
			mc.ingameGUI.drawString(fr, s, halfWidth - 5 - fr.getStringWidth(s), height / 2 + 5, 0xffffff);
		}
	}
}