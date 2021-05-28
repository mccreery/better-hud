package jobicade.betterhud.config;

import static jobicade.betterhud.BetterHud.MODID;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

/**
 * Maintains a list of config slots for saving and loading.
 * Configs come from inside the jar, listed in
 * {@code betterhud:configs/configs.json}, and outside the jar in the user's
 * config folder.
 */
public class ConfigManager implements IResourceManagerReloadListener {
    /**
     * The resource location for lists of configs, where each entry is a resource
     * string. Configs from resource packs will stack, if used.
     */
    public static final ResourceLocation CONFIGS_LOCATION = new ResourceLocation(MODID, "configs/configs.json");

    private PathMatcher pathMatcher;
    private IResourceManager resourceManager;
    private Gson gson = new Gson();
    private List<ConfigSlot> internalConfigs;
    private Path rootDirectory;

    private final Path configPath;
    private HudConfig config;

    /**
     * Constructor for the config manager.
     * @param configPath The path of the config file.
     * @param rootDirectory The directory path where config saves are stored.
     */
    public ConfigManager(Path configPath, Path rootDirectory) {
        this.setRootDirectory(rootDirectory);
        try {
            Files.createDirectories(rootDirectory);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        this.configPath = configPath;
        this.reloadConfig();
    }

    /**
     * Reloads the current config, for example after overwriting it.
     */
    public void reloadConfig() {
        this.config = new HudConfig(configPath.toFile());
    }

    /**
     * Getter for the current config.
     * @return The current config.
     */
    public HudConfig getConfig() {
        return config;
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
    public void func_110549_a(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.internalConfigs = null;
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
            return resourceManager.func_135056_b(CONFIGS_LOCATION).stream().flatMap(this::streamJsonSlots);
        } catch(IOException e) {
            return Stream.empty();
        }
    }

    private Stream<ConfigSlot> streamJsonSlots(IResource resource) {
        try(Reader reader = new InputStreamReader(resource.func_110527_b())) {
            String[] paths = gson.fromJson(reader, String[].class);
            return Arrays.stream(paths).map(path -> new ResourceConfigSlot(new ResourceLocation(path)));
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
}
