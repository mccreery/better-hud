package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import tk.nukeduck.hud.util.Direction.Options;

public class PickupCount extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Options.X, Options.CORNERS);

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

	public final List<StackNode> stacks = new CopyOnWriteArrayList<>();

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.SOUTH_EAST);
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
	public Bounds render(Event event) {
		Bounds bounds = getBounds();
		Direction alignment = position.getContentAlignment();
		Direction rowAlignment = alignment.withRow(1);

		// The bounds to draw each item in
		Bounds stackBounds = alignment.anchor(bounds.withSize(16, 16), bounds);

		long updateCounter = MC.ingameGUI.getUpdateCounter();
		long lifetime = getLifetime();

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		int maximum = Math.min(stacks.size(), maxStacks.getInt());

		int i;
		for(i = 0; i < maximum; i++) {
			StackNode node = stacks.get(i);

			long age = updateCounter - node.updateCounter;
			// All stacks past this point are dead
			if(age >= lifetime) break;

			float opacity = (lifetime - age) / (float)lifetime;

			// Opacity lower than 4 defaults to 255
			int color = Colors.setAlpha(Colors.WHITE, Math.max(4, Math.round(opacity * 255)));

			//GlStateManager.color(1, 1, 1, opacity);
			GlUtil.renderSingleItem(node.stack, stackBounds.getPosition());
			GlUtil.drawString(node.toString(), rowAlignment.mirrorColumn().getAnchor(stackBounds.withPadding(SPACER)), rowAlignment, color);

			stackBounds = stackBounds.withY(alignment.mirrorRow().getAnchor(stackBounds.withPadding(2)).getY());
		}

		// Remove invisible stacks
		if(i != stacks.size()) {
			stacks.subList(i, stacks.size()).clear();
		}
		return bounds;
	}

	private Bounds getBounds() {
		int maximum = Math.min(stacks.size(), maxStacks.getInt());
		Bounds bounds;

		if(maximum == 0) {
			bounds = Bounds.EMPTY;
		} else {
			int width = 0;

			for(StackNode node : stacks) {
				int lineWidth = MC.fontRenderer.getStringWidth(node.toString());
	
				if(lineWidth > width) {
					width = lineWidth;
				}
			}
			bounds = new Bounds(16 + SPACER + width, (16 + 2) * stacks.size() - 2);
		}

		if(position.isDirection(Direction.CENTER)) {
			return bounds.position(Direction.CENTER, new Point(5, 5), Direction.NORTH_WEST);
		} else {
			return position.applyTo(bounds);
		}
	}

	/** @return The lifetime in ticks for each item entry */
	private long getLifetime() {
		return Math.round(MathUtil.mapToRange(HudElement.PICKUP.fadeSpeed.get().floatValue(), 400, 40));
	}

	public void pickupItem(ItemStack stack) {
		StackNode foundNode;

		int i;
		for(i = 0; i < stacks.size(); i++) {
			if(PickupNotifier.stackEqualExact(stacks.get(i).stack, stack)) {
				break;
			}
		}

		if(i != stacks.size()) {
			foundNode = stacks.remove(i);
			foundNode.stack.grow(stack.getCount());
		} else {
			foundNode = new StackNode(stack);
		}
		foundNode.updateCounter = MC.ingameGUI.getUpdateCounter();

		stacks.add(0, foundNode);
	}
}
