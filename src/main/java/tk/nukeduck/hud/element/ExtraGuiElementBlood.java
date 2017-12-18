package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import tk.nukeduck.hud.BetterHud;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;

public class ExtraGuiElementBlood extends ExtraGuiElement {
	public static ResourceLocation blood;
	
	public ExtraGuiElementBlood() {
		name = "bloodSplatters";
		modes = new String[] {"blood.sparse", "blood.normal", "blood.dense", "blood.denser"};
		defaultMode = 1;
		blood = new ResourceLocation("hud", "textures/gui/blood.png");
	}
	
	@Override
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(blood);
		for(int[] coords : BetterHud.bloodSplatters.toArray(new int[BetterHud.bloodSplatters.size()][])) {
			glPushMatrix(); {
				glColor4f(1.0F, 1.0F, 1.0F, ((float) coords[2]) / 500.0F);
				glTranslatef(coords[0], coords[1], 0.0F);
				glRotatef(coords[6], 0.0F, 0.0F, 1.0F);
				glScalef(((float) coords[3]) / 64F, ((float) coords[3]) / 64F, 1.0F);
				mc.ingameGUI.drawTexturedModalRect(0, 0, coords[4] * 16, coords[5] * 16, 16, 16);
				if(coords[2]-- <= 0) BetterHud.bloodSplatters.remove(coords);
			}
			glPopMatrix();
		}
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}