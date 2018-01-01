package tk.nukeduck.hud.network;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.events.PickupNotifier;
import tk.nukeduck.hud.util.Tickable;

public class PickupHandler implements Tickable {
	// TODO replace map with better data structure
	public ConcurrentHashMap<ItemStack, Float> pickedUp = new ConcurrentHashMap<ItemStack, Float>();

	public void pickupItem(ItemStack it) {
		ItemStack i = it.copy();
		ItemStack existing = existingItem(i);
		if(existing != null) {
			pickedUp.remove(existing);
			existing.setCount(existing.getCount() + i.getCount());
			pickedUp.put(existing, 1.0F);
		} else {
			pickedUp.put(i, 1.0F);
		}
	}

	public ItemStack existingItem(ItemStack stack) {
		for(ItemStack existing : pickedUp.keySet()) {
			if(PickupNotifier.stackEqualExact(stack, existing)) {
				return stack;
			}
		}
		return null;
	}

	@Override
	public void tick() {
		if(!MC.isGamePaused()) {
			Iterator<Entry<ItemStack, Float>> it2 = pickedUp.entrySet().iterator();
			float delta = 0.0025F + (float)HudElement.PICKUP.fadeSpeed.value * 0.0225F;

			while(it2.hasNext()) {
				Entry<ItemStack, Float> entry = it2.next();

				if(entry.setValue(entry.getValue() - delta) <= 0) {
					it2.remove();
				}
			}
		}
	}
}
