package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Setting;
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
	private SettingBoolean overlay;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		overlay.set(true);
		position.setPreset(Direction.SOUTH_EAST);
		settings.priority.set(1);
	}

	public ArrowCount() {
		super("arrowCount", new SettingPosition(Options.CORNERS, Options.NONE));
		position.setEnableOn(() -> !overlay.get());
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(overlay = new SettingBoolean("overlay"));
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
			if(HudElement.OFFHAND.isEnabledAndSupported() && offhandHeld) {
				return true;
			}

			if(HudElement.HOTBAR.isEnabledAndSupported()) {
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
			Bounds stackBounds = new Bounds(16, 16).anchor(HOTBAR.getLastBounds().grow(-3), Direction.WEST);

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

	private static void drawCounter(Bounds stackBounds, int count) {
		String countDisplay = String.valueOf(count);

		Bounds text = new Bounds(GlUtil.getStringSize(countDisplay)).align(stackBounds.grow(1, 1, 1, 2).getAnchor(Direction.NORTH_EAST), Direction.NORTH_EAST);

		MC.ingameGUI.drawString(MC.fontRenderer, countDisplay, text.getX(), text.getY(), Colors.WHITE);
	}
}
