package tk.nukeduck.hud.util;

import net.minecraft.item.ItemStack;

public class FuncsUtil {
	private FuncsUtil() {}

	public static int getSmallestDistance(int value, Iterable<Integer> clips) {
		int currentDistance = Integer.MAX_VALUE;
		int clip = value;
		for(int i : clips) {
			int distance = Math.abs(i - value);
			if(distance < currentDistance) {
				currentDistance = distance;
				clip = i;
			}
		}
		return clip;
	}

	/** @param array The source array to reverse
	 * @return The source array, in reverse order */
	public static <T> T[] flip(T[] array) {
		T[] output = array.clone();

		for(int i = 0, j = array.length - 1; i < array.length / 2; i++, j--) {
			output[i] = array[j];
			output[j] = array[i];
		}
		return output;
	}

	/** @see net.minecraft.entity.player.InventoryPlayer#stackEqualExact(ItemStack, ItemStack) */
	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/** Considers {@code null} to be empty
	 * @see ItemStack#isEmpty() */
	public static boolean isStackEmpty(ItemStack stack) {
		return stack == null || stack.isEmpty();
	}
}
