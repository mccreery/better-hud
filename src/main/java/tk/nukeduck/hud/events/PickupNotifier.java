package tk.nukeduck.hud.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.network.MessagePickup;
import tk.nukeduck.hud.util.FuncsUtil;

public class PickupNotifier {
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onItemPickedUp(EntityItemPickupEvent e) {
		int count = pickupCount(e.getEntityPlayer(), e.getItem());
		if(count != 0) {
			ItemStack deltaStack = e.getItem().getItem().copy();
			deltaStack.setCount(count);

			BetterHud.netWrapper.sendTo(new MessagePickup(deltaStack), (EntityPlayerMP)e.getEntityPlayer());
		}
	}

	/** Calculates how many items would be picked up from the given item
	 * @param player The player to search for free space
	 * @param item The stack to add */
	public static int pickupCount(EntityPlayer player, EntityItem item) {
		InventoryPlayer inventory = player.inventory;
		ItemStack stack = item.getItem();

		if(item.getOwner() == null && !stack.isEmpty()) { // Is empty
			// Creative mode always picks up all items, a damaged item will only fit in an empty stack
			if(player.capabilities.isCreativeMode || stack.isItemDamaged() && inventory.getFirstEmptyStack() != -1) {
				return stack.getCount();
			} else {
				int count = 0;
				int minSlot = -2; // All slots before this point are confirmed full
				// -2 is code for the current item, -1 is code for offhand

				int stackMax = Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit());

				while(count < stack.getCount()) { // Stack size
					int nextSlot = findMergeSlot(inventory, stack, minSlot);
					if(nextSlot == -1) nextSlot = inventory.getFirstEmptyStack();
					if(nextSlot == -1) break;

					// We have space to store more items
					count += stackMax - inventory.getStackInSlot(nextSlot).getCount();

					if(nextSlot == inventory.currentItem) minSlot = -1; // Ignore current item
					else if(nextSlot == 40) minSlot = 0; // Ignore offhand
					else minSlot = nextSlot + 1; // Ignore 'full' slots
				}
				// Make sure not to report more items than we have
				return count > stack.getCount() ? stack.getCount() : count;
			}
		}
		return 0;
	}

    /** checks if the stack can be merged into a non-empty slot
     * @return The first valid slot to be merged into */
    private static int findMergeSlot(InventoryPlayer inventory, ItemStack stack, int start) {
    	/* The currently held and offhand slots are ignored using indices greater than -2 or -1
    	 * (if these slots are checked, the other slots will also be checked) */
    	if(start <= -2 && canMergeStacks(inventory, inventory.getStackInSlot(inventory.currentItem), stack)) return inventory.currentItem;
    	if(start <= -1 && canMergeStacks(inventory, inventory.getStackInSlot(40), stack)) return 40;
    	
    	if(start < 0) start = 0;
    	for(int i = start; i < 36; i++) {
    		// Must ignore current item here
    		if(i != inventory.currentItem && canMergeStacks(inventory, inventory.getStackInSlot(i), stack)) return i;
    	}
    	return -1;
    }

    private static boolean canMergeStacks(InventoryPlayer inventory, ItemStack stack1, ItemStack stack2) {
        return !stack1.isEmpty() && FuncsUtil.isEqual(stack1, stack2) &&
        	stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() &&
        	stack1.getCount() < inventory.getInventoryStackLimit();
    }
}
