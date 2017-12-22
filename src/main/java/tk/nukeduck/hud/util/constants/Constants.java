package tk.nukeduck.hud.util.constants;

import net.minecraftforge.fml.common.Loader;

public class Constants {
	public static final int SPACER = 5;

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String CONFIG_PATH = Loader.instance().getConfigDir() + FILE_SEPARATOR + "hud.txt";
	public static final String PROPERTY_SEPARATOR = ":";
	public static final String VALUE_SEPARATOR = ",";
}
