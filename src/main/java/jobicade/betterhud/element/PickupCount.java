package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.MANAGER;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.items.ItemHandlerHelper;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
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
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
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

	/**
	 * Searches for and removes an equivalent stack. Stacks are considered
	 * equivalent if their items are equivalent, ignoring max stack size.
	 *
	 * @param stack The item to search for.
	 * @return The removed item stack, if any, or {@code null}.
	 */
	private StackNode removeStack(ItemStack stack) {
		for(StackNode node : stacks) {
			if(ItemHandlerHelper.canItemStacksStack(stack, node.stack)) {
				stacks.remove(node);
				return node;
			}
		}
		return null;
	}

	/**
	 * Adds or refreshes an item in the list.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemPickupPlayer(ItemPickupEvent e) {
		ItemStack stack = e.getStack();
		if(stack.isEmpty()) return;

		refreshStack(stack);
	}

	/**
	 * Brings a stack to the front of the list of recent stacks.
	 *
	 * @param stack The stack to find and refresh.
	 */
	private synchronized void refreshStack(ItemStack stack) {
		StackNode node = removeStack(stack);

		if(node != null) {
			node.increaseStackSize(stack.getCount());
		} else {
			node = new StackNode(stack);
		}
		stacks.add(0, node);
	}

	/**
	 * Returns the list of recently picked up stacks, newest first.
	 * Expired stacks are removed, and the limit is enforced before returning.
	 *
	 * @return The list of recently picked up stacks.
	 */
	private synchronized List<StackNode> getStacks() {
		stacks.removeIf(StackNode::isDead);

		int limit = maxStacks.getInt();
		if(limit < 11 && limit < stacks.size()) {
			stacks.subList(limit, stacks.size()).clear();
		}
		return stacks;
	}

	@Override
	public Rect render(Event event) {
		List<StackNode> stacks = getStacks();
		Grid<StackNode> grid = new Grid<>(new Point(1, stacks.size()), stacks)
			.setAlignment(position.getContentAlignment())
			.setCellAlignment(position.getContentAlignment());

		Rect bounds = new Rect(grid.getPreferredSize());

		if(position.isDirection(Direction.CENTER)) {
			bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(5, 5), Direction.NORTH_WEST);
		} else {
			bounds = position.applyTo(bounds);
		}
		grid.setBounds(bounds).render();
		return bounds;
	}

	private class StackNode extends DefaultBoxed {
		private final ItemStack stack;
		private long updateCounter;

		public StackNode(ItemStack stack) {
			this.stack = stack;
			this.updateCounter = MC.ingameGUI.getUpdateCounter();
		}

		public void increaseStackSize(int size) {
			stack.setCount(stack.getCount() + size);
			this.updateCounter = MC.ingameGUI.getUpdateCounter();
		}

		private Label getLabel() {
			return new Label(stack.getCount() + " " + stack.getDisplayName())
				.setColor(Color.WHITE.withAlpha(Math.round(getOpacity() * 255)));
		}

		private float getOpacity() {
			return 1.0f - (MC.ingameGUI.getUpdateCounter() - updateCounter) / fadeAfter.get().floatValue();
		}

		private boolean isDead() {
			return getOpacity() <= 0;
		}

		@Override
		public Size negotiateSize(Point size) {
			return getLabel().getPreferredSize().withHeight(16).add(21, 0);
		}

		@Override
		public void render() {
			Direction alignment = position.getContentAlignment().withRow(1);
			GlUtil.renderSingleItem(stack, new Rect(16, 16).anchor(bounds, alignment).getPosition());

			Label label = getLabel();
			label.setBounds(new Rect(label.getPreferredSize()).anchor(bounds, alignment.mirrorCol())).render();
		}
	}
}
