package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.*;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ExtraGuiElementBreedIndicator extends ExtraGuiElement {
	public ExtraGuiElementBreedIndicator() {
		name = "breedIndicator";
		modes = new String[] {"left", "right"};
	}
	
	public int getBreedingItemIndex(EntityAnimal entity) {
		Item[] items = new Item[] {Items.carrot, Items.wheat_seeds, Items.wheat};
		for(int i = 0; i < items.length; i++) {
			if(entity.isBreedingItem(new ItemStack(items[i]))) return i + 1;
		}
		return -1;
	}
	
	public void renderInfo(EntityLivingBase entity, Minecraft mc, float partialTicks) {
		if(enabled && entity instanceof EntityAnimal) {
			EntityPlayer player = mc.thePlayer;
			Tessellator t = Tessellator.getInstance();
			
			int breedItemIndex = getBreedingItemIndex((EntityAnimal) entity);
			//int s = BetterHud.bredEntities.containsKey(entity.getUniqueID()) ? BetterHud.bredEntities.get(entity.getUniqueID()) : 0;
			
			int s = 0;
			s /= 20;
			
			//s = ((EntityAnimal) entity).getEntityData().getInteger("InLove");
			
			if(breedItemIndex != -1 && s >= 0) {
				glPushMatrix(); {
					RenderUtil.billBoard(entity, player, partialTicks);
					
					String text = FormatUtil.formatTime((int) s / 60, s % 60, true);
					
					float perLine = 10;
					
					int width = 25 + BetterHud.fr.getStringWidth(text);
					int height = 20;
					
					float scale = 0.5F / width;
					
					if(currentMode().equals("right")) glTranslatef(1, 0, 0);
					
					glScalef(scale, scale, scale);
					
					glTranslatef(currentMode().equals("left") ? -width - 5 : 5, 0, 0);
					
					// Rendering starts
					
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					
					RenderUtil.renderQuad(t, 0, 0, width, height, 0, 0, 0, 0.5F);
					RenderUtil.zIncrease();
					
					glPushAttrib(GL_ALL_ATTRIB_BITS);
					GL11.glColor3f(1, 1, 1);
					mc.getTextureManager().bindTexture(ExtraGuiElementBlood.blood);
					BetterHud.itemRendererGui.drawTexturedModalRect(2, 2, breedItemIndex * 16, 64, 16, 16);
					glPopAttrib();
					
					mc.ingameGUI.drawString(BetterHud.fr, text, 22, 6, 0xffffff);
				}
				glPopMatrix();
			}
		}
	}
}