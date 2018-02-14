package tk.nukeduck.hud.network;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.util.Tickable;

public class PickupHandler implements Tickable {
	public static class StackNode {
		public final ItemStack stack;
		public float opacity = 1;

		private StackNode(ItemStack stack) {
			this.stack = stack;
		}
	}

	public final Queue<StackNode> recents = new LinkedList<StackNode>();

	public void pickupItem(ItemStack stack) {
		StackNode existing = null;

		for(Iterator<StackNode> it = recents.iterator(); it.hasNext(); ) {
			StackNode node = it.next();

			if(PickupNotifier.stackEqualExact(node.stack, stack)) {
				it.remove();
				existing = node;
				break;
			}
		}

		if(existing == null) {
			existing = new StackNode(stack);
		} else {
			existing.stack.grow(stack.getCount());
			existing.opacity = 1;
		}
		recents.add(existing);
	}

	@Override
	public void tick() {
		float delta = 0.0025F + HudElement.PICKUP.fadeSpeed.get().floatValue() * 0.0225F;
		delta = 0;

		for(Iterator<StackNode> it = recents.iterator(); it.hasNext(); ) {
			if((it.next().opacity -= delta) <= 0) {
				it.remove();
			}
		}
	}
}
