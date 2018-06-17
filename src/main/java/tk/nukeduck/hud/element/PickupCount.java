package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;

public class PickupCount extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.CENTER.flag());

	private final SettingSlider maxStacks = new SettingSlider("maxStacks", 1, 11, 1) {
		@Override
		public String getDisplayValue(double scaledValue) {
			return scaledValue == getMaximum() ? I18n.format("betterHud.value.unlimited") : super.getDisplayValue(scaledValue);
		}
	};

	public final SettingSlider fadeSpeed = new SettingPercentage("speed", 0, 1) {
		@Override
		public String getDisplayValue(double value) {
			if(value == 0) {
				return I18n.format("betterHud.value.slowest");
			} else if(value == 1) {
				return I18n.format("betterHud.value.fastest");
			} else {
				return super.getDisplayValue(value);
			}
		}
	};

	public static class StackNode {
		public final ItemStack stack;
		public long updateCounter;

		private StackNode(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public String toString() {
			return String.format("%dx %s", stack.getCount(), stack.getDisplayName());
		}
	}

	public final LinkedList<StackNode> stacks = new LinkedList<>();

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.SOUTH_EAST);
		fadeSpeed.set(.5);
		maxStacks.set(11);
	}

	public PickupCount() {
		super("itemPickup");

		settings.add(position);
		settings.add(fadeSpeed);
		settings.add(maxStacks);
	}

	@Override
	public Version getMinimumServerVersion() {
		return new Version(1, 3, 9);
	}

	@Override
	public synchronized Bounds render(Event event) {
		Bounds bounds = new Bounds(64, 16);

		if(position.getDirection() == Direction.CENTER) {
			bounds = bounds.position(Direction.CENTER, new Point(5, 5), Direction.NORTH_WEST);
		} else {
			bounds = position.applyTo(bounds);
		}

		long updateCounter = MC.ingameGUI.getUpdateCounter();
		long lifetime = getLifetime();

		Bounds lineBounds = bounds.withSize(16, 16);
		Iterator<StackNode> iterator = stacks.iterator();
		StackNode node;

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		int maximum = maxStacks.getInt();
		for(int i = 0; iterator.hasNext() && i < maximum; i++) {
			node = iterator.next();

			long age = updateCounter - node.updateCounter;
			// All stacks past this point are dead
			if(age >= lifetime) {
				iterator.remove();
				break;
			}

			float opacity = (lifetime - age) / (float)lifetime;
			int color = Colors.setAlpha(Colors.WHITE, Math.max(4, Math.round(opacity * 255))); // Opacity lower than 4 defaults to 255

			//GlStateManager.color(1, 1, 1, opacity);
			GlUtil.renderSingleItem(node.stack, lineBounds.getPosition());

			GlUtil.drawString(node.toString(), Direction.EAST.getAnchor(lineBounds.withPadding(SPACER)), Direction.WEST, color);
			lineBounds = lineBounds.withY(lineBounds.getBottom() + SPACER);
		}

		// Remove invisible stacks
		while(iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		return bounds;
	}

	/** @return The lifetime in ticks for each item entry */
	private long getLifetime() {
		return Math.round(MathUtil.mapToRange(HudElement.PICKUP.fadeSpeed.get().floatValue(), 400, 40));
	}

	public synchronized void pickupItem(ItemStack stack) {
		Iterator<StackNode> iterator = stacks.iterator();
		StackNode foundNode = null;

		while(iterator.hasNext()) {
			StackNode testNode = iterator.next();

			if(PickupNotifier.stackEqualExact(testNode.stack, stack)) {
				iterator.remove();
				foundNode = testNode;
				break;
			}
		}

		if(foundNode == null) {
			foundNode = new StackNode(stack);
		} else {
			foundNode.stack.grow(stack.getCount());
		}
		foundNode.updateCounter = MC.ingameGUI.getUpdateCounter();

		stacks.offerFirst(foundNode);
	}
}
