package jobicade.betterhud.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.element.settings.Setting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles saving and loading config files through Forge's system. Note that
 * actual settings are stored in each element's settings object.
 */
public class HudConfig {
    private final Path path;

    public HudConfig(Path path) {
        this.path = path;
    }

    public void load() {
        JsonObject json;

        try (JsonReader reader = new JsonReader(Files.newBufferedReader(path))) {
            json = new JsonParser().parse(reader).getAsJsonObject();
        } catch (IOException exception) {
            BetterHud.getLogger().error(exception);
            return;
        }

        loadSetting(HudElement.GLOBAL.settings, json.get(HudElement.GLOBAL.name).getAsJsonObject(), null);

        for(HudElement element : HudElement.ELEMENTS) {
            loadSetting(element.settings, json.get(element.name).getAsJsonObject(), null);
        }

        HudElement.SORTER.markDirty(SortType.ENABLED);
        HudElement.normalizePriority();
    }

    /**
     * Loads a setting and all its children.
     */
    private void loadSetting(Setting<?> setting, JsonObject json, String path) {
        setting.load(json.get(path));
        for (Setting<?> childSetting : setting.getChildren()) {
            loadSetting(childSetting, json, join(path, childSetting.name));
        }
    }

    public void save() {
        JsonObject json = new JsonObject();

        JsonObject globalJson = new JsonObject();
        saveSetting(HudElement.GLOBAL.settings, globalJson, null);
        json.add(HudElement.GLOBAL.name, globalJson);

        for(HudElement element : HudElement.ELEMENTS) {
            JsonObject elementJson = new JsonObject();
            saveSetting(element.settings, elementJson, null);
            json.add(element.name, elementJson);
        }

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(path))) {
            new Gson().toJson(json, writer);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Saves a setting and all its children.
     */
    private void saveSetting(Setting<?> setting, JsonObject json, String path) {
        json.add(path, setting.save());
        for (Setting<?> childSetting : setting.getChildren()) {
            saveSetting(childSetting, json, join(path, childSetting.name));
        }
    }

    private static String join(String head, String tail) {
        if (tail == null || tail.isEmpty()) {
            return head;
        } else if (head == null || head.isEmpty()) {
            return tail;
        } else {
            return head + "." + tail;
        }
    }
}
