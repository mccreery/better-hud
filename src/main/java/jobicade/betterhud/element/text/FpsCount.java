package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.SettingBoolean;
import net.minecraft.client.Minecraft;

public class FpsCount extends TextElement {
	private SettingBoolean numberOnly;

	public FpsCount() {
		super("fpsCount");

		settings.addChild(numberOnly = new SettingBoolean("numberOnly"));
	}

	@Override
	protected List<String> getText() {
		String fps = Minecraft.getMinecraft().debug.substring(0, Minecraft.getMinecraft().debug.indexOf(' '));

		if(!numberOnly.get()) {
			fps = getLocalizedName() + ": " + fps;
		}
		return Arrays.asList(fps);
	}
}
