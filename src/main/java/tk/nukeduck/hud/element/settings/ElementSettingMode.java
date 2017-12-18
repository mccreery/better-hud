package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.lang3.ArrayUtils;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.RenderUtil;

public class ElementSettingMode extends ElementSetting {
	protected GuiButton last, next, backing;
	
	public ElementSettingMode(String name, String[] modes) {
		super(name);
		this.modes = modes;
	}
	
	public String[] modes;
	public int index = 0;
	
	public int last() {
		int i = (index - 1) % modes.length;
		if(i < 0) i += modes.length;
		set(i);
		return index;
	}
	public int next() {set((index + 1) % modes.length); return index;}
	public void set(int index) {
		this.index = index;
	}
	public void set(String value) {
		set(ArrayUtils.indexOf(this.modes, value));
	}
	
	public String getValue() {
		return index < 0 || index >= modes.length ? String.valueOf(index) : this.modes[index];
	}
	
	@Override
	public Gui[] getGuiParts(int width, int y) {
		last = new GuiButton(0, width / 2 - 100, y, 20, 20, "<");
		next = new GuiButton(1, width / 2 + 80, y, 20, 20, ">");
		backing = new GuiButton(2, width / 2 - 100, y, 200, 20, "");
		backing.enabled = false;
		return new Gui[] {backing, last, next};
	}
	
	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		if(button.id == 0) last();
		else next();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {}
	
	@Override
	public void render(GuiScreen gui, int yScroll) {
		int color = modes.length > 1 ? RenderUtil.colorRGB(255, 255, 255) : RenderUtil.colorRGB(85, 85, 85);
		gui.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, FormatUtil.translatePre("setting." + getValue()), last.xPosition + backing.width / 2, last.yPosition + 6, color);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.index);
	}
	
	@Override
	public void fromString(String val) {
		try {
			this.index = Integer.parseInt(val);
		} catch(NumberFormatException e) {}
	}
	@Override
	public void otherAction(Collection<ElementSetting> settings) {
		boolean enabled = this.getEnabled();
		last.enabled = enabled;
		next.enabled = enabled;
	}
}