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

/**
 * A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name.
 *
 * <p>Settings use a variant of the "Curiously Recurring Template Pattern"
 * (CRTP), which is originally a C++ patttern. In this case a similar idea is
 * used to support a fluent interface that doesn't lose specificity after each
 * method. Without this, chaining {@code .setA(5).setB(6)} fails if {@code setA}
 * is a method of the superclass and {@code setB} is a method of the subclass,
 * and the final return value has to be casted unless no superclass methods are
 * called.
 *
 * <p>One side effect of the pattern is that all the fluent interface methods
 * return the first concrete class in the heirarchy (which specifies {@code T}).
 * Further subclasses should be avoided (e.g. mark final) or fall back on
 * casting.
 */
public abstract class Setting {
	private final String name;
	private Setting parent = null;
	protected final List<Setting> children = new ArrayList<>();

	/** Set to {@code true} to hide the setting from the GUI
	 * @see #getGuiParts(List, Map, Point) */
	private boolean hidden = false;

	private BooleanSupplier enableOn = () -> true;

	public Setting(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Iterable<Setting> getChildren() {
		return children;
	}

	// Upcasting fluent interface without covariance

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

	public final void addChildren(Setting... settings) {
		for (Setting setting : settings) {
			addChild(setting);
		}
	}

	public boolean isEmpty() {
		return children.isEmpty();
	}

	public String getUnlocalizedName() {
		return "betterHud.setting." + name;
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
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
	}

	/** Updates the GUI elements based on the state of other settings.
	 * This is called when any button tied to a setting callback is pressed */
	public void updateGuiParts(Collection<Setting> settings) {
		for(Setting setting : children) {
			setting.updateGuiParts(settings);
		}
	}

	/**
	 * @return A string setting if this setting has a value
	 * or {@code null} otherwise.
	 */
	public IStringSetting getStringSetting() {
		return null;
	}
}
