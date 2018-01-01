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

public abstract class Setting implements ISaveLoad {
	protected final List<Setting> children = new ArrayList<Setting>();

	/** Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
	 * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
	 *
	 * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, int, int)})
	 * populates {@code parts} and {@code callbacks} with those of the element's children
	 *
	 * @param width The screen width
	 * @param y The topmost Y coordinate of {@code Gui}s in this setting
	 * @return The bottommost Y coordinate of {@code Gui}s in this setting */
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y) {
		return getGuiParts(parts, callbacks, width, y, children);
	}

	/** Populates {@code parts} and {@code callbacks} by calling
	 * {@link #getGuiParts(List, Map, int, int)} on the given {@code settings},
	 * and maintaining {@code y} between them
	 *
	 * @return The bottommost Y coordinate of all {@code Gui}s across all {@code settings}
	 * @see #getGuiParts(List, Map, int, int) */
	public static int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, int width, int y, List<Setting> settings) {
		if(!settings.isEmpty()) {
			for(Setting setting : settings) {
				int bottom = setting.getGuiParts(parts, callbacks, width, y); 
				if(bottom != -1) y = bottom;
			}
		}
		return y;
	}

	private Setting parent = null;

	public void add(Setting element) {
		children.add(element);
		element.parent = this;
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	/** Renders extra parts of this GUI */
	public void draw() {}

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
