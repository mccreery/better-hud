package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class PickupCount extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Options.X, Options.CORNERS);

	private final SettingSlider maxStacks = new SettingSlider("maxStacks", 1, 11, 1) {
		@Override
		public String getDisplayValue(double scaledValue) {
			return scaledValue == getMaximum() ? I18n.format("betterHud.value.unlimited") : super.getDisplayValue(scaledValue);
		}
	};

	public final SettingSlider fadeAfter = new SettingSlider("fadeAfter", 20, 600, 20).setDisplayScale(0.05).setUnlocalizedValue("betterHud.hud.seconds");

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
		fadeAfter.set(.5);
		maxStacks.set(11);
	}

	public PickupCount() {
		super("itemPickup");

		settings.add(position);
		settings.add(fadeAfter);
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
		Bounds stackBounds = bounds.withSize(16, 16).anchor(bounds, alignment);

		long updateCounter = MC.ingameGUI.getUpdateCounter();
		long lifetime = fadeAfter.getInt();

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		int maximum = maxStacks.getInt() <= 10 ? Math.min(stacks.size(), maxStacks.getInt()) : stacks.size();

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
			GlUtil.drawString(node.toString(), stackBounds.grow(SPACER).getAnchor(rowAlignment.mirrorColumn()), rowAlignment, color);

			stackBounds = stackBounds.align(stackBounds.grow(2).getAnchor(alignment.mirrorRow()), alignment);
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
			return bounds.positioned(Direction.CENTER, new Point(5, 5), Direction.NORTH_WEST);
		} else {
			return position.applyTo(bounds);
		}
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
