package tk.nukeduck.hud.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.translation.I18n;

public class FormatUtil {
	public static String formatTime(int hours, int minutes, boolean twentyFourHour) {
		if(twentyFourHour) {
			return translatePre("strings.time", String.valueOf(hours), Strings.padStart(String.valueOf(minutes), 2, '0'));
		} else {
			int h = hours % 12;
			if(h == 0) h = 12;
			return translatePre("strings.time." + (hours >= 12 ? "pm" : "am"), String.valueOf(h), Strings.padStart(String.valueOf(minutes), 2, '0'));
		}
	}
	
	public static int getLongestWidth(FontRenderer fr, ArrayList<String> strings) {
		int longest = 0;
		for(String s : strings) {
			int length = fr.getStringWidth(s);
			if(length > longest) longest = length;
		}
		return longest;
	}
	
	public static final String PARAM_CHAR = "*";
	public static final String HUD_PREFIX = "betterHud.";
	
	public static String translatePre(String path, String...params) {
		return translate(HUD_PREFIX + path, params);
	}
	
	public static String translate(String path, String... params) {
		String translation = I18n.translateToLocal(path);
		translation = translation == path ? I18n.translateToFallback(path) : translation;
		if(params.length > 0) {
			int i = 0;
			while(i < params.length && translation.contains(PARAM_CHAR)) {
				translation = translation.replaceFirst(Pattern.quote(PARAM_CHAR), params[i]);
				i++;
			}
		}
		return translation;
	}
	
	public static String separate(String... parts) {
		if(parts.length == 0) return "";
		if(parts.length == 1) return parts[0];
		return translatePre("strings.separated", parts[0], separate((String[]) Arrays.copyOfRange(parts, 1, parts.length)));
	}
}