package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ExtraGuiElementClock extends ExtraGuiElement {
	public ExtraGuiElementClock() {
		name = "clock";
		modes = new String[] {"clock.12hrleft", "clock.24hrleft", "clock.12hrright", "clock.24hrright"};
		defaultMode = 2;
		staticHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2 + 12;
	}
	
	int staticHeight;
	
	public static final long night = 18540;
	public static final long morning = 5460;
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		boolean twentyFourHour = currentMode().startsWith("clock.24hr");
		boolean right = currentMode().endsWith("right");
		
		if(right) {
			rightHeight = staticHeight;
			leftHeight = -5;
		} else {
			leftHeight = staticHeight;
			rightHeight = -5;
		}
		
		long t = (mc.theWorld.getWorldTime() + 6000) % 24000;
		String day = "Day " + ((mc.theWorld.getWorldTime() + 6000) / 24000 + 1);
		String time;
		int h = (int) (t / 1000);
		
		if(twentyFourHour) {
			// String m = String.valueOf((int) ((t % 1000) / 1000.0 * 60.0));
			// time = String.valueOf(h) + ":" + (m.length() < 2 ? "0" + m : m);
			time = FormatUtil.formatTime(h, (int) ((t % 1000) / 1000.0 * 60.0));
		} else {
			time = h > 12 ? String.valueOf(h - 12) : String.valueOf(h);
			if(time.equals("0")) {time = "12";}
			time += ":" + String.format("%02d", (int) ((t % 1000) / 1000.0 * 60.0));
			time += (int) (t / 1000) >= 12 ? " PM" : " AM";
		}
		
		int staticWidth = Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) + 10;
		
		if(right) {
			mc.ingameGUI.drawRect(width - staticWidth, BetterHud.currentRightHeight, width, BetterHud.currentRightHeight + staticHeight, 0x55000000);
			mc.ingameGUI.drawString(fr, time, width - fr.getStringWidth(time) - 5, BetterHud.currentRightHeight + 5, 0xffffff);
			mc.ingameGUI.drawString(fr, day, width - fr.getStringWidth(day) - 5, BetterHud.currentRightHeight + 7 + fr.FONT_HEIGHT, 0xffffff);
			
			if(t > night || t < morning) { // Night: 6:32 PM - 5:28 AM... Of course
				glPushAttrib(GL_ALL_ATTRIB_BITS);
				ri.renderItemAndEffectIntoGUI(fr, mc.getTextureManager(), new ItemStack(Items.bed), width - Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) - 31, BetterHud.currentRightHeight + 8);
				glPopAttrib();
			}
		} else {
			mc.ingameGUI.drawRect(0, BetterHud.currentLeftHeight, staticWidth, BetterHud.currentLeftHeight + staticHeight, 0x55000000);
			mc.ingameGUI.drawString(fr, time, 5, BetterHud.currentLeftHeight + 5, 0xffffff);
			mc.ingameGUI.drawString(fr, day, 5, BetterHud.currentLeftHeight + 7 + fr.FONT_HEIGHT, 0xffffff);
			
			if(t > night || t < morning) { // Night: 6:32 PM - 5:28 AM... Of course
				glPushAttrib(GL_ALL_ATTRIB_BITS);
				ri.renderItemAndEffectIntoGUI(fr, mc.getTextureManager(), new ItemStack(Items.bed), Math.max(fr.getStringWidth(time), fr.getStringWidth(day)) + 15, BetterHud.currentLeftHeight + 8);
				glPopAttrib();
			}
		}
	}
}