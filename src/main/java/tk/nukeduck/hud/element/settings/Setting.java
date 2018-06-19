package tk.nukeduck.hud.element.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.HudConfig;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;
import tk.nukeduck.hud.util.Point;

/** A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name
 *
 * @see IGetSet */
public abstract class Setting<T> implements IGetSet<T> {
	private Setting<?> parent = null;
	protected final List<Setting<?>> children = new ArrayList<Setting<?>>();
	public final String name;

	private String unlocalizedName;

	/** The config property associated with this setting */
	private Property property;

	/** Set to {@code true} to hide the setting from the GUI
	 * @see #getGuiParts(List, Map, Point) */
	private boolean hidden = false;

	public Setting(String name) {
		this.name = name;
		if(name != null) this.unlocalizedName = "betterHud.setting." + name;
	}

	public Setting<T> setHidden() {
		this.hidden = true;
		return this;
	}

	public void add(Setting<?> element) {
		children.add(element);
		element.parent = this;
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public Setting<T> setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** @return {@code true} if this setting has a value to save */
	protected boolean hasValue() {return name != null;}

	/** @return {@code true} if this element and its ancestors are enabled */
	public boolean enabled() {
		return parent == null || parent.enabled();
	}

	/** Binds properties to elements for loading and saving */
	protected final void bindConfig(HudConfig config, String category, StringBuilder path) {
		if(hasValue()) {
			property = getProperty(config, category, path.toString());
		}
		int length = path.length();

		for(Setting<?> child : children) {
			if(length > 0) path.append('.');
			path.append(child.name);

			child.bindConfig(config, category, path);

			path.setLength(length);
		}
	}

	/** Loads this setting and all its children */
	public final void loadConfig() {
		if(property != null) {
			load(property.getString());
		}

		for(Setting<?> child : children) {
			child.loadConfig();
		}
	}

	/** Saves this setting and all its children to {@code config} */
	public final void saveConfig() {
		if(property != null) {
			String save = save();

			if(property.getString() != save) {
				property.setValue(save);
			}
		}

		for(Setting<?> child : children) {
			child.saveConfig();
		}
	}

	protected Property getProperty(HudConfig config, String category, String path) {
		ConfigCategory configCategory = config.getCategory(category);
		Property property = null;
		String save = save();
	
		if(configCategory.containsKey(path)) {
			property = configCategory.get(path);
		}
	
		if(property == null || property.getType() != getPropertyType()) {
			property = new Property(path, save(), getPropertyType());
			property.setValue(save);
	
			configCategory.put(path, property);
		}
	
		property.setDefaultValue(save);
		return property;
	}

	protected Type getPropertyType() {
		return Type.STRING;
	}

	/** Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
	 * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
	 *
	 * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, Point)})
	 * populates {@code parts} and {@code callbacks} with those of the element's children
	 * @param origin TODO
	 *
	 * @return The bottommost Y coordinate of {@code Gui}s in this setting */
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin) {
		return getGuiParts(parts, callbacks, origin, children);
	}

	/** Populates {@code parts} and {@code callbacks} by calling
	 * {@link #getGuiParts(List, Map, Point)} on the given {@code settings},
	 * and maintaining {@code y} between them
	 * @param origin TODO
	 *
	 * @return The bottommost Y coordinate of all {@code Gui}s across all {@code settings}
	 * @see #getGuiParts(List, Map, Point) */
	public static Point getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Point origin, List<Setting<?>> settings) {
		if(!settings.isEmpty()) {
			for(Setting<?> setting : settings) {
				if(!setting.hidden) {
					Point bottom = setting.getGuiParts(parts, callbacks, origin);

					if(bottom != null) {
						origin = bottom;
					}
				}
			}
		}
		return origin;
	}

	/** Renders extra parts of this GUI */
	public void draw() {
		for(Setting<?> setting : children) {
			setting.draw();
		}
	}

	/** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
	 * @param button The GuiButton that was pressed. */
	public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);

	/** Updates the GUI elements based on the state of other settings.
	 * This is called when any button tied to a setting callback is pressed */
	public void updateGuiParts(Collection<Setting<?>> settings) {
		for(Setting<?> setting : children) {
			setting.updateGuiParts(settings);
		}
	}
}
