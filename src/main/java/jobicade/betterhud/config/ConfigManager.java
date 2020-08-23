package jobicade.betterhud.config;

import static jobicade.betterhud.BetterHud.MODID;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.registry.HudRegistry;
import jobicade.betterhud.util.json.ColorTypeAdapter;
import jobicade.betterhud.util.json.ElementTypeAdapter;
import jobicade.betterhud.util.json.PointTypeAdapter;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * Maintains a list of config slots for saving and loading.
 * Configs come from inside the jar, listed in
 * {@code betterhud:configs/configs.json}, and outside the jar in the user's
 * config folder.
 */
public class ConfigManager implements IFutureReloadListener {
    public static final ResourceLocation CONFIGS_LOCATION = new ResourceLocation(MODID, "configs/configs.json");

    private final Path configFile;
    private final Path configDirectory;

    private final HudRegistry<?> elementRegistry;
    private final Gson gson;

    /**
     * {@code configDirectory} defaults to {@code betterhud} in the same
     * directory as {@code configFile}.
     *
     * @see #ConfigManager(Path, Path, HudRegistry)
     */
    public ConfigManager(Path configFile, HudRegistry<?> elementRegistry) {
        this(configFile, configFile.resolveSibling(BetterHud.MODID), elementRegistry);
    }

    /**
     * @param configFile The config file to load from.
     * @param configDirectory The directory containing alternate configs to swap.
     * @param elementRegistry Registry for looking up elements to load.
     */
    public ConfigManager(Path configFile, Path configDirectory, HudRegistry<?> elementRegistry) {
        this.configFile = configFile;
        this.configDirectory = configDirectory;

        this.elementRegistry = elementRegistry;
        gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(HudElement.class, new ElementTypeAdapter(elementRegistry))
            .registerTypeAdapter(Point.class, new PointTypeAdapter())
            .create();
    }

    public Path getConfigFile() {
        return configFile;
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

    private BetterHudConfig modSettings;

    public BetterHudConfig getModSettings() {
        return modSettings;
    }

    /**
     * Loads new settings from the config file.
     */
    public void loadFile() throws IOException {
        JsonObject elementSettings;

        try (Reader reader = new BufferedReader(new FileReader(configFile.toFile()))) {
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(reader).getAsJsonObject();

            modSettings = new BetterHudConfig(elementRegistry, gson.fromJson(rootObject.get("modSettings"), BetterHudConfig.Data.class));
            elementSettings = rootObject.getAsJsonObject("elementSettings");
        }

        for (Entry<String, JsonElement> entry : elementSettings.entrySet()) {
            HudElement<?> element = elementRegistry.getRegistered(entry.getKey());

            if (element != null) {
                element.getRootSetting().loadJson(gson, entry.getValue());
            } else {
                BetterHud.getLogger().warn("Unknown element in config file: \"%s\"", entry.getKey());
            }
        }
    }

    /**
     * Saves the current settings to the config file.
     */
    public void saveFile() throws IOException {
        JsonObject rootObject = new JsonObject();
        JsonObject elementSettings = new JsonObject();

        for (HudElement<?> element : elementRegistry.getRegistered()) {
            elementSettings.add(element.getName(), element.getRootSetting().saveJson(gson));
        }
        rootObject.add("elementSettings", elementSettings);
        rootObject.add("modSettings", gson.toJsonTree(new BetterHudConfig.Data(modSettings)));

        try (Writer writer = new BufferedWriter(new FileWriter(configFile.toFile()))) {
            gson.toJson(elementSettings, writer);
        }
    }

    private List<ConfigSlot> configSlots;
    /**
     * @return A list of config slots supplied by resources and the file system.
     */
    public List<ConfigSlot> getConfigSlots() {
        return configSlots;
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager,
            IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
            Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            try {
                ConfigResourceList configResourceList = loadConfigResourceList(resourceManager);
                configSlots = loadConfigSlots(resourceManager, configResourceList);

                if (!Files.exists(configFile)) {
                    IResource defaultResource = resourceManager.getResource(configResourceList.getDefaultConfig());
                    ConfigSlot defaultSlot = new ResourceConfigSlot(defaultResource);

                    defaultSlot.copyTo(configFile);
                    loadFile();
                }
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, gameExecutor);
    }

    /**
     * Loads the config resource list from configs.json.
     */
    private ConfigResourceList loadConfigResourceList(IResourceManager resourceManager) throws IOException {
        ConfigResourceList list = new ConfigResourceList();

        for (IResource resource : resourceManager.getAllResources(CONFIGS_LOCATION)) {
            try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                list.combine(gson.fromJson(reader, ConfigResourceList.class));
            }
        }
        return list;
    }

    /**
     * Loads config slots from resources and the file system.
     */
    private List<ConfigSlot> loadConfigSlots(IResourceManager resourceManager, ConfigResourceList configResourceList) throws IOException {
        List<ConfigSlot> slots = new ArrayList<>();

        for (ResourceLocation resourceLocation : configResourceList.getConfigs()) {
            IResource resource = resourceManager.getResource(resourceLocation);
            slots.add(new ResourceConfigSlot(resource));
        }

        try (Stream<Path> paths = Files.walk(configDirectory)) {
            paths
                .filter(p -> Files.isRegularFile(p) && p.getFileName().endsWith(".json"))
                .map(FileConfigSlot::new)
                .forEachOrdered(slots::add);
        }
        return slots;
    }
}
