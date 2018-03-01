package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class ArrowCount extends HudElement {
	private static final ItemStack ARROW = new ItemStack(Items.ARROW, 1);

	private final SettingBoolean overlay = new SettingBoolean("overlay");

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS) {
		@Override
		public boolean enabled() {
			return !overlay.get();
		}
	};

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		overlay.set(true);
		position.set(Direction.SOUTH_EAST);
	}

	public ArrowCount() {
		super("arrowCount");

		settings.add(overlay);
		settings.add(position);
	}

	/** Note this method only cares about arrows which can be shot by a vanilla bow
	 * @return The number of arrows in the player's inventory
	 * @see net.minecraft.item.ItemBow#isArrow(ItemStack) */
	private int arrowCount(EntityPlayer player) {
		int count = 0;

		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if(stack != null && stack.getItem() instanceof ItemArrow) {
				count += stack.getCount();
			}
		}
		return count;
	}

	@Override
	public boolean shouldRender(RenderPhase phase) {
		if(!super.shouldRender(phase)) return false;

		if(MC.player.getHeldItemOffhand() != null && MC.player.getHeldItemOffhand().getItem() == Items.BOW) {
			return true;
		} else if(overlay.get()) {
			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);

				if(stack != null && stack.getItem() == Items.BOW) {
					return true;
				}
			}
			return false;
		} else {
			return MC.player.getHeldItemMainhand() != null && MC.player.getHeldItemMainhand().getItem() == Items.BOW;
		}
	}

	@Override
	public Bounds render(RenderPhase phase) {
		int totalArrows = arrowCount(MC.player);

		if(overlay.get()) {
			int center = MANAGER.getResolution().x / 2;
			Bounds stackBounds = new Bounds(center - 88, MANAGER.getResolution().y - 17, 16, 16);

			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);

				if(stack != null && stack.getItem() == Items.BOW) {
					drawCounter(stackBounds, totalArrows);
				}
				stackBounds.x(stackBounds.x() + 20);
			}

			ItemStack stack = MC.player.inventory.getStackInSlot(40);

			if(stack != null && stack.getItem() == Items.BOW) {
				if (MC.player.getPrimaryHand() == EnumHandSide.RIGHT) {
					stackBounds.x(center - 117);
				} else {
					stackBounds.x(center + 101);
				}
				drawCounter(stackBounds, totalArrows);
			}
			return null;
		} else {
			Bounds bounds = position.applyTo(new Bounds(16, 16));

			GlUtil.renderSingleItem(ARROW, bounds.position);
			drawCounter(bounds, totalArrows);

			return bounds;
		}
	}

	@Override
	public Bounds getLastBounds() {
		Bounds bounds = super.getLastBounds();

		if(bounds != null || overlay.get()) {
			return bounds;
		} else {
			return position.applyTo(new Bounds(16, 16));
		}
	}

	private static void drawCounter(Bounds stackBounds, int count) {
		GlStateManager.disableDepth();
		String countDisplay = String.valueOf(count);
		Bounds text = Direction.SOUTH_EAST.anchor(new Bounds(GlUtil.getStringSize(countDisplay)), stackBounds);

		MC.ingameGUI.drawString(MC.fontRenderer, countDisplay, text.x(), text.y(), Colors.WHITE);
		GlStateManager.enableDepth();
	}
}
