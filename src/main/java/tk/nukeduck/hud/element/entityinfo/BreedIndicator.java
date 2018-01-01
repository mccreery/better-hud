package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.SettingDirection;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.RenderUtil;

public class BreedIndicator extends EntityInfo {
	private final SettingDirection pos;

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.set(true);
		pos.value = Direction.WEST;
		distance.value = 100;
	}

	public BreedIndicator() {
		super("breedIndicator");
		settings.add(pos = new SettingDirection("side", Direction.WEST, Direction.EAST));
	}

	public int getBreedingItemIndex(EntityAnimal entity) {
		Item[] items = new Item[] {Items.CARROT, Items.WHEAT_SEEDS, Items.WHEAT};
		for(int i = 0; i < items.length; i++) {
			if(entity.isBreedingItem(new ItemStack(items[i]))) return i + 1;
		}
		return -1;
	}

	public void renderInfo(EntityLivingBase entity, float partialTicks) {
		if(entity instanceof EntityAnimal) {
			EntityPlayer player = MC.player;

			int breedItemIndex = getBreedingItemIndex((EntityAnimal) entity);
			//int s = BetterHud.bredEntities.containsKey(entity.getUniqueID()) ? BetterHud.bredEntities.get(entity.getUniqueID()) : 0;

			if(breedItemIndex != -1 && ((EntityAnimal) entity).getGrowingAge() >= 0) {
				glPushMatrix(); {
					RenderUtil.billBoard(entity, player, partialTicks);

					//Integer love = BetterHud.breedNotifier.loveMap.get(entity.getEntityId());
					//if(love == null) love = 0;

					// TODO find a better way
					//boolean love = ((EntityAnimal) entity).isInLove();
					//String text = love ? ChatFormatting.RED + "\u2718"/* + ChatFormatting.RESET + FormatUtil.formatTime(love / 60, love % 60, true)*/ : ChatFormatting.GREEN + "\u2714";
					//text = String.valueOf(love);
					//String text = new ItemStack(new Item[] {Items.carrot, Items.wheat_seeds, Items.wheat}[breedItemIndex - 1]).getDisplayName();

					//String text = FormatUtil.formatTime((int) s / 60, s % 60, true);

					//float perLine = 10;

					int width = 20;//25 + mc.fontRendererObj.getStringWidth(text);
					int height = 20;

					if(pos.value == Direction.EAST) glTranslatef(1, 0, 0);

					float scale = 0.0125F;//0.25F / width;
					glScalef(scale, scale, scale);

					glTranslatef(pos.value == Direction.WEST ? -width - 5 : 5, 0, 0);

					// Rendering starts

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					Gui.drawRect(0, 0, width, height, Colors.TRANSLUCENT);
					zIncrease();

					glPushAttrib(GL_ALL_ATTRIB_BITS);
					GL11.glColor3f(1, 1, 1);
					MC.getTextureManager().bindTexture(HUD_ICONS);
					RenderUtil.renderQuadWithUV(Tessellator.getInstance(), 2, 2, breedItemIndex * 16 / 256F, 64 / 256F, 16, 16);
					glPopAttrib();

					//mc.ingameGUI.drawString(mc.fontRendererObj, text, 22, 6, RenderUtil.colorRGB(255, 255, 255));
				}
				glPopMatrix();
			}
		}
	}
}
