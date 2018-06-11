package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.entityinfo.HorseInfo;
import tk.nukeduck.hud.element.entityinfo.MobInfo;
import tk.nukeduck.hud.element.entityinfo.PlayerInfo;
import tk.nukeduck.hud.element.particles.BloodSplatters;
import tk.nukeduck.hud.element.particles.WaterDrops;
import tk.nukeduck.hud.element.settings.RootSetting;
import tk.nukeduck.hud.element.text.BiomeName;
import tk.nukeduck.hud.element.text.Connection;
import tk.nukeduck.hud.element.text.Coordinates;
import tk.nukeduck.hud.element.text.CpsCount;
import tk.nukeduck.hud.element.text.Distance;
import tk.nukeduck.hud.element.text.FoodHealthStats;
import tk.nukeduck.hud.element.text.FpsCount;
import tk.nukeduck.hud.element.text.FullInvIndicator;
import tk.nukeduck.hud.element.text.GameClock;
import tk.nukeduck.hud.element.text.LightLevel;
import tk.nukeduck.hud.element.text.SystemClock;
import tk.nukeduck.hud.element.vanilla.AirBar;
import tk.nukeduck.hud.element.vanilla.ArmorBar;
import tk.nukeduck.hud.element.vanilla.Crosshair;
import tk.nukeduck.hud.element.vanilla.Experience;
import tk.nukeduck.hud.element.vanilla.FoodBar;
import tk.nukeduck.hud.element.vanilla.HealthBar;
import tk.nukeduck.hud.element.vanilla.HelmetOverlay;
import tk.nukeduck.hud.element.vanilla.Hotbar;
import tk.nukeduck.hud.element.vanilla.JumpBar;
import tk.nukeduck.hud.element.vanilla.PortalOverlay;
import tk.nukeduck.hud.element.vanilla.RidingHealth;
import tk.nukeduck.hud.element.vanilla.Sidebar;
import tk.nukeduck.hud.element.vanilla.Vignette;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Sorter;
import tk.nukeduck.hud.util.SortField;

public abstract class HudElement {
	/** A list of all the registered elements */
	public static final List<HudElement> ELEMENTS = new ArrayList<HudElement>();

	public static final ArmorBars ARMOR_BARS = new ArmorBars();
	public static final ArrowCount ARROW_COUNT = new ArrowCount();
	public static final BiomeName BIOME_NAME = new BiomeName();
	public static final BlockViewer BLOCK_VIEWER = new BlockViewer();
	public static final BloodSplatters BLOOD_SPLATTERS = new BloodSplatters();
	public static final GameClock CLOCK = new GameClock();
	public static final Compass COMPASS = new Compass();
	public static final Connection CONNECTION = new Connection();
	public static final Coordinates COORDINATES = new Coordinates();
	public static final Distance DISTANCE = new Distance();
	public static final MaxLevelIndicator MAX_LEVEL = new MaxLevelIndicator();
	public static final ExperienceInfo EXPERIENCE_INFO = new ExperienceInfo();
	public static final FoodHealthStats STATS = new FoodHealthStats();
	public static final FpsCount FPS = new FpsCount();
	public static final FullInvIndicator FULL_INV = new FullInvIndicator();
	public static final HandBar HOLDING = new HandBar();
	public static final HealIndicator HEAL = new HealIndicator();
	public static final HungerIndicator HUNGER = new HungerIndicator();
	public static final LightLevel LIGHT_LEVEL = new LightLevel();
	public static final SignReader SIGN_READER = new SignReader();
	public static final SystemClock SYSTEM_CLOCK = new SystemClock();
	public static final WaterDrops WATER_DROPS = new WaterDrops();
	public static final GlobalSettings GLOBAL = new GlobalSettings();
	public static final HidePlayers HIDE_PLAYERS = new HidePlayers();
	public static final PickupCount PICKUP = new PickupCount();
	public static final PlayerInfo PLAYER_INFO = new PlayerInfo();
	public static final PotionBar POTION_BAR = new PotionBar();
	public static final HorseInfo HORSE_INFO = new HorseInfo();
	public static final MobInfo MOB_INFO = new MobInfo();
	public static final CpsCount CPS = new CpsCount();

