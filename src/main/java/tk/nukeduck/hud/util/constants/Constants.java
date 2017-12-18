package tk.nukeduck.hud.util.constants;

import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
	public static final String MOD_ID = "hud";
	public static final String MOD_NAME = "Better HUD";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final int SPACER = 5;

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static final String CONFIG_PATH = Loader.instance().getConfigDir() + FILE_SEPARATOR + "hud.txt";
	public static final String PROPERTY_SEPARATOR = ":";
	public static final String VALUE_SEPARATOR = ",";
}
