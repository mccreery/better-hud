package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.entityinfo.EntityInfo;
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
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;

public abstract class HudElement { // Can't extend Gui due to @SideOnly
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
	public static final ExperienceInfo EXPERIENCE = new ExperienceInfo();
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

	public static final HudElement[] ELEMENTS = {
		ARMOR_BARS, ARROW_COUNT, BIOME_NAME, BLOCK_VIEWER, BLOOD_SPLATTERS,
		CLOCK, COMPASS, CONNECTION, COORDINATES, DISTANCE,
		MAX_LEVEL, EXPERIENCE, STATS, FPS, FULL_INV,
		HOLDING, HEAL, HUNGER, LIGHT_LEVEL, SIGN_READER,
		SYSTEM_CLOCK, WATER_DROPS, HIDE_PLAYERS,
		HORSE_INFO, MOB_INFO, PICKUP, PLAYER_INFO, POTION_BAR, CPS
	};

	public static final EntityInfo[] ENTITY_INFO = {
		HORSE_INFO, MOB_INFO, PLAYER_INFO
	};

	public final RootSetting settings = new RootSetting(this);

	public boolean isEnabled() {
		return settings.get() && isSupportedByServer();
	}

	public final String name;

	protected HudElement(String name) {
		this.name = name;
	}

	public Version getMinimumServerVersion() {
		return Version.ZERO;
	}

	public boolean isSupportedByServer() {
		return BetterHud.serverVersion.compareTo(getMinimumServerVersion()) >= 0;
	}

	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	public final String getUnlocalizedName() {
		return "betterHud.element." + name;
	}

	public boolean shouldRender() {return true;}

	/** Renders this element to the screen
	 * @return The bounds containing the element drawn */
	public abstract Bounds render(RenderGameOverlayEvent event);

	/** Calls {@link #render(RenderGameOverlayEvent)} if the element
	 * should be rendered and caches the bounds so they are available from {@link #getLastBounds()} */
	public void tryRender(RenderGameOverlayEvent event) {
		if(isEnabled() && shouldRender()) {
			MC.mcProfiler.startSection(name);
			lastBounds = render(event);
			MC.mcProfiler.endSection();
		} else {
			lastBounds = Bounds.EMPTY;
		}
	}

	private Bounds lastBounds = Bounds.EMPTY;

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

	/** Loads this element's default settings. */
	public abstract void loadDefaults();
}
