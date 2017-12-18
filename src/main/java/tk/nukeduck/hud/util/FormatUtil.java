package tk.nukeduck.hud.util;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;

public class FormatUtil {
	public static String formatTime(int minutes, int seconds) {
		String secondsString = String.valueOf(seconds);
		return String.valueOf(minutes) + ":" + (secondsString.length() < 2 ? "0" + secondsString : secondsString);
	}
	
	public static int getLongestWidth(FontRenderer fr, ArrayList<String> strings) {
		int longest = 0;
		for(String s : strings) {
			int length = fr.getStringWidth(s);
			if(length > longest) longest = length;
		}
		return longest;
	}
}