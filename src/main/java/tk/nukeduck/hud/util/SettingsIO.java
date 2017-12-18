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

import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.element.settings.ElementSetting;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.network.proxy.ClientProxy;
import tk.nukeduck.hud.util.constants.Constants;

public class SettingsIO {
	public static HashMap<String, String> generateKeyVal(ArrayList<String> lines) {
		HashMap<String, String> keyVal = new HashMap<String, String>();
		for(String line : lines) {
			if(!line.contains(Constants.PROPERTY_SEPARATOR)) continue;
			String[] parts = line.split(Constants.PROPERTY_SEPARATOR);
			keyVal.put(parts[0], parts[1]);
		}
		return keyVal;
	}

	public static ArrayList<String> generateSrc(ArrayList<ElementSetting> settings) {
		ArrayList<String> lines = new ArrayList<String>();
		for(ElementSetting setting : settings) {
			if(setting instanceof ElementSettingDivider || setting.getName() == "enabled") continue;
			for(String comment : setting.comments) {
				lines.add("\t# " + comment);
			}
			lines.add("\t" + setting.getName() + Constants.PROPERTY_SEPARATOR + setting.toString());
		}
		return lines;
	}

	/** Saves settings to the config file.
	 * @param logger A {@link Logger} instance for info or error messages */
	public static void saveSettings(Logger logger, ClientProxy proxy) {
		try {
			FileWriter writer = new FileWriter(Constants.CONFIG_PATH);
			StringBuilder src = new StringBuilder();

			String[] comments = {Constants.MOD_NAME + " configuration file", "Any removed or corrupted properties will be reverted to default.", new Timestamp(new Date().getTime()).toString()};
			for(String comment : comments) {
				src.append("# ").append(comment).append(Constants.LINE_SEPARATOR);
			}
			src.append(Constants.LINE_SEPARATOR);

			// Special case for global settings
			src.append(proxy.elements.globalSettings.getName()).append(Constants.PROPERTY_SEPARATOR).append(Constants.LINE_SEPARATOR);
			src.append("\tenabled").append(Constants.PROPERTY_SEPARATOR).append(String.valueOf(proxy.elements.globalSettings.enabled)).append(Constants.LINE_SEPARATOR);
			ArrayList<String> global = generateSrc(proxy.elements.globalSettings.settings);
			for(String line : global) {
				src.append(line).append(Constants.LINE_SEPARATOR);
			}

			for(ExtraGuiElement element : proxy.elements.elements) {
				src.append(element.getName()).append(Constants.PROPERTY_SEPARATOR).append(Constants.LINE_SEPARATOR);
				src.append("\tenabled").append(Constants.PROPERTY_SEPARATOR).append(String.valueOf(element.enabled)).append(Constants.LINE_SEPARATOR);
				ArrayList<String> s = generateSrc(element.settings);
				for(String line : s) {
					src.append(line).append(Constants.LINE_SEPARATOR);
				}
			}

			writer.write(src.toString());
			writer.close();

			logger.log(Level.INFO, "Settings have been saved at " + Constants.CONFIG_PATH);
		} catch(IOException e) {
			logger.log(Level.WARN, "Failed to save settings to " + Constants.CONFIG_PATH + "." + Constants.LINE_SEPARATOR
				+ e.getMessage());
		}
	}

	/** Loads settings from the config file.
	 * @param logger A {@link Logger} instance for info or error messages */
	public static void loadSettings(Logger logger, ClientProxy proxy) {
		try {
			logger.log(Level.INFO, "Loading HUD settings from " + Constants.CONFIG_PATH);

			String currentName = null;
			ArrayList<String> valueLines = new ArrayList<String>();
			HashMap<String, ArrayList<String>> namedSections = new HashMap<String, ArrayList<String>>();

			FileReader reader = new FileReader(Constants.CONFIG_PATH);
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

			for(ExtraGuiElement element : proxy.elements.elements) {
				if(!namedSections.containsKey(element.getName())) continue;
				try {
					element.loadSettings(generateKeyVal(namedSections.get(element.getName())));
				} catch(Exception e) {}
			}
			// Special case for global settings
			if(namedSections.containsKey(proxy.elements.globalSettings.getName())) {
				try {
					proxy.elements.globalSettings.loadSettings(generateKeyVal(namedSections.get(proxy.elements.globalSettings.getName())));
				} catch(Exception e) {}
			}

			buffer.close();
			reader.close();
		} catch(IOException e) {
			saveSettings(logger, proxy);
			logger.log(Level.WARN, "Failed to load settings from " + Constants.CONFIG_PATH
				+ "." + Constants.LINE_SEPARATOR + e.getMessage() + Constants.LINE_SEPARATOR
				+ "The default configuration was saved.");
		}
	}
}
