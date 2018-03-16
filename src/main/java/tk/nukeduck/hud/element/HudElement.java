package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Sorter;
import tk.nukeduck.hud.util.SortField;

public abstract class HudElement {
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

	public enum RenderPhase {
		HUD, BILLBOARD;
	}

	public boolean shouldRender(RenderPhase phase) {
		return phase == RenderPhase.HUD;
	}

	/** Renders this element to the screen.<br>
	 * Should only be called if {@link #shouldRender(RenderPhase)} returns {@code true}
	 *
	 * @param phase The current render phase
	 * @return The bounds containing the element. {@code null} will be replaced by {@link Bounds#EMPTY} */
	protected abstract Bounds render(RenderPhase phase);

	/** Calls {@link #render(RenderPhase)} if the element
	 * should be rendered and caches the bounds so they are available from {@link #getLastBounds()} */
	public void tryRender(RenderPhase phase) {
		if(isEnabled() && shouldRender(phase)) {
			MC.mcProfiler.startSection(name);

			Bounds bounds = render(phase);
			if(bounds != null) {
				lastBounds = bounds;
			}

			MC.mcProfiler.endSection();
		}
	}

	public static void renderAll(RenderPhase phase) {
		for(HudElement element : SORTER.getSortedData(SortType.PRIORITY)) {
			element.tryRender(phase);
		}
	}

	private Bounds lastBounds;

	/** @return The last or appropriate bounds for this element.<br>
	 * {@link Bounds#EMPTY} if the element has no appropriate bounds */
	public Bounds getLastBounds() {
		return lastBounds;
	}

	public static void resetBounds() {
		for(HudElement element : ELEMENTS) {
			element.lastBounds = Bounds.EMPTY;
		}
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
	}
}
