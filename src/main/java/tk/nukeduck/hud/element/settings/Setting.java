package tk.nukeduck.hud.element.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.ISaveLoad;

public abstract class Setting extends Gui implements ISaveLoad {
	/** Returns the Gui objects which should be added to the settings screen for this setting.
	 * @param width The current screen width, in scaled pixels.
	 * @param y The current Y position of the space below the previous setting on the screen.
	 * @return The new Y position */
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		return getGuiParts(parts, callbacks, width, y, children);
	}

	public static int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y, List<Setting> settings) {
		if(!settings.isEmpty()) {
			for(Setting setting : settings) {
				int yNext = setting.getGuiParts(parts, callbacks, width, y);
				if(yNext != -1) y = yNext + SPACER;
			}
			y -= SPACER;
		}
		return y;
	}

	private Setting parent = null;
	private final List<Setting> children = new ArrayList<Setting>();

	protected void addChild(Setting child) {
		children.add(child);
		child.parent = this;
	}

	/** Renders extra parts of this GUI TODO consider drawing GUI parts here */
	public void draw() {}

	protected static final int SPACER = 5;
	
	/** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
	 * @param button The GuiButton that was pressed. */
	public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);

	/** Passed on from the element's setting screen when a key is pressed. */
	public abstract void keyTyped(char typedChar, int keyCode) throws IOException;

	/** Called when another GuiButton is pressed in the same window, but not from this setting. */
	public abstract void otherAction(Collection<Setting> settings);

	public final String name;

	public Setting(String name) {
		this.name = name;
	}

	public String getUnlocalizedName() {
		return "betterHud.setting." + name;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** @return {@code true} if this setting has a value to save */
	protected boolean shouldRegister() {return name != null;}

	/** Registers this setting for saving */
	public void register(List<Setting> settings) {
		if(shouldRegister()) {
			settings.add(this);
		}

		for(Setting child : children) {
			child.register(settings);
		}
	}

	/** @return {@code true} if this element and its ancestors are enabled */
	public boolean enabled() {
		return parent == null || parent.enabled();
	}

	// TODO integrate with forge system
	public ArrayList<String> comments = new ArrayList<String>();
}
