package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class ArrowCount extends HudElement {
	private static final ItemStack ARROW = new ItemStack(Items.ARROW, 1);

	private final SettingBoolean overlay = new SettingBoolean("overlay");

	private final SettingPosition position = new SettingPosition("position", Options.CORNERS, Options.NONE) {
		@Override
		public boolean enabled() {
			return !overlay.get();
		}
	};

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		overlay.set(true);
		position.setPreset(Direction.SOUTH_EAST);
		settings.priority.set(1);
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
	public boolean shouldRender(Event event) {
		if(!super.shouldRender(event)) return false;

		ItemStack stack = MC.player.getHeldItemOffhand();
		boolean offhandHeld = stack != null && stack.getItem() == Items.BOW;

		if(overlay.get()) {
			if(HudElement.OFFHAND.isEnabled() && offhandHeld) {
				return true;
			}

			if(HudElement.HOTBAR.isEnabled()) {
				for(int i = 0; i < 9; i++) {
					stack = MC.player.inventory.getStackInSlot(i);

					if(stack != null && stack.getItem() == Items.BOW) {
						return true;
					}
				}
			}
			return false;
		} else if(offhandHeld) {
			return true;
		} else {
			stack = MC.player.getHeldItemMainhand();
			return stack != null && stack.getItem() == Items.BOW;
		}
	}

	@Override
	public Bounds render(Event event) {
		int totalArrows = arrowCount(MC.player);

		if(overlay.get()) {
			Bounds stackBounds = Direction.WEST.anchor(new Bounds(16, 16), HOTBAR.getLastBounds().withInset(3));

			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);

				if(stack != null && stack.getItem() == Items.BOW) {
					drawCounter(stackBounds, totalArrows);
				}
				stackBounds = stackBounds.withX(stackBounds.getX() + 20);
			}

			ItemStack stack = MC.player.inventory.getStackInSlot(40);

			if(stack != null && stack.getItem() == Items.BOW) {
				drawCounter(new Bounds(OFFHAND.getLastBounds().getPosition().add(3, 3), new Point(16, 16)), totalArrows);
			}
			return Bounds.EMPTY;
		} else {
			Bounds bounds = position.applyTo(new Bounds(16, 16));

			GlUtil.renderSingleItem(ARROW, bounds.getPosition());
			drawCounter(bounds, totalArrows);

			return bounds;
		}
	}

	@Override
	public Bounds getLastBounds() {
		Bounds bounds = super.getLastBounds();

		if(!overlay.get() && bounds.equals(Bounds.EMPTY)) {
			bounds = position.applyTo(new Bounds(16, 16));
		}
		return bounds;
	}

	private static void drawCounter(Bounds stackBounds, int count) {
		GlStateManager.disableDepth();
		String countDisplay = String.valueOf(count);

		Bounds text = Direction.NORTH_EAST.align(new Bounds(GlUtil.getStringSize(countDisplay)), Direction.NORTH_EAST.getAnchor(stackBounds.withPadding(1, 1, 1, 2)));

		MC.ingameGUI.drawString(MC.fontRenderer, countDisplay, text.getX(), text.getY(), Colors.WHITE);
		GlStateManager.enableDepth();
	}
}
