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
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
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
    /**
     * The resource location for lists of configs, where each entry is a resource
     * string. Configs from resource packs will stack, if used.
     */
    public static final ResourceLocation CONFIGS_LOCATION = new ResourceLocation(MODID, "configs/configs.json");

    private PathMatcher pathMatcher;
    private IResourceManager resourceManager;
    private List<ConfigSlot> internalConfigs;

    private Path rootDirectory;
    private Path configPath;

    private final HudRegistry<?> elementRegistry;
    private final Gson gson;

    public ConfigManager(HudRegistry<?> elementRegistry) {
        this.elementRegistry = elementRegistry;
        gson = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(HudElement.class, new ElementTypeAdapter(elementRegistry))
            .registerTypeAdapter(Point.class, new PointTypeAdapter())
            .create();
    }

    public void loadFile() throws IOException {
        JsonObject rootObject;

        try (Reader reader = new BufferedReader(new FileReader(configPath.toFile()))) {
            JsonParser parser = new JsonParser();
            rootObject = parser.parse(reader).getAsJsonObject();
        }

        for (Entry<String, JsonElement> entry : rootObject.entrySet()) {
            HudElement<?> element = elementRegistry.getRegistered(entry.getKey());

            if (element != null) {
                element.getRootSetting().loadJson(gson, entry.getValue());
            } else {
                BetterHud.getLogger().warn("Unknown element in config file: \"%s\"", entry.getKey());
            }
        }
    }

    public void saveFile() throws IOException {
        JsonObject rootObject = new JsonObject();

        for (HudElement<?> element : elementRegistry.getRegistered()) {
            rootObject.add(element.getName(), element.getRootSetting().saveJson(gson));
        }

        try (Writer writer = new BufferedWriter(new FileWriter(configPath.toFile()))) {
            gson.toJson(rootObject, writer);
        }
    }

    public void setConfigPath(Path configPath) {
        this.configPath = configPath;
        rootDirectory = configPath.resolveSibling(BetterHud.MODID);
    }

    /**
     * Getter for the path of the current config.
     * @return The path of the current config.
     */
    public Path getConfigPath() {
        return configPath;
    }

    /**
     * Getter for the directory path where saved configs are stored.
     * @return The root directory.
     */
    public Path getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Setter for the directory where saved configs are stored.
     * @param rootDirectory The new root directory.
     */
    public void setRootDirectory(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.pathMatcher = rootDirectory.getFileSystem().getPathMatcher("glob:**/*.cfg");
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager,
            IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
            Executor gameExecutor) {
        this.resourceManager = resourceManager;
        this.internalConfigs = null;

        // TODO config path should not be null
        if (configPath != null && !Files.exists(configPath)) {
            for (IResource config : getAllConfigs()) {
                try (InputStreamReader reader = new InputStreamReader(config.getInputStream())) {
                    Configs configs = gson.fromJson(reader, Configs.class);
                    ConfigSlot slot = new ResourceConfigSlot(configs.defaultConfig);

                    slot.copyTo(configPath);
                    loadFile();
                } catch (IOException e) {
                    BetterHud.getLogger().warn("Unable to load default config file", e);
                }
            }
        }
        // TODO
        //config.sortAvailable();

        return CompletableFuture.completedFuture(null);
    }

    private List<IResource> getAllConfigs() {
        try {
            return resourceManager.getAllResources(CONFIGS_LOCATION);
        } catch (IOException e) {
            throw new RuntimeException("Finding internal configs JSON", e);
        }
    }

    /**
     * Generates or returns a list of all internal and external save slots.
     * Internal save slots are stored in resources, and external save slots
     * are stored in the root directory.
     *
     * @return A list of all internal and external save slots.
     * @see #getInternalSlots()
     * @see #getExternalSlots()
     */
    public List<ConfigSlot> getSlots() {
        return Stream.concat(getInternalSlots().stream(), streamExternalSlots())
            .filter(distinctBy(ConfigSlot::getName))
            .collect(ImmutableList.toImmutableList());
    }

    /**
     * Generates or returns a list of internal save slots.
     * Internal save slots are stored in resources, and can be stacked using
     * resource packs. The resource locations of the config files are stored in
     * a JSON list inside the resource {@link #CONFIGS_LOCATION}.
     *
     * @return A list of all internal save slots.
     */
    public List<ConfigSlot> getInternalSlots() {
        if(internalConfigs == null) {
            internalConfigs = streamInternalSlots().collect(ImmutableList.toImmutableList());
        }
        return internalConfigs;
    }

    private Stream<ConfigSlot> streamInternalSlots() {
        try {
            return resourceManager.getAllResources(CONFIGS_LOCATION).stream().flatMap(this::streamJsonSlots);
        } catch(IOException e) {
            return Stream.empty();
        }
    }

    private Stream<ConfigSlot> streamJsonSlots(IResource resource) {
        try(Reader reader = new InputStreamReader(resource.getInputStream())) {
            Configs configs = gson.fromJson(reader, Configs.class);
            return Arrays.stream(configs.configs).map(ResourceConfigSlot::new);
        } catch(IOException e) {
            return Stream.empty();
        }
    }

    /**
     * Generates or returns a list of external save slots.
     * External save slots are stored in the root directory, as files.
     *
     * @return A list of all external save slots.
     * @see #getRootDirectory()
     */
    public List<ConfigSlot> getExternalSlots() {
        return streamExternalSlots().collect(ImmutableList.toImmutableList());
    }

    private Stream<ConfigSlot> streamExternalSlots() {
        try {
            return Files.walk(rootDirectory).filter(pathMatcher::matches).map(FileConfigSlot::new);
        } catch(IOException e) {
            return Stream.empty();
        }
    }

    /**
     * Creates a stateful filter which adds the result of the key function
     * on each object it filters to a set, returning true the first time it
     * sees a key and false for all subsequent times.
     *
     * <p>The filter is intended to be used for {@link Stream#filter(Predicate)}
     * and mimics {@link Stream#distinct()} for comparing different keys.
     */
    private static <T, U> Predicate<T> distinctBy(Function<? super T, U> key) {
        Set<U> seen = new HashSet<>();
        return t -> seen.add(key.apply(t));
    }

    // null members populated using reflection in GSON
    private static final class Configs {
        private final ResourceLocation[] configs = null;
        private final ResourceLocation defaultConfig = null;
    }
}
