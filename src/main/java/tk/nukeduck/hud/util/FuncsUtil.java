package tk.nukeduck.hud.util;

import net.minecraft.item.ItemStack;

public class FuncsUtil {
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

	public static boolean isEqual(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public static double clamp(double value, double min, double max) {
		if(max < min) throw new IllegalArgumentException("Maximum must be greater than minimum");
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static float clamp(float value, float min, float max) {
		if(max < min) throw new IllegalArgumentException("Maximum must be greater than minimum");
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}
}
