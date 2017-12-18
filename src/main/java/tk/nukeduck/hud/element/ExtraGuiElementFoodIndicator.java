package tk.nukeduck.hud.element;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.RenderUtil;

public class ExtraGuiElementFoodIndicator extends ExtraGuiElement {
	public ExtraGuiElementFoodIndicator() {
		name = "foodIndicator";
		modes = new String[] {"aboveBar", "center"};
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(mc.thePlayer.getFoodStats().needFood()) {
			double alpha = Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4d(1.0, 1.0, 1.0, alpha);
			mc.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
			int x = 0, y = 0;
			if(currentMode().equals("center")) {
				x = halfWidth + 5;
				y = height / 2 + 5;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, halfWidth + 5, height / 2 + 5, );
			} else {
				x = halfWidth + 75;
				y = height - 56;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, halfWidth + 75, height - 56, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
			}
			BetterHud.itemRendererGui.drawTexturedModalRect(x, y, 0, 64, 16, 16);
		}
	}
	
	private static final ItemStack beef = new ItemStack(Items.cooked_beef);
}