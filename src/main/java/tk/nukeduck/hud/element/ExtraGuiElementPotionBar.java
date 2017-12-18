package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;

public class ExtraGuiElementPotionBar extends ExtraGuiElement {
	public static ResourceLocation inventory;
	
	public ExtraGuiElementPotionBar() {
		name = "potionBar";
		modes = new String[] {"left", "center", "right"};
		defaultMode = 1;
		inventory = new ResourceLocation("textures/gui/container/inventory.png");
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		String m = currentMode();
		boolean right = m.equals("right");
		boolean left = m.equals("left");
		
		int amount = mc.thePlayer.getActivePotionEffects().size();
		
		if(right) {
			rightHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			leftHeight = -5;
		} else if(left) {
			leftHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			rightHeight = -5;
		} else {
			leftHeight = rightHeight = -5;
		}
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Render potion icons
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(inventory);
		int it = 0;
		
		int x = right ? width - amount * 16 - 5 : left ? 5 : halfWidth + 5;
		int y = right ? BetterHud.currentRightHeight + fr.FONT_HEIGHT + 2 : left ? BetterHud.currentLeftHeight + fr.FONT_HEIGHT + 2 : height / 2 - 23;
		
		for(Iterator i = mc.thePlayer.getActivePotionEffects().iterator(); i.hasNext(); it++) {
			PotionEffect pe = (PotionEffect) i.next();
			
			int iIndex = Potion.potionTypes[pe.getPotionID()].getStatusIconIndex();
			glColor4f(1.0F, 1.0F, 1.0F, ((float) pe.getDuration() / 600F));
			
			mc.ingameGUI.drawTexturedModalRect(x + (it * 16), y, 18 * (iIndex % 8), 198 + ((iIndex >> 3) * 18), 18, 18); // >> 3 = / 8
		}
		
		// Render potion potencies
		it = 0;
		for(Iterator i = mc.thePlayer.getActivePotionEffects().iterator(); i.hasNext(); it++) {
			mc.ingameGUI.drawString(fr, FormatUtil.translate("potion.potency." + ((PotionEffect) i.next()).getAmplifier()).replace("potion.potency.", ""), x + (it * 16) + 4, y - fr.FONT_HEIGHT - 2, 0xffffffff);
		}
	}
}