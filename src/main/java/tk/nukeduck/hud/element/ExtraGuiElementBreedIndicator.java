package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.*;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ExtraGuiElementBreedIndicator extends ExtraGuiElement {
	public ExtraGuiElementBreedIndicator() {
		name = "breedIndicator";
		modes = new String[] {"left", "right"};
	}
	
	public Item getBreedingItem(EntityAnimal entity) {
		for(Item i : new Item[] {Items.wheat_seeds, Items.wheat, Items.carrot}) {
			if(entity.isBreedingItem(new ItemStack(i))) return i;
		}
		return null;
	}
	
	RenderItem ri = new RenderItem();
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled && entity instanceof EntityAnimal) {
			EntityPlayer player = mc.thePlayer;
			Tessellator t = Tessellator.instance;
			
			Item breedItem = getBreedingItem((EntityAnimal) entity);
			int s = ((EntityAnimal) entity).getGrowingAge() / 20;
			if(breedItem != null && s >= 0) {
				glPushMatrix(); {
					RenderUtil.billBoard(entity, player, partialTicks);
					
					String text = s == 0 ? "0:00" : FormatUtil.formatTime((int) s / 60, s % 60);
					
					float perLine = 10;
					
					int width = 25 + mc.fontRenderer.getStringWidth(text);
					int height = 20;
					
					float scale = 0.5F / width;
					
					if(currentMode().equals("right")) glTranslatef(1, 0, 0);
					
					glScalef(scale, scale, scale);
					
					glTranslatef(currentMode().equals("left") ? -width - 5 : 5, 0, 0);
					
					// Rendering starts
					
					RenderUtil.renderQuad(t, 0, 0, width, height, 0, 0, 0, 0.5F);
					RenderUtil.zIncrease();
					
					glPushAttrib(GL_ALL_ATTRIB_BITS);
					ri.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(breedItem, 1), 2, 2);
					glPopAttrib();
					
					mc.ingameGUI.drawString(mc.fontRenderer, text, 22, 6, 0xffffff);
				}
				glPopMatrix();
			}
		}
	}
}