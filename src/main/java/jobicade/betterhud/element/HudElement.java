package jobicade.betterhud.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import jobicade.betterhud.util.SortField;
import jobicade.betterhud.util.Sorter;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @param T context object passed to render methods.
 */
public abstract class HudElement<T> {
	/** A list of all the registered elements */
	public static final List<HudElement<?>> ELEMENTS = new ArrayList<HudElement<?>>();

	// TODO MOVE
	public static final ArmorBars ARMOR_BARS = null;//new ArmorBars();
	public static final ArrowCount ARROW_COUNT = null;//new ArrowCount();
	public static final BiomeName BIOME_NAME = null;//new BiomeName();
	public static final BlockViewer BLOCK_VIEWER = null;//new BlockViewer();
	public static final BloodSplatters BLOOD_SPLATTERS = null;//new BloodSplatters();
	public static final Compass COMPASS = null;//new Compass();
	public static final Connection CONNECTION = null;//new Connection();
	public static final Coordinates COORDINATES = null;//new Coordinates();
	public static final CpsCount CPS = null;//new CpsCount();
	public static final Distance DISTANCE = null;//new Distance();
	public static final ExperienceInfo EXPERIENCE_INFO = null;//new ExperienceInfo();
	public static final FpsCount FPS = null;//new FpsCount();
	public static final FullInvIndicator FULL_INV = null;//new FullInvIndicator();
	public static final GameClock CLOCK = null;//new GameClock();
	public static final GlobalSettings GLOBAL = /*null;*/new GlobalSettings();
	public static final HandBar HOLDING = null;//new HandBar();
	public static final HealIndicator HEAL = null;//new HealIndicator();
	public static final HorseInfo HORSE_INFO = null;//new HorseInfo();
	public static final LightLevel LIGHT_LEVEL = null;//new LightLevel();
	public static final MobInfo MOB_INFO = null;//new MobInfo();
	public static final PickupCount PICKUP = null;//new PickupCount();
	public static final PlayerInfo PLAYER_INFO = null;//new PlayerInfo();
	public static final Saturation SATURATION = null;//new Saturation();
	public static final SignReader SIGN_READER = null;//new SignReader();
	public static final SystemClock SYSTEM_CLOCK = null;//new SystemClock();
	public static final WaterDrops WATER_DROPS = null;//new WaterDrops();

	public static final AirBar AIR_BAR = null;//new AirBar();
	public static final ArmorBar ARMOR_BAR = null;//new ArmorBar();
	public static final Crosshair CROSSHAIR = null;//new Crosshair();
	public static final Experience EXPERIENCE = null;//new Experience();
	public static final FoodBar FOOD_BAR = null;//new FoodBar();
	public static final HealthBar HEALTH = null;//new HealthBar();
	public static final HelmetOverlay HELMET_OVERLAY = null;//new HelmetOverlay();
	public static final Hotbar HOTBAR = null;//new Hotbar();
	public static final JumpBar JUMP_BAR = null;//new JumpBar();
	public static final Offhand OFFHAND = null;//new Offhand();
	public static final PortalOverlay PORTAL = null;//new PortalOverlay();
	public static final PotionBar POTION_BAR = null;//new PotionBar();
	public static final RidingHealth MOUNT = null;//new RidingHealth();
	public static final Sidebar SIDEBAR = null;//new Sidebar();
	public static final Vignette VIGNETTE = null;//new Vignette();

	public enum SortType implements SortField<HudElement<?>> {
		ALPHABETICAL("alphabetical", false) {
			@Override
			public int compare(HudElement<?> a, HudElement<?> b) {
				return a.getLocalizedName().compareTo(b.getLocalizedName());
			}
		}, ENABLED("enabled", false) {
			@Override
			public int compare(HudElement<?> a, HudElement<?> b) {
				int compare = b.settings.get().compareTo(a.settings.get());
				return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
			}
		}, PRIORITY("priority", false) {
			@Override
			public int compare(HudElement<?> a, HudElement<?> b) {
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

	public static final Sorter<HudElement<?>> SORTER = new Sorter<HudElement<?>>(ELEMENTS);

	/** The settings saved to the config file for this element */
	public final RootSetting settings;
	protected final SettingPosition position;

	public boolean isEnabled() {
		return settings.get();
	}

	public void setEnabled(boolean value) {
		settings.set(value);
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

	private static final VersionRange DEFAULT_SERVER_DEPENDENCY
		= VersionRange.newRange(null, Arrays.asList(Restriction.EVERYTHING));

	private VersionRange serverDependency = DEFAULT_SERVER_DEPENDENCY;

	/**
	 * Version spec is converted to range using
	 * {@link VersionRange#createFromVersionSpec(String)}.
	 */
	protected final void setServerDependency(String versionSpec) {
		VersionRange serverDependency;
		try {
			serverDependency = VersionRange.createFromVersionSpec(versionSpec);
		} catch (InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
		setServerDependency(serverDependency);
	}

	protected final void setServerDependency(VersionRange serverDependency) {
		this.serverDependency = serverDependency;
	}

	public final VersionRange getServerDependency() {
		return serverDependency;
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

	/** @return The localized name of the element
	 * @see #getUnlocalizedName() */
	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** @return The unlocalized name of the element */
	public final String getUnlocalizedName() {
		return "betterHud.element." + name;
	}

    /**
     * Checks any conditions for rendering apart from being enabled or
     * compatible. For example, the health bar would return {@code false} in
     * creative mode. Most elements will not need to override this method.
     *
     * @return {@code true} if extra conditions for rendering are met.
     */
	public boolean shouldRender(T context) {
		return true;
	}

	// TODO specify return value when the element is full screen
	/**
	 * @return The bounding box containing the rendered element.
	 */
	public abstract Rect render(T context);

	/** Calls {@link #render(Event)} if the element
	 * should be rendered and caches the bounds so they are available from {@link #getLastRect()} */
	public final void tryRender(Event event) {
		// TODO remove stub
		/*if(shouldRender(event) && isEnabledAndSupported()) {
			Minecraft.getMinecraft().mcProfiler.startSection(name);

			lastBounds = render(event);
			if(lastBounds == null) lastBounds = Rect.empty();
			postRender(event);

			Minecraft.getMinecraft().mcProfiler.endSection();
		}*/
	}

	private Rect lastBounds = Rect.empty();

	protected void postRender(T context) {}

	/** Renders all elements for the current render event
	 * @param event The current render event */
	public static void renderAll(Event event) {
		for(HudElement<?> element : SORTER.getSortedData(SortType.PRIORITY)) {
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
	public static void initAll(FMLInitializationEvent event) {
		for(HudElement<?> element : ELEMENTS) {
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
	public void init(FMLInitializationEvent event) {}

	/** Calls {@link #loadDefaults()} on all elements
	 * @see #loadDefaults() */
	public static void loadAllDefaults() {
		GLOBAL.loadDefaults();

		for (HudElement<?> element : ELEMENTS) {
			element.loadDefaults();
		}
		normalizePriority();
	}

	public static void normalizePriority() {
		SORTER.markDirty(SortType.PRIORITY);
		List<HudElement<?>> prioritySort = SORTER.getSortedData(SortType.PRIORITY);

		for(int i = 0; i < prioritySort.size(); i++) {
			prioritySort.get(i).settings.priority.set(i);
		}
	}

	/** Loads this element's default settings.<br>
	 *
	 * You should always call the {@code super} implementation to handle the default enabled value of {@code true}
	 * and to allow for future expansion */
	protected void loadDefaults() {
		setEnabled(true);
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
