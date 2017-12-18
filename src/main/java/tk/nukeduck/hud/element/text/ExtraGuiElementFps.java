package tk.nukeduck.hud.element.text;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import scala.actors.threadpool.Arrays;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

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
		if(!numberOnly.value) currentFps = FormatUtil.translatePre("strings.fps", currentFps);
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