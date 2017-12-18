package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;

public abstract class ElementSetting {
	/** Returns the Gui objects which should be added to the settings screen for this setting.
	 * @param width The current screen width, in scaled pixels.
	 * @param y The current Y position of the space below the previous setting on the screen.
	 * @return An array of Gui objects to add to the screen. */
	public abstract Gui[] getGuiParts(int width, int y);
	public int getGuiHeight() {
		return 20;
	}
	
	/** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
	 * @param button The GuiButton that was pressed. */
	public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);
	/** Passed on from the element's setting screen when a key is pressed. */
	public abstract void keyTyped(char typedChar, int keyCode) throws IOException;
	/** Called when another GuiButton is pressed in the same window, but not from this setting. */
	public abstract void otherAction(Collection<ElementSetting> settings);
	/** Renders any additional graphics to the settings screen, such as text or images.
	 * @param gui The current element setting GuiScreen. */
	public abstract void render(GuiScreen gui, int yScroll);
	/** Returns the string value of this element's value to be saved. */
	public abstract String toString();
	/** Loads this element's value from the given string.
	 * @param val The value to load, as a string. */
	public abstract void fromString(String val);
	
	private String name;
	
	public ElementSetting(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLocalizedName() {
		return I18n.format("betterHud.setting." + this.getName());
	}
	
	public boolean getEnabled() {
		return true;
	}
	
	public ArrayList<String> comments = new ArrayList<String>();
}