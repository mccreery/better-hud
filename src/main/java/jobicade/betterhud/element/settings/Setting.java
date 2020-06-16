package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/** A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name */
public abstract class Setting implements ISetting {
	private Setting parent = null;
	protected final List<Setting> children = new ArrayList<>();
	public final String name;

	private String unlocalizedName;

	/** Set to {@code true} to hide the setting from the GUI
	 * @see #getGuiParts(List, Map, Point) */
	private boolean hidden = false;

	private BooleanSupplier enableOn = () -> true;

	public Setting(String name) {
		this.name = name;
		if(name != null) this.unlocalizedName = "betterHud.setting." + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Iterable<? extends ISetting> getChildren() {
		return children;
	}

	public Setting setEnableOn(BooleanSupplier enableOn) {
		this.enableOn = enableOn;
		return this;
	}

	public Setting setHidden() {
		this.hidden = true;
		return this;
	}

	public final void addChild(Setting setting) {
		children.add(setting);
		setting.parent = this;
	}

	public final void addChildren(Iterable<Setting> settings) {
		for (Setting setting : settings) {
			addChild(setting);
		}
	}

	@SafeVarargs
	public final void addChildren(Setting... settings) {
		for (Setting setting : settings) {
			addChild(setting);
		}
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public Setting setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** @return {@code true} if this element and its ancestors are enabled */
	public final boolean enabled() {
		return (parent == null || parent.enabled()) && enableOn.getAsBoolean();
	}

	/** Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
	 * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
	 *
	 * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, Point)})
	 * populates {@code parts} and {@code callbacks} with those of the element's children
	 *
	 * @param origin The top center point for GUI parts being added
	 * @return The new origin directly below this setting's parts */
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
		return getGuiParts(parts, callbacks, origin, children);
	}

	/** Populates {@code parts} and {@code callbacks} by calling
	 * {@link #getGuiParts(List, Map, Point)} on the given {@code settings},
	 * and maintaining {@code y} between them
	 *
	 * @param origin The top center point for GUI parts being added
	 * @return The bottom center point of all {@code settings}
	 * @see #getGuiParts(List, Map, Point) */
	public static Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin, List<Setting> settings) {
		if(!settings.isEmpty()) {
			for(Setting setting : settings) {
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
		for(Setting setting : children) {
			setting.draw();
		}
	}

	// TODO why here?
	/** Passed on from the element's setting screen when a GuiButton for this setting is pressed.
	 * @param button The GuiButton that was pressed. */
	public abstract void actionPerformed(GuiElementSettings gui, GuiButton button);

	/** Updates the GUI elements based on the state of other settings.
	 * This is called when any button tied to a setting callback is pressed */
	public void updateGuiParts(Collection<Setting> settings) {
		for(Setting setting : children) {
			setting.updateGuiParts(settings);
		}
	}
}