	public static final HelmetOverlay HELMET_OVERLAY = new HelmetOverlay();
	public static final Hotbar HOTBAR = new Hotbar();
	public static final Experience EXPERIENCE = new Experience();
	public static final HealthBar HEALTH = new HealthBar();
	public static final FoodBar FOOD_BAR = new FoodBar();
	public static final ArmorBar ARMOR_BAR = new ArmorBar();
	public static final AirBar AIR_BAR = new AirBar();
	public static final JumpBar JUMP_BAR = new JumpBar();
	public static final RidingHealth MOUNT = new RidingHealth();
	public static final Vignette VIGNETTE = new Vignette();
	public static final Crosshair CROSSHAIR = new Crosshair();
	public static final Sidebar SIDEBAR = new Sidebar();
	public static final PortalOverlay PORTAL = new PortalOverlay();

	public enum SortType implements SortField<HudElement> {
		ALPHABETICAL("alphabetical", false) {
			@Override
			public int compare(HudElement a, HudElement b) {
				return a.name.compareTo(b.name);
			}
		}, ENABLED("enabled", false) {
			@Override
			public int compare(HudElement a, HudElement b) {
				int compare = a.settings.get().compareTo(b.settings.get());
				return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
			}
		}, PRIORITY("priority", true) {
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
	public final RootSetting settings = new RootSetting(this);

	public void setEnabled(boolean value) {
		settings.set(value);
	}

	public boolean isEnabled() {
		return settings.get() && isSupportedByServer();
	}

	public final int id;
	public final String name;

	protected HudElement(String name) {
		this.name = name;

		id = ELEMENTS.size();
		ELEMENTS.add(this);
	}

	/** @return The minimum server version that supports this element
	 * @see #isSupportedByServer() */
	public Version getMinimumServerVersion() {
		return Version.ZERO;
	}

	/** @return {@code true} if the current connected server supports the element.
	 * If the server version is too low, some communications may not be supported
	 * @see #getMinimumServerVersion() */
	public boolean isSupportedByServer() {
		return BetterHud.serverVersion.compareTo(getMinimumServerVersion()) >= 0;
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

	/** @return {@code true} if the element should render in the current world and event context
	 * @param event The current render event */
	public boolean shouldRender(Event event) {
		return event instanceof RenderGameOverlayEvent;
	}

	/** Renders this element to the screen.<br>
	 * Should only be called if {@link #shouldRender(Event)} returns {@code true}
	 *
	 * @param event The current render event
	 * @return The bounds containing the element. {@code null} will be replaced by {@link Bounds#EMPTY} */
	protected abstract Bounds render(Event event);

	/** Calls {@link #render(Event)} if the element
	 * should be rendered and caches the bounds so they are available from {@link #getLastBounds()} */
	public void tryRender(Event event) {
		if(isEnabled() && shouldRender(event)) {
			MC.mcProfiler.startSection(name);

			Bounds bounds = render(event);
			lastBounds = Bounds.isEmpty(bounds) ? Bounds.EMPTY : bounds;

			MC.mcProfiler.endSection();
		}
	}

	/** Renders all elements for the current render event
	 * @param event The current render event */
	public static void renderAll(Event event) {
		for(HudElement element : SORTER.getSortedData(SortType.PRIORITY)) {
			element.tryRender(event);
		}
	}

	/** The previous bounds within which the element was rendered */
	protected Bounds lastBounds = Bounds.EMPTY;

	/** @return The last or appropriate bounds for this element.<br>
	 * {@link Bounds#EMPTY} if the element has no appropriate bounds */
	public Bounds getLastBounds() {
		return lastBounds;
	}

	/** Calls {@link #init(FMLInitializationEvent)} on all elements
	 * @see #init(FMLInitializationEvent)
	 * @see BetterHud#init(FMLInitializationEvent) */
	public static void initAll(FMLInitializationEvent event) {
		for(HudElement element : ELEMENTS) {
			element.init(event);
		}
	}

	/** Called for all elements during {@link FMLPreInitializationEvent}
	 * @see BetterHud#init(FMLPreInitializationEvent) */
	public void init(FMLInitializationEvent event) {}

	/** Calls {@link #loadDefaults()} on all elements
	 * @see #loadDefaults() */
	public static void loadAllDefaults() {
		GLOBAL.loadDefaults();

		for (HudElement element : ELEMENTS) {
			element.loadDefaults();
		}
	}

	/** Loads this element's default settings.<br>
	 *
	 * You should always call the {@code super} implementation to handle the default enabled value of {@code true}
	 * and to allow for future expansion */
	public void loadDefaults() {
		setEnabled(true);
		settings.priority.set(0);
	}
}
