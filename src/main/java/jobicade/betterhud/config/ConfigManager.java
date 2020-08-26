package jobicade.betterhud.config;

import static jobicade.betterhud.BetterHud.MODID;

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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
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
import jobicade.betterhud.render.Color;
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
    private final Path configFile;
    private final Path configDirectory;

    private final HudRegistry<?> elementRegistry;
    private final Gson gson;

    private BetterHudConfig modSettings;

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

        try {
            Files.createDirectories(configDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.elementRegistry = elementRegistry;
        gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(HudElement.class, new ElementTypeAdapter(elementRegistry))
            .registerTypeAdapter(Point.class, new PointTypeAdapter())
            .setPrettyPrinting()
            .create();

        modSettings = new BetterHudConfig(elementRegistry);

        if (Files.exists(configFile)) {
            try {
                loadFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Path getConfigFile() {
        return configFile;
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

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
            gson.toJson(rootObject, writer);
        }
    }

    /**
     * @return A list of config slots supplied by resources and the file system.
     */
    public List<ConfigSlot> getConfigSlots() {
        List<ConfigSlot> fileSlots;
        try {
            fileSlots = loadFileSlots();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ConfigSlot> allSlots = new ArrayList<>(resourceSlots.size() + fileSlots.size());
        allSlots.addAll(resourceSlots);
        allSlots.addAll(fileSlots);
        return allSlots;
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager,
            IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
            Executor gameExecutor) {
        return CompletableFuture.runAsync(() -> {
            try {
                loadResources(resourceManager);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, backgroundExecutor);
    }

    public void loadResources(IResourceManager resourceManager) throws IOException {
        loadData(resourceManager);

        if (!Files.exists(configFile)) {
            ConfigSlot defaultSlot = new ResourceConfigSlot(defaultResource);
            defaultSlot.copyTo(configFile);
            loadFile();
        }
    }

    private List<ConfigSlot> loadFileSlots() throws IOException {
        try (Stream<Path> paths = Files.walk(configDirectory)) {
            return paths
                .filter(p -> Files.isRegularFile(p) && p.getFileName().endsWith(".json"))
                .map(FileConfigSlot::new)
                .collect(Collectors.toList());
        }
    }

    private List<ConfigSlot> resourceSlots = Collections.emptyList();
    private IResource defaultResource;

    private void loadData(IResourceManager resourceManager) throws IOException {
        resourceSlots = new ArrayList<>();
        final ResourceLocation dataLocation = new ResourceLocation(MODID, "configs/configs.json");

        for (IResource dataResource : resourceManager.getAllResources(dataLocation)) {
            try (Reader reader = new BufferedReader(new InputStreamReader(dataResource.getInputStream()))) {
                Data data = gson.fromJson(reader, Data.class);

                for (ResourceLocation configLocation : data.configs) {
                    IResource configResource = resourceManager.getResource(configLocation);
                    resourceSlots.add(new ResourceConfigSlot(configResource));
                }

                if (data.defaultConfig != null) {
                    defaultResource = resourceManager.getResource(data.defaultConfig);
                }
            }
        }
    }

    private static class Data {
        List<ResourceLocation> configs;
        ResourceLocation defaultConfig;
    }
}
