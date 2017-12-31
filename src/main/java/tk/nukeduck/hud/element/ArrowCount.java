package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;

public class ArrowCount extends HudElement {
	private static final ItemStack ARROW = new ItemStack(Items.ARROW, 1);

	private final SettingBoolean overlay = new SettingBoolean("overlay");

	private final SettingPosition position = new SettingPosition("position", Direction.WEST, Direction.EAST) {
		@Override
		public boolean enabled() {
			return !overlay.get();
		}
	};

	@Override
	public void loadDefaults() {
		setEnabled(true);
		overlay.set(true);
		position.load(Direction.EAST);
	}

	public ArrowCount() {
		super("arrowCount");
		settings.add(overlay);
		settings.add(position);
	}

	@Deprecated
	private boolean isHoldingBow(EntityPlayer player) {
		return (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == Items.BOW)
			|| (player.getHeldItemOffhand()  != null && player.getHeldItemOffhand().getItem()  == Items.BOW);
	}

	/** Note this method only cares about arrows which can be shot by a vanilla bow
	 * @return The number of arrows in the player's inventory
	 * @see net.minecraft.item.ItemBow#isArrow(ItemStack) */
	private int arrowCount(EntityPlayer player) {
		int count = 0;

		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			// TODO NPE check necessary?
			if(stack != null && stack.getItem() instanceof ItemArrow) {
				count += stack.getCount();
			}
		}
		return count;
	}

	@Override
	public boolean shouldRender() {
		if(overlay.get()) {
			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() == Items.BOW) {
					return true;
				}
			}
			return false;
		} else {
			return isHoldingBow(MC.player);
		}
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		String arrowsDisplay = String.valueOf(arrowCount(MC.player));

		if(!overlay.get()) { // TODO test
			Bounds bounds = position.applyTo(new Bounds(16, 16), manager);

			MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), ARROW, bounds.x(), bounds.y());
			drawHotbarText(arrowsDisplay, bounds.x(), bounds.y());

			return bounds;
		} else { // Look through hotbar
			int center = event.getResolution().getScaledWidth() / 2;
			int y = event.getResolution().getScaledHeight() - 1;

			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() == Items.BOW) {
					drawHotbarText(arrowsDisplay, center - 71 + (i * 20), y);
				}
			}
			ItemStack stack = MC.player.inventory.getStackInSlot(40);
			if(stack != null && stack.getItem() == Items.BOW) {
				drawHotbarText(arrowsDisplay, center - 100, y);
			}
			return null;
		}
	}

	@Deprecated // TODO delet
	private void drawHotbarText(String text, int x, int y) {
		glPushMatrix();
		glTranslatef(0.0f, 0.0f, 151.0f);
		MC.ingameGUI.drawString(MC.fontRenderer, text, x - MC.fontRenderer.getStringWidth(text), y - MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		glPopMatrix();
	}
}
