package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A setting which can have children.
 */
public final class ParentSetting extends Setting {
    private final List<Setting> children = new ArrayList<>();

    public ParentSetting(String name) {
        super(name);
    }

    public void addChild(Setting setting) {
        children.add(setting);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    private boolean requireAll;
    /**
     * @param requireAll {@code true} to only treat JSON as valid if it contains
     * properties for all children.
     * @see #loadJson(Gson, JsonElement)
     */
    public void setRequireAll(boolean requireAll) {
        this.requireAll = requireAll;
    }

    /**
     * {@inheritDoc}
     * <p>This implementation accepts incomplete objects (with properties
     * matching only some of the setting's children) and updates only those
     * children which are present. To disable this, use
     * {@link #setRequireAll(boolean)}.
     */
    @Override
    public boolean loadJson(Gson gson, JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();

            if (requireAll) {
                JsonElement original = saveJson(gson);

                if (loadChildren(gson, object) < children.size()) {
                    // Rollback
                    loadJson(gson, original);
                    return false;
                } else {
                    return true;
                }
            } else {
                return loadChildren(gson, object) > 0;
            }
        } else {
            return false;
        }
    }

    /**
     * Loads child values from a JSON object by their key.
     * @return The number of children found and updated.
     */
    private int loadChildren(Gson gson, JsonObject object) {
        int updated = 0;

        for (Setting childSetting : children) {
            JsonElement childElement = object.get(childSetting.getName());

            if (childElement != null && childSetting.loadJson(gson, childElement)) {
                ++updated;
            }
        }
        return updated;
    }

    @Override
    public JsonElement saveJson(Gson gson) {
        JsonObject element = new JsonObject();

        for (Setting childSetting : children) {
            element.add(childSetting.getName(), childSetting.saveJson(gson));
        }
        return element;
    }
}
