package jobicade.betterhud.element.settings;

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
    protected ParentSetting parent;

    private final String name;

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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean getHidden() {
        return hidden;
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
    public abstract Point getGuiParts(GuiElementSettings.Populator populator, Point topAnchor);

    /** Renders extra parts of this GUI */
    public abstract void draw();

    /** Updates the GUI elements based on the state of other settings.
     * This is called when any button tied to a setting callback is pressed */
    public abstract void updateGuiParts();
}
