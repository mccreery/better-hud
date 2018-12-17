package jobicade.betterhud.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.network.MessagePickup;

public class PickupNotifier {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemPickedUp(EntityItemPickupEvent e) {
		if(e.isCanceled()) return;

		if(canPickup(e.getEntityPlayer(), e.getItem())) {
			ItemStack delta = getDelta(e.getEntityPlayer(), e.getItem().getItem());

			if(!delta.isEmpty()) {
				BetterHud.NET_WRAPPER.sendTo(new MessagePickup(delta), (EntityPlayerMP)e.getEntityPlayer());
			}
		}
	}

	/** @return The substack of {@code stack} that the player can pick up
	 * @see InventoryPlayer#addItemStackToInventory(ItemStack) */
	private static ItemStack getDelta(EntityPlayer player, ItemStack stack) {
		InventoryPlayer inventory = player.inventory;

		// Creative mode always picks up all items, a damaged item will only fit in an empty stack
		if(player.capabilities.isCreativeMode || stack.isItemDamaged() && inventory.getFirstEmptyStack() != -1) {
			return stack;
		}

		/* All slots before this point are full
		 * -2 means current item, -1 means offhand */
		int minSlot = -2;
		int count = 0;

		int max = getStackLimit(inventory, stack);

		while(count < stack.getCount()) { // Stack size
			int nextSlot = findMergeSlot(inventory, stack, minSlot);
			if(nextSlot == -1) nextSlot = inventory.getFirstEmptyStack();
			if(nextSlot == -1) break;

			// We have space to store more items
			count += max - inventory.getStackInSlot(nextSlot).getCount();

			if(nextSlot == inventory.currentItem) minSlot = -1; // Ignore current item
			else if(nextSlot == 40) minSlot = 0; // Ignore offhand
			else minSlot = nextSlot + 1; // Ignore 'full' slots
		}

		// The whole stack can fit
		if(count > stack.getCount()) {
			return stack;
		} else {
			ItemStack delta = stack.copy();
			delta.setCount(count);
			return delta;
		}
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

	/** @see InventoryPlayer#canMergeStacks(ItemStack, ItemStack) */
	private static int getStackLimit(IInventory inventory, ItemStack stack) {
		return Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
	}

	/** @see InventoryPlayer#canMergeStacks(ItemStack, ItemStack) */
	private static boolean canMergeStacks(InventoryPlayer inventory, ItemStack dest, ItemStack source) {
		return !dest.isEmpty() && stackEqualExact(dest, source) && dest.isStackable() && dest.getCount() < getStackLimit(inventory, dest);
	}

	/** @see InventoryPlayer#stackEqualExact(ItemStack, ItemStack) */
	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/** @return {@code true} if the player would try to add this item to their inventory
	 * @see net.minecraft.entity.item.EntityItem#onCollideWithPlayer(EntityPlayer) */
	private static boolean canPickup(EntityPlayer player, EntityItem item) {
		String owner = item.getOwner();
		return owner == null || item.lifespan - item.getAge() <= 200 || owner.equals(player.getName());
	}
}
