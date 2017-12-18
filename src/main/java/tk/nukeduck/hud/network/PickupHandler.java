package tk.nukeduck.hud.network;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.util.FuncsUtil;

public class PickupHandler {
	public ConcurrentHashMap<ItemStack, Float> pickedUp = new ConcurrentHashMap<ItemStack, Float>();

	public void pickupItem(ItemStack it) {
		ItemStack i = it.copy();
		ItemStack existing = existingItem(i);
		if(existing != null) {
			float rem = pickedUp.remove(existing);
			existing.stackSize += i.stackSize;
			pickedUp.put(existing, 1.0F);
		} else {
			pickedUp.put(i, 1.0F);
		}
	}

	public ItemStack existingItem(ItemStack item) {
		for(ItemStack stack : pickedUp.keySet()) {
			if(FuncsUtil.isEqual(stack, item)) {
				return stack;
			}
		}
		return null;
	}

	public void update(Minecraft mc) {
		if(!mc.isGamePaused()) {
			Iterator<Entry<ItemStack, Float>> it2 = pickedUp.entrySet().iterator();
			while(it2.hasNext()) {
				Entry<ItemStack, Float> entry = it2.next();

				if(entry.setValue(entry.getValue() - 0.01F) <= 0) {
					it2.remove();
				}
			}
		}
	}
}
