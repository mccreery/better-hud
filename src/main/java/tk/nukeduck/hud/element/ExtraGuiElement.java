package tk.nukeduck.hud.element;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;

public class ExtraGuiElement {
	protected String name;
	protected String[] modes = new String[]{"default"};
	
	/** Only ever used for elements which will be rendering in the top left or right,
	 * to notify the string renderer how tall they are. */
	public int leftHeight = -5, rightHeight = -5;
	
	public int mode = 0;
	public boolean enabled = true;
	public int defaultMode = 0;
	
	public ExtraGuiElement() {}
	public void render(Minecraft mc, FontRenderer fr, RenderItem ri, int width, int halfWidth, int height, ArrayList<String> leftStrings, ArrayList<String> rightStrings) {}
	
	public String getName() {
		return name;
	}
	
	public int getModesSize() {
		return modes.length;
	}
	
	public String modeAt(int i) {
		return modes[i];
	}
	
	public String currentMode() {
		return modeAt(mode);
	}
}