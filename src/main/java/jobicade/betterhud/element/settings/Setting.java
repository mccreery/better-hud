package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.gui.GuiElementSettings;
import net.minecraft.client.resources.I18n;

/** A setting for a {@link HudElement}. Child elements will be saved under
 * the namespace of the parent's name */
public abstract class Setting {
    private final ParentSetting parent;

    public final String name;

    /** Set to {@code true} to hide the setting from the GUI
     * @see #getGuiParts(List, Map, Point) */
    private boolean hidden = false;

    private BooleanSupplier enableOn = () -> true;

    /**
     * Creates a setting as one of the root settings in an element.
     */
    public Setting(String name) {
        this(null, name);
    }

    /**
     * Creates a child setting of any other setting including the root setting.
     */
    public Setting(ParentSetting parent, String name) {
        this.parent = parent;
        this.name = name;
        parent.children.add(this);
    }

    public String getName() {
        return name;
    }

    /**
     * Loads a new setting value from a JSON element. If the JSON is invalid
     * (e.g. the wrong type entirely), the value will not be updated. If the
     * JSON is incomplete, the value may be partially updated or not updated at
     * all, depending on the implementation.
     *
     * @param gson The GSON instance for deserializing JSON.
     * @param element The JSON subtree representing the new value.
     * @return {@code true} if the value was fully or partially updated.
     */
    public abstract boolean loadJson(Gson gson, JsonElement element);

    /**
     * Saves the current setting value to a JSON element.
     *
     * @param gson The GSON instance for serializing JSON.
     * @return A JSON subtree representing the current value.
     */
    public abstract JsonElement saveJson(Gson gson);

    public void setEnableOn(BooleanSupplier enableOn) {
        this.enableOn = enableOn;
    }

    public void setHidden() {
        this.hidden = true;
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

    /**
     * Populates {@code parts} with {@link Gui}s which should be added to the settings screen.<br>
     * Also populates {@code callbacks} with {@link #keyTyped(char, int)} and {@link #actionPerformed(GuiElementSettings, GuiButton)} callbacks.
     *
     * <p>The minimum implementation (in {@link Setting#getGuiParts(List, Map, Point)})
     * populates {@code parts} and {@code callbacks} with those of the element's children
     *
     * @param topAnchor The top center anchor for GUI parts being added
     * @return The bottom center anchor directly below this setting's parts
     */
    public Point getGuiParts(GuiElementSettings.Populator populator, Point topAnchor) {
        for (Setting setting : children) {
            if (!setting.hidden) {
                topAnchor = setting.getGuiParts(populator, topAnchor);
            }
        }
        return topAnchor;
    }

    /** Renders extra parts of this GUI */
    public void draw() {
        for(Setting setting : children) {
            setting.draw();
        }
    }

    /** Updates the GUI elements based on the state of other settings.
     * This is called when any button tied to a setting callback is pressed */
    public void updateGuiParts() {
        for(Setting setting : children) {
            setting.updateGuiParts();
        }
    }
}
