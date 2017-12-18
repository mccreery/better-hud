package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ExtraGuiElementEnchantIndicator extends ExtraGuiElement {
	public ExtraGuiElementEnchantIndicator() {
		name = "enchantIndicator";
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		if(!mc.thePlayer.capabilities.isCreativeMode && !(mc.thePlayer.ridingEntity != null && mc.thePlayer.ridingEntity instanceof EntityHorse) && mc.thePlayer.experienceLevel >= 30) {
			glPushAttrib(GL_ALL_ATTRIB_BITS);
			ri.renderItemAndEffectIntoGUI(fr, mc.getTextureManager(), enchbook, halfWidth - 8, height - 50);
			glPopAttrib();
		}
	}
	
	private static ItemStack enchbook = new ItemStack(Items.enchanted_book);
}