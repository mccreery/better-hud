package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;
import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.PickupNotifier;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.GlUtil;

public class PickupCount extends HudElement {
	private SettingSlider maxStacks, fadeAfter;
	public final List<StackNode> stacks = new CopyOnWriteArrayList<>();

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.SOUTH_EAST);
		fadeAfter.set(.5);
		maxStacks.set(11);
	}

	public PickupCount() {
		super("itemPickup", new SettingPosition(DirectionOptions.X, DirectionOptions.CORNERS));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(fadeAfter = new SettingSlider("fadeAfter", 20, 600, 20).setDisplayScale(0.05).setUnlocalizedValue("betterHud.hud.seconds"));
		settings.add(maxStacks = new SettingSlider("maxStacks", 1, 11, 1) {
			@Override
			public String getDisplayValue(double scaledValue) {
				return scaledValue == getMaximum() ? I18n.format("betterHud.value.unlimited") : super.getDisplayValue(scaledValue);
			}
		});
	}

	@Override
	public VersionRange getServerDependency() throws InvalidVersionSpecificationException {
		return VersionRange.createFromVersionSpec("[1.4-beta,)");
	}

	@Override
	public Rect render(Event event) {
		Rect bounds = getRect();
		Direction alignment = position.getContentAlignment();
		Direction rowAlignment = alignment.withRow(1);

		// The bounds to draw each item in
		Rect stackRect = bounds.resize(16, 16).anchor(bounds, alignment);

		long updateCounter = MC.ingameGUI.getUpdateCounter();
		long lifetime = fadeAfter.getInt();

		int maximum = maxStacks.getInt() <= 10 ? Math.min(stacks.size(), maxStacks.getInt()) : stacks.size();

		int i;
		for(i = 0; i < maximum; i++) {
			StackNode node = stacks.get(i);

			long age = updateCounter - node.updateCounter;
			// All stacks past this point are dead
			if(age >= lifetime) break;

			float opacity = (lifetime - age) / (float)lifetime;

			// Opacity lower than 4 defaults to 255
			Color color = Color.WHITE.withAlpha(Math.max(4, Math.round(opacity * 255)));

			GlUtil.renderSingleItem(node.stack, stackRect.getPosition());
			GlUtil.drawString(node.toString(), stackRect.grow(SPACER).getAnchor(rowAlignment.mirrorCol()), rowAlignment, color);

			stackRect = stackRect.align(stackRect.grow(2).getAnchor(alignment.mirrorRow()), alignment);
		}

		// Remove invisible stacks
		if(i != stacks.size()) {
			stacks.subList(i, stacks.size()).clear();
		}
		return bounds;
	}

	private Rect getRect() {
		int maximum = Math.min(stacks.size(), maxStacks.getInt());
		Rect bounds;

		if(maximum == 0) {
			bounds = Rect.empty();
		} else {
			int width = 0;

			for(StackNode node : stacks) {
				int lineWidth = MC.fontRenderer.getStringWidth(node.toString());

				if(lineWidth > width) {
					width = lineWidth;
				}
			}
			bounds = new Rect(16 + SPACER + width, (16 + 2) * stacks.size() - 2);
		}

		if(position.isDirection(Direction.CENTER)) {
			return bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(5, 5), Direction.NORTH_WEST);
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
}
