package jobicade.betterhud.element;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.entityinfo.HorseInfo;
import jobicade.betterhud.element.entityinfo.MobInfo;
import jobicade.betterhud.element.entityinfo.PlayerInfo;
import jobicade.betterhud.element.particles.BloodSplatters;
import jobicade.betterhud.element.particles.WaterDrops;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.RootSetting;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.text.BiomeName;
import jobicade.betterhud.element.text.Connection;
import jobicade.betterhud.element.text.Coordinates;
import jobicade.betterhud.element.text.CpsCount;
import jobicade.betterhud.element.text.Distance;
import jobicade.betterhud.element.text.FpsCount;
import jobicade.betterhud.element.text.FullInvIndicator;
import jobicade.betterhud.element.text.GameClock;
import jobicade.betterhud.element.text.LightLevel;
import jobicade.betterhud.element.text.Saturation;
import jobicade.betterhud.element.text.SystemClock;
import jobicade.betterhud.element.vanilla.AirBar;
import jobicade.betterhud.element.vanilla.ArmorBar;
import jobicade.betterhud.element.vanilla.Crosshair;
import jobicade.betterhud.element.vanilla.Experience;
import jobicade.betterhud.element.vanilla.FoodBar;
import jobicade.betterhud.element.vanilla.HealthBar;
import jobicade.betterhud.element.vanilla.HelmetOverlay;
import jobicade.betterhud.element.vanilla.Hotbar;
import jobicade.betterhud.element.vanilla.JumpBar;
import jobicade.betterhud.element.vanilla.Offhand;
import jobicade.betterhud.element.vanilla.PortalOverlay;
import jobicade.betterhud.element.vanilla.PotionBar;
import jobicade.betterhud.element.vanilla.RidingHealth;
import jobicade.betterhud.element.vanilla.Sidebar;
import jobicade.betterhud.element.vanilla.Vignette;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.proxy.ClientProxy;
import jobicade.betterhud.util.IGetSet.IBoolean;
import jobicade.betterhud.util.SortField;
import jobicade.betterhud.util.Sorter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class HudElement implements IBoolean {
    /** A list of all the registered elements */
    public static final List<HudElement> ELEMENTS = new ArrayList<HudElement>();

    public static final ArmorBars ARMOR_BARS = new ArmorBars();
    public static final ArrowCount ARROW_COUNT = new ArrowCount();
    public static final BiomeName BIOME_NAME = new BiomeName();
    public static final BlockViewer BLOCK_VIEWER = new BlockViewer();
    public static final BloodSplatters BLOOD_SPLATTERS = new BloodSplatters();
    public static final Compass COMPASS = new Compass();
    public static final Connection CONNECTION = new Connection();
    public static final Coordinates COORDINATES = new Coordinates();
    public static final CpsCount CPS = new CpsCount();
    public static final Distance DISTANCE = new Distance();
    public static final ExperienceInfo EXPERIENCE_INFO = new ExperienceInfo();
    public static final FpsCount FPS = new FpsCount();
    public static final FullInvIndicator FULL_INV = new FullInvIndicator();
    public static final GameClock CLOCK = new GameClock();
    public static final GlobalSettings GLOBAL = new GlobalSettings();
    public static final HandBar HOLDING = new HandBar();
    public static final HealIndicator HEAL = new HealIndicator();
    public static final HorseInfo HORSE_INFO = new HorseInfo();
    public static final LightLevel LIGHT_LEVEL = new LightLevel();
    public static final MobInfo MOB_INFO = new MobInfo();
    public static final PickupCount PICKUP = new PickupCount();
    public static final PlayerInfo PLAYER_INFO = new PlayerInfo();
    public static final Saturation SATURATION = new Saturation();
    public static final SignReader SIGN_READER = new SignReader();
    public static final SystemClock SYSTEM_CLOCK = new SystemClock();
    public static final WaterDrops WATER_DROPS = new WaterDrops();

    public static final AirBar AIR_BAR = new AirBar();
    public static final ArmorBar ARMOR_BAR = new ArmorBar();
    public static final Crosshair CROSSHAIR = new Crosshair();
    public static final Experience EXPERIENCE = new Experience();
    public static final FoodBar FOOD_BAR = new FoodBar();
    public static final HealthBar HEALTH = new HealthBar();
    public static final HelmetOverlay HELMET_OVERLAY = new HelmetOverlay();
    public static final Hotbar HOTBAR = new Hotbar();
    public static final JumpBar JUMP_BAR = new JumpBar();
    public static final Offhand OFFHAND = new Offhand();
    public static final PortalOverlay PORTAL = new PortalOverlay();
    public static final PotionBar POTION_BAR = new PotionBar();
    public static final RidingHealth MOUNT = new RidingHealth();
    public static final Sidebar SIDEBAR = new Sidebar();
    public static final Vignette VIGNETTE = new Vignette();

    public enum SortType implements SortField<HudElement> {
        ALPHABETICAL("alphabetical", false) {
            @Override
            public int compare(HudElement a, HudElement b) {
                return a.getLocalizedName().compareTo(b.getLocalizedName());
            }
        }, ENABLED("enabled", false) {
            @Override
            public int compare(HudElement a, HudElement b) {
                int compare = b.settings.get().compareTo(a.settings.get());
                return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
            }
        }, PRIORITY("priority", false) {
            @Override
            public int compare(HudElement a, HudElement b) {
                int compare = a.settings.priority.get().compareTo(b.settings.priority.get());
                return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
            }
        };

        private final String unlocalizedName;
        private final boolean inverted;

        SortType(String unlocalizedName, boolean inverted) {
            this.unlocalizedName = "betterHud.menu." + unlocalizedName;
            this.inverted = inverted;
        }

        public String getUnlocalizedName() {
            return unlocalizedName;
        }
        public boolean isInverted() {
            return inverted;
        }
    };

    public static final Sorter<HudElement> SORTER = new Sorter<HudElement>(ELEMENTS);

    /** The settings saved to the config file for this element */
    public final RootSetting settings;
    protected final SettingPosition position;

    /**
     * {@inheritDoc}
     * <p>For {@link HudElement}, gets whether the element is enabled, ignoring
     * support. Rather use {@link #isEnabledAndSupported()} to check for support.
     *
     * @see #isEnabledAndSupported()
     */
    @Override
    public Boolean get() {
        return settings.get();
    }

    /**
     * {@inheritDoc}
     * <p>For {@link HudElement}, enables or disables the element. The element
     * will still not be rendered if it is not supported.
     *
     * @see #isSupportedByServer()
     */
    @Override
    public void set(Boolean value) {
        settings.set(value);
    }

    /**
     * Checks whether the element can be rendered before deciding.
     * @see #shouldRender(Event)
     * @return {@code true} if the element can be rendered.
     */
    public boolean isEnabledAndSupported() {
        return settings.get() && isSupportedByServer();
    }

    public final int id;
    public final String name;

    protected HudElement(String name) {
        this(name, new SettingPosition(DirectionOptions.NONE, DirectionOptions.NONE));
    }

    protected HudElement(String name, SettingPosition position) {
        this.name = name;
        this.position = position;

        List<Setting<?>> rootSettings = new ArrayList<>();
        addSettings(rootSettings);
        this.settings = new RootSetting(this, rootSettings);

        id = ELEMENTS.size();
        ELEMENTS.add(this);
    }

    /**
     * Adds all the element-specific settings to the settings window.
     * Include {@code super.addSettings()} for all child classes.
     *
     * <p>Note that this method is called before instance initializers for the
     * child class, including member initializers, so look out for {@code null}s.
     *
     * @param settings The list of settings to add new settings to.
     */
    protected void addSettings(List<Setting<?>> settings) {
        if(position.getDirectionOptions() != DirectionOptions.NONE || position.getContentOptions() != DirectionOptions.NONE) {
            settings.add(position);
        }
    }

    /** @return The minimum server version that supports this element
     * @see #isSupportedByServer() */
    public VersionRange getServerDependency() {
        return VersionRange.newRange(null, Arrays.asList(Restriction.EVERYTHING));
    }

    /** @return {@code true} if the current connected server supports the element.
     * If the server version is too low, some communications may not be supported
     * @see #getServerDependency()  */
    public boolean isSupportedByServer() {
        return getServerDependency().containsVersion(BetterHud.getServerVersion());
    }

    /** @return The localized name of the element
     * @see #getUnlocalizedName() */
    public final String getLocalizedName() {
        return I18n.get(getUnlocalizedName());
    }

    /** @return The unlocalized name of the element */
    public final String getUnlocalizedName() {
        return "betterHud.element." + name;
    }

    /**
     * Checks whether the element should render, in the current event context.
     * Note that this neither checks that the element is enabled, nor that
     * it is supported by the server. For that, use {@link #isEnabledAndSupported()}.
     *
     * @return {@code true} if the element should render.
     * @param event The current render event
     * @see #isEnabledAndSupported()
     */
    public boolean shouldRender(Event event) {
        return event instanceof RenderGameOverlayEvent;
    }

    /** Renders this element to the screen.<br>
     * Should only be called if {@link #shouldRender(Event)} returns {@code true}
     *
     * @param event The current render event
     * @return The bounds containing the element. {@code null} will be replaced by {@link Rect#EMPTY} */
    protected abstract Rect render(Event event);

    /** Calls {@link #render(Event)} if the element
     * should be rendered and caches the bounds so they are available from {@link #getLastRect()} */
    public final void tryRender(Event event) {
        if(shouldRender(event) && isEnabledAndSupported()) {
            Minecraft.getInstance().profiler.push(name);

            lastBounds = render(event);
            if(lastBounds == null) lastBounds = Rect.empty();
            postRender(event);

            Minecraft.getInstance().profiler.pop();
        }
    }

    private Rect lastBounds = Rect.empty();

    protected void postRender(Event event) {}

    /** Renders all elements for the current render event
     * @param event The current render event */
    public static void renderAll(Event event) {
        for(HudElement element : SORTER.getSortedData(SortType.PRIORITY)) {
            element.tryRender(event);
        }
    }

    /** @return The last or appropriate bounds for this element.<br>
     * {@link Rect#empty()} if the element has no appropriate bounds */
    public Rect getLastBounds() {
        return lastBounds;
    }

    /** Calls {@link #init(FMLInitializationEvent)} on all elements
     * @see #init(FMLInitializationEvent)
     * @see BetterHud#init(FMLInitializationEvent) */
    public static void initAll(FMLClientSetupEvent event) {
        for(HudElement element : ELEMENTS) {
            element.init(event);
        }
    }

    /**
     * Called for all elements during {@link FMLInitializationEvent} on the
     * physical client only. Elements registering themselves as event
     * subscribers can only use client-side events.
     *
     * @see ClientProxy#init(FMLInitializationEvent)
     */
    public void init(FMLClientSetupEvent event) {}

    /** Calls {@link #loadDefaults()} on all elements
     * @see #loadDefaults() */
    public static void loadAllDefaults() {
        GLOBAL.loadDefaults();

        for (HudElement element : ELEMENTS) {
            element.loadDefaults();
        }
        normalizePriority();
    }

    public static void normalizePriority() {
        SORTER.markDirty(SortType.PRIORITY);
        List<HudElement> prioritySort = SORTER.getSortedData(SortType.PRIORITY);

        for(int i = 0; i < prioritySort.size(); i++) {
            prioritySort.get(i).settings.priority.set(i);
        }
    }

    /** Loads this element's default settings.<br>
     *
     * You should always call the {@code super} implementation to handle the default enabled value of {@code true}
     * and to allow for future expansion */
    protected void loadDefaults() {
        set(true);
        settings.priority.set(0);
    }

    /** @return The partial ticks from the given event */
    public static float getPartialTicks(Event event) {
        if(event instanceof RenderGameOverlayEvent) {
            return ((RenderGameOverlayEvent)event).getPartialTicks();
        } else if(event instanceof RenderWorldLastEvent) {
            return ((RenderWorldLastEvent)event).getPartialTicks();
        } else {
            return 0;
        }
    }
}
