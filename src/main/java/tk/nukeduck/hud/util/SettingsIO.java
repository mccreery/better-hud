package tk.nukeduck.hud.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Loader;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.Setting;

public class SettingsIO {
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String CONFIG_PATH = Loader.instance().getConfigDir() + FILE_SEPARATOR + "hud.txt";

	public static HashMap<String, String> generateKeyVal(ArrayList<String> lines) {
		HashMap<String, String> keyVal = new HashMap<String, String>();
		for(String line : lines) {
			int colon = line.indexOf(':');
			if(colon < 0) continue;

			keyVal.put(line.substring(0, colon), line.substring(colon + 1));
		}
		return keyVal;
	}

	public static ArrayList<String> generateSrc(ArrayList<Setting<?>> settings) {
		ArrayList<String> lines = new ArrayList<String>();
		for(Setting<?> setting : settings) {
			if(setting instanceof Legend || setting.name == "enabled") continue;
			for(String comment : setting.comments) {
				lines.add("\t# " + comment);
			}
			lines.add("\t" + setting.name + ":" + setting.toString());
		}
		return lines;
	}

	/** Saves settings to the config file.
	 * @param logger A {@link Logger} instance for info or error messages */
	public static void saveSettings(Logger logger) {
		try {
			FileWriter writer = new FileWriter(CONFIG_PATH);
			StringBuilder src = new StringBuilder();

			String[] comments = {BetterHud.MODID, new Timestamp(new Date().getTime()).toString()};
			for(String comment : comments) {
				src.append("# ").append(comment).append(LINE_SEPARATOR);
			}
			src.append(LINE_SEPARATOR);

			// Special case for global settings
			/*src.append(HudElement.GLOBAL.name).append(":").append(LINE_SEPARATOR);
			src.append("\tenabled").append(":").append(String.valueOf(BetterHud.isEnabled())).append(LINE_SEPARATOR);
			ArrayList<String> global = generateSrc(HudElement.GLOBAL.settings);
			for(String line : global) {
				src.append(line).append(LINE_SEPARATOR);
			}*/

			/*for(HudElement element : HudElement.ELEMENTS) { TODO
				src.append(element.name).append(":").append(LINE_SEPARATOR);
				src.append("\tenabled").append(":").append(String.valueOf(element.isEnabled())).append(LINE_SEPARATOR);
				ArrayList<String> s = generateSrc(element.settings);
				for(String line : s) {
					src.append(line).append(LINE_SEPARATOR);
				}
			}*/

			writer.write(src.toString());
			writer.close();

			logger.log(Level.INFO, "Settings have been saved at " + CONFIG_PATH);
		} catch(IOException e) {
			logger.log(Level.WARN, "Failed to save settings to " + CONFIG_PATH + "." + LINE_SEPARATOR + e.getMessage());
		}
	}

	/** Loads settings from the config file.
	 * @param logger A {@link Logger} instance for info or error messages */
	public static void loadSettings(Logger logger) {
		try {
			logger.log(Level.INFO, "Loading HUD settings from " + CONFIG_PATH);

			String currentName = null;
			ArrayList<String> valueLines = new ArrayList<String>();
			HashMap<String, ArrayList<String>> namedSections = new HashMap<String, ArrayList<String>>();

			FileReader reader = new FileReader(CONFIG_PATH);
			BufferedReader buffer = new BufferedReader(reader);

			String line;
			while((line = buffer.readLine()) != null) {
				if(line.trim().startsWith("#") || line.isEmpty()) continue;

				if(line.startsWith("\t")) {
					valueLines.add(line.substring(1));
				} else {
					if(currentName != null) namedSections.put(currentName, valueLines);
					valueLines = new ArrayList<String>();
					currentName = line.substring(0, line.length() - 1);
				}
			}
			namedSections.put(currentName, valueLines);

			for(HudElement element : HudElement.ELEMENTS) {
				if(!namedSections.containsKey(element.name)) continue;
				try {
					element.loadSettings(generateKeyVal(namedSections.get(element.name)));
				} catch(Exception e) {}
			}
			// Special case for global settings
			if(namedSections.containsKey(HudElement.GLOBAL.name)) {
				try {
					HudElement.GLOBAL.loadSettings(generateKeyVal(namedSections.get(HudElement.GLOBAL.name)));
				} catch(Exception e) {}
			}

			buffer.close();
			reader.close();
		} catch(IOException e) {
			saveSettings(logger);
			logger.log(Level.WARN, "Failed to load settings from " + CONFIG_PATH
				+ "." + LINE_SEPARATOR + e.getMessage() + LINE_SEPARATOR
				+ "The default configuration was saved.");
		}
	}
}
