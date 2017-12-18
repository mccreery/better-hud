package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;

public class ExtraGuiElementFps extends ExtraGuiElementText {
	private ElementSettingBoolean numberOnly;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		numberOnly.value = false;
	}
	
	@Override
	public String getName() {
		return "fpsCount";
	}
	
	public ExtraGuiElementFps() {
		super();
		this.settings.add(0, new ElementSettingDivider("position"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(numberOnly = new ElementSettingBoolean("numberOnly"));
		this.registerUpdates(UpdateSpeed.FAST);
	}
	
	String currentFps = "";
	public void update(Minecraft mc) {
		currentFps = mc.debug.split(" ")[0];
		if(!numberOnly.value) currentFps = I18n.format("betterHud.strings.fps", currentFps);
	}
	
	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		return new String[] {currentFps};
	}
}